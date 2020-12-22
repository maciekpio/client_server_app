import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Protocol {
    public String processInput(String theInput, Object[][] file_in_table) {
        String theOutput = null;
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
        for(int i=0; i < file_in_table.length; i++){
            if(list.contains(file_in_table[i][0]) && file_in_table[i][1].toString().contains(request)) {
                result = result.concat(file_in_table[i][1].toString() + "\r\n");
            }
        }
        theOutput = result;
        return theOutput;
    }
}