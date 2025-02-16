import java.io.*; 
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;


public class NewServer {
    private static ArrayList<NewClient> clients = new ArrayList<>();
    private static String[][] questions = {
        {"The sun rises from the west?", "false"},
        {"Water boils at 100 degrees Celsius?", "true"},
        {"The moon is a planet?", "false"},
        {"There are 26 letters in the English alphabet?", "false"},
        {"Lightning never strikes the same place twice?", "false"}
    };

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("Waiting for client connections...");

        while (clients.size() < 2) { // Allowing two players to join
            Socket client = serverSocket.accept();
            System.out.println("Connected to client");
            NewClient clientThread = new NewClient(client, clients);
            clients.add(clientThread);
            new Thread(clientThread).start();
        }

        startGame(); // Start game after two players join
    }

    private static void startGame() throws IOException {
    for (String[] q : questions) {
        String question = q[0];
        String correctAnswer = q[1];

        // إرسال السؤال مرة واحدة لجميع اللاعبين
        broadcast("Question: " + question);

        boolean answered = false;
        while (!answered) {
            for (NewClient player : clients) {
                String answer = player.readMessage();  // قراءة الإجابة من اللاعب
                if (answer.equalsIgnoreCase(correctAnswer)) {
                    player.incrementScore();  // زيادة النقطة لللاعب
                    broadcast("Player " + player.getName() + " answered correctly! Score: " + player.getScore());
                    answered = true;  // الانتقال للسؤال التالي
                    break;  // إنهاء الدورة الداخلية والانتقال للسؤال التالي
                }
            }
        }
    }

    declareWinner();  // إعلان الفائز في النهاية
}


    private static void declareWinner() {
        NewClient winner = clients.get(0);
        for (NewClient player : clients) {
            if (player.getScore() > winner.getScore()) {
                winner = player;
            }
        }
        broadcast("The winner is " + winner.getName() + " with " + winner.getScore() + " points!");
    }

    private static void broadcast(String message) {
        for (NewClient player : clients) {
            player.sendMessage(message);
        }
    }
}
