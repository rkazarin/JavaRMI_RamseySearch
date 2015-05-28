package api;


public interface Proxy<R> {

	public abstract void updateState(SharedState updatedState, boolean force);

	public abstract int getId();

	public abstract Capabilities getCapabilities();
	
	public abstract void assignTask(Task<R> task) throws ProxyStoppedException;
	
	public abstract boolean isBufferFull();

}