import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MultiServerThread extends Thread {
    private Socket socket = null;

    public MultiServerThread(Socket socket) {
        super("MultiServerThread");
        this.socket = socket;
    }

    public void run() {
        try {
            System.out.println("Buffer size: "+this.socket.getReceiveBufferSize());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String inputLine;
            Protocol p = new Protocol();
            while ((inputLine = in.readLine()) != null) {
                System.out.println("requete recue");
                String finalInputLine = inputLine;
                (new Thread(){
                    @Override
                    public void run() {
                        String outputLine;
                        if(finalInputLine.contains(";")) {
                            try {
                                outputLine=(MultiServer.executor.submit(()->p.processInput(finalInputLine, MultiServer.file_in_table))).get();
                                out.println(outputLine);
                                System.out.println("reponse envoye");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }else{
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