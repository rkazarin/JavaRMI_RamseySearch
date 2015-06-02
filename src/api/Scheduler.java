package api;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Defines a scheduler that runs on the Space
 * A Scheduler handles the assignment of tasks to PRoxies.
 * Scheduler may have a custom method for processing results
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 * @param <R> Type of result to be produced
 */
public interface Scheduler<R> extends Serializable{

	/**
	 * Sets the initial task to the scheduler
	 * This task will point to the final solution in the space
	 * @param task to start job with
	 */
	void setJob(Task<R> task);

	/**
	 * Process the result recieved by Proxy and take apropriate action
	 * @param result to process
	 */
	void processResult(Result<R> result);
	
	/**
	 * Called by proxy when a computer disconnects to reschedule tasks
	 * @param leftoverTasks to reschedule
	 */
	void rescheduleTasks(Collection<Task<R>> leftoverTasks);
	
	/**
	 * Gets a status string that explains the progress of this Scheduler
	 * @return the status string
	 */
	String statusString();
	
	/**
	 * Called by Space when the state has been updated, can be used by scheduler to take action
	 * @param state new state
	 */
	void updateState(SharedState state);
	
	//Should not be blocking!!!
	/**
	 * Start the scheduler on the space
	 * WARNING: Should not be blocking, start any blocking process in new thread
	 * @param initialState of the Space
	 * @param proxies Pointer to proxies in Space, any modifications of objects carried to parent space
	 * @param solutions Pointer to solutions in Space, any modifications of objects carried to parent space
	 * @param exceptions Pointer to exceptions in Space, any modifications of objects carried to parent space
	 */
	void start(SharedState initialState, Map<Integer, Proxy<R>> proxies, BlockingQueue<Result<R>> solutions, BlockingQueue<Exception> exceptions);
	
	/**
	 * Stops the space
	 * WARNING: Should not be blocking, start any blocking process in new thread
	 */
	void stop();
	
}