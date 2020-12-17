import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Server {

    //fonction pour placer la database dans la main memory
    public static Map<Integer, List<String>> getIdMap(final String pathToFile) throws IOException {
        // we'll use this later to store our mappings
        final Map<Integer, List<String>> map = new HashMap<Integer, List<String>>();
        // read the file into a String
        final String rawFileContents = new String(Files.readAllBytes(Paths.get(pathToFile)));
        // assumes each line is an ID + value
        final String[] fileLines = rawFileContents.split("\\r?\\n");
        // iterate over every line, and create a mapping for the ID to Value
        for (final String line : fileLines) {
            Integer id = null;
            try {
                // assumes the id is part 1 of a 2 part line in CSV "," format
                id = Integer.parseInt(line.split("@@@")[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            // assumes the value is part 2 of a 2 part line in CSV "," format
            final String value = line.split("@@@")[1];
            // put the pair into our map
            map.computeIfAbsent(id,k->new ArrayList<>()).add(value);
        }
        return map;
    }

    /**
     * args[0] : port number
     * args[1] : path to dbdata.txt file
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java KnockKnockServer <port number>");
            System.exit(1);
        }

        //for Maciek
        Map<Integer, List<String>> map = getIdMap("C:\\Users\\katol\\Desktop\\LINGI2241-Architecture and performance of computer systems\\client_server_app\\dbdata.txt");
        //Map<Integer, List<String>> map = getIdMap(args[1]);
        int portNumber = Integer.parseInt(args[0]);

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String inputLine, outputLine;

            // Initiate conversation with client
            Protocol p = new Protocol();

            while ((inputLine = in.readLine()) != null) {
                outputLine = p.processInput(inputLine,map);
                out.println(outputLine);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}