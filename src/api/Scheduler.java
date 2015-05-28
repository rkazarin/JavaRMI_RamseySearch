package api;

import java.util.Map;

import system.ProxyImp;

public interface Scheduler<R>{

	public abstract void schedule(Task<R> task);

	public abstract void registerProxyPool( Map<Integer, ProxyImp<R>> proxies );
	
	//Should not be blocking!!!
	public abstract void start();
	
	public abstract void stop();

}