import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;
import static java.nio.file.StandardOpenOption.*;

public class MultiServer {
    //Server's response times
    static ArrayList<Long> server_time = new ArrayList<>();
    final static Object syn_server_time = new Object();
    //The map containing the dbdata.txt file in memory
    static Map<Integer, HashSet<String>> map;
    //The 2D array containing the dbdata.txt file in memory
    protected static Object[][] file_in_table;
    //Server's thread pool
    static final int nThreads = 4;
    protected static ExecutorService executor= Executors.newFixedThreadPool(nThreads);

    //Saving the dbdata.txt in main memory
    public static Map<Integer, HashSet<String>> getIdMap(final String pathToFile) throws IOException {
        final Map<Integer, HashSet<String>> map = new HashMap<>();
        final String rawFileContents = new String(Files.readAllBytes(Paths.get(pathToFile)));
        final String[] fileLines = rawFileContents.split("\\r?\\n");
        for(int z=0;z<6;z++){
            map.put(z,new HashSet<>());
        }
        int a=0;
        int percent=0;
        int file_length=fileLines.length;
        for (final String line : fileLines) {
            a++;
            if (a * 100 / file_length > percent) {
                if(percent%10==0) System.out.println(a * 100 / file_length + "%");
                percent++;
            }
            Integer id = Integer.parseInt(line.split("@@@")[0]);
            final String value = line.split("@@@")[1];
            map.get(id).add(value);
        }
        return map;
    }

    public static Object[][] getTable(final String pathToFile) throws IOException {
        final String rawFileContents = new String(Files.readAllBytes(Paths.get(pathToFile)));
        final String[] fileLines = rawFileContents.split("\\r?\\n");
        final Object[][] file_in_table = new Object[fileLines.length][2];
        int percent = 0;
        int file_length = fileLines.length;
        for ( int i = 0; i < file_length; i++) {
            if (i*100/file_length > percent) {
                if(percent%10==0) System.out.println(i*100/file_length + "%");
                percent++;
            }
            String[] line_split = fileLines[i].split("@@@");
            try {
                file_in_table[i][0] = Integer.parseInt(line_split[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            file_in_table[i][1] = line_split[1];
        }
        return file_in_table;
    }

    /**
     * args[0] : port number
     * args[1] : the path to the dbdata.txt file
     * args[2] : protocol name
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: java MultiServer <port number> <file_name> <protocol_name>");
            System.exit(1);
        }

        if (args[2].equals("basic")) file_in_table = getTable(args[1]);
        else if (args[2].equals("advanced"))   map = getIdMap(args[1]);
        else {
            System.err.println("Usage: <protocol_name> should be 'basic' or 'advanced'");
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        new Thread(() -> {
            while(true) {
                String fromUser = null;
                try {
                    fromUser = stdIn.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (fromUser != null && fromUser.equals("save") && server_time.size() > 0) save();
            }
        }).start();

        System.out.println("Server ready !");

        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;

        //Launching a new MultiServerThread each time a client connect
        try (ServerSocket serverSocket = new ServerSocket(portNumber,100)) {
            while (listening) {
                new MultiServerThread(serverSocket.accept(), args[2]).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
        executor.shutdown();
    }

    private static void save() {
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int minutes = rightNow.get(Calendar.MINUTE);
        String file_path = "experiences/Server_experience.txt";

        Long mean = new Long(0);
        for(Long time : server_time){
            System.out.println(time);
            mean += time;
        }
        mean /= server_time.size();

        synchronized (syn_server_time) {server_time = new ArrayList<>();}

        String text = hour + "h" + minutes + " : " + mean.toString() + "\n";
        try {
            Files.write(Paths.get(file_path), text.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
