package com.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class ChatController {

    @FXML
    private ListView<String> listViewMessage;

    @FXML
    private TextArea textAreaMessage;

    private ClientChat clientChat;

    public void setClientChat(ClientChat clientChat) {
        this.clientChat = clientChat;
    }

    @FXML
    public void sendMessageInTheChat(ActionEvent event) {
        String message = textAreaMessage.getText().trim();

        if (message.isEmpty()) {
            return;
        }

        clientChat.sendMessage(message);

        if (!message.equalsIgnoreCase("exit")) {
            addMessageToListView("You: " + message);
        }

        textAreaMessage.clear();
    }
    public void addMessageToListView(String message) {
        listViewMessage.getItems().add(message);
    }

    @FXML
    public void closeConnection(ActionEvent event) {
        textAreaMessage.setDisable(true);
        if(clientChat != null) {
            clientChat.closeResources();
        }

    }
}
