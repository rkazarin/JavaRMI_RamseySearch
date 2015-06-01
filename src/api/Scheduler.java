package api;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public interface Scheduler<R> extends Serializable{

	void schedule(Task<R> task);

	void scheduleInitial(Task<R> task);

	void processResult(Result<R> result);
	
	//Should not be blocking!!!
	void start(SharedState initialState, Map<Integer, Proxy<R>> proxies, BlockingQueue<Result<R>> solutions, BlockingQueue<Exception> exceptions);
	
	void stop();
	
	String statusString();
	
	void updateState(SharedState state);

}