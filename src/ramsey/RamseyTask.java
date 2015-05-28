package ramsey;

import system.TaskClosure;
import api.ComputerCallback;
import api.Result;
import api.SharedState;

public class RamseyTask extends TaskClosure<Graph> {

	private static final long serialVersionUID = 6673708275266440578L;

	private SharedTabooList taboo;
	private Graph current;
	private int graphComputationLimit;
	
	public RamseyTask(Graph graph, int graphComputationLimit) {
		super("Ramsey", DEFAULT_PRIORITY, NO_INPUTS, LONG_RUNNING);
		this.graphComputationLimit = graphComputationLimit;
		this.current = graph;
	}

	@Override
	public void updateState(SharedState updatedState) {
		taboo = (SharedTabooList) updatedState;
	}

	@Override
	protected Result<Graph> execute(SharedState currentState, ComputerCallback<Graph> callback) throws Exception {
		taboo = (SharedTabooList) currentState;
		
		boolean isSolved = false;
		do{
			isSolved = findCounterExample(current, callback);
			 
			if(!isSolved){ 
				throw new Exception("No solution found for size "+current.size());
			}
			
			//Send Solution
			callback.producePartialResult( new Result<Graph>(current) );
			
			//Extend and keep solving
			current = current.extendRandom();
		}
		while(current.size() <= graphComputationLimit);

		return null;
	}
	
	private boolean findCounterExample(Graph g, ComputerCallback<Graph> callback)  throws Exception{
		
		while(true){
			//find out how we are doing
			int count = g.cliqueCount();
	
			//if we have a counter example
			if(count == 0) {
				//YAY FOUND IT!
				return true;
			}
	
			/*
			 * otherwise, we need to consider flipping an edge
			 *
			 * let's speculative flip each edge, record the new count,
			 * and unflip the edge.  We'll then remember the best flip and
			 * keep it next time around
			 *
			 * only need to work with upper triangle of matrix =>
			 * notice the indices
			 */
			
			int best_count = Integer.MAX_VALUE;
			int best_i=-1;
			int best_j=-1;
			for(int i=0; i < g.size(); i++)
			{
				for(int j=i+1; j < g.size(); j++)
				{
					// flip it
					g.flip(i,j);
					count = g.cliqueCount();
	
					if(count == 0){
						//YAY FOUND IT!
						return true;
					}
					
					// is it better and the i,j,count not taboo?
					if( count < best_count && !taboo.contains(i, j))
					{
						best_count = count;
						best_i = i;
						best_j = j;	
					}
	
					//flip it back
					g.flip(i,j);
				}
			}
	
			if(best_count == Integer.MAX_VALUE) {
				System.out.println("!! No best edge to remove");
				return false;
			}
			
			// keep the best flip we saw
			g.flip(best_i, best_j);
	
			/*
			 * taboo this graph configuration so that we don't visit
			 * it again
			 */
			count = g.cliqueCount();
			taboo.add(best_i,best_j);
			callback.updateState(taboo);
	
			/*
			System.out.println("size: "+g.size()+"\t"
					+ "best_count: "+best_count+"\t"
					+ "best edge: ("+best_i+","+best_j+")\t"
					+ "color: "+ (g.get(best_i, best_j)?1:0)
					);
			*/
			// rinse and repeat
		}
		
	}

}
