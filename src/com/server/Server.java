package com.server;

import com.client.ClientHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private static final Logger logger = LogManager.getLogger(Server.class);
    static ArrayList<ClientHandler> clientHandlerArrayList = new ArrayList<>();
    private static final int maxClients = 2;
    static ArrayList<Message> messageHistory = new ArrayList<>();

    public static void main(String[] args) {
        File file = new File("cronologiaChat.json");
        try {
            if(file.exists()) {
                messageHistory = JsonUtils.readOnFileJson("cronologiaChat.json", Message.class);
            }
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server in ascolto sulla porta 5000");

            while (true) {
                Socket client = serverSocket.accept();
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

                ClientHandler clientHandler = new ClientHandler(client);

                if (tryAddClient(clientHandler)) {
                    writer.println("Benvenuto");
                    System.out.println("Client accettato");
                    clientHandler.start();
                } else {
                    writer.println("Massimo numero di client raggiunto. Connessione rifiutata");
                    logger.log(Level.INFO, "Massimo numero di client raggiunto. Connessione rifiutata.");
                    client.close();
                }
            }

        } catch (IOException e) {
            logger.log(Level.ERROR, "Errore durante l'avvio del server");
        }
    }

    public static synchronized boolean tryAddClient(ClientHandler clientHandler) {
        if (clientHandlerArrayList.size() < maxClients) {
            clientHandlerArrayList.add(clientHandler);
            return true;
        }
        return false;
    }

    public static synchronized void removeClient(ClientHandler obj) {
        clientHandlerArrayList.remove(obj);
    }

    public static synchronized void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler clientHandler : clientHandlerArrayList) {
            if (clientHandler != sender) {
                clientHandler.sendMessage(message);
            }
        }
    }
    public static synchronized void saveMessage(Message message) {
        messageHistory.add(message);
        JsonUtils.writeOnFileJson(messageHistory, "cronologiaChat.json");
    }
}