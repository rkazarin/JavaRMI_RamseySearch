package api;

import java.util.Map;

import system.ProxyImp;

public interface Scheduler<R>{

	void schedule(Task<R> task);

	void scheduleInitial(Task<R> task);

	Result<R> getSolution() throws InterruptedException;
	
	void processResult(Result<R> result);
	
	void registerProxyPool( Map<Integer, ProxyImp<R>> proxies );
	
	//Should not be blocking!!!
	void start();
	
	void stop();

}