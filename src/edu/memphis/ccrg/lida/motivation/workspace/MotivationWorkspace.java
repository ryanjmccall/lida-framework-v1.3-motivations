package edu.memphis.ccrg.lida.motivation.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.shared.Link;
import edu.memphis.ccrg.lida.framework.shared.LinkCategory;
import edu.memphis.ccrg.lida.framework.shared.Linkable;
import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.shared.NodeStructure;
import edu.memphis.ccrg.lida.motivation.shared.FeelingNode;
import edu.memphis.ccrg.lida.motivation.shared.FeelingNodeImpl;
import edu.memphis.ccrg.lida.workspace.Workspace;
import edu.memphis.ccrg.lida.workspace.WorkspaceContent;
import edu.memphis.ccrg.lida.workspace.WorkspaceImpl;
import edu.memphis.ccrg.lida.workspace.workspacebuffers.BroadcastQueue;
import edu.memphis.ccrg.lida.workspace.workspacebuffers.WorkspaceBuffer;

/**
 * This {@link Workspace} adds percepts directly to the CSM as opposed to the PerceptualBuffer. It also builds
 * temporal links whenever a non-feeling node is added to the CSM.
 * @author ryanjmccall
 */
public class MotivationWorkspace extends WorkspaceImpl {
	
	private static final Logger logger = Logger.getLogger(MotivationWorkspace.class.getCanonicalName());
	private LinkCategory temporalCategory;
	private Object csmLock = new Object();

	/**
	 * Sets the temporal {@link LinkCategory}.
	 * @param c a {@link LinkCategory}
	 */
	void setTemporalCategory(LinkCategory c){
		temporalCategory = c;
	}
	
	@Override
	public void receivePercept(Link l) {
		WorkspaceBuffer buffer = (WorkspaceBuffer)getSubmodule(ModuleName.CurrentSituationalModel);
		NodeStructure ns = buffer.getBufferContent(null);
		synchronized (csmLock) {
			ns.addLink(l,l.getFactoryType());
		}
	}
	@Override
	public void receivePercept(NodeStructure ns) {
		WorkspaceBuffer buffer = (WorkspaceBuffer)getSubmodule(ModuleName.CurrentSituationalModel);
		synchronized (csmLock) {
			buffer.addBufferContent((WorkspaceContent) ns);
		}
	}

	@Override
	public void receivePercept(Node n) {
		WorkspaceBuffer csm = (WorkspaceBuffer)getSubmodule(ModuleName.CurrentSituationalModel);
		NodeStructure csmStructure = csm.getBufferContent(null);
		csmStructure.addNode(n, n.getFactoryType());
		if(!(n instanceof FeelingNode)){
			buildTemporalLink(n, csmStructure);
		}
	}

	/*
	 * It appears preferable to perform this operation right when a node is added to the CSM
	 * as opposed to performing it cyclically in a task.
	 */
	private void buildTemporalLink(Node n, NodeStructure csmStructure) {
		BroadcastQueue queue = (BroadcastQueue) getSubmodule(ModuleName.BroadcastQueue);
		NodeStructure latestBroadcast = queue.getPositionContent(0);
		if (latestBroadcast != null) {
			Node sink = n;
			if(!latestBroadcast.containsNode(sink)){ 
				Node source = getFirstEventNode(latestBroadcast, temporalCategory);
				if(source != null){
					if(!csmStructure.containsNode(source)){ // Avoid endlessly exciting source
						csmStructure.addNode(source, source.getFactoryType());
					}
					Collection<Node> visitedNodes = new HashSet<Node>();
					visitedNodes.add(source); //Initialize by marking source as already visited, if encountered again there's a cycle
					synchronized (csmLock) {
						boolean createsCycle = isCycle(sink, csmStructure, visitedNodes);
						if(!createsCycle){						
							csmStructure.addDefaultLink(source, sink, temporalCategory, 1.0, 0.0);						
						}else{
							logger.log(Level.INFO, "Cycle avoided: Source: {0}, Sink: {1}",
									new Object[]{source, sink});
							logger.log(Level.INFO, "BQ structure: {0}", latestBroadcast);
							logger.log(Level.INFO, "CSM structure: {0}", csmStructure);
						}
					}
				}	
			}
		}
	}
	/**
	 * Returns true if a traversal of 'graph' from 'currentNode' encounters a Node in 'visitedNodes'.
	 * @param currentNode current node being explored
	 * @param graph a NodeStructure graph
	 * @param visitedNodes Collection of explored nodes
	 * @return true if a node is encountered twice, else false
	 */
	static boolean isCycle(Node currentNode, NodeStructure graph, Collection<Node> visitedNodes){
		if(visitedNodes.contains(currentNode)){
			return true;
		}
		visitedNodes.add(currentNode);
		for(Linkable lnk: graph.getConnectedSinks(currentNode).keySet()){
			if(lnk instanceof Node && !(lnk instanceof FeelingNode)){ //Just to ensure lnk is a plain node
				if(isCycle((Node)lnk, graph, visitedNodes)){ //recursive call using sink of currentNode
					return true; //As soon as any cycle is found, then return
				}
			}
		}
		return false;
	}
	
	/*
	 * Returns the earliest (based on temporal links) and maximally activated event node in the
	 * specified {@link NodeStructure}.
	 */
	private static Node getFirstEventNode(NodeStructure ns, LinkCategory temporalCategory){
		Collection<Node> temporalSources = new HashSet<Node>();
		Collection<Linkable> temporalSinks = new HashSet<Linkable>();
		for(Link l: ns.getLinks()){
			if(l.getCategory().equals(temporalCategory)){
				temporalSources.add(l.getSource());
				temporalSinks.add(l.getSink());
			}
		}
		Collection<Node> firstEventNodes = new ArrayList<Node>();
		for(Node n: temporalSources){
			if(!temporalSinks.contains(n)){ //First in the chain
				firstEventNodes.add(n);
			}
		}
		Node result = null;
		if(firstEventNodes.isEmpty()){
			//No temporal link are in the NodeStructure, get the max.
			result = getMaxEventNode(ns.getNodes()); 
		}else{
			//There may be multiple nodes occurring first in a chain
			result = getMaxEventNode(firstEventNodes); 
		}
		return result;
	}
	/*
	 * Returns the maximally activated non-feeling node among the specific Collection.
	 */
	private static Node getMaxEventNode(Collection<Node> nodes) {
		double maxActivation = 0.0;
		Node maxEventNode = null;
		for (Node n: nodes) {
			if (!(n instanceof FeelingNodeImpl)) {
				if (n.getActivation() > maxActivation) {
					maxActivation = n.getActivation();
					maxEventNode = n;
				}
			}
		}
		return maxEventNode;
	}	
}