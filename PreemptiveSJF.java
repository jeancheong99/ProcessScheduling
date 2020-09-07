import java.util.*;
import java.text.DecimalFormat;

class PreemptiveSJF{
    private LinkedHashMap<String, Integer> sortedArrival;
    private List<Integer> arrivalValueList;
    private Set<String> arrivalKey;
    private List<String> arrivalKeyList;

    private List<String> readyQueue = new ArrayList<>();
    private List<String> executionList = new ArrayList<>();
    private List<Integer> timestamp = new ArrayList<>();

    private int currentTime = 0;
    private int processTime = 0;
    private String currentProcess;

    public PreemptiveSJF(){
        System.out.println("Tested by Aw");
    }

    public void schedule(LinkedHashMap<String, Integer> arrivalTime, LinkedHashMap<String, Integer> burstTime) {
        sort(arrivalTime);
        calculateProcessTime(burstTime);

        while(currentTime < processTime){
            //if still have process haven't entered
            if((!arrivalKeyList.isEmpty()) && currentTime == arrivalValueList.get(0)){
                //first entry in
                if(currentProcess == null){
                    //check duplicate value
                    if(Collections.frequency(arrivalValueList, arrivalValueList.get(0)) <= 1){
                        currentProcess = arrivalKeyList.get(0);
                        executionList.add(currentProcess);
                        timestamp.add(currentTime);
                        arrivalValueList.remove(0);
                        arrivalKeyList.remove(0);
                    }
                    else{
                        int value = arrivalValueList.get(0);
                        List<Integer> temp = new ArrayList<>(arrivalValueList);
                        for(int i = 0; i < Collections.frequency(temp, value); i++){
                            readyQueue.add(arrivalKeyList.get(0));
                            arrivalKeyList.remove(0);
                            arrivalValueList.remove(0);
                        }
                        currentProcess = getMin(burstTime, readyQueue);
                        executionList.add(currentProcess);
                        timestamp.add(currentTime);
                        readyQueue.remove(currentProcess);
                        System.out.println(readyQueue);
                    }
                    burstTime.put(currentProcess, burstTime.get(currentProcess) - 1);
                    currentTime++;
                    continue;
                }

                if(Collections.frequency(arrivalValueList, arrivalValueList.get(0)) <= 1){
                    //if burst time of new entry is shorter than old entry
                    if(burstTime.get(arrivalKeyList.get(0)) < burstTime.get(currentProcess)){
                        readyQueue.add(currentProcess);
                        currentProcess = arrivalKeyList.get(0);
                        executionList.add(currentProcess);
                        timestamp.add(currentTime);
                    }
                    else{
                        readyQueue.add(arrivalKeyList.get(0));
                    }
                    //if current process finish running
                    if(burstTime.get(currentProcess) == 0){
                        burstTime.remove(currentProcess);
                        currentProcess = getMin(burstTime, readyQueue);
                        executionList.add(currentProcess);
                        timestamp.add(currentTime);
                        readyQueue.remove(currentProcess);
                    }
                    burstTime.put(currentProcess, burstTime.get(currentProcess) - 1);
                    arrivalValueList.remove(0);
                    arrivalKeyList.remove(0);
                }
                else{
                    int value = arrivalValueList.get(0);
                    List<Integer> temp = new ArrayList<>(arrivalValueList);
                    for(int i = 0; i < Collections.frequency(temp, value); i++){
                        readyQueue.add(arrivalKeyList.get(0));
                        arrivalKeyList.remove(0);
                        arrivalValueList.remove(0);
                    }
                    String tempKey = getMin(burstTime, readyQueue);
                    //if burst time of new entry is shorter than old entry
                    if(burstTime.get(tempKey) < burstTime.get(currentProcess)){
                        readyQueue.add(currentProcess);
                        currentProcess = tempKey;
                        executionList.add(currentProcess);
                        timestamp.add(currentTime);
                        readyQueue.remove(currentProcess);
                    }
                    //if current process finish running
                    if(burstTime.get(currentProcess) == 0){
                        burstTime.remove(currentProcess);
                        currentProcess = getMin(burstTime, readyQueue);
                        executionList.add(currentProcess);
                        timestamp.add(currentTime);
                        readyQueue.remove(currentProcess);
                    }
                    burstTime.put(currentProcess, burstTime.get(currentProcess) - 1);
                }
            }
            //if all process have entered
            else{
                if(currentProcess != null){
                    if(burstTime.get(currentProcess) == 0){
                        burstTime.remove(currentProcess);
                        currentProcess = getMin(burstTime, readyQueue);
                        executionList.add(currentProcess);
                        timestamp.add(currentTime);
                        readyQueue.remove(currentProcess);
                    }
                    burstTime.put(currentProcess, burstTime.get(currentProcess) - 1);
                }
            }
            currentTime++;
        }
        timestamp.add(processTime);
        ganttChart(executionList.size());
    }

    public String getMin(LinkedHashMap<String, Integer> burstTime, List<String> readyQueue){
        List<Integer> temp = new ArrayList<>();
        String min;
        for(int i = 0; i < readyQueue.size(); i++){
            temp.add(burstTime.get(readyQueue.get(i)));
        }
        min = readyQueue.get(temp.indexOf(Collections.min(temp)));
        return min;
    }

    public void calculateProcessTime(LinkedHashMap<String, Integer> burstTime){
        ArrayList<Integer> sumBurstTime = new ArrayList<Integer>(burstTime.values());
        for(int i = 0; i < sumBurstTime.size(); i++){
            processTime = processTime + sumBurstTime.get(i);
        }
        processTime = processTime + arrivalValueList.get(0);
    }

    public void sort(LinkedHashMap<String, Integer> arrivalTime){
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

    public void ganttChart(int numProcess){
        for(int i=0; i<numProcess; i++){
            System.out.print("|---" + executionList.get(i) + "---");
        }
        System.out.println("|");
        for(int i=0; i<=numProcess; i++){
            System.out.print(new DecimalFormat("00").format(timestamp.get(i)) + "       ");
        }
        System.out.print("\n");
    }       
}
