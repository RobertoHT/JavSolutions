package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roberto on 05/06/17.
 */

public class Server extends Thread {
    private List<ClientThread> clients;
    private ServerSocket serverSocket = null;
    private boolean accept;
    private int IDs;

    Server() {
        int port = 2000;
        IDs = 1;
        accept = true;

        try {
            System.out.println("Server IP: " + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            System.err.println("UnknownHostException: " + e);
        }

        clients = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Error establishing connection");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Socket socket = null;
        ClientThread newClient;

        System.out.println("Welcome to Central Store!");
        System.out.println("Waiting for clients to connect...");

        while (accept) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            if (socket != null && !serverSocket.isClosed()){
                newClient = new ClientThread(socket, this, IDs++);
                newClient.start();
                clients.add(newClient);
            }
        }
    }

    public void removeClient(ClientThread clientClosed) {
        clients.remove(clientClosed);
        System.out.println("Client finished: #" + clientClosed.id);
    }

    public void closedConnection(){
        if (clients.size() >= 1){
            clients.forEach(ClientThread::closedConnection);
        }
        try {
            accept = false;
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closed connections");
            e.printStackTrace();
        }
    }
}
