import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

class NewClient implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<NewClient> clients;
    private String username;

    public NewClient(Socket client, ArrayList<NewClient> clients) throws IOException {
        this.client = client;
        this.clients = clients;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            out.println("Enter your username:");
            username = in.readLine();
            NewServer.handlePairRequest(this);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("play")) {
                    NewServer.handlePairRequest(this);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}