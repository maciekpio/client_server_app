import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiServer {
    //Server's response times
    static ArrayList<Long> ServerTime=new ArrayList<>();
    //The map containing the dbdata.txt file in memory
    static Map<Integer, HashSet<String>> map;
    //Server's thread pool
    protected static ExecutorService executor= Executors.newFixedThreadPool(4);

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

    /**
     * args[0] : port number
     * args[1] : the path to the dbdata.txt file
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java MultiServer <port number>");
            System.exit(1);
        }

        map = getIdMap(args[1]);

        System.out.println("Server ready !");

        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;

        //Launching a new MultiServerThread each time a client connect
        try (ServerSocket serverSocket = new ServerSocket(portNumber,100)) {
            while (listening) {
                new MultiServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
        executor.shutdown();
    }
}
