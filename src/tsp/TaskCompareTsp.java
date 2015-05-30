package tsp;

import system.TaskClosure;
import api.ComputerCallback;
import api.Result;
import api.SharedState;

public class TaskCompareTsp extends TaskClosure<ChunkTsp>{

	private static final long serialVersionUID = -2157191669367268779L;
	
	public TaskCompareTsp(long target, int targetPort, int num_inputs) {
		super("Compare TSP", DEFAULT_PRIORITY, num_inputs, SHORT_RUNNING, target, targetPort);
	}

	@Override
	public Result<ChunkTsp> execute(SharedState currentState, ComputerCallback<ChunkTsp> callback) {
		//find the shortest path and return that list of cities
		ChunkTsp bestChunk = (ChunkTsp) input[0];
		for(int i = 1; i < input.length; i++) {
			ChunkTsp currChunk = (ChunkTsp) input[i];
			if(currChunk.getBestLength() < bestChunk.getBestLength()){
				bestChunk = currChunk;
			}
		}
		
		callback.updateState(new StateTsp(bestChunk.getBestLength()));
		return new Result<ChunkTsp>(bestChunk);
	}

	@Override
	public void updateState(SharedState updatedState) {
		/* don't care about state update for comparing */
	}
	
}
