import java.util.*;
import java.text.DecimalFormat;

class PreemptiveSJF{
    private LinkedHashMap<String, Integer> sortedArrival;
    private List<Integer> arrValueList;
    private Set<String> arrKey;
    private List<String> arrKeyList;

    private List<String> readyQueue = new ArrayList<>();
    private List<String> executionList = new ArrayList<>();
    private List<Integer> timestamp = new ArrayList<>();

    private int counter = 0;
    private int currTime = 0;
 
    public PreemptiveSJF(){
        System.out.println("Testing");
    }

    public void schedule(LinkedHashMap<String, Integer> arrivalTime, LinkedHashMap<String, Integer> burstTime, LinkedHashMap<String, Integer> priority, int numProcess) {
        sortedArrival = sortByValue(numProcess, arrivalTime);
        arrValueList = new ArrayList<>(sortedArrival.values());
        arrKey = sortedArrival.keySet(); 
        arrKeyList = new ArrayList<>(arrKey);


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
