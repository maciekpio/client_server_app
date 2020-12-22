import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MultiServer {
/* MAP
    protected static Map<Integer, List<String>> map;
    protected static ExecutorService executor= Executors.newFixedThreadPool(5);
    */

    protected static Object[][] file_in_table;
    protected static ExecutorService executor= Executors.newFixedThreadPool(1);

    public static Object[][] getTable(final String pathToFile) throws IOException {
        final String rawFileContents = new String(Files.readAllBytes(Paths.get(pathToFile)));
        final String[] fileLines = rawFileContents.split("\\r?\\n");
        final Object[][] file_in_table = new Object[fileLines.length][2];
        int percent = 0;
        int file_length = fileLines.length;
        for ( int i = 0; i < file_length; i++) {
            if (i*100/file_length > percent) {
                System.out.println(i*100/file_length + "%");
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

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java MultiServer <port number>");
            System.exit(1);
        }

        file_in_table = getTable(args[1]);

        System.out.println("Server ready !");

        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;


        try (ServerSocket serverSocket = new ServerSocket(portNumber,1)) {
            //setting the buffer size with the help of setReceiveBufferSize() method
            //serverSocket.setReceiveBufferSize(1);
            //getReceiveBufferSize() method returns the buffer size set for this socket
            System.out.println("Buffer size: "+serverSocket.getReceiveBufferSize());
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
