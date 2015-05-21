package ramsey;

import util.Log;
import api.Result;
import api.SharedState;
import api.Space;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RamseyClient {

	/** Serial ID	 */
	private static final long serialVersionUID = 6911008092238762097L;
	
	public RamseyClient() {

	}

    @SuppressWarnings("unchecked")
	public static void main(String[] args) throws RemoteException, InterruptedException, MalformedURLException, NotBoundException {
		String domain = (args.length > 0)? args[0] : "localhost";
		int graphStartSize = (args.length > 1)? Integer.parseInt(args[1]) : 10;
		
		Log.startLog("ramsey-client.csv");
		System.out.println("Starting Client Targeting Space @ "+domain);

		String url = "rmi://" + domain + ":" + Space.DEFAULT_PORT + "/" + Space.DEFAULT_NAME;
		
		Space<RamseyChunk> space = (Space<RamseyChunk>) Naming.lookup(url);

		//RamseyClient client = new RamseyClient(graphStartSize);
		
		System.out.println("Start graph size:\t"+graphStartSize);

		Log.log("Component, Time (ms)");
    
		long clientStartTime = System.nanoTime();
		
		//SharedState initial = branchAndBound? new StateTsp(cities): new StateTspStatic(cities);
		space.setTask( new RamseyTask(graphStartSize));

        //Will probably never get here...
		Result<RamseyChunk> result =  space.getSolution();

		
		Log.log("TSP, Result: "+result.getValue());
		Log.log( "Client Time,"+( System.nanoTime() - clientStartTime) / 1000000.0 );
		Log.log("T1, "+result.getRunTime());
		Log.log("Tinf, "+result.getCriticalLengthOfParents());
		Log.close();
	}
}
