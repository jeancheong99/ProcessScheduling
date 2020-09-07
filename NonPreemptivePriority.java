import java.util.*;
import java.text.DecimalFormat;

class NonPreemptivePriority {
    DecimalFormat df = new DecimalFormat("00");

    private LinkedHashMap<String, Integer> sortedArrival = new LinkedHashMap<>();;
    private List<Integer> arrValueList;
    private Set<String> arrKey;
    private List<String> arrKeyList;

    private LinkedHashMap<String, Integer> finishTime = new LinkedHashMap<>();
    private Set<String> ftKey;
    private List<String> ftKeyList;

    private LinkedHashMap<String, Integer> turnaroundTime = new LinkedHashMap<>();
    private LinkedHashMap<String, Integer> waitingTime = new LinkedHashMap<>();

    private List<String> readyQueue = new ArrayList<>();
    private List<String> executionList = new ArrayList<>();
    private List<Integer> timestamp = new ArrayList<>();


    private int numProcess;
    private int counter = 0;
    private int currTime = 0;
    private int currArrTime;
    private int total = 0;

    public NonPreemptivePriority(int numProcess) {
        this.numProcess = numProcess;
    }

    public void schedule(LinkedHashMap<String, Integer> arrivalTime, LinkedHashMap<String, Integer> burstTime, LinkedHashMap<String, Integer> priority){
        sortedArrival = sortByValue(arrivalTime);
        arrValueList = new ArrayList<>(sortedArrival.values());
        arrKey = sortedArrival.keySet(); 
        arrKeyList = new ArrayList<>(arrKey);
        timestamp.add(counter,0); 
        counter++;

        while(!(executionList.size() == numProcess)) {     
            //selecting the first process to be executed at time = 0
            if(executionList.size() == 0){
                currArrTime = arrValueList.get(0);
                for(int i=0; i<numProcess-1; i++) {
                    if(currArrTime == arrValueList.get(i+1)) {
                        total++;
                    }
                }
                if (total == 0) {
                    executionList.add(arrKeyList.get(0));
                    currTime += burstTime.get(arrKeyList.get(0));
                    timestamp.add(counter, currTime);
                    arrKeyList.remove(0);
                    arrValueList.remove(0);
                    counter++;
                }
                else {
                    String highest = "";
                    int temp = 0;
                    for(int i=0; i<total; i++) {
                        if(priority.get(arrKeyList.get(i)) <= priority.get(arrKeyList.get(i+1))) {
                            highest = arrKeyList.get(i);
                            temp = i;
                        }
                        else {
                            highest = arrKeyList.get(i+1);
                            temp = i;
                        }
                    }    
                    executionList.add(highest);
                    currTime += burstTime.get(highest);
                    timestamp.add(counter, currTime);
                    arrKeyList.remove(highest);
                    arrValueList.remove(temp);
                    counter++;
                }
            }
            else if(executionList.size() < numProcess-1 ) {
                // add process that comes while previous process is executing into readyQueue
                for(int i=0; i < arrKeyList.size(); i++) {
                    if(arrValueList.get(i) <= currTime) {
                        readyQueue.add(arrKeyList.get(i));
                    }                    
                }
                //compare and insert process to executionList based on priority
                if(priority.get(readyQueue.get(0)) <= priority.get(readyQueue.get(1))) {
                    executionList.add(readyQueue.get(0));
                    currTime += burstTime.get(readyQueue.get(0));
                    timestamp.add(counter, currTime);
                    arrKeyList.remove(readyQueue.get(0));
                    arrValueList.remove(0);
                    readyQueue.clear();
                    counter++;
                }
                else {
                    executionList.add(readyQueue.get(1));
                    currTime += burstTime.get(readyQueue.get(1));
                    timestamp.add(counter, currTime);
                    arrKeyList.remove(readyQueue.get(1));
                    arrValueList.remove(1);
                    readyQueue.clear();
                    counter++;
                }
            }
            else if(executionList.size() == numProcess-1) {
                executionList.add(arrKeyList.get(arrKeyList.size()-1));
                currTime += burstTime.get(arrKeyList.get(arrKeyList.size()-1));
                timestamp.add(counter, currTime);
                arrKeyList.remove(arrKeyList.size()-1);
                arrValueList.remove(arrValueList.size()-1);
                counter++;
            }
        }
        for(int i=1; i < timestamp.size(); i++){
            finishTime.put(executionList.get(i-1), timestamp.get(i));
        }
        ftKey = finishTime.keySet();
        ftKeyList = new ArrayList<>(ftKey);
        ganttChart();
        turnaroundTimeCalc(arrivalTime);
        waitingTimeCalc(burstTime);
        printCalcTable(arrivalTime, burstTime);
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
    public void ganttChart(){
        for(int i=0; i<numProcess; i++){
            System.out.print("|---" + executionList.get(i) + "---");
        }
        System.out.println("|");
        for(int i=0; i<=numProcess; i++){
            System.out.print(df.format(timestamp.get(i)) + "       ");
        }
        System.out.print("\n");
    }       
    public void turnaroundTimeCalc(LinkedHashMap<String,Integer> arrivalTime) {
        // Finish time - Arrival time
        int finish;
        int arr;
        for(int i=0; i < numProcess; i++) {
            finish = finishTime.get(ftKeyList.get(i));
            arr = arrivalTime.get(executionList.get(i));
            turnaroundTime.put(ftKeyList.get(i), (finish-arr)); 
        }
    }
    public void waitingTimeCalc(LinkedHashMap<String,Integer> burstTime) {
        // turnaround time - burst time
        int turnaround;
        int burst;
        for(int i=0; i < numProcess; i++) {
            turnaround = turnaroundTime.get(("P"+i));
            burst = burstTime.get(("P"+i));
            waitingTime.put(("P"+i), (turnaround-burst));
        }
    }
    public void printCalcTable(LinkedHashMap<String,Integer> arrivalTime, LinkedHashMap<String,Integer> burstTime) {
        String key;
        int totalTurnaround = 0;
        int totalWaiting = 0;
        System.out.print("\n");
        System.out.println("|-----------|--------------|------------|-------------|-----------------|--------------|");
        System.out.println("|  Process  | Arrival Time | Burst Time | Finish Time | Turnaround Time | Waiting Time |");
        System.out.println("|-----------|--------------|------------|-------------|-----------------|--------------|");
        for(int i=0; i < numProcess; i++) {
            key = "P"+i;
            System.out.println("|  " + key + "       | " + arrivalTime.get(key) + "            | " + burstTime.get(key) + "          | " +
                               df.format(finishTime.get(key)) + "          | " + df.format(turnaroundTime.get(key)) + "              | " +
                               df.format(waitingTime.get(key)) + "           |" );
            totalTurnaround += turnaroundTime.get(key);
            totalWaiting += waitingTime.get(key);
        }
        System.out.println("|--------------------------------------------------------------------------------------|");
        System.out.println("Average Turnaround time: " + (totalTurnaround/numProcess));
        System.out.println("Average Waiting time   : " + (totalWaiting/numProcess));
    }
}