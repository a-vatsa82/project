import java.io.*;
import java.net.*;
import java.util.*;

public class Main {
    private static final int PORT = 12345;
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Map<String, String> botResponses = new HashMap<>();

    public static void main(String[] args) {
        initializeBotResponses();
        new Thread(Main::startServer).start();

        // Adding a small delay to ensure the server starts before the client tries to connect.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(Main::startClient).start();
    }

    public static void initializeBotResponses() {
        botResponses.put("hello", "Hi there! How can I help you today?");
        botResponses.put("how are you", "I'm just a bunch of code, but I'm doing great! How about you?");
        botResponses.put("bye", "Goodbye! Have a great day!");
        botResponses.put("help", "I can respond to 'hello', 'how are you', 'bye', 'help', 'what's your name?', 'what can you do?', and 'tell me a joke'.");
        botResponses.put("what's your name?", "I'm ChatBot, your virtual assistant.");
        botResponses.put("what can you do?", "I can chat with you and answer simple questions. Ask me anything!");
        botResponses.put("tell me a joke", "Why don't scientists trust atoms? Because they make up everything!");
        botResponses.put("what is the meaning of life?", "42. But seriously, it's to find your own purpose and make the most of it!");
        botResponses.put("how old are you?", "I was created quite recently, so I'm quite young!");
        botResponses.put("who created you?", "I was created by a team of developers at OpenAI.");
        botResponses.put("what's your favorite color?", "As a bot, I don't have preferences, but I've heard blue is quite popular!");
        botResponses.put("where are you from?", "I'm from the digital world, a place full of codes and algorithms.");
        botResponses.put("what's the weather like?", "I can't check the weather right now, but I hope it's nice wherever you are!");
    }

    public static void startServer() {
        System.out.println("Chat server started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startClient() {
        try (Socket socket = new Socket("localhost", PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            Thread listener = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            listener.start();

            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                out.println(userInput);
                String response = getBotResponse(userInput);
                if (response != null) {
                    System.out.println("Bot: " + response); // Only print the bot's response on the client side
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getBotResponse(String userInput) {
        String sanitizedInput = userInput.toLowerCase().trim();
        return botResponses.getOrDefault(sanitizedInput, "Sorry, I don't understand that.");
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }
                String message;
                while ((message = in.readLine()) != null) {
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters) {
                            if (writer != out) { // Avoid sending the message back to the sender
                                writer.println(message);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }
    }
}
