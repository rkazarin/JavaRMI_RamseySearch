package api;

public class ProxyStoppedException extends Exception {

	public ProxyStoppedException() {}

	public ProxyStoppedException(String arg0) {
		super(arg0);
	}

	public ProxyStoppedException(Throwable arg0) {
		super(arg0);
	}

	public ProxyStoppedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ProxyStoppedException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
