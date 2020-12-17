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
    public static void main(String[] args) throws IOException {

        String hostName = "";
        int portNumber = -1;
        String dbfile = "";
        boolean test = false;
        int regex_complexity = 0;
        int type_complexity = 0;
        int sequence_length = -1;
        int request_variance = 0;
        int nThreads = 1;
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
                case "-nt" :
                    i++;
                    nThreads = Integer.parseInt(args[i]);
                    break;
                default :
                    System.out.println("incorrect input parameter");
            }
        }

        if (portNumber == -1 || hostName.equals("")) {
            System.err.println(
                    "you need to specify a valide port number and host name");
            System.exit(1);
        }

        ExecutorService executor = Executors.newFixedThreadPool(nThreads);

        try (
                Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
        ){

            BufferedReader stdIn;
            if (test)
                stdIn = feedBuffer(new Random().nextLong(),
                                    regex_complexity,
                                    type_complexity,
                                    sequence_length,
                                    request_variance,
                                    dbfile);
            else stdIn = new BufferedReader(new InputStreamReader(System.in));
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