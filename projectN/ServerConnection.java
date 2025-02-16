import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;



public class ServerConnection implements Runnable {
    private Socket server;
    private BufferedReader in;

    public ServerConnection(Socket s) throws IOException {
        this.server = s;
        this.in = new BufferedReader(new InputStreamReader(server.getInputStream()));
    }

    @Override
    public void run() {
        try {
            String serverResponse;
            while ((serverResponse = in.readLine()) != null) {
                System.out.println("Server says: " + serverResponse);
            }
        } catch (IOException ex) {
            System.out.println("Connection to the server lost.");
        } finally {
            try {
                in.close();
                server.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
