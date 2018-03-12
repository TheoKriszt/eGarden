package fr.kriszt.theo.egarden.utils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by wizehunt on 12/03/18.
 */

public class Connexion {



    public static void sendPostRequest(String path, HashMap<String, String> postParams){
        URL url = null;
        HttpURLConnection client = null;

        try {
            url = new URL(path);

            client = (HttpURLConnection) url.openConnection();

            client.setRequestMethod("POST");

            for (String k : postParams.keySet()){
                client.setRequestProperty(k, postParams.get(k));
            }

            client.setDoOutput(true);

            OutputStream outputPost = new BufferedOutputStream(client.getOutputStream());

//        writeStream(outputPost);

            outputPost.flush();
            outputPost.close();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (client != null){
                client.disconnect();
            }
        }

    }

    public static boolean checkPort(int port) {
        return port > 0 && port <= 65535;
    }

    public static boolean checkIP(String ip, String errorMessage) {
        boolean retVal = true;

        if (ip.isEmpty()){
            errorMessage = "IP manquante";
            return false;
        }

        String[] matches = ip.split("\\.");

        if (matches.length != 4){
            retVal = false;
            errorMessage = "Format de l'adresse IPv4 invalide (xxx.xxx.xxx.xxx)";
        }

        for (String s : matches){
            try {
                int i = Integer.parseInt(s);
                if(i < 0 || i > 255){
                    retVal =  false;
                    errorMessage = "IP invalide : " + i;
                    break;
                }
            }catch (NumberFormatException e){
                retVal = false;
                errorMessage = "IP invalide : " + s;
                break;
            }

        }

        return retVal;
    }


}
