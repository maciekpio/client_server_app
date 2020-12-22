import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.text.*;

public class Evaluation {

    public static ArrayList<Object[]> data_to_plot = new ArrayList<>();
    private static int experience = 0;

    public static void main(String[] args) {
        //java Evaluation -h LAPTOP-71465AB6 -p 4444 -f dbdata_simple.txt -rc 10 -tc 1 -l 2 -rv 2 -pa 1000 -r 2 -nc 2 -s 0 -e 5 -st 1 -v type_complexity
        //java Evaluation -h LAPTOP-TPLG6GOL -p 1444 -f dbdata.txt -rc 40 -tc 1 -l 6 -rv 6 -pa 500 -r 5 -nc 2 -s 1 -e 102 -st 10 -v nbr_client

        String hostName = "";
        int portNumber = -1;
        String dbfile = "";
        int regex_complexity = 0;
        int type_complexity = 0;
        int sequence_length = -1;
        int request_variance = 0;
        int pause = 1000;
        int start = 0;
        int end = 0;
        int step = 1;
        int repetition = 1;
        int nbr_client = 1;
        String variant = "";

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "" :case "-h" :
                    i++;
                    hostName = args[i];
                    break;
                case "-p" :
                    i++;
                    portNumber = Integer.parseInt(args[i]);
                    break;
                case "-f" :
                    i++;
                    dbfile = args[i];
                    break;
                case "-rc" :
                    i++;
                    regex_complexity = Integer.parseInt(args[i]);
                    if (regex_complexity <= 0)
                        System.out.println("The regex complexity need to be strictly positive");
                    break;
                case "-tc" :
                    i++;
                    type_complexity = Integer.parseInt(args[i]);
                    if (type_complexity < 0 || type_complexity > 6)
                        System.out.println("The type complexity need to be an integer in [0;5]");
                    break;
                case "-l" :
                    i++;
                    sequence_length = Integer.parseInt(args[i]);
                    if (sequence_length <= 0)
                        System.out.println("The sequence length need to be strictly positive");
                    break;
                case "-rv" :
                    i++;
                    request_variance = Integer.parseInt(args[i]);
                    if (sequence_length == -1)
                        System.out.println("-l need to be put before -rv");
                    if (request_variance <= 0 || request_variance > sequence_length)
                        System.out.println("The request variance need to be an integer in [1;sequence length]");
                    break;
                case "-pa" :
                    i++;
                    pause = Integer.parseInt(args[i]);
                    break;
                case "-s" :
                    i++;
                    start = Integer.parseInt(args[i]);
                    break;
                case "-e" :
                    i++;
                    end = Integer.parseInt(args[i]);
                    break;
                case "-st" :
                    i++;
                    step = Integer.parseInt(args[i]);
                    break;
                case "-r" :
                    i++;
                    repetition = Integer.parseInt(args[i]);
                    break;
                case "-nc" :
                    i++;
                    nbr_client = Integer.parseInt(args[i]);
                    break;
                case "-v" :
                    i++;
                    if (!args[i].equals("regex_complexity") &&
                        !args[i].equals("type_complexity") &&
                        !args[i].equals("sequence_length") &&
                        !args[i].equals("request_variance") &&
                        !args[i].equals("pause")&&
                         !args[i].equals("nbr_client"))
                        System.out.println("the variant is suppose to be 'regex_complexity', 'type_complexity', 'sequence_length', 'request_variance', 'pause' or 'nbr_client'.");
                    variant = args[i];
                    break;
                default :
                    System.out.println("incorrect input parameter : " + args[i]);
            }
        }

        final String[] file = Sequence.loadFile(dbfile);

        Object syn1 = new Object();
        Object syn2 = new Object();

        for (int i = start; i <= end; i += step) {
            ArrayList<Object> repetions = new ArrayList<>();
            if (variant.equals("nbr_client")) nbr_client = i;
            for (int j = 0; j < repetition; j++) {
                ArrayList<Object> clients_results = new ArrayList<>();
                final int[] flag = {nbr_client};
                for (int k = 0; k < nbr_client; k++) {
                    String finalVariant = variant;
                    int finalI = i;
                    String finalHostName = hostName;
                    int finalPortNumber = portNumber;
                    int finalRegex_complexity = regex_complexity;
                    int finalType_complexity = type_complexity;
                    int finalSequence_length = sequence_length;
                    int finalRequest_variance = request_variance;
                    int finalPause = pause;
                    new Thread(() -> {
                        ArrayList<Long> durations = call_client_execution(finalVariant,
                                                                          finalI,
                                                                          finalHostName,
                                                                          finalPortNumber,
                                                                          finalRegex_complexity,
                                                                          finalType_complexity,
                                                                          finalSequence_length,
                                                                          finalRequest_variance,
                                                                          finalPause,
                                                                          file);
                        System.out.println(durations);
                        synchronized (syn1) { clients_results.add(durations); }
                        synchronized (syn2) { flag[0]--; }
                    }).start();
                }
                while(flag[0] > 0){
                }
                repetions.add(clients_results);
            }
            data_to_plot.add(new Object[]{i, repetions});
        }
        save(variant, start, end, step, regex_complexity, type_complexity, sequence_length, request_variance, pause);

    }

    private static ArrayList<Long> call_client_execution(String variant,
                                                         int i,
                                                         String hostName,
                                                         int portNumber,
                                                         int regex_complexity,
                                                         int type_complexity,
                                                         int sequence_length,
                                                         int request_variance,
                                                         int pause,
                                                         String[] file) {
        switch (variant) {
            case "regex_complexity" :
                return Client.clientExecution(hostName,
                                              portNumber,
                                              true,
                                              i,
                                              type_complexity,
                                              sequence_length,
                                              request_variance,
                                              pause,
                                              file);
            case "type_complexity" :
                return Client.clientExecution(hostName,
                                              portNumber,
                                              true,
                                              regex_complexity,
                                              i,
                                              sequence_length,
                                              request_variance,
                                              pause,
                                              file);
            case "sequence_length" :
                return Client.clientExecution(hostName,
                                              portNumber,
                                              true,
                                              regex_complexity,
                                              type_complexity,
                                              i,
                                              request_variance,
                                              pause,
                                              file);
            case "request_variance" :
                return Client.clientExecution(hostName,
                                              portNumber,
                                              true,
                                              regex_complexity,
                                              type_complexity,
                                              sequence_length,
                                              i,
                                              pause,
                                              file);
            case "pause" :
                return Client.clientExecution(hostName,
                                              portNumber,
                                              true,
                                              regex_complexity,
                                              type_complexity,
                                              sequence_length,
                                              request_variance,
                                              i,
                                              file);
            case "nbr_client" :
                return Client.clientExecution(hostName,
                        portNumber,
                        true,
                        regex_complexity,
                        type_complexity,
                        sequence_length,
                        request_variance,
                        pause,
                        file);
            default :
                return Client.clientExecution(hostName,
                        portNumber,
                        true,
                        regex_complexity,
                        type_complexity,
                        sequence_length,
                        request_variance,
                        pause,
                        file);
        }
    }

    private static String toString2(){
        String data_to_string = "";
        for(Object[] repetitions : data_to_plot) {
            String repetition_to_string = repetitions[0] + "|";
            for (Object clients_results : (ArrayList<Object>)(repetitions[1])) {
                String clients_results_to_string = "";
                for (Object durations : (ArrayList<Object>)clients_results) {
                    String durations_to_string = "";
                    for (Long duration : (ArrayList<Long>)durations) {
                        durations_to_string += duration.toString() + ',';
                    }
                    clients_results_to_string +=
                            durations_to_string.substring(0, durations_to_string.length()-1) + ';';
                }
                repetition_to_string +=
                        clients_results_to_string.substring(0,clients_results_to_string.length()-1) + '.';
            }
            data_to_string +=
                    repetition_to_string.substring(0, repetition_to_string.length()-1) + '!';
        }
        return data_to_string.substring(0, data_to_string.length()-1);
    }

    private static void save(String variant, int start, int end, int step, int regex_complexity, int type_complexity, int sequence_length, int request_variance, int pause) {
        String file_path = "experiences/Client_experience_" +
                            variant + "_" +
                            start + "_" +
                            end + "_" +
                            step + "_" +
                            regex_complexity + "_" +
                            type_complexity + "_" +
                            sequence_length + "_" +
                            request_variance + "_" +
                            pause + "_" +
                            ".txt";
        File file = new File(file_path);

        if (file.exists()) return;

        try {
            file.createNewFile();
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(toString2());
            myWriter.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

}
