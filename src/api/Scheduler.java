package api;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import system.ProxyImp;

public interface Scheduler<R> extends Serializable{

	void schedule(Task<R> task);

	void scheduleInitial(Task<R> task);

	void processResult(Result<R> result);
	
	//Should not be blocking!!!
	void start(Map<Integer, ProxyImp<R>> proxies, BlockingQueue<Result<R>> solution);
	
	void stop();

}