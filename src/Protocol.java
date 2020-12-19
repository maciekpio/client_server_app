import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Protocol {
    //exemple pris du web, à adapter pour le projet
    public String processInput(String theInput, Object[][] file_in_table) {
        String theOutput = null;
        String string1 = theInput.split(";")[0];
        Integer[] intlist;
        if (string1.length() == 0) {
            intlist = new Integer[6];
            for (int i = 0; i < 6; i++) {
                intlist[i] = i;
            }
        } else {
            String[] string2 = string1.split(",");
            intlist = new Integer[string2.length];
            for (int i = 0; i < intlist.length; i++) {
                intlist[i] = Integer.parseInt(string2[i]);
            }
        }
        List<Integer> list = Arrays.asList(intlist);

        String request = theInput.split(";")[1];

        String result = "";
        for(int i=0; i < file_in_table.length; i++){
            if(list.contains(file_in_table[i][0])
                    && file_in_table[i][1].toString().contains(request))
                result=result.concat(file_in_table[i][1].toString() + "\r\n");
        }

        theOutput = result;
        return theOutput;
    }
}