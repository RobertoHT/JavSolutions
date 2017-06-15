package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Roberto on 05/06/17.
 */

public class InputUserServer extends Thread {
    private static final String PATH = "src/outputFiles/";
    private Server server;
    private boolean accept;

    InputUserServer(Server server) {
        this.server = server;
        this.accept = true;
    }

    @Override
    public void run() {
        System.out.println("Enter your instructions");
        BufferedReader userIn = null;

        try{
            userIn = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            while (accept && (userInput = userIn.readLine()) != null){
                switch (userInput) {
                    case "REPORT":
                        report();
                        break;
                    case "DELETE":
                        delete();
                        break;
                    case "CLOSE":
                        accept = false;
                        closedConnections();
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error read line.");
            e.printStackTrace();
        } finally {
            try {
                if (userIn != null) {
                    userIn.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void report(){
        File[] files = getFiles();
        if (files != null && files.length > 0) {
            List<String> items = new ArrayList<>();
            Float amount = 0f;
            for(File name:files){
                try {
                    FileReader reader = new FileReader(name);
                    JSONParser parser = new JSONParser();
                    JSONObject object = (JSONObject) parser.parse(reader);
                    JSONObject structure = (JSONObject) object.get("invoice");
                    JSONArray array = (JSONArray) structure.get("products");
                    for (Object anArray : array) {
                        JSONObject invoice = (JSONObject) anArray;
                        String key = invoice.get("name").toString();
                        items.add(key);
                    }
                    amount += Float.parseFloat(structure.get("total").toString());
                } catch (ParseException | IOException e) {
                    System.err.println("Error parsing JSON");
                    e.printStackTrace();
                }
            }

            System.out.println(String.format("Total amount:  %.2f", amount));

            Map<String, Long> itemsCount = items
                                                .stream()
                                                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));
            int maxCount = itemsCount
                                    .entrySet()
                                    .stream()
                                    .max(Map.Entry.comparingByValue()).map(Map.Entry::getValue)
                                    .get()
                                    .intValue();
            itemsCount
                    .entrySet()
                    .stream().filter(s -> s.getValue() == maxCount)
                    .collect(Collectors.toList())
                    .forEach(a -> System.out.println("The most popular item: " + a.getKey()));
        }
    }

    private void delete(){
        Arrays.stream(getFiles()).forEach(File::delete);
        System.out.println("Files deleted");
    }

    private void closedConnections(){
        server.closedConnection();
        System.out.println("Closed clients connections");
    }

    private File[] getFiles(){
        File fileDir = new File(PATH);
        return fileDir.listFiles((dir, name) -> name.endsWith(".txt"));
    }
}
