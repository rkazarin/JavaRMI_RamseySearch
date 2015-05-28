package ramsey;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import ramsey.Graph;
import ramsey.Task;
import ramsey.mine.Solution;

public class Miner implements Runnable{

	protected transient Graph current;
	protected transient Queue<Solution> solutionQueue = new ConcurrentLinkedQueue<>();
	protected transient final int minUseful, maxUseful;
	
	private final UUID id = UUID.randomUUID();
	
	protected UUID taskId;
	protected String error;
	protected boolean running = false;
	protected boolean failedToFindSolution = false;
	
	public Miner(int minUseful, int maxUseful){
		this.maxUseful = maxUseful;
		this.minUseful = minUseful;
	}
	
	private void reset(){
		failedToFindSolution = false;
		running = false;
		current = null;
		taskId = null;
		error = null;
	}
	
	public void assign(Task task){
		if(task != null && !this.id.equals(task.getTargetMiner())) return;
		
		reset();
		
		//if no task we are done!
		if(task == null || task.getSeed() == null){
			return;
		}
		
		//Wait until stopped
		while(running) try {Thread.sleep(10);} catch (InterruptedException e) {}
		
		//Setup new thread;
		this.current = task.getSeed();
		this.taskId = task.getTaskId();
		
		//Start it up!
		(new Thread(this) ).start();
	}
	
	public Solution poll(){
		return solutionQueue.poll();
	}
	
	public UUID getId(){
		return id;
	}
	
	protected void sendSolution(Graph solvedGraph){
		Graph normalized = solvedGraph.normalize();
		
		if(normalized.size()> minUseful)
			solutionQueue.add(new Solution(taskId,normalized));
	}
	
	@Override
	public void run() {
		running = true;
		
		try {
			process();
		} catch (Exception e) {
			error = e.getMessage();
		}
		running = false;
	}
		
	public String getError() 	{ return error; }
	public boolean isRunning()	{ return running; }
	public UUID getTask() 		{ return taskId; }
	public boolean hasTask() 	{ return taskId != null; }
	public boolean failedToFindSolution(){ return failedToFindSolution; }

	public void process() throws Exception{}

}
