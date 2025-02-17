import java.io.*; 
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.net.*;
import java.util.*;

public class NewServer {
    private static ArrayList<NewClient> clients = new ArrayList<>();
    private static HashMap<Integer, List<NewClient>> rooms = new HashMap<>();
    private static int roomCounter = 1;
    private static final int MAX_PLAYERS_PER_ROOM = 2;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("Server started...");

        while (true) {
            System.out.println("Waiting for client connection");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connected to client");
            
            NewClient clientThread = new NewClient(clientSocket, clients);
            clients.add(clientThread);
            new Thread(clientThread).start();
        }
    }

    public static synchronized void handlePairRequest(NewClient client) {
        for (int roomId : rooms.keySet()) {
            List<NewClient> room = rooms.get(roomId);
            if (room.size() < MAX_PLAYERS_PER_ROOM) {
                room.add(client);
                notifyRoom(room, "Player joined: " + client.getUsername());
                return;
            }
        }
        // If no available rooms, create a new one
        List<NewClient> newRoom = new ArrayList<>();
        newRoom.add(client);
        rooms.put(roomCounter++, newRoom);
        notifyRoom(newRoom, "New room created. Waiting for players...");
    }

    private static void notifyRoom(List<NewClient> room, String message) {
        for (NewClient player : room) {
            player.sendMessage(message);
        }
    }
}

