import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiServerBeta {
    //Server's response times
    static ArrayList<Long> ServerTime=new ArrayList<>();
    //The 2D array containing the dbdata.txt file in memory
    protected static Object[][] file_in_table;
    //Server's thread pool
    protected static ExecutorService executor= Executors.newFixedThreadPool(1);

    //Saving the dbdata.txt in main memory
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
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java MultiServerBeta <port number>");
            System.exit(1);
        }
        file_in_table = getTable(args[1]);
        System.out.println("Server ready !");

        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;

        //Launching a new MultiServerThreadBeta each time a client connect
        try (ServerSocket serverSocket = new ServerSocket(portNumber,1)) {
            while (listening) {
                new MultiServerThreadBeta(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
        executor.shutdown();
    }
}
