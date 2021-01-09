import java.util.Arrays;
import java.util.Iterator;

public class Task {

    public static int[] graphColoringTask(Graph graph, int start, int end, boolean[] available, int[] result) {
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
        return result;
    }

    public static int[] constructResult(Graph g, Object[] results, int size) {
        int[] finalResult = new int[g.getSize()];
        int step = g.getSize() / (size - 1);
        int start = 0;
        int end = step;
        for (int i = 0; i < size-1; i++) {
            for (int j = start; j < end; j++) {
                int[] smallResult = (int[]) results[i];
                finalResult[j] = smallResult[j];
            }
            start += step;
            if (i == size - 2) {
                end = g.getSize();
            } else
                end += step;
        }
        return finalResult;
    }
}
