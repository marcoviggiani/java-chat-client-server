package com.client;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientConnection {
    private static final int port = 5000;
    private static final int retryDelay = 3000;
    private static final Logger logger = LogManager.getLogger(ClientConnection.class);

    public ClientConnection(){}

    public static Socket connectToServer(String ipServer) {
        if (!isValidAddress(ipServer)) {
            return null;
        }

        while (true) {
            Socket client = tryConnection(ipServer);

            if (client == null) {
                logger.log(Level.ERROR, "Impossibile raggiungere il server!");
                return null;
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                String serverMsg = reader.readLine();

                if (serverMsg == null) {
                    client.close();
                    return null;
                }

                // Se c'è posto
                if (serverMsg.equalsIgnoreCase("Benvenuto")) {
                    return client;
                }

                // Se non c'è posto
                if (serverMsg.equalsIgnoreCase("Massimo numero di client raggiunto. Connessione rifiutata")) {
                    Socket waitClient = waitForSlot(ipServer);
                    if (waitClient != null) {
                        return waitClient;
                    }
                }

                client.close();
                return null;

            } catch (IOException e) {
                logger.log(Level.ERROR, "Errore durante la lettura del messaggio del server", e);
                try {
                    client.close();
                } catch (IOException _) {}
                return null;
            }
        }
    }

    public static boolean isValidAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }

        if (address.equalsIgnoreCase("localhost")) {
            return true;
        }

        String[] parts = address.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        for (String part : parts) {
            try {
                if (part.isEmpty()) {
                    return false;
                }

                int num = Integer.parseInt(part);

                if (num < 0 || num > 255) {
                    return false;
                }

            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }

    public static Socket tryConnection(String ipServer) {
        try {
            return new Socket(ipServer, port);
        } catch (IOException e) {
            return null;
        }
    }

    private static Socket waitForSlot(String ipServer) {
        while (true) {
            try {
                Thread.sleep(retryDelay);

                Socket retryClient = tryConnection(ipServer);

                if (retryClient == null) {
                    return null;
                }

                BufferedReader retryReader = new BufferedReader(new InputStreamReader(retryClient.getInputStream()));
                String retryMsg = retryReader.readLine();

                if (retryMsg == null) {
                    retryClient.close();
                    return null;
                }

                if (retryMsg.equalsIgnoreCase("Benvenuto")) {
                    return retryClient;
                }

                retryClient.close();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            } catch (IOException e) {
                logger.log(Level.ERROR, "Errore durante la riconnessione", e);
                return null;
            }
        }
    }
}