package api;

import java.io.Serializable;

public class Result<R> implements Serializable{

	private static final long serialVersionUID = 4342849579820024479L;

	private final Task<R>[] tasks;
	private final R value;
	private final Exception exception;
	
	private double runTime;	
	private long creatorId;
	private double criticalLength;
	
	public Result(R value) {
		this(value, null, null);
	}
	
	public Result(Task<R>[] tasks) {
		this(null, tasks, null);
	}
	
	public Result(Exception exception) {
		this(null,null,exception);
	}
	
	public Result(R value, Task<R>[] tasks, Exception exception) {
		this.value = value;
		this.tasks = tasks;
		this.exception = exception;
	}
	
	public boolean hasException()	{ return exception != null; }
	public boolean hasValue()		{ return value  != null; }
	public boolean hasTasks()		{ return tasks != null; }

	public Exception getException() { return exception;}
	public R getValue() { return value; }
	public Task<R>[] getTasks() { return tasks;}

	
	public double getRunTime()		{ return runTime; }
	public void setRunTime(double time) { runTime = time; }

	public long getTaskCreatorId()				{ return creatorId; }	
	public void setCreatorID(long creatorId)	{ this.creatorId = creatorId;}

	public void setCriticalLength(double criticalLength) {	this.criticalLength = criticalLength;}	
	public double getCriticalLengthOfParents() { return criticalLength; }

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
