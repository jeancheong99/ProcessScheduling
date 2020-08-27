import java.util.*;
import java.util.Map.*;
import java.util.HashMap.*;
import java.util.LinkedList.*;

public class NonPreemptiveSJF{
    int position = 0;
    int count = 0;
    int accumulator = 0;
    public NonPreemptiveSJF() {}

    public void simulate(LinkedHashMap<String, Integer> arrivalTime, LinkedHashMap<String, Integer> burstTime, int numProcess){
        LinkedHashMap<String, Integer> sortedArrival = sortByValue(numProcess, arrivalTime);
        List<Integer> arrValue = new ArrayList<>(sortedArrival.values());
        Set<String> arrKey = sortedArrival.keySet(); 
        List<String> arrKeyList = new ArrayList<>(arrKey);
        List<String> execProcess = new ArrayList<>();
        List<Integer> timestamp = new ArrayList<>();
        List<Integer> burstValue = new ArrayList<>();
        
        for(int i=0; i<numProcess-1; i++){
            if(arrValue.get(i) == arrValue.get(i+1)){
                count++;
                position = i+1;
            }
        }
        if(position == 0){
            execProcess.add(arrKeyList.get(0));
        }
        for(int i=0; i<position; i++){
            if(burstTime.get(arrKeyList.get(i)) < burstTime.get(arrKeyList.get(i+1))){
                execProcess.add(arrKeyList.get(i));
                execProcess.add(arrKeyList.get(i+1));
            }
            else if(burstTime.get(arrKeyList.get(i)) > burstTime.get(arrKeyList.get(i+1))){
                    execProcess.add(arrKeyList.get(i+1));
                    execProcess.add(arrKeyList.get(i));
            }
        }
        for(int i=0; i<numProcess; i++){
            execProcess.add(arrKeyList.get(i));
        }

        for(int i=0; i<numProcess; i++){
            int temp = burstTime.get(execProcess.get(i));
            burstValue.add(i,temp);
        }
        timestamp.add(0,0);
        for(int i=1; i<numProcess; i++){
            accumulator += burstValue.get(i);
            timestamp.add(i, accumulator);
        }
        ganttChart(execProcess, timestamp, numProcess);
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
    public void ganttChart(List<String> execProcess, List<Integer> timestamp, int numProcess){
        for(int i=0; i<numProcess; i++){
            System.out.print("|---" + execProcess.get(i) + "---");
        }
        System.out.println("|");
        for(int i=0; i<numProcess; i++){
            System.out.print(timestamp.get(i) + "        ");
        }
        System.out.print("\n");
    }
}