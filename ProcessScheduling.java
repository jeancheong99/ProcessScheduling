import java.text.DecimalFormat;
import java.util.*;
import java.text.DecimalFormat;

public class ProcessScheduling {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        DecimalFormat df = new DecimalFormat("00");
        LinkedHashMap<String, Integer> arrivalTime = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> burstTime = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> priority = new LinkedHashMap<>();
        boolean run = true;
        int numProcess = 0;

        System.out.print("* * * * * * * * * * * * * * * * * * * * *\n");
        System.out.print(" Welcome to Process Scheduling Simulator\n");
        System.out.print("* * * * * * * * * * * * * * * * * * * * *\n\n");

        while(true){
            System.out.println("Enter the number of process(s) to schedule : ");
            numProcess = in.nextInt();
            
            if(numProcess >= 3 && numProcess <= 10){
                break;
            }
            System.out.println("NOTE: Enter at least 3 and up to 10 number of processes!\n");
        }
        
        for(int i = 0; i < numProcess; i++){
            System.out.print("Enter arrival time for Process P" + i + ": ");
            arrivalTime.put(("P" + i), in.nextInt());
            System.out.print("Enter burst time for Process P" + i + ": ");
            burstTime.put(("P" + i), in.nextInt());
            System.out.print("Enter priority for Process P" + i + ": ");
            priority.put(("P" + i), in.nextInt());
            
            System.out.print("\n");
        }
                            
        System.out.println("      |-----------|---------------|-------------|-----------|");
        System.out.println("      |  Process  |  Arrival Time |  Burst Time |  Priority |");
        System.out.println("      |-----------|---------------|-------------|-----------|");
        for(int i = 0; i < numProcess; i++){
        System.out.println("      |  P" + i + "       | " + 
                            df.format(arrivalTime.get("P"+i)) + "            | " + 
                            df.format(burstTime.get("P"+i)) + "          | " +
                            df.format(priority.get("P"+i)) + "        |");
        }
        System.out.println("      |-----------------------------------------------------|");
        
        System.out.println("----------------------------------------------------------------------------------------" +
                           "\n                                Simulation Results                                    " + 
                           "\n----------------------------------------------------------------------------------------");
        System.out.println("\nNon-preemtive SJF:");
        NonPreemptiveSJF a = new NonPreemptiveSJF(numProcess, burstTime);
        a.schedule(arrivalTime);
        System.out.print("\n****************************************************************************************\n");
        System.out.println("\nNon-Preemptive Priority:");
        NonPreemptivePriority b = new NonPreemptivePriority(numProcess, burstTime);
        b.schedule(arrivalTime, priority);
        System.out.print("\n****************************************************************************************\n");

        System.out.println("\nPreemptive SJF:");
        PreemptiveSJF c = new PreemptiveSJF(numProcess);
        c.schedule(arrivalTime, burstTime);

    }
}