import java.util.*;

public class BasicProtocol extends Protocol {
    public String basic_processInput(String theInput, Object[][] file_in_table) {
        String theOutput = null;

        //Retrieve the <types> and <regex> part of the request
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
        String result = "";
        ArrayList<String> result_list = new ArrayList<>();

        //Iterate over the 2D array to find all the matching sentences
        for(int i=0; i < file_in_table.length; i++){
            if(list.contains(file_in_table[i][0]) && file_in_table[i][1].toString().contains(request)) {
                //check if the founded sentence didn't occur yet
                if (!result_list.contains(file_in_table[i][1].toString()))
                    result_list.add(file_in_table[i][1].toString());
            }
        }

        //Return the response
        theOutput = result;
        return theOutput;
    }
}