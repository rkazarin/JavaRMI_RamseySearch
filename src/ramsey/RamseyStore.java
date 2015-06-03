package ramsey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RamseyStore extends UnicastRemoteObject implements Serializable, Iterable<Graph>, GraphStore{

	private static final long serialVersionUID = 8867753203624856389L;
	private static final long CHECKPOINT_SAVE_INTERVAL= 30000;
	
	public static final String BANK_FILENAME = "bank.save";
	public static final String TEMP_EXTENSION = ".tmp";

	private transient Queue<Graph>[] hierarchy;
	private transient Queue<Graph>[] unassigned;
	
	private Map<UUID,Graph> map = new ConcurrentHashMap<>();
	 
	@SuppressWarnings("unchecked")
	public RamseyStore(int maxSize) throws RemoteException {
		super();
		hierarchy = new ConcurrentLinkedQueue[maxSize+1];
		unassigned = new ConcurrentLinkedQueue[maxSize+1];

		for(int i=0; i<hierarchy.length; i++){
			hierarchy[i]= new ConcurrentLinkedQueue<Graph>();
			unassigned[i]= new ConcurrentLinkedQueue<Graph>();
		}
	}

    /**
     * Store a given graph into the graph store
     * @param graph a graph instnace
     */
	@Override
	public synchronized boolean put(Graph graph){
		Queue<Graph> set = hierarchy[graph.size()];
		
		//Do Isomorph check
		for(Graph g: set) if(g.isIsomorphOf(graph)) return false;

		map.put(graph.getId(),graph);
		hierarchy[graph.size()].add(graph);
		unassigned[graph.size()].add(graph);
		
		return true;
	}
	
	@Override
	public synchronized Graph getBestUnasigned(){
		return getBestUnasigned(Integer.MAX_VALUE);
	}

    /**
     * Find the best graph currently in the graph store, starting at a given size
     * @param startingAt starting size
     */
	@Override
	public synchronized Graph getBestUnasigned(int startingAt){
		if(startingAt > unassigned.length-1) 
			startingAt = unassigned.length-1;
		
		//Search for best starting point
		for(int size=startingAt; size>=0; size--){
			if(unassigned[size].size() > 0)
				return unassigned[size].poll();
		}
		//could not find one
		return null;
	}
	
	@Override
	public synchronized boolean contains(UUID graphId){
		return map.containsKey(graphId);
	}
	
	@Override
	public int sizeOfStore(){
		return map.size();
	}

    /**
     * Save graph store to disk
     */
	public synchronized void save() throws IOException{
		File bankFile = new File(BANK_FILENAME);
		File bankTempFile = new File(BANK_FILENAME+TEMP_EXTENSION);
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(bankTempFile));
		
		Map<UUID,Graph> mapCopy;
		synchronized (map) {
			mapCopy = new ConcurrentHashMap<>(map);
		}
		out.writeObject(mapCopy);
		
		out.close();

		//Move temp to permanent
	    bankFile.delete();
	    bankTempFile.renameTo(bankFile);
	}

    /**
     * Load graph store from disk
     * @param filename name of file
     * @param maxSize size of graph store
     */
	@SuppressWarnings("unchecked")
	public static RamseyStore load( String filename, int maxSize ) throws RemoteException{
		File bankFile = new File(filename);
		
		try {
			RamseyStore bank = new RamseyStore(maxSize);
			
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(bankFile));
			bank.map = (ConcurrentHashMap<UUID,Graph>)in.readObject();
			in.close();

			for(Graph g: bank.map.values()){
				bank.hierarchy[g.size()].add(g);
				bank.unassigned[g.size()].add(g);
			}
			return bank;
		}
		catch (IOException | ClassNotFoundException e) {
			return null;
		}
	}

	@Override
	public Iterator<Graph> iterator() {
		return map.values().iterator();
	}
	
	public String contentsReportAsString(){
		String out = "------------------------------------ Size:"+map.size()+" ------------------------------------\n";
		
		int i=0;
		while(i<hierarchy.length){
			out+=i+":\t"+(hierarchy[i].size()<=0?"-":hierarchy[i].size())+"\t";
			if(i%5==4) out+="\n";
			i++;
		}
		out += "--------------------------------------------------------------------------------\n";
		return out;
	}
	
	/* ------------ Main Method ------------ */
	private static final int RAMSEY_STORE_SIZE = 49;
	public static void main(String[] args) throws RemoteException {	
		// Set Security Manager 
        System.setSecurityManager( new SecurityManager() );

        // Create Registry on JVM
        Registry registry = LocateRegistry.createRegistry( GraphStore.DEFAULT_PORT );

        //Print Acknowledgement
        System.out.println("Starting Store as '"+GraphStore.DEFAULT_NAME+"' on port "+GraphStore.DEFAULT_PORT+"\n");
        
        // Create Store
        RamseyStore store = RamseyStore.load(BANK_FILENAME, RAMSEY_STORE_SIZE);
        
        if(store != null){
        	System.out.println("Loading Store from: '"+BANK_FILENAME+"'");
        	System.out.println(store.contentsReportAsString());
        }
        else {
        	System.out.println("Store Does not exist. Creating new one of size: "+RAMSEY_STORE_SIZE);
        	store = new RamseyStore(RAMSEY_STORE_SIZE);
        }
     
        registry.rebind( GraphStore.DEFAULT_NAME, store );

        //Checkpointer
        final RamseyStore theStore = store; 
        new Thread( new Runnable() {	
			
        	public void run() {
				while(true) try {
					Thread.sleep(CHECKPOINT_SAVE_INTERVAL);
					
					System.out.println("Checkpoint Saved:");
					theStore.save();
					System.out.println(theStore.contentsReportAsString());
				}
				catch (InterruptedException e) {} 
				catch (IOException e) {
					System.err.println("Error Saving Checkpoint");
				}
			}
		}).start();
	}
}
