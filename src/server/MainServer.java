package server;

/**
 * Created by Roberto on 01/06/17.
 */

public class MainServer {
    public static void main(String args[]) {
        Server server = new Server();
        InputUserServer inputUser = new InputUserServer(server);

        server.start();
        inputUser.start();
    }
}
