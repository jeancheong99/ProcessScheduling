import java.util.*;
import java.text.DecimalFormat;

class NonPreemptiveSJF{
    DecimalFormat df = new DecimalFormat("00");

    private LinkedHashMap<String, Integer> sortedArrival;
    private List<Integer> arrValue;
    private Set<String> arrKey;
    private List<String> arrKeyList;
    
    private LinkedHashMap<String, Integer> sortedBurst = new LinkedHashMap<>();
    private Set<String> SBKey;
    private List<String> SBKeyList;

    private LinkedHashMap<String, Integer> finishTime = new LinkedHashMap<>();
    private Set<String> ftKey;
    private List<String> ftKeyList;

    private LinkedHashMap<String, Integer> turnaroundTime = new LinkedHashMap<>();
    private LinkedHashMap<String, Integer> waitingTime = new LinkedHashMap<>();

    private List<String> executionList = new ArrayList<>();
    private List<Integer> timestamp = new ArrayList<>();
    private List<Integer> burstValue = new ArrayList<>();

    private int numProcess;
    private int position = 0;
    private int prevPosition;
    private int count = 0;
    private int accumulator = 0;
    private int currArrTime; 

    public NonPreemptiveSJF(int numProcess) {
        this.numProcess = numProcess;
    }

    public void schedule(LinkedHashMap<String, Integer> arrivalTime, LinkedHashMap<String, Integer> burstTime){
        sortedArrival = sortByValue(arrivalTime);
        arrValue = new ArrayList<>(sortedArrival.values());
        arrKey = sortedArrival.keySet(); 
        arrKeyList = new ArrayList<>(arrKey);
        
        while(executionList.size() != numProcess) {
            currArrTime = arrValue.get(position);
            prevPosition = position;
            for(int i=position; i<numProcess-1; i++) {
                if(currArrTime == arrValue.get(i+1)) {
                    count++;
                    position = i+1;
                }
                else{
                    position++;
                    break;
                }     
            }
            if(count == 0) {
                executionList.add(arrKeyList.get(prevPosition));               
            }
            else{
                for(int k=0; k<=count; k++, prevPosition++) {
                    sortedBurst.put(arrKeyList.get(prevPosition), burstTime.get(arrKeyList.get(prevPosition)));
                }
                sortedBurst = sortByValue(sortedBurst);
                SBKey = sortedBurst.keySet();
                SBKeyList = new ArrayList<>(SBKey);

                for(int m=0; m<SBKeyList.size(); m++) {
                    executionList.add(SBKeyList.get(m));
                }                
            }
            count = 0; 
            sortedBurst.clear(); 
        }

        for(int i=0; i<numProcess; i++) {
            int temp = burstTime.get(executionList.get(i));
            burstValue.add(i,temp);
        }
        timestamp.add(0,0);
        for(int i=0; i<numProcess; i++){
            accumulator += burstValue.get(i);
            timestamp.add(i+1, accumulator);
            finishTime.put(executionList.get(i), accumulator);
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