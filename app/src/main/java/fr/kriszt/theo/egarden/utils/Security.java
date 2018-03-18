package fr.kriszt.theo.egarden.utils;

import android.net.sip.SipSession;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Created by T.Kriszt on 12/03/18.
 */

public class Security {

    private static String auth_token = null;

    private static String LOGIN_URI = "/login";

    public static String md5(String s) {
        // Create MD5 Hash
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        digest.update(s.getBytes());
        byte messageDigest[] = digest.digest();

        // Create Hex String
        StringBuffer hexString = new StringBuffer();
        for (int i=0; i<messageDigest.length; i++)
            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
        return hexString.toString();
    }

    public static boolean requestToken(String username, String password, final Response.Listener<String> responseListener, Response.ErrorListener errorListener){

        HashMap<String, String> postParams = new HashMap<>();
        postParams.put("username", username);
        postParams.put("password", md5(password));

        Response.Listener<String> responseWrapper = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                auth_token = response;
                responseListener.onResponse(response);
            }
        };

        Connexion.O().sendPostRequest(LOGIN_URI, postParams, responseWrapper, errorListener);

        return true;
    }



}
