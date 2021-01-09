import java.util.*;
import java.util.LinkedList;
// This class represents an undirected graph using adjacency list 
class Graph {
    private int V;   // No. of vertices 
    private LinkedList<Integer> adj[]; //Adjacency List 

    //Constructor 
    Graph(int v) {
        int counter=0;
        V = v;
        adj = new LinkedList[v];
        for (int i = 0; i < v; ++i)
            adj[i] = new LinkedList();

        for (int i = 0 ;i < v-1; i++){
            this.addEdge(i,i+1);
            counter++;
        }

        Random random= new Random();

        for (int i = 0; i < v/2 ; i++){
            int node1 = random.nextInt(v - 1);
            int node2 = random.nextInt(v - 1);
            if (!containsEdge(node1,node2) && (node1 != node2)){
                this.addEdge(node1,node2);
                counter++;
            }
        }
        System.out.println("Number of edges: " + counter);
    }

    public int getSize(){
        return this.V;
    }

    public LinkedList<Integer> getAdj(int u) {
        return this.adj[u];
    }

    //Function to add an edge into the graph
    void addEdge(int v, int w) {
        adj[v].add(w);
        adj[w].add(v); //Graph is undirected 
    }

    boolean containsEdge(int v, int w) {
        if (adj[v].contains(w)){
            return true;
        }
        return false;
    }

    void printGraph(){
        for (int i = 0 ; i< this.V ;i++){
            Object[] adjlist = this.adj[i].toArray();
            System.out.println("Node " + i + " : "+ Arrays.toString(adjlist));
        }
    }

    // Assigns colors (starting from 0) to all vertices and 
    // prints the assignment of colors 
    int[] greedyColoring() {
        int result[] = new int[V];

        // Initialize all vertices as unassigned 
        Arrays.fill(result, -1);

        // Assign the first color to first vertex 
        result[0] = 0;

        // A temporary array to store the available colors. False 
        // value of available[cr] would mean that the color cr is 
        // assigned to one of its adjacent vertices 
        boolean available[] = new boolean[V];

        // Initially, all colors are available 
        Arrays.fill(available, true);

        // Assign colors to remaining V-1 vertices 
        for (int u = 1; u < V; u++) {
            // Process all adjacent vertices and flag their colors 
            // as unavailable 
            Iterator<Integer> it = adj[u].iterator();
            while (it.hasNext()) {
                int i = it.next();
                if (result[i] != -1)
                    available[result[i]] = false;
            }

            // Find the first available color 
            int cr;
            for (cr = 0; cr < V; cr++) {
                if (available[cr])
                    break;
            }

            result[u] = cr; // Assign the found color 

            // Reset the values back to true for the next iteration 
            Arrays.fill(available, true);
        }

        return result;
    }

}