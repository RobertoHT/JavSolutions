package server;

import java.io.*;
import java.net.Socket;

/**
 * Created by Roberto on 01/06/17.
 */

public class ClientThread extends Thread {
    private static final String PATH = "src/outputFiles/";
    private Server server;
    private Socket socket;
    private PrintWriter out;
    public int id;
    private boolean fileName = false, fileData = false;

    ClientThread(Socket clientSocket, Server server, int id) {
        this.socket = clientSocket;
        this.server = server;
        this.id = id;
    }

    public void run() {
        InputStream inp;
        BufferedReader in;

        try {
            inp = socket.getInputStream();
            in = new BufferedReader(new InputStreamReader(inp));
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }
        System.out.println("User#"+ id +" connected.");
        out.println("Welcome user#" + id);
        out.flush();

        try {
            String line;
            String name = "", file = "";
            int flow;
            while ((line = in.readLine()) != null) {
                flow = determinateFlow(line);

                switch (flow){
                    case 2:
                        writeFile(name, file);
                        name = "";
                        file = "";
                        break;
                    case 3:
                        name = line;
                        break;
                    case 4:
                        file += line + "\n";
                        break;
                }
            }
            socket.close();
            server.removeClient(this);
            server = null;
        } catch (IOException e) {
            System.err.println("Error read line.");
            e.printStackTrace();
        }
    }

    public void closedConnection(){
        out.println("CLOSE");
        out.flush();
    }

    private int determinateFlow(String line){
        if (line.equals("NEWFILE")){
            fileName = true;
            return 1;
        }
        else if (line.equals("ENDFILE")){
            fileData = false;
            return 2;
        }
        else if (fileName){
            fileName = false;
            fileData = true;
            return 3;
        }
        else if (fileData){
            return 4;
        }
        else{
            return 0;
        }
    }

    private void writeFile(String name, String fileData) throws IOException {
        File directory;
        BufferedWriter output = null;
        try {
            directory = new File(PATH);
            if(!directory.exists()){
                directory.mkdirs();
            }
            output = new BufferedWriter(new FileWriter(PATH + name));
            output.write(fileData);
        } catch (IOException e) {
            System.err.println("I/O Exception: write file");
            e.printStackTrace();
        } finally {
            System.out.println("File " + name + " received from user#"+ id);
            if (output != null){
                output.close();
            }
        }
    }
}
