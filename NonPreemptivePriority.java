import java.util.*;
import java.text.DecimalFormat;

class NonPreemptivePriority {
    private LinkedHashMap<String, Integer> sortedArrival;
    private List<Integer> arrValueList;
    private Set<String> arrKey;
    private List<String> arrKeyList;

    private List<String> readyQueue = new ArrayList<>();
    private List<String> executionList = new ArrayList<>();
    private List<Integer> timestamp = new ArrayList<>();

    private int counter = 0;
    private int currTime = 0;

    public NonPreemptivePriority() {}

    public void schedule(LinkedHashMap<String, Integer> arrivalTime, LinkedHashMap<String, Integer> burstTime, LinkedHashMap<String, Integer> priority, int numProcess){
        sortedArrival = sortByValue(numProcess, arrivalTime);
        arrValueList = new ArrayList<>(sortedArrival.values());
        arrKey = sortedArrival.keySet(); 
        arrKeyList = new ArrayList<>(arrKey);
        timestamp.add(counter,0); 
        counter++;

        while(!(executionList.size() == numProcess)) {     
            //selecting the first process to be executed at time = 0
            if(executionList.size() == 0){
                executionList.add(arrKeyList.get(0));
                currTime += burstTime.get(arrKeyList.get(0));
                timestamp.add(counter, currTime);
                arrKeyList.remove(0);
                arrValueList.remove(0);
            }
            else if(executionList.size() < (numProcess-1) ) {
                // add process that comes while previous process is executing into readyQueue
                for(int i=0; i < arrKeyList.size(); i++) {
                    if(arrValueList.get(i) <= currTime) {
                        readyQueue.add(arrKeyList.get(i));
                    }                    
                }
                //compare and insert process to executionList based on priority
                if(priority.get(readyQueue.get(0)) <= priority.get(readyQueue.get(1))) {
                    executionList.add(readyQueue.get(0));
                    timestamp.add(counter, currTime);
                    currTime += burstTime.get(readyQueue.get(0));
                    arrKeyList.remove(readyQueue.get(0));
                    arrValueList.remove(0);
                    readyQueue.clear();
                    counter++;
                }
                else {
                    executionList.add(readyQueue.get(1));
                    timestamp.add(counter, currTime);
                    currTime += burstTime.get(readyQueue.get(1));
                    arrKeyList.remove(readyQueue.get(1));
                    arrValueList.remove(1);
                    readyQueue.clear();
                    counter++;
                }
            }
            else if(executionList.size() == numProcess-1) {
                executionList.add(arrKeyList.get(arrKeyList.size()-1));
                timestamp.add(counter, currTime);
                currTime += burstTime.get(arrKeyList.get(arrKeyList.size()-1));
                arrKeyList.remove(arrKeyList.size()-1);
                arrValueList.remove(arrValueList.size()-1);
                counter++;
            }
            timestamp.add(counter, currTime);
        }


        ganttChart(numProcess);
    }  
    public LinkedHashMap<String, Integer> sortByValue(int numProcess, LinkedHashMap<String, Integer> temp){
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