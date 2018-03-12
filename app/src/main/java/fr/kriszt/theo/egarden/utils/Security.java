package fr.kriszt.theo.egarden.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Created by wizehunt on 12/03/18.
 */

public class Security {

    private static String auth_token = null;

    private static String LOGIN_URI = "http://sparklife.freeboxos.fr:1880/login";

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

    public static void requestToken(String username, String password){

        HashMap<String, String> postParams = new HashMap<>();
        postParams.put("username", username);
        postParams.put("password", md5(password));
        Connexion.sendPostRequest(LOGIN_URI, postParams);
    }



}
