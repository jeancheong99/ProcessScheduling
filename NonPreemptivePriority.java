import java.util.*;
import java.text.DecimalFormat;

class NonPreemptivePriority{
    private DecimalFormat df = new DecimalFormat("00");

    private LinkedHashMap<String, Integer> sortedArrival;
    private LinkedHashMap<String, Integer> burstTime;
    private LinkedHashMap<String, Integer> burstTimeClone;
    private LinkedHashMap<String, Integer> turnaroundTime = new LinkedHashMap<>();
    private LinkedHashMap<String, Integer> waitingTime = new LinkedHashMap<>();
    private LinkedHashMap<String, Integer> finishTime = new LinkedHashMap<>();
    
    private Set<String> arrivalKey;
    private Set<String> ftKey;

    private List<String> arrivalKeyList;
    private List<Integer> arrivalValueList;
    private List<String> ftKeyList;
    private List<String> readyQueue = new ArrayList<>();
    private List<String> executionList = new ArrayList<>();
    private List<Integer> timestamp = new ArrayList<>();
    
    
    private int numProcess;
    private int currentTime = 0;
    private int processTime = 0;
    
    private String currentProcess;

    public NonPreemptivePriority(int numProcess, LinkedHashMap<String, Integer> burstTime){
        this.numProcess = numProcess;
        cloneBurstTimeMap(burstTime);
    }

    public void schedule(LinkedHashMap<String, Integer> arrivalTime, LinkedHashMap<String, Integer> priority){
        sortArrivalTime(arrivalTime);
        cloneBurstTimeMap(burstTime);
        calculateTotalProcessTime();

        while(currentTime < processTime) {
            // if still have process haven't entered
            if((!arrivalKeyList.isEmpty()) && currentTime == arrivalValueList.get(0)) {
                // first process go in
                if(currentProcess == null){
                    // check process with same arrival time
                    if(Collections.frequency(arrivalValueList, arrivalValueList.get(0)) == 1) {
                        currentProcess = arrivalKeyList.get(0);
                        executionList.add(currentProcess);
                        timestamp.add(currentTime);
                        arrivalKeyList.remove(0);
                        arrivalValueList.remove(0);
                    } 
                    else{
                        int value = arrivalValueList.get(0);
                        List<Integer> temp = new ArrayList<>(arrivalValueList);
                        for(int i = 0; i < Collections.frequency(temp, value); i++) {
                            readyQueue.add(arrivalKeyList.get(0));
                            arrivalKeyList.remove(0);
                            arrivalValueList.remove(0);
                        }
                        currentProcess = getJob(priority);
                        executionList.add(currentProcess);
                        timestamp.add(currentTime);
                        readyQueue.remove(currentProcess);
                    }
                    burstTime.put(currentProcess, burstTime.get(currentProcess) - 1);
                    currentTime++;
                    continue;
                }

                // check process with same arrival time                                    
                if(Collections.frequency(arrivalValueList, arrivalValueList.get(0)) == 1){
                    readyQueue.add(arrivalKeyList.get(0));
                    arrivalKeyList.remove(0);
                    arrivalValueList.remove(0);
                }
                else{
                    int value = arrivalValueList.get(0);
                    List<Integer> temp = new ArrayList<>(arrivalValueList);
                    for(int i = 0; i < Collections.frequency(temp, value); i++) {
                        readyQueue.add(arrivalKeyList.get(0));
                        arrivalKeyList.remove(0);
                        arrivalValueList.remove(0);
                    }    
                }
    
                //check if current process finish execution
                if (burstTime.get(currentProcess) == 0){
                    finishTime.put(currentProcess, currentTime);
                    timestamp.add(currentTime);
                    burstTime.remove(currentProcess);
                    currentProcess = getJob(priority);
                    executionList.add(currentProcess);
                    readyQueue.remove(currentProcess);
                    burstTime.put(currentProcess, burstTime.get(currentProcess) - 1);
                }
                else{
                    burstTime.put(currentProcess, burstTime.get(currentProcess) - 1);
                }
                
            }
            else{
                if(currentProcess != null){
                    if(burstTime.get(currentProcess) == 0){
                        finishTime.put(currentProcess, currentTime);
                        timestamp.add(currentTime);
                        burstTime.remove(currentProcess);
                        currentProcess = getJob(priority);
                        executionList.add(currentProcess);
                        readyQueue.remove(currentProcess);
                    }
                    burstTime.put(currentProcess, burstTime.get(currentProcess) - 1);
                }
            }
            currentTime++; 
        }

        timestamp.add(processTime);
        finishTime.put(currentProcess, processTime);

        ganttChart(executionList.size());
        turnaroundTimeCalc(arrivalTime);
        waitingTimeCalc();
        printCalcTable(arrivalTime, burstTime);    
    }
    

    public void cloneBurstTimeMap(LinkedHashMap<String, Integer> burstTime){
        this.burstTime = new LinkedHashMap<>(burstTime);
        burstTimeClone =  new LinkedHashMap<>(burstTime);
    }

    public void sortArrivalTime(LinkedHashMap<String, Integer> arrivalTime){
        sortedArrival = sortByValue(arrivalTime);
        arrivalValueList = new ArrayList<>(sortedArrival.values());
        arrivalKey = sortedArrival.keySet();
        arrivalKeyList = new ArrayList<>(arrivalKey);
    }

    public LinkedHashMap<String, Integer> sortByValue(LinkedHashMap<String, Integer> temp){
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(temp.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> lhs, Map.Entry<String, Integer> rhs){
                return lhs.getValue().compareTo(rhs.getValue());
            }
        });

        temp.clear();
        for(Map.Entry<String, Integer> e : entries){
            temp.put(e.getKey(), e.getValue());
        }

        return temp;
    }

    public void calculateTotalProcessTime(){
        for(int i = 0; i < numProcess; i++){
            processTime += burstTimeClone.get("P"+i);           
        }
    }

    public String getJob(LinkedHashMap<String, Integer> priority){
        String job;
        int lowestPriority = priority.get(readyQueue.get(0));
        job = readyQueue.get(0);

        for(int i = 0; i < readyQueue.size() - 1; i++){
            if(priority.get(readyQueue.get(i+1)) < lowestPriority){
                lowestPriority = priority.get(readyQueue.get(i+1));
                job = readyQueue.get(i+1);
            }
        }

        return job;
    }

    public int getNumberOfSameArrival(int time){
        int total = 0;
        for(int i = 0; i < readyQueue.size(); i++){
            if(sortedArrival.get(readyQueue.get(i)) == time){
                total++;
            }
        }
        return total;
    }

    public void turnaroundTimeCalc(LinkedHashMap<String,Integer> arrivalTime) {
        // Finish time - Arrival time
        ftKey = finishTime.keySet();
        ftKeyList = new ArrayList<>(ftKey);
        int finish;
        int arr;
        for(int i = 0; i < numProcess; i++) {
            finish = finishTime.get(ftKeyList.get(i));
            arr = arrivalTime.get(ftKeyList.get(i));
            turnaroundTime.put(ftKeyList.get(i), (finish-arr)); 
        }
    }

    public void waitingTimeCalc() {
        // turnaround time - burst time
        int turnaround;
        int burst;
        String process;

        for(int i = 0; i < numProcess; i++) {
            process = "P" + i;
            turnaround = turnaroundTime.get(("P"+i));
            burst = burstTimeClone.get(("P"+i));
            waitingTime.put(("P"+i), (turnaround-burst));
        }
    }

    public void ganttChart(int numProcess){
        for(int i = 0; i < numProcess; i++){
            System.out.print("|---" + executionList.get(i) + "---");
        }
        System.out.println("|");
        for(int i = 0; i <= numProcess; i++){
            System.out.print(df.format(timestamp.get(i)) + "       ");
        }
        System.out.print("\n");
    }
    
    public void printCalcTable(LinkedHashMap<String,Integer> arrivalTime, LinkedHashMap<String,Integer> burstTime) {
        int totalTurnaround = 0;
        int totalWaiting = 0;
        String process;
        System.out.print("\n");
        System.out.println("|-----------|--------------|------------|-------------|-----------------|--------------|");
        System.out.println("|  Process  | Arrival Time | Burst Time | Finish Time | Turnaround Time | Waiting Time |");
        System.out.println("|-----------|--------------|------------|-------------|-----------------|--------------|");
        for(int i = 0; i < numProcess; i++) {
            process = "P"+i;
            System.out.println("|  " + process + "       | " + arrivalTime.get(process) + "            | " + burstTimeClone.get(process) + "          | " +
                               df.format(finishTime.get(process)) + "          | " + df.format(turnaroundTime.get(process)) + "              | " +
                               df.format(waitingTime.get(process)) + "           |" );
            totalTurnaround += turnaroundTime.get(process);
            totalWaiting += waitingTime.get(process);
        }
        System.out.println("|--------------------------------------------------------------------------------------|");
        System.out.println("Total Turnaround Time  : " + totalTurnaround);
        System.out.println("Average Turnaround Time: " + (totalTurnaround/numProcess));
        System.out.println("\nTotal Waiting Time   : " + totalWaiting);
        System.out.println("Average Waiting Time   : " + (totalWaiting/numProcess));
    }


}