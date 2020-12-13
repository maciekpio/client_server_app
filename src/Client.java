import java.net.*;
import java.io.*;
import java.util.Random;
import java.util.stream.Collectors;

public class Client {


    /**
     * args[0] : hostname, device name
     * args[1] : port number
     * args[2] : -a, automated test
     * args[3] : the path to the dbdata.txt file
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
        ){
            BufferedReader stdIn;
            if (args[2].equals("-a"))
                stdIn = feedBuffer(new Random().nextLong(),
                                    30,
                                    1,
                                    10,
                                    5,
                                    args[3]);
            else stdIn = new BufferedReader(new InputStreamReader(System.in));

            String fromServer;
            String fromUser;
            while(true) {
                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
                System.out.println("Server: ");
                while (!(fromServer = in.readLine()).equals("")) {
                    System.out.print(fromServer + "\r\n");
                }
                fromUser=null;
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }


    private static BufferedReader feedBuffer(long seed, int regex_complexity, int type_complexity, int sequence_length, int request_variance, String file_path) {
        try {
            Sequence seq1 = new Sequence(seed, regex_complexity, type_complexity, sequence_length, request_variance, file_path);
            seq1.save();
            String sequence_file_path =  "sequences/" + String.valueOf(seed) + ".txt";
            File file = new File(sequence_file_path);
            BufferedReader stdIn = new BufferedReader(new FileReader(file));
            for (int i = 0; i < 5; i++) stdIn.readLine();
            return stdIn;
        } catch (IOException ex) {
            System.out.println(ex);
            return null;
        }
    }

}