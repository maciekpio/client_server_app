import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutionException;


public class MultiServerThread extends Thread {
    private Socket socket = null;

    //Constructor
    public MultiServerThread(Socket socket) {
        super("MultiServerThread");
        this.socket = socket;
    }

    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String inputLine;
            Protocol p = new Protocol();
            while ((inputLine = in.readLine()) != null) {
                String finalInputLine = inputLine;
                //Launching a thread when the server receive a request
                (new Thread(){
                    @Override
                    public void run() {
                        String outputLine;
                        if(finalInputLine.contains(";")) {
                            try {
                                long serverStart=System.currentTimeMillis();

                                //Launching a thread from the server's thread pool to process the request
                                outputLine=(MultiServer.executor.submit(()->p.processInput(finalInputLine, MultiServer.map))).get();

                                long serverEnd=System.currentTimeMillis();

                                //Sending the response to the client
                                out.println(outputLine);

                                synchronized (MultiServer.syn_server_time) {MultiServer.server_time.add(serverEnd-serverStart);}
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }else{
                            //When the request doesn't match the <types>;<regex> format
                            out.println("wrong format");
                        }
                    }
                }).start();
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}