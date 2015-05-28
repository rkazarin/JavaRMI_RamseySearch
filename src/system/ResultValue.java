package system;

import api.Result;
import api.Task;

public class ResultValue<R> implements Result<R> {

	/** Serial ID */
	private static final long serialVersionUID = -6448590220525987278L;

	private double runTime;
	private final R value;

	private long creatorId;
	private double criticalLength;
	
	public ResultValue(R value){
		this.value = value;
	}
	
	
	@Override
	public Task<R>[] getTasks() {
		throw new UnsupportedOperationException("This result is a single value");
	}
	
	@Override
	public String toString() {
		return "Value: "+value.toString();
	}

	@Override
	public void setRunTime(double time) {runTime = time;}
	
	@Override
	public boolean isValue() 		{ return true; }

	@Override
	public R getValue() 			{ return value; }
	
	@Override
	public double getRunTime() 		{ return runTime;}

	@Override
	public long getTaskCreatorId()	{ return creatorId; }

	@Override
	public double getCriticalLengthOfParents() { return criticalLength; }
	
	@Override
	public void setCreatorID(long creatorId)	{ this.creatorId = creatorId;}
	
	@Override
	public void setCriticalLength(double criticalLength) {	this.criticalLength = criticalLength;}
}
