package api;


/**
 * Define Proxy between Space and Computer
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 * @param <R> type of result to collect
 */
public interface Proxy<R> {

	/**
	 * Get Id of this proxy (unique on the space)
	 * @return the id
	 */
	int getId();

	/**
	 * Get the capabilities on the associated Computer 
	 * @return the capabilities
	 */
	Capabilities getCapabilities();

	/**
	 * Update the state on the attached Computer
	 * @param updatedState the new state to send
	 * @param force this update?
	 */
	void updateState(SharedState updatedState, boolean force);
	
	/**
	 * Assign a new task to the associated Computer Queue
	 * @param task to enqueue
	 * @throws ProxyStoppedException if proxy is already stopped (for example if computer has disconected)
	 */
	void assignTask(Task<R> task) throws ProxyStoppedException;
		
	/**
	 * Get the number of tasks enqued on the associated Computer (running, or in the queue)
	 * @return the number of queued tasks
	 */
	int getNumQueued();

	/**
	 * Looks a the capabilites of the associated Computer 
	 * @return true if maximum number of tasks have been enqueued
	 */
	boolean isBufferFull();

}