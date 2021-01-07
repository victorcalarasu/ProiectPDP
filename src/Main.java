import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static void main(String args[]) throws InterruptedException {
        Graph g1 = new Graph(10000);
        //g1.printGraph();
        System.out.println("Sequential Coloring:");
        long startTime = System.nanoTime();
        int[] result1=g1.greedyColoring();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;
        if(!verifyColoring(g1,result1)){
            System.out.println("ERROR AT SEQUENTIAL");
        }
        else {
//            for (int u = 0; u < g1.getSize(); u++)
//                System.out.println("Vertex " + u + " --->  Color "
//                        + result1[u]);
            System.out.println(" Duration = " + duration + "ms");
        }


        long startTime2 = System.nanoTime();
        int[] result2=ParallelColoringTasks(g1,10);
        long endTime2 = System.nanoTime();
        long duration2 = (endTime2 - startTime2) / 1000000;
        System.out.println("Parallel Coloring:");
        if(!verifyColoring(g1,result2)){
            System.out.println("ERROR AT PARALLEL");
        }
//        else {
//            for (int u = 0; u < g1.getSize(); u++)
//                System.out.println("Vertex " + u + " --->  Color "
//                        + result2[u]);
//        }
        System.out.println(" Duration = " + duration2 + "ms");


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


    public static int[] ParallelColoringTasks(Graph graph, int numberOfThreads) throws InterruptedException {
        Lock lock = new ReentrantLock();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);
        int result[] = new int[graph.getSize()];

        Arrays.fill(result, -1);

        result[0] = 0;

        boolean available[] = new boolean[graph.getSize()];

        Arrays.fill(available, true);

        int step=graph.getSize() / numberOfThreads;
        int start = 0;
        int end=step;
        Task t1= new Task(graph,lock,1,end,available,result);
        executor.execute(t1);
        start+=step;
        end+=step;
        for (int i=1; i<numberOfThreads; i++){
            Task t= new Task(graph,lock, start,end,available,result);
            start+=step;
            end+=step;
            executor.execute(t);
        }
        executor.shutdown();
        executor.awaitTermination(50, TimeUnit.SECONDS);

        return result;
    }
}
