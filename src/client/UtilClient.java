package client;

import java.io.File;

/**
 * Created by Roberto on 31/05/17.
 */

public class UtilClient {
    public static boolean validateFile(String filePath){
        File file = new File(filePath);

        return (file.exists() && !file.isDirectory());
    }

    public static boolean validIPAddress(String ip){
        final String IP_PATTERN =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

        return ip.matches(IP_PATTERN);
    }

    public static boolean validPortNumber(String port){
        if(port.matches("^[0-9]+$")){
            int p = Integer.parseInt(port);
            return (p >= 1 && p <= 65535);
        }else{
            return false;
        }
    }
}
