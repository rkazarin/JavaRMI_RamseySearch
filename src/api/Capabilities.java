package api;

import java.io.Serializable;

public interface Capabilities extends Serializable {

	int getNumberOfThreads();
	int getBufferSize();
	boolean isLongRunning();
	boolean isOnSpace();
	
}
