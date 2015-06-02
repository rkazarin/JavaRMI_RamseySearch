package api;

import java.io.Serializable;

/**
 * Defines a basic computation task on the system.
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 * @param <R> type of REsult to expect
 */
public interface Task<R> extends Serializable {

	/**
	 * Have all the inputs and conditions been met for this task to execute
	 * @return true if task is ready for execution
	 */
	boolean isReady();
	
	/**
	 * Sets the target of the task in a DAG
	 * @param targetUid UID of task to send output to
	 * @param targetPort input slot of Target task to send output to
	 */
	void setTarget(long targetUid, int targetPort);

	/**
	 * Sets an input on this task
	 * @param num slot
	 * @param value to set to
	 */
	void setInput(int num, Object value);

	/**
	 * Sets the runtime of worst performing execution path to this task in DAG
	 * @param timeInf runtime sum to this point
	 */
	void addCriticalLengthOfParent(double timeInf);
	
	/**
	 * Sets the Unique Idetifier of this space (called by Space)
	 * @param uid to set
	 */
	void setUid(long uid);

	/**
	 * Gets the assigned Unique ID of this task
	 * @return the UID
	 */
	long getUID();

	/**
	 * Gets the UID of the target task that this task should send output to
	 * @return
	 */
	long getTargetUid();

	/**
	 * Get the target port this task will send output to
	 * @return
	 */
	int getTargetPort();

	/**
	 * Called by Computer to execute this task
	 * @param currentState at the time it is called (updated via UpdateState() method)
	 * @param callback for sending partial results back to computer
	 * @return a final Result back to the space
	 */
	Result<R> call(SharedState currentState, ComputerCallback<R> callback);
	
	/**
	 * Updates the known state in the executing task
	 * @param updatedState
	 */
	void updateState(SharedState updatedState);
	
	/**
	 * Gets the priority of this Task
	 * (Used by some schedulers to priorities execution)
	 * @return priority (greater value is higher priority)
	 */
	int getPriority();
	
	/**
	 * Should this task be run on Space
	 * (Only implemented on certain scheduleres)
	 * @return true if runnable of the space
	 */
	boolean isSpaceRunnable();
	
	/**
	 * Gets the name of the task
	 * Useful for tracing through the system
	 * Ex: Add, Compare, TSP, Fibbinachi, Ramsey, etc..
	 * @return the name
	 */
	String getName();
}