package packages.userinteraction;
import java.util.*;
import org.json.simple.JSONObject;
import com.google.common.collect.Multimap;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
/**
 * SocialGraph
 */
public class SocialGraph {
    int vertices;
    int edges;
    Map<Integer, Integer> adjacencyList[];

    public SocialGraph(int v) {
        this.vertices = v;
        this.edges = 0;
        adjacencyList = new HashMap[v];
        for (int i = 0; i < v; i++) {
            this.adjacencyList[i] = new HashMap<Integer, Integer>();
        }
    }

    public void addEdge(int source, int destination) {
        edges++;
        System.out.println(edges + ". Adding edge : " + source + "---->" + destination + "\tWeight : ");
        int weight = adjacencyList[source].containsKey(destination) ? adjacencyList[source].get(destination) + 1
                : 1;
        adjacencyList[source].put(destination, weight);
        adjacencyList[destination].put(source, weight);
    }

    public Double getWeightedDistance(int a, int b) {
        Double WeightedDistance = Double.POSITIVE_INFINITY;
        // DIJKSTRA'S SHORTEST PATH O(ElogV)
        return WeightedDistance;
    }

    public void display() {

        System.out.print(" Vertex     \t Adjacency List\n");
        for (int i = 0; i < this.vertices; i++) {
            System.out.print(i + "\t" + adjacencyList[i]);
        }
    }
}
