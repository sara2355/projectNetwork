import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NewClient implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int score = 0;
    private String name;
    private List<NewClient> clients;

    public NewClient(Socket socket, List<NewClient> clients) {
        this.socket = socket;
        this.clients = clients;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("enter your name :");
            name = in.readLine();
            System.out.println("done : " + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        out.println(" wait  ...");
    }

    public String readMessage() {
        try {
            return in.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void incrementScore() {
        score++;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }
}


