/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.project;

import java.io.*; 
import java.net.*;
import java.util.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class NewServer {
    private static List<NewClient> room = new ArrayList<>();
    private static final int MAX_PLAYERS_PER_ROOM = 4;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("Server started...");

        while (true) {
            System.out.println("Waiting for client connection...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connected to client");

            NewClient clientThread = new NewClient(clientSocket);
            handlePlayerConnection(clientThread);
        }
    }

  
    
   // عندما ينضم لاعب جديد إلى غرفة الانتظار
public static synchronized void handlePlayerConnection(NewClient client) {
    if (room.size() < MAX_PLAYERS_PER_ROOM) {
        room.add(client);
        new Thread(client).start();
        notifyRoom("Player joined: " + client.getUsername()); // إعلام الجميع بانضمام لاعب جديد
        sendPlayerListToAll(); // إرسال قائمة اللاعبين المحدثة
        client.sendMessage("Waiting room"); // إرسال رسالة دخول غرفة الانتظار
    } else {
        client.sendMessage("Room is full. Please wait..."); // إخبار العميل بأن الغرفة ممتلئة
    }
}

public static synchronized void sendPlayerListToAll() {
StringBuilder playerList = new StringBuilder("PLAYERS: ");
    for (NewClient player : room) {
        playerList.append(player.getUsername()).append(",");
    }

    if (playerList.length() > 0) {
        playerList.setLength(playerList.length() - 1); // إزالة الفاصلة الزائدة
    }

    String playerListMessage = playerList.toString();
    // إرسال الرسالة إلى كل اللاعبين في الغرفة
    for (NewClient player : room) {
        player.sendMessage(playerListMessage); // إرسال قائمة اللاعبين
    }
}




    public static synchronized void removePlayer(NewClient client) {
        room.remove(client);
        notifyRoom("Player left: " + client.getUsername());
        sendPlayerListToAll();
    }

    public static void notifyRoom(String message) {
        for (NewClient player : room) {
            player.sendMessage(message);
        }
    }

    public static synchronized List<NewClient> getPlayers() {
        return new ArrayList<>(room);
    }

   
}
