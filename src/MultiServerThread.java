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
                (new Thread(){
                    @Override
                    public void run() {
                        System.out.println("le serveur a recu une request \n");
                        String outputLine;
                        if(finalInputLine.matches("(.*);(.*)")) {
                            try {
                                outputLine=(MultiServer.executor.submit(()->p.processInput(finalInputLine, MultiServer.map))).get();
                                out.println(outputLine);
                                System.out.println("le serveur a renvoye une reponse \n");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }else{
                            out.println("mauvais format");
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