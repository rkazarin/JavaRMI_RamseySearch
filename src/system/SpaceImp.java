package system;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import util.Log;
import api.Capabilities;
import api.Computer;
import api.Proxy;
import api.ProxyCallback;
import api.Result;
import api.Scheduler;
import api.SharedState;
import api.Space;
import api.Task;

public class SpaceImp<R> extends UnicastRemoteObject implements Space<R>{

	/** Serial ID */
	private static final long serialVersionUID = -1147376615845722661L;

	private static final int STATUS_OUTPUT_INTERVAL = 1000;
	
	private static final boolean FORCE_STATE = true;
	private static final boolean SUGGEST_STATE = false;
	private static final int BUFFER_SIZE_OF_LOCAL_COMPUTER = 1;
		
	private int PROXY_ID_POOL = 0;
	
	private Scheduler<R> scheduler;
	
	protected BlockingQueue<Result<R>> solution = new LinkedBlockingQueue<Result<R>>();
	private Map<Integer, ProxyImp<R>> allProxies = new ConcurrentHashMap<Integer, ProxyImp<R>>();
	
	private SharedState state = new StateBlank();

	public SpaceImp(int numLocalThreads) throws RemoteException {
		super();		
		new StatusPrinter().start();
		int actualNumberOfThreadsToSet = numLocalThreads>0?numLocalThreads:1;
		ComputeNodeSpec localSpec = new ComputeNodeSpec(BUFFER_SIZE_OF_LOCAL_COMPUTER, actualNumberOfThreadsToSet, ComputeNode.RUNS_ON_SPACE, ComputeNode.SHORT_RUNNING);
		register(new ComputeNode<R>(localSpec), localSpec);
	}

	@Override
	public void setTask(Task<R> task) throws RemoteException, InterruptedException {
		setTask(task, new StateBlank());
	}
	
	@Override
	public void setTask(Task<R> task, SharedState initialState) throws RemoteException, InterruptedException {
		setTask(task, initialState, new SchedulerDefault<R>());
	}
	
	@Override
	public void setTask(Task<R> task, SharedState initialState, Scheduler<R> customScheduler) throws RemoteException, InterruptedException {
		state = initialState;
				
		for(Proxy<R> p: allProxies.values()){
			p.updateState(state, FORCE_STATE);
		}
		
		if( scheduler != null) scheduler.stop();
		scheduler = customScheduler;
		scheduler.start(allProxies, solution);
		scheduler.scheduleInitial(task);
	}

	@Override
	public Result<R> getSolution() throws RemoteException, InterruptedException {
		return solution.take();
	}
	
	@Override
	public int register(Computer<R> computer, Capabilities spec) throws RemoteException {
		int proxyID = PROXY_ID_POOL++;
		
		computer.assignSpace(this, proxyID);
		
		ProxyImp<R> proxy = new ProxyImp<R>(computer, spec, proxyID, proxyCallback);
		
		System.out.println("Registering "+proxy);
		proxy.updateState(state, FORCE_STATE);
		allProxies.put(proxyID, proxy );
		return proxyID;
	}
	
	
	@Override
	public void updateState(int originatorID, SharedState updatedState) throws RemoteException {
		SharedState original = state;
		
		this.state = state.update(updatedState);
		
		Log.debug("<="+originatorID+"= "+updatedState+(updatedState !=null && (original != state)?" Updated":" Kept") );
		if( original != state){
			
			for(Proxy<R> p: allProxies.values()){
				if(p.getId() != originatorID)
					p.updateState(updatedState, SUGGEST_STATE);
			}
		}	
	}
	
	private ProxyCallback<R> proxyCallback = new ProxyCallback<R>() {

		@Override
		public synchronized void doOnError(int proxyId, Collection<Task<R>> leftoverTasks) {
			System.out.println("Requeing "+leftoverTasks.size()+ " tasks");
			
			for(Task<R> task : leftoverTasks)
				scheduler.schedule(task);
			
			allProxies.remove(proxyId);
		}

		@Override
		public synchronized void processResult(Result<R> result) {
			scheduler.processResult(result);
		}
	};
	
	class StatusPrinter extends Thread {
		String last = "";
		@Override
		public void run() {
			while(true){
				try { Thread.sleep(STATUS_OUTPUT_INTERVAL); } catch (InterruptedException e) {}
				
				String newOutput = "Progress: "+scheduler+" Computers:";
				
				for(ProxyImp<R> p: allProxies.values())
					newOutput+= " ["+p.getId()+":"+p.getNumDispatched()+"|"+p.getNumCollected()+"]";
				if(!last.equals(newOutput)){
					last = newOutput;
					Log.debug(newOutput);
				}
			}
		}
	}
	
	/* ------------ Main Method ------------ */
	public static void main(String[] args) throws RemoteException {
		int numLocalThreads = (args.length > 0)? Integer.parseInt(args[0]) : 0;
		String logFile = "space";

		Log.startLog(logFile);
		
		// Set Security Manager 
        System.setSecurityManager( new SecurityManager() );

        // Create Registry on JVM
        Registry registry = LocateRegistry.createRegistry( Space.DEFAULT_PORT );

        //Print Acknowledgement
        System.out.println("Starting Space as '"+Space.DEFAULT_NAME+"' on port "+Space.DEFAULT_PORT);
        
        // Create Space
        SpaceImp<Object> space = new SpaceImp<Object>(numLocalThreads);
        registry.rebind( Space.DEFAULT_NAME, space );

        //Log.close();
	}

}
