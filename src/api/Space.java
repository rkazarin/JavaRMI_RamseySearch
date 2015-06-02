package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Defines a space that mediates between clients and connected Computers
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 * @param <R> type of result to expect
 */
public interface Space<R> extends Remote {

	public static int DEFAULT_PORT = 8001;
	public static String DEFAULT_NAME = "Space";

	/**
	 * Sets the job of the space to specified task
	 * @see Space.setJob(Task<R> task, SharedState initialState, Scheduler<R> customScheduler)
	 */
	void setJob(Task<R> task) throws RemoteException, InterruptedException;
	
	/**
	 * Sets the job of the space to specified task
	 * @see Space.setJob(Task<R> task, SharedState initialState, Scheduler<R> customScheduler)
	 */
	void setJob(Task<R> task, SharedState initialState) throws RemoteException, InterruptedException;
	
	/**
	 * Sets the job of the space to specified task
	 * @param task to set, will point to solution
	 * @param initialState to pass to system
	 * @param customScheduler to run on Space 
	 * @throws RemoteException 
	 * @throws InterruptedException
	 */
	void setJob(Task<R> task, SharedState initialState, Scheduler<R> customScheduler) throws RemoteException, InterruptedException;
	
	/**
	 * Gets a solution from the solution queue. 
	 * Blocks until solution is available.
	 * @return solution
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
	Result<R> getSolution() throws RemoteException, InterruptedException;
	
	/**
	 * Gets an exception from the exception queue
	 * Blocks until exception is encountered
	 * @return
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
	Exception getException() throws RemoteException, InterruptedException;
	
	/**
	 * REgister a new computer on this space
	 * @param computer to register
	 * @param spec Capabilities of this computer
	 * @return unique ID on this space
	 * @throws RemoteException
	 */
	int register( Computer<R> computer, Capabilities spec ) throws RemoteException;

	/**
	 * Update the shared state on this space
	 * @param originatorID of the Computer that requests the update
	 * @param state to update to (will compare the states)
	 * @throws RemoteException
	 */
	void updateState(int originatorID, SharedState state) throws RemoteException;

}