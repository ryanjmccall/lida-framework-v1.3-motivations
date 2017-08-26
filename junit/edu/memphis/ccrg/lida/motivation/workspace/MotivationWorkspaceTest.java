package edu.memphis.ccrg.lida.motivation.workspace;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.shared.NodeStructure;
import edu.memphis.ccrg.lida.framework.shared.NodeStructureImpl;
import edu.memphis.ccrg.lida.pam.PerceptualAssociativeMemoryImpl;

/**
 * @author ryanjmccall
 *
 */
public class MotivationWorkspaceTest {

	private static final ElementFactory factory = ElementFactory.getInstance();
	private NodeStructure graph;
	private Collection<Node> visitedNodes;
	private Node n1, n2, n3, n4;
	
	@Before
	public void setup(){
		graph = new NodeStructureImpl();
		visitedNodes = new HashSet<Node>();
		n1 = factory.getNode();
		n2 = factory.getNode();
		n3 = factory.getNode();
		n4 = factory.getNode();
	}
	
	@Test
	public void testIsCycle1() {
		//Simple chain 
		graph.addDefaultNode(n1);
		graph.addDefaultNode(n2);
		graph.addDefaultNode(n3);
		graph.addDefaultNode(n4);
		graph.addDefaultLink(n1, n2, PerceptualAssociativeMemoryImpl.DEFAULT_LINK_CATEGORY, 1.0, 0.0);
		graph.addDefaultLink(n2, n3, PerceptualAssociativeMemoryImpl.DEFAULT_LINK_CATEGORY, 1.0, 0.0);
		graph.addDefaultLink(n3, n4, PerceptualAssociativeMemoryImpl.DEFAULT_LINK_CATEGORY, 1.0, 0.0);
		//
		Node source = n1;
		Node sink = n4;
		visitedNodes.add(source); 
		boolean result = MotivationWorkspace.isCycle(sink, graph, visitedNodes);
		assertFalse(result);
	}
	
	@Test
	public void testIsCycle2() {
		//Simple chain 
		graph.addDefaultNode(n1);
		graph.addDefaultNode(n2);
		graph.addDefaultNode(n3);
		graph.addDefaultNode(n4);
		graph.addDefaultLink(n1, n2, PerceptualAssociativeMemoryImpl.DEFAULT_LINK_CATEGORY, 1.0, 0.0);
		graph.addDefaultLink(n2, n3, PerceptualAssociativeMemoryImpl.DEFAULT_LINK_CATEGORY, 1.0, 0.0);
		graph.addDefaultLink(n3, n4, PerceptualAssociativeMemoryImpl.DEFAULT_LINK_CATEGORY, 1.0, 0.0);
		//
		Node source = n4;
		Node sink = n1;
		visitedNodes.add(source); 
		boolean result = MotivationWorkspace.isCycle(sink, graph, visitedNodes);
		assertTrue(result);
	}
	
	@Test
	public void testIsCycle3() {
		boolean result = MotivationWorkspace.isCycle(n1, graph, visitedNodes);
		assertFalse(result);
	}
	
	@Test
	public void testIsCycle4() {
		//Simple chain 
		graph.addDefaultNode(n1);
		graph.addDefaultNode(n2);
		
		//
		Node source = n1;
		Node sink = n2;
		visitedNodes.add(source);
		boolean result = MotivationWorkspace.isCycle(sink, graph, visitedNodes);
		assertFalse(result);
	}

}
