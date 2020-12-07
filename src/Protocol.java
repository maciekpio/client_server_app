import java.net.*;
import java.io.*;
import java.util.List;
import java.util.Map;

public class Protocol {
    //exemple pris du web, à adapter pour le projet
    private static final int WAITING = 0;
    private static final int WORKING = 1;

    private int state = WAITING;

    public String processInput(String theInput, Map<Integer, List<String>> map) {
        String theOutput = null;

        if (state == WAITING) {
            theOutput = "J'attends une requête!";
            state = WORKING;
        } else if (state == WORKING) {
            String string1=theInput.split(";")[0];
            int nbsize;
            int[] intlist;
            if(string1.length()==0){
                nbsize=6;
                intlist= new int[nbsize];
                for(int i=0; i<nbsize; i++) {
                    intlist[i] = i;
                    System.out.println(intlist[i]);
                }
            }else{
                String[] string2= string1.split(",");
                nbsize=string2.length;
                intlist=new int[string2.length];
                for(int i=0; i<nbsize; i++) {
                    intlist[i] = Integer.parseInt(string2[i]);
                }
            }

            String request=theInput.split(";")[1];

            String result="";

            for(int i=0;i<nbsize;i++){
                for(int j=0;j<map.get(intlist[i]).size();j++){
                    if(map.get(intlist[i]).get(j).contains(request)){
                        result=result.concat(map.get(intlist[i]).get(j)+"\n");
                    }
                }
            }
            theOutput =result;
        }
        return theOutput;
    }
}