package ramsey;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Queue;
import java.util.UUID;

public interface GraphStore extends Remote{

	public static int DEFAULT_PORT = 8002;
	public static String DEFAULT_NAME = "GraphStore";
	
	boolean put(Graph example) throws RemoteException;
	boolean contains(UUID graphId) throws RemoteException;

	Graph getBestUnasigned() throws RemoteException;
	Graph getBestUnasigned(int startingAt) throws RemoteException;

	int sizeOfStore() throws RemoteException;

}
