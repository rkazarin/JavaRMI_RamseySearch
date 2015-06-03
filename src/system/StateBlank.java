package system;

import api.SharedState;

/**
 * Basic Shared State Implementation
 * 
 * Never updates. Stores no data.
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 */
public class StateBlank implements SharedState {

	private static final long serialVersionUID = -448945541763944065L;

	@Override
	public SharedState update( SharedState newState) {
		return this;
	}

	@Override
	public String toString() {
		return "State: [BLANK]";
	}
}
