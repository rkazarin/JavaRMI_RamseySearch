package ramsey;

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
	
	private Map<Integer, ProxyImp<Graph>> proxies = new ConcurrentHashMap<Integer, ProxyImp<Graph>>();
	private BlockingQueue<Result<Graph>> solution = new LinkedBlockingQueue<Result<Graph>>();
	
	private boolean isRunning = false;
	
	@Override
	public void scheduleInitial(Task<Graph> task) {}
	
	@Override
	public void schedule(Task<Graph> task) {}
	
	@Override
	public void start() {
		isRunning = true;
		new Thread(generateTasker()).start();
	}

	@Override
	public void stop() {
		isRunning = false;	
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
		
		return new RamseyTask(new Graph(6), 11);	
	}

	@Override
	public void processResult(Result<Graph> result) {
		//If Single value pass it on to target	
		if(result.hasValue()){
			solution.add(result);
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
