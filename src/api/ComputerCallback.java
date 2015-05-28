package api;

public interface ComputerCallback<R> {

	void updateState(SharedState state);
	
	void producePartialResult(Result<R> result);
}
