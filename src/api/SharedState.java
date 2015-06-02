package api;

import java.io.Serializable;

/**
 * Defines a state that is propagated to Space, Computers, and Tasks
 *   
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 */
public interface SharedState extends Serializable {

	/**
	 * Called when two states need to be compared.
	 * 
	 * The two states can be fused or one or other can be chosen.
	 * The desired output state is passed back from the update method.
	 * 
	 * If the object pointer differs then the object is propogated throughout the system.
	 * So two objects are fused, make sure to create a new Object, for the state to be registered as having changed
	 * 
	 * @param newState to compare to
	 * @return the final state (original, newState, or new mixture of the two)
	 */
	SharedState update(SharedState newState);
	
}
