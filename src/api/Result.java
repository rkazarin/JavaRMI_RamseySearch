package api;

import java.io.Serializable;

public interface Result<R> extends Serializable {
	
	boolean isValue();
	
	R getValue();
	
	Task<R>[] getTasks();
	
	double getRunTime();
	
	void setRunTime(double d);
	
	void setCreatorID(long creatorId);
	
	void setCriticalLength(double criticalLength);
	
	long getTaskCreatorId();
	
	double getCriticalLengthOfParents();
}

