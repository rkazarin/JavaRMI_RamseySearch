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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RamseyStore extends UnicastRemoteObject implements Serializable, Iterable<Graph>, GraphStore{

	private static final long serialVersionUID = 8867753203624856389L;
	public static final long CHECKPOINT_SAVE_INTERVAL= 10000;
	
	public static final int GRAPH_LESS_THAN = 103;
	public static final int GRAPH_AT_LEAST = 8;
	public static final String BANK_FILENAME = "bank.save";
	public static final String TEMP_EXTENSION = ".tmp";
	public static final int IGNORE_FAILED_MORE_THAN = 2;

	@SuppressWarnings("unchecked")
	private transient List<Graph>[] hierarchy = new List[GRAPH_LESS_THAN];
	private Map<UUID,Graph> map = new HashMap<>();
	 
	protected RamseyStore() throws RemoteException {
		super();
		for(int i=0; i<hierarchy.length; i++){
			hierarchy[i]= new LinkedList<Graph>();
		}
	}

	public synchronized boolean put( Graph example){
		List<Graph> set = hierarchy[example.size()];
		
		//Do Isomorph check
		for(Graph a: set) if(a.isIsomorphOf(example)) return false;

		map.put(example.getId(),example);
		hierarchy[example.size()].add(example);
		
		return true;
	}
	
	public synchronized Graph get(UUID graphID){
		return map.get(graphID);
	}
	
	public synchronized List<Graph> getLevel(int level){
		return hierarchy[level];
	}
	
	public synchronized List<Graph> getGraphsGreaterThan(int level){
		LinkedList<Graph> list = new LinkedList<>();
		for(int i = hierarchy.length-1; i > level ; i--){
			list.addAll(hierarchy[i]);
		}
		return list;
	}
	
	public synchronized Graph getBest(int startingAt){
		if(startingAt > hierarchy.length-1) 
			startingAt = hierarchy.length-1;
		
		//Search for best starting point
		for(int size=startingAt; size>=GRAPH_AT_LEAST; size--){
			for(Graph g: hierarchy[size]){
				if(!g.isAssigned() && g.timesFailedToFindSolution() < IGNORE_FAILED_MORE_THAN)
					return g;
			}
		}
		//could not find one, so make one
		Graph g = Graph.generateRandom(GRAPH_AT_LEAST);
	
		put(g);
		return g;
	}
	
	public synchronized boolean contains(UUID graphId){
		return map.containsKey(graphId);
	}
	

	public int size(){
		return map.size();
	}
	
	public int numLevels(){
		return hierarchy.length;
	}
	
	public synchronized void save() throws IOException{
		File bankFile = new File(BANK_FILENAME);
		File bankTempFile = new File(BANK_FILENAME+TEMP_EXTENSION);
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(bankTempFile));
		
		synchronized (map) {
			out.writeObject(map);
		}
		
		out.close();

		//Move temp to permanent
	    bankFile.delete();
	    bankTempFile.renameTo(bankFile);
	}
	
	@SuppressWarnings("unchecked")
	public static RamseyStore loadOrCreate( String filename ) throws RemoteException{

		File bankFile = new File(filename);
		
		RamseyStore bank = new RamseyStore();
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(bankFile));
			
			bank.map = (HashMap<UUID,Graph>)in.readObject();
			
			for(Graph g: bank.map.values())
				bank.hierarchy[g.size()].add(g);
				
			in.close();
		}
		catch (IOException | ClassNotFoundException e) {}
		
		return bank;
	}

	@Override
	public Iterator<Graph> iterator() {
		return map.values().iterator();
	}
	
	/* ------------ Main Method ------------ */
	public static void main(String[] args) throws RemoteException {		
		// Set Security Manager 
        System.setSecurityManager( new SecurityManager() );

        // Create Registry on JVM
        Registry registry = LocateRegistry.createRegistry( GraphStore.DEFAULT_PORT );

        //Print Acknowledgement
        System.out.println("Starting Store as '"+GraphStore.DEFAULT_NAME+"' on port "+GraphStore.DEFAULT_PORT);
        
        // Create Store
        RamseyStore store = RamseyStore.loadOrCreate(BANK_FILENAME);
        registry.rebind( GraphStore.DEFAULT_NAME, store );

        new Thread( new Runnable() {	
			
        	public void run() {
				while(true) try {
					Thread.sleep(CHECKPOINT_SAVE_INTERVAL);
					store.save();
					System.out.println("Checkpoint Saved");
				}
				catch (InterruptedException e) {} 
				catch (IOException e) {
					System.err.println("Error Saving Checkpoint");
				}
			}
		}).start();
        
	}

}
