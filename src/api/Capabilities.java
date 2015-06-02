package api;

import java.io.Serializable;

/**
 * Defines a set of capabilities for a computer
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 */
public interface Capabilities extends Serializable {

	/**
	 * Number of threads running on this computer
	 * @return number of threads
	 */
	int getNumberOfThreads();
	
	/**
	 * Size of Pre-Fetch Buffer
	 * @return size of buffer
	 */
	int getBufferSize();
	
	/**
	 * Is the computer predicted to have long longevity
	 * @return true if long runnin, fals if short
	 */
	boolean isLongRunning();
	
	/**
	 * Is this computer running on a Space
	 * @return true if running on space
	 */
	boolean isOnSpace();
	
}
