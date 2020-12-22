import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Protocol {
    public String processInput(String theInput, Map<Integer, HashSet<String>> map) {
        String theOutput = "";
        String string1 = theInput.split(";")[0];
        Integer[] intlist;
        final Integer[] allTypes = {0,1,2,3,4,5};
        if (string1.length() == 0) intlist = allTypes;
        else {
            String[] string2 = string1.split(",");
            intlist = new Integer[string2.length];
            for (int i = 0; i < intlist.length; i++) intlist[i] = Integer.parseInt(string2[i]);
        }
        List<Integer> list = Arrays.asList(intlist);
        String request = theInput.split(";")[1];
        ArrayList<String> result_list = new ArrayList<>();
        for(int i=0;i<intlist.length;i++){
            for(String sentence : map.get(intlist[i])){
                if(sentence.contains(request)){
                    if (!result_list.contains(sentence))
                        result_list.add(sentence);
                }
            }
        }
        for (String result : result_list) theOutput += result + '\n';
        return theOutput;
    }
}