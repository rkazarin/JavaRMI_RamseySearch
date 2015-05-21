package ramsey;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import system.ResultTasks;
import system.ResultValue;
import system.TaskClosure;
import api.Result;
import api.SharedState;
import api.Task;
import api.UpdateStateCallback;

public class RamseyTask extends Task<RamseyChunk> {

	private static final long serialVersionUID = -2567928535294012341L;
	
	private static final int BASIC_TSP_PROBLEM_SIZE = 11;
	
	int graphSize;

	public RamseyTask(int graphSize){

        this.graphSize = graphSize;

	}

	@SuppressWarnings("unchecked")
	@Override
	public Result<RamseyChunk> execute(SharedState initialState, UpdateStateCallback callback) {


	}
	
	@Override
	public void updateState(SharedState updatedState) {

	}



	@Override
	public String toString() {

	}
	
	private RamseyChunk solve(){


	}
}
