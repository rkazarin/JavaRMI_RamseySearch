package ramsey;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import system.ProxyImp;
import api.Capabilities;
import api.Proxy;
import api.ProxyStoppedException;
import api.Result;
import api.Scheduler;
import api.Task;

public class RamseyScheduler implements Scheduler<Graph> {

	private static final long serialVersionUID = -5111266450833430476L;
	
	private String graphStoreAddress;
	private static final int GRAPH_STORE_LOOKUP_TIMEOUT = 1000;
	
	private transient Map<Integer, ProxyImp<Graph>> proxies = new ConcurrentHashMap<Integer, ProxyImp<Graph>>();
	private transient BlockingQueue<Result<Graph>> solution = new LinkedBlockingQueue<Result<Graph>>();
	
	private transient Store store;	
	private transient boolean isRunning = false;
	
	public RamseyScheduler(String graphStoreAddress) {
		this.graphStoreAddress = graphStoreAddress;
	}
	
	@Override
	public void scheduleInitial(Task<Graph> task) {}
	
	@Override
	public void schedule(Task<Graph> task) {}
	
	@Override
	public void start() {
		isRunning = true;
		
		findAndSetStore();
		
		new Thread(generateTasker()).start();
	}

	@Override
	public void stop() {
		isRunning = false;	
	}
	
	private void findAndSetStore(){
		while(isRunning) try {
			store = (Store) Naming.lookup(graphStoreAddress);
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
						if(task == null) continue;
						proxy.assignTask(task);
					} 
					catch (ProxyStoppedException e) {}				
				}	
			}
		};
	};
	
	private Task<Graph> generateTask(Proxy<Graph> proxy){
		Capabilities spec = proxy.getCapabilities();
		if(spec.isOnSpace()) return null; //dont schedule on space
		if(proxy.isBufferFull()) return null;
		
		return new RamseyTask(new Graph(10), 30);	
	}

	@Override
	public void processResult(Result<Graph> result) {
		//If Single value pass it on to target	
		if(result.hasValue()){
			solution.add(result);
			sendToStore(result.getValue());
		}
	}
	
	private void sendToStore(Graph graph){
		while(isRunning) try {
			store.put(graph);
			break;
		} catch (RemoteException e) {
			try { Thread.sleep(GRAPH_STORE_LOOKUP_TIMEOUT); }
			catch (InterruptedException e1) {}
		}
	}

	@Override
	public Result<Graph> getSolution() throws InterruptedException {
		return solution.take();
	}
	
	@Override
	public void registerProxyPool(Map<Integer, ProxyImp<Graph>> proxies) {
		this.proxies = proxies;
	}

}
