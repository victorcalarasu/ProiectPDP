import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;

public class Task implements Runnable {

    private Graph graph;
    private int start;
    private int end;
    private int[] result;
    private Lock lock;
    private boolean[] available;


    public Task(Graph g, Lock l, int s, int e, boolean[] a, int[] r) {
        this.graph = g;
        this.start = s;
        this.end = e;
        this.available = a;
        this.lock = l;
        this.result = r;
    }

    @Override
    public void run() {
        this.lock.lock();
        for (int u = start; u < end; u++) {
            // Process all adjacent vertices and flag their colors
            // as unavailable
            Iterator<Integer> it = graph.getAdj(u).iterator();
            while (it.hasNext()) {
                int i = it.next();
                if (result[i] != -1)
                    available[result[i]] = false;
            }
            // Find the first available color
            int cr;
            for (cr = 0; cr < graph.getSize(); cr++) {
                if (available[cr])
                    break;
            }
            result[u] = cr; // Assign the found color
            Arrays.fill(available, true);
        }
        this.lock.unlock();
    }
}
