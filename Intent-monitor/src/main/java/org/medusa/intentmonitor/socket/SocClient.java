package org.medusa.intentmonitor.socket;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.medusa.intentmonitor.ErrorHandler;
import org.medusa.intentmonitor.controllers.MainScreenController;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class SocClient {

    private static SocClient instance;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final MainScreenController msgQueue;
    private final BooleanProperty connected = new SimpleBooleanProperty(false);

    public SocClient(MainScreenController populateTo){
        this.msgQueue = populateTo;
        instance = this;
    }

    public void connect(String serverAddress, int port) throws IOException  {
        InetAddress serverInetAddress = InetAddress.getByName(serverAddress);
        socket = new Socket(serverInetAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        startCommunication();
        setConnected(true);
    }

    private void startCommunication() throws IOException {
        Thread readerThread = new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    msgQueue.addItem(message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                closeSocket();
            }
        });
        readerThread.start();
    }

    public void closeSocket() {
        try {
            if (connected.get()) {
                sendMessage("close"); // Send the "close" command to the server
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
                setConnected(false);
                System.out.println("Socket closed");
            }
        } catch (IOException e) {
            ErrorHandler.handleException(e);
        }
    }

    public void sendMessage(String message) {
        if (isConnected()) {
            out.println(message);
        }
    }

    public boolean connected(){
        return socket != null && socket.isConnected();
    }

    // Getter for connected property
    public BooleanProperty connectedProperty() {
        return connected;
    }

    // Getter for connected state
    public boolean isConnected() {
        return connected.get();
    }

    // Setter for connected state
    public void setConnected(boolean isConnected) {
        connected.set(isConnected);
    }

    public static synchronized SocClient getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SocClient instance has not been initialized.");
        }
        return instance;
    }

}
