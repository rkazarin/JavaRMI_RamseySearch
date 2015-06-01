package api;

public interface Proxy<R> {

	void updateState(SharedState updatedState, boolean force);

	int getId();

	Capabilities getCapabilities();
	
	void assignTask(Task<R> task) throws ProxyStoppedException;
	
	boolean isBufferFull();
	
	int getNumQueued();

}