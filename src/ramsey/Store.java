package ramsey;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

public interface Store extends Remote{

	public static int DEFAULT_PORT = 8002;
	public static String DEFAULT_NAME = "GraphStore";
	
	boolean put(Graph example) throws RemoteException;

	Graph get(UUID graphID) throws RemoteException;

	List<Graph> getLevel(int level) throws RemoteException;

	List<Graph> getGraphsGreaterThan(int level) throws RemoteException;

	Graph getBest(int startingAt) throws RemoteException;

	boolean contains(UUID graphId) throws RemoteException;

	int size() throws RemoteException;

	int numLevels() throws RemoteException;

}
