package api;

/**
 * Class for exception that is thrown if the Proxy being accessed has already been stopped.
 * Happens when a Scheduler holds on to a pointer to a prxy that has been stopped already.
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 */
public class ProxyStoppedException extends Exception {

	private static final long serialVersionUID = 3246961259565736013L;

	/**
	 * Construct a new Exception
	 */
	public ProxyStoppedException() {}

	/**
	 * Construct a new Exception with an error message
	 * @param arg0 error message
	 */
	public ProxyStoppedException(String arg0) { super(arg0); }

}
