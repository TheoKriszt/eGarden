package fr.kriszt.theo.egarden.utils;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ContentHandler;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by T.Kriszt on 12/03/18.
 */

public class Connexion {

    private static final String TAG = "eGardenConnexion";
    private static final String prefixURL = "http://sparklife.freeboxos.fr:1880";
    private static String port = null;
    private static String address = null;


    private static Connexion O = null;
    public RequestQueue requestQueue;

//    private Connexion(Context context){
//        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
//    }

    public static synchronized Connexion O(Context context, String p, String addr){
        if (O == null){
            O = new Connexion();
        }
        O.requestQueue = Volley.newRequestQueue(context);
        port = p;
        address = (addr.startsWith("http://") ? "" : "http://") + addr;


        return O;
    }

    public static synchronized Connexion O(){
        if (O == null){
            throw new IllegalStateException(Connexion.class.getSimpleName() + "is not initialized, call O(Context context, String port, String address) first ");
        }
        return O;
    }



    /**
     * @deprecated
     */
    public static void sendPost(String path, HashMap<String, String> postParams){
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

    public void sendPostRequest(String url, final HashMap<String, String> params){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, address + ":" + port  + url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Réponse obtenue : ");
                System.out.println(response);
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                System.err.println("Erreur : ");
                System.err.println(error);
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
//                Map<String, String> payload = new HashMap<String, String>();
//                payload.put("username", "pi");
//                payload.put("password", "pi");
                return params;
            }
        };


        requestQueue.add(stringRequest);

//        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>()
//                {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // display response
//                        Log.d("Response", response.toString());
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.d("Error.Response", response);
//                    }
//                }
//        );

    }


}
