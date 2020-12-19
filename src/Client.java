import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class Client {

    /**
     * args[0] : hostname, device name
     * args[1] : port number
     * args[2] : -a, automated test
     * args[3] : the path to the dbdata.txt file
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        //java Client -h LAPTOP-71465AB6 -p 4444 -f dbdata.txt -rc 10 -tc 1 -l 10 -rv 8 -pa 1000 -t

        String hostName = "";
        int portNumber = -1;
        int max_iteration = 1;
        String dbfile = "";
        boolean test = false;
        int regex_complexity = 0;
        int type_complexity = 0;
        int sequence_length = -1;
        int request_variance = 0;
        int pause = 1000;
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
                    max_iteration = sequence_length;
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
                default :
                    System.out.println("incorrect input parameter");
            }
        }

        if (portNumber == -1 || hostName.equals("")) {
            System.err.println(
                    "you need to specify a valide port number and host name");
            System.exit(1);
        }

        ArrayList<Long> err = clientExecution(hostName,
                portNumber,
                test,
                regex_complexity,
                type_complexity,
                sequence_length,
                request_variance,
                pause,
                dbfile);
        System.out.println(err.toString());
        if (err == null) System.exit(1);
    }

    public static ArrayList<Long> clientExecution(String hostName,
                                       int portNumber,
                                       boolean test,
                                       int regex_complexity,
                                       int type_complexity,
                                       int sequence_length,
                                       int request_variance,
                                       int pause,
                                       String dbfile) {
        ArrayList<Long> request_durations = new ArrayList<>();
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

            System.out.println("Client ready !");

            final String[] fromUser = new String[1];
            final String[] fromServer = new String[1];
            final long[] startTime = new long[1];
            final long[] endTime = new long[1];
            Random generator = new Random();
            final long[] duration = new long[1];
            Object syn = new Object();
            final int[] flag = new int[1];
            if (test)  flag[0] = sequence_length+1;
            else flag[0] = 0;
            for (int i = 0; condition(test, i, sequence_length); i++) {
                if (test) {
                    long pa = (long) ((generator.nextGaussian() * (pause / 2)) + pause);
                    System.out.println(pa);
                    sleep(pa);
                }
                Thread t = null;
                try {
                    fromUser[0] = stdIn.readLine();
                    if (fromUser[0] != null) {
                        t = new Thread(() -> {

                            startTime[0] = System.nanoTime();
                            out.println(fromUser[0]);
                            while (true) {
                                try {
                                    while (!(fromServer[0] = in.readLine()).equals(""))
                                        System.out.print(fromServer[0] + "\r\n");
                                    fromServer[0] = null;
                                    break;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            endTime[0] = System.nanoTime();
                            duration[0] = endTime[0] - startTime[0];
                            request_durations.add(duration[0]);
                            System.out.println("duration : " + duration[0]);
                            synchronized (syn) { flag[0]--; }
                        });
                        t.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            while(flag[0] <= 0);
            return request_durations;
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            return null;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +hostName);
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean condition(boolean test, int i, int sequence_lenght) {
        if (test) return i<=sequence_lenght;
        else return true;
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