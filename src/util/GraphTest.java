package util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Roman on 5/28/15.
 */
public class GraphTest {

    @Test
    public void testNewGraph(){
        Graph g = new Graph(4);

        //graph of size 4 should have 6 unique edges
        assertEquals(g.graph.length, 6);
    }

    @Test
    public void testPrintGraph(){
        Graph g = new Graph(5);

        g.printGraph();
    }

    @Test
    public void testIncreaseGraph(){
        Graph g = new Graph(4);

        System.out.println("Graph of size 4");
        g.printGraph();

        System.out.println("Same graph of size 5");
        Graph newG = g.incrementGraph();
        newG.printGraph();
    }



}
