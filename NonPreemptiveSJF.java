import java.util.*;
import java.text.DecimalFormat;

class NonPreemptiveSJF{
    private LinkedHashMap<String, Integer> sortedArrival;
    private List<Integer> arrValue;
    private Set<String> arrKey;
    private List<String> arrKeyList;
    private List<String> execProcess = new ArrayList<>();
    private List<Integer> timestamp = new ArrayList<>();
    private List<Integer> burstValue = new ArrayList<>();
    private LinkedHashMap<String, Integer> sortedBurst = new LinkedHashMap();
    private Set<String> SBKey;
    private List<String> SBKeyList;

    private int position = 0;
    private int prevPosition;
    private int count = 0;
    private int accumulator = 0;
    private int currArrTime;

    public NonPreemptiveSJF() {}

    public void schedule(LinkedHashMap<String, Integer> arrivalTime, LinkedHashMap<String, Integer> burstTime, int numProcess){
        sortedArrival = sortByValue(numProcess, arrivalTime);
        arrValue = new ArrayList<>(sortedArrival.values());
        arrKey = sortedArrival.keySet(); 
        arrKeyList = new ArrayList<>(arrKey);
        
        for(int a=0; a<= position; a++) {
            currArrTime = arrValue.get(position);
            prevPosition = position;
            for(int i=position; i<numProcess-1; i++) {
                if(currArrTime == arrValue.get(i+1)) {
                    //System.out.println("yes"+i);
                    count++;
                    position = i+1;
                }
                else{
                    position++;
                    //System.out.println("no");
                    break;
                }     
            }
            if(count == 0) {
                //System.out.println("count==0");
                //System.out.println(arrKeyList.get(prevPosition));
                execProcess.add(arrKeyList.get(prevPosition));               
            }
            else{
                //System.out.println(count);
                for(int k=0; k<=count; k++, prevPosition++) {
                    //System.out.println(prevPosition);
                    sortedBurst.put(arrKeyList.get(prevPosition), burstTime.get(arrKeyList.get(prevPosition)));
                    //System.out.println(sortedBurst.toString());
                }
                sortedBurst = sortByValue(count, sortedBurst);
                SBKey = sortedBurst.keySet();
                SBKeyList = new ArrayList<>(SBKey);
                //System.out.println("SBKeyList " +SBKeyList.toString());

                for(int m=0; m<SBKeyList.size(); m++) {
                    execProcess.add(SBKeyList.get(m));
                    //System.out.println(execProcess.toString());
                }                
            }
            count = 0; 
            sortedBurst.clear(); 
        }

        for(int i=0; i<numProcess; i++) {
            int temp = burstTime.get(execProcess.get(i));
            burstValue.add(i,temp);
        }
        timestamp.add(0,0);
        for(int i=0; i<numProcess; i++){
            accumulator += burstValue.get(i);
            timestamp.add(i+1, accumulator);
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
            System.out.print("|---" + execProcess.get(i) + "---");
        }
        System.out.println("|");
        for(int i=0; i<=numProcess; i++){
            System.out.print(new DecimalFormat("00").format(timestamp.get(i)) + "       ");
        }
        System.out.print("\n");
    }       
}