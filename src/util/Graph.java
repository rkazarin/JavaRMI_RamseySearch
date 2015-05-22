package util;


public class Graph {

    boolean [] graph; //will only hold the lower left triangle of the graph
    int size;

    //create a new graph with random "colored" edges
    public Graph(int graphSize) {

        int numEdges = (graphSize * (graphSize - 1)) /2;
        graph = new boolean[numEdges];

        //initialize the graph to random colorings
        for(int i = 0; i < graph.length; i++){
            if(Math.random() > 0.5){
                graph[i] = true;
            } else {
                graph[i] = false;
            }
        }
    }

    //create a graph one bigger than the one being passed in (will be used when we find a counter example
    //for n, and want to start with this counterexample for n+1
    public Graph incrementGraph(Graph oldGraph){

        int newGraphSize = oldGraph.size + 1;
        Graph newGraph = new Graph(newGraphSize);

        for(int i = 0; i < oldGraph.size; i++){
            for(int j = 0; j < oldGraph.size; j++){
                if(i == j){ //we don't care about the diagonal
                    continue;
                }
                boolean oldColor = oldGraph.getColor(i, j);
                newGraph.changeColor(i, j, oldColor);
            }
        }

        return newGraph;

    }

    //Gets color given (i, j) coordinates
    public boolean getColor(int i, int j) {
        //because we are only caring about the lower left triangle
        if(j > i){
            int temp = i;
            i = j;
            j = temp;
        }

        //now convert from 2d to 1d
        int oneDCoord = ((i * (i - 1)) / 2) + j;
        return graph[oneDCoord];
    }

    public void changeColor(int i, int j, boolean newColor) {
        //because we are only caring about the lower left triangle
        if(j > i){
            int temp = i;
            i = j;
            j = temp;
        }

        //now convert from 2d to 1d
        int oneDCoord = ((i * (i - 1)) / 2) + j;
        graph[oneDCoord] = newColor;
    }

}
