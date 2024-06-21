import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

public class Main {
    private static final int PORT = 12345;
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Map<String, String> botResponses = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("chatbot.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LOGGER.info("Chatbot application starting...");
        initializeBotResponses();
        new Thread(Main::startServer).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted while waiting for server to start", e);
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
        LOGGER.info("Chat server started on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("New client connected: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error in server socket", e);
        }
    }

    public static void startClient() {
        LOGGER.info("Starting client...");
        try (Socket socket = new Socket("localhost", PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            LOGGER.info("Client connected to server");

            Thread listener = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                        LOGGER.info("Received message: " + message);
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error in client listener", e);
                }
            });

            listener.start();

            String userInput;
            while ((userInput = consoleInput.readLine()) != null) {
                out.println(userInput);
                LOGGER.info("Sent message: " + userInput);
                String response = getBotResponse(userInput);
                if (response != null) {
                    System.out.println("Bot: " + response);
                    LOGGER.info("Bot response: " + response);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error in client", e);
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
                    LOGGER.info("Received message from client: " + message);
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters) {
                            if (writer != out) {
                                writer.println(message);
                                LOGGER.info("Forwarded message to other client");
                            }
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Error in client handler", e);
            } finally {
                try {
                    socket.close();
                    LOGGER.info("Client disconnected: " + socket.getInetAddress());
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Error closing client socket", e);
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
            }
        }
    }
}
