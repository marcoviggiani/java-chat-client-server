package com.server;

import com.client.ChatController;
import javafx.application.Platform;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientReceiver extends Thread {
    private BufferedReader reader;
    private String str = "";
    private ChatController chatController;
    private static final Logger logger = LogManager.getLogger(ClientReceiver.class);

    public ClientReceiver() {}

    public ClientReceiver(BufferedReader reader, ChatController chatController) {
        this.reader = reader;
        this.chatController = chatController;
    }

    @Override
    public void run() {
        try {
            while (true) {
                str = reader.readLine();

                if (str == null) {
                    Platform.runLater(() -> chatController.addMessageToListView("Disconnected from server."));
                    break;
                }

                String finalStr = str;
                Platform.runLater(() -> chatController.addMessageToListView(finalStr));
            }

        } catch (IOException e) {
            logger.log(Level.INFO, "Connessione chiusa");
            Platform.runLater(() -> chatController.addMessageToListView("Connessione chiusa."));
        }
    }
}