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
    private static final String PATH = "src/inputFiles/";
    private Socket socket;
    private String ip, port;

    public void start() throws IOException {
        getIPandPort();
        setConnection();
        communication();
    }

    private void getIPandPort(){
        String msgIp = "Please write the IP number to connect to server:";
        String msgPort = "Please write the port number the server:";

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            do {
                System.out.println(msgIp);
                ip = reader.readLine();
                msgIp = "Please write a valid IP number:";
            } while (!validIPAddress(ip));

            do {
                System.out.println(msgPort);
                port = reader.readLine();
                msgPort = "Please write a valid port number";
            } while (!validPortNumber(port));
        } catch (IOException e){
            System.err.println("Error read line IP or port number");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void setConnection(){
        try {
            socket = new Socket(ip, Integer.parseInt(port));
        } catch (UnknownHostException e){
            System.err.println("Don't know about host " + ip);
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + ip + ":" + port + ". Check that the server is running.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void communication() throws IOException {
        PrintWriter out;
        BufferedReader in;
        BufferedReader userIn;

        System.out.println("Welcome to General Store!");

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            userIn = new BufferedReader(new InputStreamReader(System.in));

            System.out.println(in.readLine());

            Listen listen = new Listen(this, in);
            listen.start();

            String userInput;
            while ((userInput = userIn.readLine()) != null && !userInput.equals("CLOSE")){
                String inputs[] = userInput.split("\\s+");
                if (inputs.length > 1 && inputs[0].equals("SEND")){
                    send(inputs[1], out);
                }
            }
        } catch (IOException e) {
            System.err.println("Error read input");
            e.printStackTrace();
            System.exit(1);
        } finally {
            exit();
        }
    }

    public void exit() throws IOException{
        socket.close();
        System.exit(0);
    }

    private void send(String file, PrintWriter out){
        if (validateFile(PATH + file)){
            try {
                sendFile(file, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("File doesn't exist");
        }
    }

    private void sendFile(String file, PrintWriter out) throws IOException {
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
            System.err.println("I/O Exception: File not found");
            e.printStackTrace();
        } finally {
            out.println("ENDFILE");
            if(inFile != null){
                inFile.close();
            }
            System.out.println("File " + file + " send");
        }
    }

    public class Listen extends Thread{
        Client client;
        BufferedReader in;

        Listen(Client client, BufferedReader in) {
            this.client = client;
            this.in = in;
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = in.readLine()) != null){
                    if (line.equals("CLOSE")){
                        client.exit();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
