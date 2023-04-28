import java.util.*;

public class MultiLevelFeedbackQueueAlgorithm {

    static int[] arrivalTime;
    static int[] burstTime;
    static int[] priority;
    static int[] responseTime;
    static int[] waitingTime;
    static int[] turnaroundTime;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter number of queues: ");
        int noOfQueues = input.nextInt();
        int[][] queue = new int[noOfQueues][];
        int[] quantumTime = new int[noOfQueues];
        for (int i = 0; i < noOfQueues; i++) {
            System.out.print("Enter quantum time for queue " + (i+1) + ": ");
            quantumTime[i] = input.nextInt();
            System.out.print("Enter number of processes for queue " + (i+1) + ": ");
            int noOfProcesses = input.nextInt();
            queue[i] = new int[noOfProcesses];
            for (int j = 0; j < noOfProcesses; j++) {
                System.out.print("Enter arrival time for process " + (j+1) + " in queue " + (i+1) + ": ");
                int arrival = input.nextInt();
                System.out.print("Enter burst time for process " + (j+1) + " in queue " + (i+1) + ": ");
                int burst = input.nextInt();
                System.out.print("Enter priority for process " + (j+1) + " in queue " + (i+1) + ": ");
                int p = input.nextInt();
                queue[i][j] = p;
                if (arrivalTime == null) {
                    arrivalTime = new int[queue.length];
                }
                if (burstTime == null) {
                    burstTime = new int[queue.length];
                }
                if (priority == null) {
                    priority = new int[queue.length];
                }
                arrivalTime[j] = arrival;
                burstTime[j] = burst;
                priority[j] = p;
            }
        }

        System.out.print("Enter aging time: ");
        int agingTime = input.nextInt();
        if (responseTime == null) {
            responseTime = new int[queue.length];
        }
        if (waitingTime == null) {
            waitingTime = new int[queue.length];
        }
        if (turnaroundTime == null) {
            turnaroundTime = new int[queue.length];
        }

        simulate(noOfQueues, queue, quantumTime, agingTime);
        printReport(noOfQueues);
    }

    public static void simulate(int noOfQueues, int[][] queue, int[] quantumTime, int agingTime) {
        int currentTime = 0;
        Queue<Integer>[] queues = new LinkedList[noOfQueues];
        for (int i = 0; i < noOfQueues; i++) {
            queues[i] = new LinkedList<Integer>();
        }


        for (int p = 0; p < priority.length; p++) {
            if (arrivalTime[p] == 0) {
                queues[0].add(p);
            }
        }

        int[] remainingTime = Arrays.copyOf(burstTime, burstTime.length);
        boolean[] executed = new boolean[burstTime.length];

        while (true) {
            boolean done = true;

            for (int i = 0; i < noOfQueues; i++) {
                int q = quantumTime[i];
                while (!queues[i].isEmpty()) {
                    int process = queues[i].peek();
                    if (!executed[process]) {
                        responseTime[process] = currentTime - arrivalTime[process];
                        executed[process] = true;
                    }
                    if (remainingTime[process] > q) {
                        currentTime += q;
                        remainingTime[process] -= q;
                        done = false;
                    } else {
                        currentTime += remainingTime[process];
                        waitingTime[process] = currentTime - burstTime[process] - arrivalTime[process];
                        remainingTime[process] = 0;
                        turnaroundTime[process] = currentTime - arrivalTime[process];
                        queues[i].remove();
                    }
                }
            }

            if (done) break;

            for (int p = 0; p < priority.length; p++) {
                if (!executed[p] && arrivalTime[p]<= currentTime - agingTime) {
                    executed[p] = true;
                    queues[1].add(p);
                }
            }

            for (int p = 0; p < priority.length; p++) {
                if (!executed[p] && remainingTime[p] > 0) {
                    if (remainingTime[p] >= quantumTime[noOfQueues-1]) {
                        queues[noOfQueues-1].add(p);
                    } else {
                        for (int i = 0; i < noOfQueues-1; i++) {
                            if (remainingTime[p] >= quantumTime[i] && remainingTime[p] < quantumTime[i+1]) {
                                queues[i].add(p);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void printReport(int noOfQueues) {
        System.out.println("Sequence of execution:");

        int totalProcesses = priority.length;
        boolean[] executed = new boolean[totalProcesses];
        int currentTime = 0;
        while (true) {
            boolean done = true;
            for (int p = 0; p < totalProcesses; p++) {
                if (!executed[p] && arrivalTime[p] <= currentTime) {
                    System.out.print("P" + (p+1) + " ");
                    executed[p] = true;
                    currentTime += burstTime[p];
                    done = false;
                }
            }
            if (done) break;
        }
        System.out.println();

        System.out.printf("%-10s %-10s %-10s %-10s %-10s\n", "Process", "Arrival", "Burst", "Response", "Wait");
        double totalTurnaroundTime = 0;
        double totalWaitingTime = 0;
        for (int p = 0; p < totalProcesses; p++) {
            turnaroundTime[p] = currentTime - arrivalTime[p];
            totalTurnaroundTime += turnaroundTime[p];
            totalWaitingTime += waitingTime[p];
            System.out.printf("%-10s %-10s %-10s %-10s %-10s\n", "P"+(p+1), arrivalTime[p], burstTime[p], responseTime[p], waitingTime[p]);
        }
        System.out.println("Average turnaround time: " + (totalTurnaroundTime/totalProcesses));
        System.out.println("Average waiting time: " + (totalWaitingTime/totalProcesses));
    }
}