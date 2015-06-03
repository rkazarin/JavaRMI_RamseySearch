package ramsey;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import util.Log;
import system.StateBlank;
import api.Space;

/**
 * Runs a Client that connects to a RamseyStore and Space and begins an R(5,5) search.
 * Print out solutions as they are encountered.
 * Can be disconected at any point after seting up the run.
 * 
 * @author Michael Nekrasov
 * @author Roman Kazarin
 *
 */
public class RamseyClient {
	
    @SuppressWarnings("unchecked")
	public static void main(String[] args) throws RemoteException, InterruptedException, MalformedURLException, NotBoundException {
		String spaceIp = (args.length > 0)? args[0] : "localhost";
		String storeIp = (args.length > 0)? args[1] : "localhost";
		
		String spaceUrl = "rmi://" + spaceIp + ":" + Space.DEFAULT_PORT + "/" + Space.DEFAULT_NAME;
		String storeUrl = "rmi://" + storeIp + ":" + GraphStore.DEFAULT_PORT + "/" + GraphStore.DEFAULT_NAME;
		
		Log.startLog("ramsey-client.csv");
		System.out.println("Starting Client");
		System.out.println("\tTargeting Space: "+spaceUrl);
		System.out.println("\tTargeting Store: "+storeUrl);
		
		Space<Graph> space = (Space<Graph>) Naming.lookup(spaceUrl);
		space.setJob(null , new StateBlank(), new RamseyScheduler(storeUrl) );

		System.out.println("\nSolutions:\n");
		
		while(true){
			System.out.println(space.getSolution());
		}	
	}
}
