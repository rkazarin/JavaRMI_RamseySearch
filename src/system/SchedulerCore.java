package system;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import api.Result;
import api.Scheduler;
import api.Task;

public abstract class SchedulerCore<R> implements Scheduler<R> {
	
	protected static final long SOLUTION_UID = 0;
	protected long UID_POOL = SOLUTION_UID+1;
	
	protected BlockingQueue<Result<R>> solution = new LinkedBlockingQueue<Result<R>>();
	
	protected Map<Long, Task<R>> registeredTasks = new ConcurrentHashMap<Long, Task<R>>();
	protected BlockingQueue<Task<R>> waitingTasks = new LinkedBlockingQueue<Task<R>>();

	protected Map<Integer, ProxyImp<R>> proxies = new ConcurrentHashMap<Integer, ProxyImp<R>>();
	
	private double totalRuntime = 0;
	
	@Override
	public void registerProxyPool(Map<Integer, ProxyImp<R>> proxies) {
		this.proxies = proxies;
	}
	
	@Override
	public Result<R> getSolution() throws InterruptedException {
		return solution.take();
	}
	
	@Override
	public void scheduleInitial(Task<R> task){
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
	
}