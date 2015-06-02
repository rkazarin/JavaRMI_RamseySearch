package api;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Defines a machine that takes tasks, executes them, and produces results.
 * Connects to a space
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 * @param <R> Type of result that this computer will produce
 */
public interface Computer<R> extends Remote {

	/**
	 * Queue task for execution
	 * @param task
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
	void addTask(Task<R> task) throws RemoteException, InterruptedException;
	
	/**
	 * Collect a result that has been produced
	 * @return result
	 * @throws RemoteException
	 * @throws InterruptedException
	 */
	Result<R> collectResult() throws RemoteException, InterruptedException;
	
	/**
	 * Update Shared State on Computer
	 * Computer may choose whic stat eto keep, unless force parameter set to true
	 * @param state to update with
	 * @param force this update?
	 * @throws RemoteException
	 */
	void updateState(SharedState state, boolean force) throws RemoteException;
		
	/**
	 * Sets a handle to the space that this computer is attached to
	 * @param space RMI handle
	 * @param assignedId given by space 
	 * @throws RemoteException
	 */
	void assignSpace(Space<R> space, int assignedId) throws RemoteException;
	
}
