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
    protected static Map<Integer, List<String>> map;
    protected static ExecutorService executor= Executors.newFixedThreadPool(5);

    public static Map<Integer, List<String>> getIdMap(final String pathToFile) throws IOException {
        final Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
        final String rawFileContents = new String(Files.readAllBytes(Paths.get(pathToFile)));
        final String[] fileLines = rawFileContents.split("\\r?\\n");
        for (final String line : fileLines) {
            Integer id = null;
            try {
                id = Integer.parseInt(line.split("@@@")[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            final String value = line.split("@@@")[1];
            map.computeIfAbsent(id,k->new ArrayList<>()).add(value);
        }
        return map;
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java MultiServer <port number>");
            System.exit(1);
        }

        //for Maciek
        map = getIdMap("C:\\Users\\katol\\Desktop\\LINGI2241-Architecture and performance of computer systems\\client_server_app\\dbdata.txt");
        //map = getIdMap(args[1]);

        int portNumber = Integer.parseInt(args[0]);
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                new MultiServerThread(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }
}
