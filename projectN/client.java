import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.*;






public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 9090);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // استقبال طلب الاسم
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println(in.readLine());  // Server says: enter your name
            out.println(userInput.readLine());  // Send name to server

            // بدء اللعبة
            String question;
            while ((question = in.readLine()) != null) {
                System.out.println("Server says: " + question);  // Show the question
                System.out.print("Your answer (true/false): ");
                out.println(userInput.readLine());  // Send answer to server
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


