package system;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import api.Proxy;
import api.ProxyStoppedException;
import api.Scheduler;
import api.Task;

public class SchedulerDefault<R> implements Scheduler<R> {
	
	private static final int INITIAL_CAPACITY = 25000;
	
	private BlockingQueue<Task<R>> waitingTasks = new LinkedBlockingQueue<Task<R>>();
	private PriorityBlockingQueue<Task<R>> shortTaskPool = new PriorityBlockingQueue<Task<R>>(INITIAL_CAPACITY, new TaskComparator());
	private PriorityBlockingQueue<Task<R>> longTaskPool = new PriorityBlockingQueue<Task<R>>(INITIAL_CAPACITY, new TaskComparator());
	
	private Map<Integer, ProxyImp<R>> proxies = new ConcurrentHashMap<Integer, ProxyImp<R>>();
	private boolean isRunning = false;
	
	@Override
	public void schedule(Task<R> task){
		waitingTasks.add(task);
	}
	
	
	@Override
	public void start() {
		isRunning = true;
		
		new Thread(sorter).start();
		new Thread(shortAssigner).start();
		new Thread(longAssigner).start();
		
	}
	
	@Override
	public void stop() {
		isRunning = false;
	}

	@Override
	public void registerProxyPool(Map<Integer, ProxyImp<R>> proxies) {
		this.proxies = proxies;
	}
	
	@Override
	public String toString() {
		return longTaskPool.size()+" remote, "+shortTaskPool.size()+" local, "+waitingTasks.size()+" waiting ";
	}

	private Runnable sorter = new Runnable() {
		@Override
		public void run() {
			while(isRunning){
				try {
					Task<R> task = waitingTasks.take();
					
					if(task.isReady()){
					
						if(task.isShortRunning())
							shortTaskPool.add(task); 
						else
							longTaskPool.add(task);
					}
					else
						waitingTasks.add(task);
				}
				catch(InterruptedException e){}
			}
		
			
		}
	};
	
	private Runnable shortAssigner = new Runnable() {
		@Override
		public void run() {
			while(isRunning) for(Proxy<R> p: proxies.values()) {
				if(p.getCapabilities().isOnSpace()){
					try {
						Task<R> t = shortTaskPool.take();
						try {
							p.assignTask(t);
						} catch (ProxyStoppedException e) {
							shortTaskPool.add(t);
						}
					}
					catch (InterruptedException e1) {}
				}
			}	
		}
	};
	

	private Runnable longAssigner = new Runnable() {
		@Override
		public void run() {
			while(isRunning) for(Proxy<R> p: proxies.values()) {
				if(!p.getCapabilities().isOnSpace()){
					try {
						Task<R> t = longTaskPool.take();
						try {
							p.assignTask(t);
						} catch (ProxyStoppedException e) {
							longTaskPool.add(t);
						}
					}
					catch (InterruptedException e1) {}
				}
			}	
		}
	};
		
}