package edu.memphis.ccrg.lida.motivation.workspace;

import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.workspace.workspacebuffers.BroadcastQueueImpl;

/**
 * @author ryanjmccall
 *
 */
public class MotivationBroadcastQueueImpl extends BroadcastQueueImpl {
//TODO move MBQImpl from framework project
	private static final int decayFrequency = 600;
	
	@Override
	public void decayModule(long t) {
		if(isDecay && TaskManager.getCurrentTick() % 1000 == decayFrequency){
			broadcastQueue.clear();
		}
	}
	
}
