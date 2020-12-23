import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutionException;


public class MultiServerThread extends Thread {
    private Socket socket = null;
    private String protocol;

    //Constructor
    public MultiServerThread(Socket socket, String protocol) {
        super("MultiServerThread");
        this.socket = socket;
        this.protocol = protocol;
    }

    public void run() {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String inputLine;

            Protocol p;
            if (this.protocol.equals("basic")) p = new BasicProtocol();
            else p = new AdvancedProtocol();

            while ((inputLine = in.readLine()) != null) {
                String finalInputLine = inputLine;
                //Launching a thread when the server receive a request
                (new Thread(){
                    @Override
                    public void run() {
                        String outputLine;
                        if(finalInputLine.contains(";")) {
                            try {

                                //Launching a thread from the server's thread pool to process the request
                                if (protocol.equals("basic"))
                                    outputLine=(MultiServer.executor.submit(()->p.basic_processInput(finalInputLine, MultiServer.file_in_table))).get();

                                else
                                    outputLine=(MultiServer.executor.submit(()->p.advanced_processInput(finalInputLine, MultiServer.map))).get();

                                //Sending the response to the client
                                out.println(outputLine);

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