package optimization;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Oleksii on 26.05.2017.
 */
public class TestAlgo {

    private List<Vertex> nodes;
    private List<Edge> edges;

    public static void main(String[] args) {


        TestAlgo al=new TestAlgo();
        al.testExcute();
    }
    public void testExcute() {
        nodes = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        for (int i = 0; i < 11; i++) {
            Vertex location = new Vertex("Node_" + i, "Node_" + i);
            nodes.add(location);
        }


        addLane("Edge_0", 0, 1, 1);
        addLane("Edge_1", 1, 2, 1);
        addLane("Edge_2", 1, 3, 1);
        addLane("Edge_3", 2, 5, 1);
        addLane("Edge_4", 5, 0, 1);
        addLane("Edge_5", 5, 6, 1);
        addLane("Edge_6", 5, 8, 250);
        addLane("Edge_7", 8, 9, 84);
        addLane("Edge_8", 7, 9, 167);
        addLane("Edge_9", 4, 9, 502);
        addLane("Edge_10", 9, 10, 40);
        addLane("Edge_11", 1, 10, 600);



        // Lets check from location Loc_1 to Loc_10
        Graph graph = new Graph(nodes, edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(nodes.get(0));
        LinkedList<Vertex> path = dijkstra.getPath(nodes.get(5));


        if (path!=null) {
            for (Vertex vertex : path) {
                System.out.println(vertex);
            }

        }
        else{
            System.out.println("No path");
        }
    }

    private void addLane(String laneId, int sourceLocNo, int destLocNo,
                         int duration) {
        Edge lane = new Edge(laneId,nodes.get(sourceLocNo), nodes.get(destLocNo), duration );
        edges.add(lane);
    }
}