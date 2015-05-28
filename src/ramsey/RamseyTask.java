package ramsey;

import system.TaskClosure;
import util.Graph;
import api.Result;
import api.SharedState;
import api.UpdateStateCallback;

public class RamseyTask extends TaskClosure<RamseyChunk> {


	private static final long serialVersionUID = 6673708275266440578L;

	public RamseyTask(Graph graph) {
		super("Ramsey", 0, 0, false);
	}

	public RamseyTask(String name, int priority, int numInputs,
			boolean isShorRunning, long targetUid, int targetPort) {
		super(name, priority, numInputs, isShorRunning, targetUid, targetPort);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void updateState(SharedState updatedState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Result<RamseyChunk> execute(SharedState currentState,
			UpdateStateCallback callback) {
		// TODO Auto-generated method stub
		return null;
	}

}
