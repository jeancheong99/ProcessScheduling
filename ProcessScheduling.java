import java.util.*;
import java.util.Map.*;
import java.util.HashMap.*;
import java.util.LinkedList.*;

public class ProcessScheduling {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        LinkedHashMap<String, Integer> arrivalTime = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> burstTime = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> priority = new LinkedHashMap<>();
        boolean run = true;
        int counter = 0;

        System.out.print("* * * * * * * * * * * * * * * * * * * * *\n");
        System.out.print(" Welcome to Process Scheduling Simulator\n");
        System.out.print("* * * * * * * * * * * * * * * * * * * * *\n\n");

        while(run == true){
            System.out.print("Enter arrival time for Process " + counter + ": ");
            arrivalTime.put(("P"+counter), in.nextInt());
            System.out.print("Enter burst time for Process " + counter + ": ");
            burstTime.put(("P"+counter), in.nextInt());
            System.out.print("Enter priority for Process " + counter + ": ");
            priority.put(("P"+counter), in.nextInt());
            
            if(counter >= 2){
                System.out.print("Add new process? (Y/N): ");
                if (Character.toUpperCase(in.next().charAt(0)) == 'N')
                    run = false;
            }

            System.out.print("\n");
            counter++;
        }
                              
        System.out.println("|-----------|--------------|------------|----------|");
        System.out.println("|  Process  | Arrival Time | Burst Time | Priority |");
        System.out.println("|-----------|--------------|------------|----------|");
        for(int i = 0; i < counter; i++){
        System.out.println("|  P" + i + "       | " + arrivalTime.get("P"+i) + "            | " + burstTime.get("P"+i) + "          | " +
                            priority.get("P"+i) + "        |");
        }
        System.out.println("|---------------------------------------------------\n\n");
        
        System.out.println("Gantt Chart\n" + 
                           "-----------");
        System.out.println("Non-preemtive SJF:");
        NonPreemptiveSJF a1 = new NonPreemptiveSJF();
        a1.schedule(arrivalTime, burstTime, counter);
    }
}