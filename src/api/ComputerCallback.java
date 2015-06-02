package api;

import api.SharedState;

/**
 * Callback passed by Computers to executing Tasks
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 * @param <R> Type of result
 */
public interface ComputerCallback<R> {

	/**
	 * Called by Tasks to update state on parent Computer
	 * @param state to update to
	 */
	void updateState(SharedState state);
	
	/**
	 * Called by Tasks to add a Partial Result to the Queue On the Computer
	 * Called if a task continues execution, while producing an intermidiate result
	 * @param result to report to Computer
	 */
	void producePartialResult(Result<R> result);
	
	/**
	 * Print message on parent Computer Terminal
	 * @param message to print
	 */
	void printMessage(String message);
}
