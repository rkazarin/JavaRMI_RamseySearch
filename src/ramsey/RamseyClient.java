package ramsey;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import util.Log;
import api.Space;

public class RamseyClient {
	
	public RamseyClient() {

	}

    @SuppressWarnings("unchecked")
	public static void main(String[] args) throws RemoteException, InterruptedException, MalformedURLException, NotBoundException {
		String domain = (args.length > 0)? args[0] : "localhost";
		int graphStartSize = (args.length > 1)? Integer.parseInt(args[1]) : 10;
		
		Log.startLog("ramsey-client.csv");
		System.out.println("Starting Client Targeting Space @ "+domain);

		String url = "rmi://" + domain + ":" + Space.DEFAULT_PORT + "/" + Space.DEFAULT_NAME;
		
		Space<Graph> space = (Space<Graph>) Naming.lookup(url);

		//RamseyClient client = new RamseyClient(graphStartSize);
		
		System.out.println("Start graph size:\t"+graphStartSize);

		Log.log("Component, Time (ms)");
    
		space.setTask( null , new SharedTabooList(), new RamseyScheduler() );

		while(true){
			System.out.println(space.getSolution());
		}
		
	}
}
