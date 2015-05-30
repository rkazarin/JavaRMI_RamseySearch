package system;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import api.Proxy;
import api.ProxyStoppedException;
import api.Result;
import api.Scheduler;
import api.Task;

public class SchedulerDefault<R> implements Scheduler<R> {

	private static final long serialVersionUID = 4553427787142633L;

	protected static final long SOLUTION_UID = 0;
	protected long UID_POOL = SOLUTION_UID+1;

	private static final int INITIAL_CAPACITY = 25000;

	protected BlockingQueue<Result<R>> solution;
	protected Map<Integer, ProxyImp<R>> proxies;
	
	protected Map<Long, Task<R>> registeredTasks = new ConcurrentHashMap<Long, Task<R>>();
	protected BlockingQueue<Task<R>> waitingTasks = new LinkedBlockingQueue<Task<R>>();
	
	private PriorityBlockingQueue<Task<R>> shortTaskPool = new PriorityBlockingQueue<Task<R>>(INITIAL_CAPACITY, new TaskComparator());
	private PriorityBlockingQueue<Task<R>> longTaskPool = new PriorityBlockingQueue<Task<R>>(INITIAL_CAPACITY, new TaskComparator());
	
	private boolean isRunning = false;	
	private double totalRuntime = 0;
	
	@Override
	public void scheduleInitial(Task<R> task){
		if(task == null) return;
		task.setTarget(SOLUTION_UID, 0);
		schedule(task);
	}
	
	@Override
	public void schedule(Task<R> task){
		if(task == null) return;
	
		task.setUid(UID_POOL++);
		
		registeredTasks.put(task.getUID(), task);
		waitingTasks.add(task);
	}
	
	@Override
	public synchronized void processResult(Result<R> result) {

		Task<R> origin = registeredTasks.get(result.getTaskCreatorId()); //registeredTasks.remove(result.getTaskCreatorId());
		
		totalRuntime += result.getRunTime();
		
		//If Single value pass it on to target	
		if(result.hasValue()){
			
			if(origin.getTargetUid() == SOLUTION_UID){
				
				Result<R> terminalResult = new Result<R>(result.getValue());
				terminalResult.setCreatorID(SOLUTION_UID);
				terminalResult.setCriticalLength(result.getCriticalLengthOfParents()+result.getRunTime());
				terminalResult.setRunTime(totalRuntime);
				
				solution.add(terminalResult);
			}
			else {
				Task<R> target = registeredTasks.get(origin.getTargetUid());
				target.setInput(origin.getTargetPort(), result.getValue());
				target.addCriticalLengthOfParent(result.getCriticalLengthOfParents() + result.getRunTime());
			}
		}
	
		//Add newly created tasks to waitlist 
		if(result.hasTasks()){
			Task<R>[] tasksToAdd = result.getTasks();
			
			//First add all new tasks and generate UIDs for them
			for(Task<R> t: tasksToAdd){
				t.setUid(UID_POOL++);
				t.addCriticalLengthOfParent(result.getCriticalLengthOfParents() + result.getRunTime());
			}
			
			/*
			 * Tasks can reference other tasks in the set via a negative UID.
			 * For example to set the target to another element in the set
			 * -1 would set to the 0th element
			 * -2 would set to the 1st element
			 * etc..
			 */
			for(Task<R> t: tasksToAdd){
				
				long targetUid = t.getTargetUid();
				if(targetUid <0){
					Task<R> realTarget = tasksToAdd[ Math.abs((int)targetUid)-1];
					t.setTarget(realTarget.getUID(), t.getTargetPort());
				}
				
				registeredTasks.put(t.getUID(), t);
				this.schedule(t);
			}
		}
	}
	
	@Override
	public void start(Map<Integer, ProxyImp<R>> proxies, BlockingQueue<Result<R>> solution) {
		this.proxies = proxies;
		this.solution = solution;
		
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
