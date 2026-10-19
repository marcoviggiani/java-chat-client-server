package com.client;

import com.server.ClientReceiver;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientChat {
    private static final Logger logger = LogManager.getLogger(ClientChat.class);

    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String name;
    private ClientReceiver receiver;

    public ClientChat() {}

    public boolean startChat(Socket clientSocket, String name, ChatController chatController) {
        this.clientSocket = clientSocket;
        this.name = name;

        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);

            writer.println(name);

            receiver = new ClientReceiver(reader, chatController);
            receiver.start();

            return true;

        } catch (IOException e) {
            logger.log(Level.ERROR, "Errore durante l'avvio della chat: "+ e);
            return false;
        }
    }

    public void sendMessage(String message) {
        if (writer != null && message != null && !message.trim().isEmpty()) {
            writer.println(message);
        }
    }

    public void closeResources() {
        if (writer != null) {
            logger.log(Level.INFO, "Chiusura del client writer");
            writer.close();
        }

        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                logger.log(Level.INFO, "Chiusura del client socket");
                clientSocket.close();
            }
        } catch (IOException e) {
            logger.log(Level.ERROR, "Errore durante chiusura del client socket: " + e);
        }
    }

    public String getName() {
        return name;
    }
}