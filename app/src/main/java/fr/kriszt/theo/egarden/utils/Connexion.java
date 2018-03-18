package fr.kriszt.theo.egarden.utils;

import android.app.Activity;
import android.content.Context;
import android.icu.text.LocaleDisplayNames;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

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
    private Context context;
    private static String port = null;
    private static String address = null;


    private static Connexion O = null;
    public RequestQueue requestQueue;



    public static synchronized Connexion O(Context context, String p, String addr){
        if (O == null){
            O = new Connexion();
        }
        O.context = context;
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

    public  boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        Log.d(TAG, "isOnline: "  + (null != netInfo ? netInfo.getExtraInfo() : "False"));
        return netInfo != null && netInfo.isConnectedOrConnecting();
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

    public void sendPostRequest(final String url, final HashMap<String, String> params){

        sendPostRequest(url, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        });

    }

    public void sendPostRequest(final String url, final HashMap<String, String> params, Response.Listener<String> responseListener){

        sendPostRequest(url, params, responseListener, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error.getMessage());
            }
        });

    }

    public void sendPostRequest(final String url, final HashMap<String, String> params, Response.Listener<String> responseListener, Response.ErrorListener errorListener){

        Log.d(TAG, "sendPostRequest: "  +address + ":" + port  + url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, address + ":" + port  + url, responseListener, errorListener) {
            protected Map<String, String> getParams() {
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }



}
