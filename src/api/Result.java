package api;

import java.io.Serializable;

import api.Task;

/**
 * Encompasses results that are passed around between client space and computer.
 * 
 * Within this Result there can be:
 * 	- Zero or One Value
 *  - Zero or One Associated Exception
 *  - Zero, One, or More New Tasks
 *  
 *  In addition has fields for storing performance metrics of execution time.
 *  Each Task can produce one or more results.
 *  
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 * @param <R> Type of Result encapsulated within
 */
public class Result<R> implements Serializable{

	private static final long serialVersionUID = 4342849579820024479L;

	private final Task<R>[] tasks;
	private final R value;
	private final Exception exception;
	
	private double runTime;	
	private long creatorId;
	private double criticalLength;
	private boolean taskCompleted = false;
	
	/**
	 * Construct a new Result with a Single Value
	 * @param value associated with result
	 */
	public Result(R value) {
		this(value, null, null);
	}
	
	/**
	 * Construct a new Result with One or More new Tasks 
	 * @param tasks to enqueue
	 */
	public Result(Task<R>[] tasks) {
		this(null, tasks, null);
	}
	
	/**
	 * Construct a new Result with a single Exception
	 * @param exception associated with result
	 */
	public Result(Exception exception) {
		this(null,null,exception);
	}
	
	/**
	 * Construct a new Result with any combination of components
	 * @param value associated with result 
	 * @param tasks to enqueue
	 * @param exception associated with resilt
	 */
	public Result(R value, Task<R>[] tasks, Exception exception) {
		this.value = value;
		this.tasks = tasks;
		this.exception = exception;
	}
	
	/**
	 * Does the result contain an exception
	 * @return if contains exception
	 */
	public boolean hasException() { return exception != null; }
	
	/**
	 * Does the result contain a value
	 * @return if contains value
	 */
	public boolean hasValue() { return value  != null; }
	
	/**
	 * Does the result have one or more tasks
	 * @return if contains at least one task
	 */
	public boolean hasTasks() { return tasks != null; }

	/**
	 * Has the task that created the result finished execution
	 * @return true if it has
	 */
	public boolean isTaskCompleted() { return taskCompleted; }
	
	/**
	 * Mark the task that created this result as having been completed
	 */
	public void setTaskCompleted() { taskCompleted = true; }
	
	/**
	 * Get Exception component of result
	 * @return the exception
	 */
	public Exception getException() { return exception;}
	
	/**
	 * Get the value component of result
	 * @return the value
	 */
	public R getValue() { return value; }
	
	/**
	 * Get the tasks component of the result
	 * @return the tasks
	 */
	public Task<R>[] getTasks() { return tasks;}

	/**
	 * Get run time of the task that produced this result
	 * (Present only if implemented by Space)
	 * @return the runtime
	 */
	public double getRunTime() { return runTime; }

	/**
	 * Sets the runtime of associated task
	 * @param time to task to produce this result
	 */
	public void setRunTime(double time) { runTime = time; }

	/**
	 * Get the UID of the task that created this Result
	 * @return the UID
	 */
	public long getTaskCreatorId() { return creatorId; }	
	
	/**
	 * Sets the UID of the task that created this Result
	 * (Present only if implemented by Space)
	 * @param creatorId of creator task
	 */
	public void setCreatorID(long creatorId) { this.creatorId = creatorId;}

	/**
	 * Gets the runtime of the worst execution path that led to this result 
	 * (Present only if implemented by Space)
	 * @return the total runtime
	 */
	public double getCriticalLengthOfParents() { return criticalLength; }

	/**
	 * Sets the runtime of worst execution path that led to this result
	 * @param criticalLength of worst execution path
	 */
	public void setCriticalLength(double criticalLength) {	this.criticalLength = criticalLength;}	
	
	public String toString() {
		String out = "";

		if(hasValue()){
			out+="Value: "+value.toString();
		}
		
		if(hasTasks()){
			out+= "New Tasks:";
			for(Task<R> task: tasks)
				out += "| " +task+" ";
		}
		
		if(hasException()){
			out+= "Exception: "+exception.toString();
		}
		
		return out;
	}
}
