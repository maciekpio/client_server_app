import java.net.*;
import java.io.*;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Client {


    /**
     * args[0] : hostname, device name
     * args[1] : port number
     * args[2] : -a, automated test
     * args[3] : the path to the dbdata.txt file
     * @throws IOException
     */
<<<<<<< HEAD
    public static void main(String[] args) throws IOException {

        String hostName = "";
        int portNumber;
        String dbfile = "";
        boolean test = false;
        int regex_complexity = 0;
        int type_complexity = 0;
        int sequence_length = -1;
        int request_variance = 0;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h" :
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
                case "-t" :
                    test = true;
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
                default :
                    System.out.println("incorrect input parameter");
            }
        }
=======
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        ExecutorService executor = Executors.newFixedThreadPool(2);
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
>>>>>>> 2f406f794c5e37b3928d072dff0364b5f8a7d4dd

        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
        ){
            BufferedReader stdIn;
<<<<<<< HEAD
            if (test)
                stdIn = feedBuffer(new Random().nextLong(),
                                    regex_complexity,
                                    type_complexity,
                                    sequence_length,
                                    request_variance,
                                    dbfile);
            else stdIn = new BufferedReader(new InputStreamReader(System.in));
=======
            //to modify
            /*if (args[2].equals("-a"))
                stdIn = feedBuffer(new Random().nextLong(),
                                    30,
                                    1,
                                    10,
                                    5,
                                    args[3]);
            else */
            stdIn = new BufferedReader(new InputStreamReader(System.in));
>>>>>>> 2f406f794c5e37b3928d072dff0364b5f8a7d4dd

            String fromUser;
            while(true) {
                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    String finalFromUser = fromUser;
                    executor.submit(()-> {
                        try {
                            working(finalFromUser,out,in);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }

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

    public static void working(String fromUser,PrintWriter out,BufferedReader in) throws IOException {
        out.println(fromUser);
        String fromServer;
        while (!(fromServer = in.readLine()).equals("")) {
            System.out.print(fromServer + "\r\n");
        }
        fromUser=null;
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