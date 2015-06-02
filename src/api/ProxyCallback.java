package api;

import java.util.Collection;

import api.Task;

/**
 * Callback to be passed to Proxy when it is created that
 * Allows Proxies to comunicate with Space and Scheduler
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 * @param <R> Type of result the computers are expected to produce
 */
public interface ProxyCallback<R> {

	/**
	 * Called by proxy when it recieves a result
	 * @param result recieved
	 */
	void processResult(Result<R> result);
	
	/**
	 * Operation to preform when proxy no longer able to communicate with associated Computer
	 * @param proxyId of failing proxy
	 * @param leftoverTasks to requeue
	 */
	void doOnError(int proxyId, Collection<Task<R>> leftoverTasks);
}
