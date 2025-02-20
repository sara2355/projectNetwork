/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project;

import java.io.IOException;
 import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9090;
    private JFrame startFrame, loginFrame, gameFrame, playersFrame, waitingRoomFrame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton, playButton;
    private DefaultListModel<String> playerListModel, waitingRoomListModel;
    private JList<String> playerList, waitingRoomList;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public Client() {
        showStartWindow();
    }

    // نافذة البداية
    private void showStartWindow() {
        startFrame = new JFrame("ابدأ اللعبة");
        startFrame.setSize(300, 200);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("مرحبًا بك في لعبة المسابقة!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        startFrame.add(welcomeLabel, BorderLayout.CENTER);

        JButton startButton = new JButton("ابدأ");
        startButton.setBackground(Color.BLUE);
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startFrame.add(startButton, BorderLayout.SOUTH);

        startButton.addActionListener(e -> {
            startFrame.dispose();
            showLoginWindow();
        });

        startFrame.setVisible(true);
    }

    // نافذة إدخال الاسم
    private void showLoginWindow() {
        loginFrame = new JFrame("تسجيل الدخول");
        loginFrame.setSize(300, 200);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new BorderLayout());

        JLabel nameLabel = new JLabel("أدخل اسمك:", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        loginFrame.add(nameLabel, BorderLayout.CENTER);

        JTextField nameField = new JTextField();
        loginFrame.add(nameField, BorderLayout.NORTH);

        JButton connectButton = new JButton("اتصل");
        connectButton.setBackground(Color.BLUE);
        connectButton.setForeground(Color.WHITE);
        connectButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginFrame.add(connectButton, BorderLayout.SOUTH);

        connectButton.addActionListener(e -> {
            username = nameField.getText().trim();
            if (!username.isEmpty()) {
                loginFrame.dispose();
                connectToServer();
                showPlayersWindow();
            }
        });

        loginFrame.setVisible(true);
    }

    // الاتصال بالسيرفر
    private void connectToServer() {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(username);  // إرسال اسم اللاعب للسيرفر

            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        processMessage(serverMessage);  // معالجة الرسائل الواردة من السيرفر
                    }
                } catch (IOException e) {
                    chatArea.append("تم قطع الاتصال بالخادم.\n");
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "فشل الاتصال بالخادم!", "خطأ", JOptionPane.ERROR_MESSAGE);
        }
    }

    // عندما يتلقى العميل رسالة عن اللاعبين المتصلين
    private void processPlayersMessage(String message) {
        if (message.startsWith("PLAYERS:")) {
            updatePlayersList(message.substring(8));  // تحديث قائمة اللاعبين
        }
    }
 // نافذة اللاعبين المتصلين
    private void showPlayersWindow() {
        playersFrame = new JFrame("اللاعبين المتصلين");
        playersFrame.setSize(400, 300);
        playersFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        playersFrame.setLayout(new BorderLayout());

        playerListModel = new DefaultListModel<>();
        playerList = new JList<>(playerListModel);
        playerList.setBackground(Color.LIGHT_GRAY);
        playerList.setFont(new Font("Arial", Font.BOLD, 14));
        JScrollPane playersScrollPane = new JScrollPane(playerList);
        playersScrollPane.setPreferredSize(new Dimension(150, 0));
        playersFrame.add(playersScrollPane, BorderLayout.CENTER);

        playButton = new JButton("ابدأ اللعب");
        playButton.setBackground(Color.BLUE);
        playButton.setForeground(Color.WHITE);
        playButton.setFont(new Font("Arial", Font.BOLD, 14));
        playersFrame.add(playButton, BorderLayout.SOUTH);

        playButton.addActionListener(e -> {
            sendPlayRequest();
            playersFrame.dispose();
            showWaitingRoom();
        });

        playersFrame.setVisible(true);
    }

    // تحديث قائمة اللاعبين المتصلين في نافذة اللاعبين المتصلين
   private void updatePlayersList(String players) {
    SwingUtilities.invokeLater(() -> {
        playerListModel.clear();  // مسح القائمة القديمة
        String[] playerNames = players.split(",");
        for (String player : playerNames) {
            player = player.trim();
            if (!player.isEmpty()) {
                playerListModel.addElement(player);
            }
        }
    });
}


   
    // غرفة الانتظار
    private void showWaitingRoom() {
        waitingRoomFrame = new JFrame("غرفة الانتظار");
        waitingRoomFrame.setSize(400, 300);
        waitingRoomFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        waitingRoomFrame.setLayout(new BorderLayout());

        if (waitingRoomListModel == null) {
            waitingRoomListModel = new DefaultListModel<>();
        }

        waitingRoomList = new JList<>(waitingRoomListModel);
        waitingRoomList.setBackground(Color.LIGHT_GRAY);
        waitingRoomList.setFont(new Font("Arial", Font.BOLD, 14));
        JScrollPane waitingRoomScrollPane = new JScrollPane(waitingRoomList);
        waitingRoomScrollPane.setPreferredSize(new Dimension(150, 0));
        waitingRoomFrame.add(waitingRoomScrollPane, BorderLayout.CENTER);

        JButton startButton = new JButton("ابدأ اللعبة");
        startButton.setBackground(Color.BLUE);
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        waitingRoomFrame.add(startButton, BorderLayout.SOUTH);

        startButton.addActionListener(e -> sendPlayRequest());

        waitingRoomFrame.setVisible(true);

        // إضافة اسم اللاعب إلى غرفة الانتظار
        if (username != null && !waitingRoomListModel.contains(username)) {
            waitingRoomListModel.addElement(username);
        }

        sendPlayerListToWaitingRoom();  // إرسال قائمة اللاعبين إلى الخادم
    }

    // معالجة الرسائل الواردة من السيرفر
  private void processMessage(String message) {
    System.out.println("Received from server: " + message); // طباعة الرسالة الواردة لفحصها

    if (waitingRoomListModel == null) {
        waitingRoomListModel = new DefaultListModel<>();
    }

    if (message.startsWith("PLAYERS:")) {
        updatePlayersList(message.substring(8));  // تحديث قائمة اللاعبين المتصلين
    } else if (message.startsWith("Player joined: ")) {
        String playerName = message.substring(14).trim();
        if (!playerName.isEmpty() && !waitingRoomListModel.contains(playerName)) {
            waitingRoomListModel.addElement(playerName);  // إضافة اللاعب إلى غرفة الانتظار
        }
    } else if (message.startsWith("WAITING ROOM:")) {
        String players = message.substring(13).trim();
        updateWaitingRoomList(players);  // تحديث قائمة اللاعبين في غرفة الانتظار
    } else if (message.startsWith("Player left: ")) {
        String playerName = message.substring(13).trim();
        waitingRoomListModel.removeElement(playerName);  // إزالة اللاعب من غرفة الانتظار
    }
}



    // تحديث قائمة اللاعبين في غرفة الانتظار
 private void updateWaitingRoomList(String players) {
    SwingUtilities.invokeLater(() -> {
        waitingRoomListModel.clear(); // مسح القائمة القديمة

        String[] playerNames = players.split(",");
        for (String player : playerNames) {
            player = player.trim();
            if (!player.isEmpty() && !waitingRoomListModel.contains(player)) {
                waitingRoomListModel.addElement(player); // إضافة الاسم إلى القائمة
            }
        }
    });
}



    // إرسال طلب اللعب إلى السيرفر
  private void sendPlayRequest() {
    if (out != null) {
        out.println("play");

        // إزالة اللاعب من قائمة اللاعبين المتصلين
        SwingUtilities.invokeLater(() -> {
            playerListModel.removeElement(username);
        });

        System.out.println("تم إرسال طلب اللعب إلى السيرفر.");
    } else {
        System.err.println("فشل في إرسال طلب اللعب: `out` غير مهيأ.");
    }
}


    // إرسال قائمة اللاعبين إلى نافذة غرفة الانتظار
    private void sendPlayerListToWaitingRoom() {
        StringBuilder playerList = new StringBuilder();

        for (int i = 0; i < playerListModel.getSize(); i++) {
            playerList.append(playerListModel.getElementAt(i)).append(",");
        }

        if (playerList.length() > 0) {
            playerList.setLength(playerList.length() - 1); // إزالة الفاصلة الزائدة
        }

        // إرسال القائمة إلى الخادم
        if (out != null) {
            out.println("WAITING ROOM: " + playerList.toString());
        }
    }

    // الدالة الرئيسية
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Client(); // افتح نافذة واحدة فقط
        });
    }
}
