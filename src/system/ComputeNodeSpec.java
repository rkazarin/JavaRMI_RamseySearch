package system;

import api.Capabilities;

public class ComputeNodeSpec implements Capabilities{

	private static final long serialVersionUID = -4800920412924232081L;
	private static final int BUFFER_DEFAULT_SIZE = 5;
	
	private final int numThreads,prefetchBufferSize;
	private final boolean isLongRunning, isOnSpace;
	
	public ComputeNodeSpec(int desiredNumThreads, int desiredPrefetchBufferSize, boolean isOnSpace, boolean isLongRunning) {
		this.numThreads = desiredNumThreads>0?desiredNumThreads:Runtime.getRuntime().availableProcessors();
		this.prefetchBufferSize = desiredPrefetchBufferSize>0?desiredPrefetchBufferSize:BUFFER_DEFAULT_SIZE;
		this.isOnSpace = isOnSpace;
		this.isLongRunning = isLongRunning;
	}

	@Override
	public int getNumberOfThreads()	{ return numThreads; }

	@Override
	public int getBufferSize()		{ return prefetchBufferSize; }

	@Override
	public boolean isOnSpace()		{ return isOnSpace;}

	@Override
	public boolean isLongRunning()	{ return isLongRunning;}

}
