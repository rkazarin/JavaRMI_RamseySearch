package ramsey;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GraphStore extends Remote{

	public static int DEFAULT_PORT = 8002;
	public static String DEFAULT_NAME = "GraphStore";
	
	boolean put(Graph example) throws RemoteException;

	List<Graph> getLevel(int level) throws RemoteException;

	List<Graph> getGraphsGreaterThan(int level) throws RemoteException;

	Graph getBest(int startingAt) throws RemoteException;

	int size() throws RemoteException;

	int numLevels() throws RemoteException;

}
