import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            String inputLine, outputLine;
            Protocol p = new Protocol();
            while ((inputLine = in.readLine()) != null) {
                outputLine = p.processInput(inputLine,MultiServer.map);
                out.println(outputLine);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}