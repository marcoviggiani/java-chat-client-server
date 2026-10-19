package com.client;

import com.server.Message;
import com.server.Server;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;


public class ClientHandler extends Thread {
    private Socket client;
    private BufferedReader reader;
    private PrintWriter writer;
    private String name;
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    public ClientHandler() {}

    public ClientHandler(Socket client) {
        this.client = client;

        try {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new PrintWriter(client.getOutputStream(), true); // autoFlush = true
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            name = reader.readLine();
            if (name == null || name.trim().isEmpty()) {
                name = "Unknown";
            }

            System.out.println(name + " joined the chat.");
            Server.broadcastMessage(name + " joined the chat.", this);
            String str = "";
            while(true){
                str = reader.readLine();

                // Se il client chiude improvvisamente
                if (str == null) {
                    logger.log(Level.INFO, "Chiusura improvvisa del client!");
                    break;
                }

                Calendar calendar = Calendar.getInstance();
                int day  =calendar.get(Calendar.DATE);
                int month = calendar.get(Calendar.MONTH)+1;
                int year = calendar.get(Calendar.YEAR);
                String date = day + "/" + month + "/" + year;

                Server.broadcastMessage(name + ": " + str, this);
                Server.saveMessage(new Message(this.name, str, date));

            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            Server.removeClient(this);
            if (name != null) {
                Server.broadcastMessage(name + " left the chat.", this);
                logger.log(Level.INFO, name + " left the chat.");
            }

            try {
                if (reader != null) {
                    reader.close();
                }

                if (writer != null) {
                    writer.close();
                }

                if (client != null && !client.isClosed()) {
                    client.close();
                }

            } catch (IOException e) {
                logger.log(Level.ERROR, e);
                System.out.println(e.getMessage());
            }
        }
    }

    public void sendMessage(String message) {
        if (writer != null) {
            writer.println(message);
        }
    }
}