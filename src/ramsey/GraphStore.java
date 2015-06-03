package ramsey;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * Defines a repository of graphs.
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 */
public interface GraphStore extends Remote{

	/**
	 * Port to connect to RMI Repository on
	 */
	public static int DEFAULT_PORT = 8002;
	
	/**
	 * Enty name in RMI registry
	 */
	public static String DEFAULT_NAME = "GraphStore";
	
	/**
	 * Put a graph into the store
	 * @param graph to store
	 * @return if succeded
	 * @throws RemoteException
	 */
	boolean put(Graph graph) throws RemoteException;
	
	/**
	 * Checks if a graph with particular ID is in the store
	 * @param graphId
	 * @return if present in store
	 * @throws RemoteException
	 */
	boolean contains(UUID graphId) throws RemoteException;

	/**
	 * Get best graph (of largest size) that is not yet assigned
	 * @return the graph
	 * @throws RemoteException
	 */
	Graph getBestUnasigned() throws RemoteException;
	
	/**
	 * Get best graph of size equal to or smaller then requested that is not yet assigned
	 * @param startingAt Mzximum size of graph to return 
	 * @return the graph
	 * @throws RemoteException
	 */
	Graph getBestUnasigned(int startingAt) throws RemoteException;
}
