package com.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

public class LoginController {
    private static final Logger logger = LogManager.getLogger(LoginController.class);

    @FXML
    private TextField textFieldUsername;

    @FXML
    private TextField textFieldIpServer;

    @FXML
    public void enterInChat(ActionEvent event) {
        String username = textFieldUsername.getText().trim();
        String ipServer = textFieldIpServer.getText().trim();

        if (username.isEmpty() || ipServer.isEmpty()) {
            logger.log(Level.ERROR, "Non inserito username e IP server");
            showAlert("Errore", "Inserisci username e IP server.");
            return;
        }

        if (!ClientConnection.isValidAddress(ipServer)) {
            logger.log(Level.ERROR, "IP non valido");
            showAlert("Errore", "IP non valido. Usa localhost oppure un IP tipo 127.0.0.1");
            return;
        }

        Socket clientSocket = ClientConnection.connectToServer(ipServer);

        if (clientSocket == null) {
            logger.log(Level.ERROR, "Impossibile connettersi al server.");
            showAlert("Errore", "Impossibile connettersi al server.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("schermataChat.fxml"));
            Scene chatScene = new Scene(loader.load());

            ChatController chatController = loader.getController();

            ClientChat clientChat = new ClientChat();
            boolean started = clientChat.startChat(clientSocket, username, chatController);

            if (!started) {
                logger.log(Level.ERROR, "Impossibile avviare la chat.");
                showAlert("Errore", "Impossibile avviare la chat.");
                return;
            }

            chatController.setClientChat(clientChat);
            chatController.addMessageToListView("Connected as: " + username);
            logger.log(Level.INFO, "Connesso come: " + username);

            Stage stage = (Stage) textFieldUsername.getScene().getWindow();
            stage.setScene(chatScene);
            stage.setTitle("Chat - " + username);
            stage.show();

        } catch (Exception e) {
            logger.log(Level.ERROR, "Impossibile aprire la schermata chat: " + e);
            showAlert("Errore", "Impossibile aprire la schermata chat.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
