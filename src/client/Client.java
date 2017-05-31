package client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import static client.UtilClient.validIPAddress;
import static client.UtilClient.validPortNumber;
import static client.UtilClient.validateFile;

/**
 * Created by Roberto on 31/05/17.
 */

public class Client {
    private String ip, port;
    private Socket socket;
    private static final String PATH = "src/inputFiles/";

    Client() {}

    public void start() throws IOException {
        getIPandPort();
        setConnection();
        communication();
    }

    private void getIPandPort(){
        System.out.println("Welcome to General Store!");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            do {
                System.out.println("Please write the IP number to connect to server:");
                ip = reader.readLine();
            } while (!validIPAddress(ip));
            System.out.println("IP: "+ip);

            do {
                System.out.println("Please write the port number the server:");
                port = reader.readLine();
            } while (!validPortNumber(port));
            System.out.println("Port: "+port);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setConnection(){
        try {
            socket = new Socket(ip, Integer.parseInt(port));
        } catch (UnknownHostException e){
            System.err.println("Don't know about host " + ip);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + ip + ":" + port + ". Check that the server is running.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void communication() throws IOException {
        PrintWriter out = null;
        BufferedReader in = null;
        BufferedReader userIn = null;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            userIn = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            do {
                userInput = userIn.readLine();
                String inputs[] = userInput.split("\\s+");
                if (inputs.length > 1 && inputs[0].equals("send")){
                    if (validateFile(PATH + inputs[1])){
                        sendFile(inputs[1], out, in);
                    }else{
                        System.out.println("File doesn't exist");
                    }
                }
            } while (!userInput.equals("close"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null){
                out.close();
            }
            if (in != null){
                in.close();
            }
            if (userIn != null){
                userIn.close();
            }
        }
    }

    private void sendFile(String file, PrintWriter out, BufferedReader in) throws IOException {
        BufferedReader inFile = null;
        out.println("NEWFILE");
        out.println(file);
        try {
            inFile = new BufferedReader(new FileReader(PATH + file));
            String inputFile;
            while((inputFile = inFile.readLine()) != null){
                out.println(inputFile);
            }
        } catch (FileNotFoundException e) {
            System.err.println("I/O Exception");
            e.printStackTrace();
            System.exit(1);
        } finally {
            out.println("ENDFILE");
            if(inFile != null){
                inFile.close();
            }
        }
    }
}
