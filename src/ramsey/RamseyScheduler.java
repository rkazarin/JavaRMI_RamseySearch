package ramsey;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import api.Capabilities;
import api.Proxy;
import api.ProxyStoppedException;
import api.Result;
import api.Scheduler;
import api.SharedState;
import api.Task;
import util.Log;

public class RamseyScheduler implements Scheduler<Graph> {

	private static final long serialVersionUID = -5111266450833430476L;
	private static final int GRAPH_START_SIZE = 10;
	private static final int GRAPH_MIN_USEFUL_SIZE = 20;
	private static final int GRAPH_SMALL_PREFERED = 25;
	private static final int GRAPH_SMALL_LIMIT = 31;
	private static final int GRAPH_FINAL_LIMIT = 49;
	private static final int GRAPH_STORE_LOOKUP_TIMEOUT = 1000;
	
	private transient Map<Integer, Proxy<Graph>> proxies;
	private transient BlockingQueue<Result<Graph>> solutions;
	private transient BlockingQueue<Exception> exceptions;
	private transient BlockingQueue<Graph> solutionsToSend;
	
	private transient GraphStore store;	
	private transient SharedTabooList taboo;
	private transient int solutionsFound =0;
	private transient boolean isRunning = false;	
	
	private String graphStoreAddress;
	
	public RamseyScheduler(String graphStoreAddress) {
		this.graphStoreAddress = graphStoreAddress;
	}
	
	@Override
	public void scheduleInitial(Task<Graph> task) {}
	
	@Override
	public void schedule(Task<Graph> task) {}
	
	@Override
	public void start(SharedState initialState, Map<Integer, Proxy<Graph>> proxies, BlockingQueue<Result<Graph>> solutions, BlockingQueue<Exception> exceptions) {
		this.solutionsToSend = new LinkedBlockingQueue<Graph>();
		this.proxies = proxies;
		this.solutions = solutions;
		this.exceptions = exceptions;
		this.taboo = (SharedTabooList) initialState;
		isRunning = true;
		
		findAndSetStore();
		
		new Thread(generateTasker()).start();
		new Thread(solutionSender()).start();
	}

	@Override
	public void stop() {
		isRunning = false;	
	}
	
	private void findAndSetStore(){
		while(isRunning) try {
			store = (GraphStore) Naming.lookup(graphStoreAddress);
			break;
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			System.err.println("Unable to connect to Graph Store at '"+graphStoreAddress+"' retrying in "+GRAPH_STORE_LOOKUP_TIMEOUT+" ms");
			try { Thread.sleep(GRAPH_STORE_LOOKUP_TIMEOUT); }
			catch (InterruptedException e1) {}
		}  
	}
	
	private Runnable generateTasker() {
		return new Runnable() {
			@Override
			public void run() {
				while(isRunning) for(Proxy<Graph> proxy: proxies.values()) {
					try {
						Task<Graph> task = generateTask(proxy);
						if(task != null) 
							proxy.assignTask(task);
					} 
					catch (ProxyStoppedException e) {}				
				}	
			}
		};
	};
	
	
	private Runnable solutionSender() {
		return new Runnable() {
			@Override
			public void run() {
				while(isRunning)  {
					if(store == null) 
						try { Thread.sleep(GRAPH_STORE_LOOKUP_TIMEOUT); }
						catch (InterruptedException e1) {}
					
					try {
						Graph solution = solutionsToSend.take();
						store.put(solution);
					} catch (RemoteException e) {
						System.err.println("Error accessing Graph store");
						e.printStackTrace();
					} catch (InterruptedException e) {}
					
				}	
			}
		};
	};
	
	private Task<Graph> generateTask(Proxy<Graph> proxy){
		Capabilities spec = proxy.getCapabilities();
		if(spec.isOnSpace()) return null; //dont schedule on space
		if(proxy.getNumQueued() > 1) return null;

        try {
            if(!spec.isLongRunning()){
            	Graph graph = store.getBestUnasigned(GRAPH_SMALL_PREFERED);
            	
            	Log.verbose("Asking for Best Graph >= size "+GRAPH_SMALL_PREFERED+" "+(graph==null?"but none recieved!":"and got Graph Size "+graph.size()));
            	if(graph != null)
            		graph = graph.extendRandom();
            	else
            		graph = Graph.generateRandom(GRAPH_START_SIZE);
            	
            	return new RamseyTask(graph, GRAPH_MIN_USEFUL_SIZE, GRAPH_SMALL_LIMIT);
            }
            else{
            	Graph graph = store.getBestUnasigned();
            	Log.verbose("Asking for Best Graph "+(graph==null?"but none recieved!":"and got Graph Size "+graph.size()));
            	
            	if(graph != null)
            		graph = graph.extendRandom();
            	else 
            		graph = Graph.generateRandom(GRAPH_START_SIZE);
            	
            	return new RamseyTask(graph, GRAPH_MIN_USEFUL_SIZE,  GRAPH_FINAL_LIMIT);
            }
            
        } catch (RemoteException e) {
        	System.err.println("Error accessing Graph store");
            e.printStackTrace();
        }

        return null;
	}

	@Override
	public void processResult(Result<Graph> result) {
		
		//If Exceptions add to exceptions queue
		if(result.hasException()){
			exceptions.add(result.getException());
			
			
			//Print exceptions stack trace
			Log.verbose("Exception: "+result.getException().getMessage());
		}
		
		//If Single value pass it on to target	
		if(result.hasValue() && result.getValue().size() >= GRAPH_MIN_USEFUL_SIZE){			
			solutions.add(result);
			solutionsToSend.add(result.getValue());
			solutionsFound++;
		}
	}
	
	@Override
	public String toString() {
		return "Ramsey(5,5) Scheduler";
	}
	
	public String statusString() {
		String out = "Progress: "+solutionsFound+" found | Taboo Size: "+taboo.size()+" | Computers:";
		
		for(Proxy<Graph> p: proxies.values())
			out+= " ["+p.getId()+":"+p.getNumQueued()+"]";
		return out;
	}

	@Override
	public void updateState(SharedState state) {
		taboo = (SharedTabooList) state;
	};
}
