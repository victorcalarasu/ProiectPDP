import mpi.*;

import java.util.Arrays;

public class Main {
    public static void main(String args[]) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        if (rank == 0) {
            Graph g = new Graph(100000);
            coloringMaster(g, size);
        } else {
            coloringSlave(rank, size);
        }

        MPI.Finalize();

    }


    public static void coloringMaster(Graph g, int size) {
        long startTime = System.currentTimeMillis();
        boolean[] available = new boolean[g.getSize()];
        Arrays.fill(available, true);
        int[] result = new int[g.getSize()];
        Arrays.fill(result, -1);
        result[0] = 0;
        int step = g.getSize() / (size - 1);
        int start = 0;
        int end =0;

        MPI.COMM_WORLD.Send(new Object[]{result}, 0, 1, MPI.OBJECT, 1, 0);

        for (int i = 1; i < size; i++) {
            start =end;
            end += step;
            if (i == 1){
                start =1;
                end =step;
            }
            if (i == size - 1) {
                end = g.getSize();
            }
            MPI.COMM_WORLD.Send(new Object[]{g}, 0, 1, MPI.OBJECT, i, 0);
            MPI.COMM_WORLD.Send(new Object[]{available}, 0, 1, MPI.OBJECT, i, 0);
            MPI.COMM_WORLD.Send(new int[]{start}, 0, 1, MPI.INT, i, 0);
            MPI.COMM_WORLD.Send(new int[]{end}, 0, 1, MPI.INT, i, 0);
        }

        Object[] results = new Object[size - 1];
        MPI.COMM_WORLD.Recv(results, 0, 1, MPI.OBJECT, size-1, 0);
        long endTime = System.currentTimeMillis();
        int[] finalResult = (int[]) results[0];
        System.out.println("Duration: " + (endTime-startTime) + " ms");
        if(!verifyColoring(g,finalResult)){
            System.out.println("ERROR AT COLORING");
        }
        else {
        System.out.println("MPI");
            for (int u = 0; u < g.getSize(); u++)
                System.out.println("Vertex " + u + " --->  Color "
                        + finalResult[u]);
        }
        System.out.println("Normal:");
        int[] result2= g.greedyColoring();
        for (int u = 0; u < g.getSize(); u++)
            System.out.println("Vertex " + u + " --->  Color "
                    + result2[u]);


    }

    public static void coloringSlave(int rank, int size) {

        System.out.println("Worker " + rank + " started!");


        Object[] g = new Object[2];
        Object[] available = new Object[2];
        int[] start = new int[1];
        int[] end = new int[1];
        Object[] r = new Object[2];
        if (rank == 1) {
            MPI.COMM_WORLD.Recv(r, 0, 1, MPI.OBJECT, 0, 0);
        }
        else{
            MPI.COMM_WORLD.Recv(r, 0, 1, MPI.OBJECT, rank-1, 0);
        }
        int[] result = (int[]) r[0];
        MPI.COMM_WORLD.Recv(g, 0, 1, MPI.OBJECT, 0, 0);
        MPI.COMM_WORLD.Recv(available, 0, 1, MPI.OBJECT, 0, 0);
        MPI.COMM_WORLD.Recv(start, 0, 1, MPI.INT, 0, 0);
        MPI.COMM_WORLD.Recv(end, 0, 1, MPI.INT, 0, 0);

       if (rank == size-1){
            int[] newResult = Task.graphColoringTask((Graph) g[0], start[0], end[0], (boolean[]) available[0], result);
            MPI.COMM_WORLD.Send(new Object[]{newResult}, 0, 1, MPI.OBJECT, 0, 0);
       }
       else{
            int[] newResult = Task.graphColoringTask((Graph) g[0], start[0], end[0], (boolean[]) available[0], result);
            MPI.COMM_WORLD.Send(new Object[]{newResult}, 0, 1, MPI.OBJECT, rank +1, 0);
        }
    }

    public static boolean verifyColoring(Graph graph, int[] result){
        for (int i = 0 ; i< graph.getSize(); i++){
            for (int j=0; j<graph.getAdj(i).size(); j++){
                if (result[i]==result[graph.getAdj(i).get(j)])
                    return false;
            }
        }
        return true;
    }
}
