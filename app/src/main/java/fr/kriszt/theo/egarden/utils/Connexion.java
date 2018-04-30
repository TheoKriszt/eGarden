package fr.kriszt.theo.egarden.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by T.Kriszt on 12/03/18.
 */

public class Connexion {

    private static final String TAG = "eGardenConnexion";
    private static final int TIMEOUT_MS = 5000;
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

    public static synchronized Connexion O(Context context){
        if (port == null || address == null){
            throw new IllegalStateException(Connexion.class.getSimpleName() + "is not initialized, call O(Context context, String port, String address) first ");
        }
//        O = new Connexion();
        O.context = context;
        O.requestQueue.stop();

        O.requestQueue = Volley.newRequestQueue(context);
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


    public void sendJSON(String url, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, responseListener, errorListener);

        requestQueue.add(jsonObjectRequest);

//        requestQueue.addToRequestQueue(jsonObjectRequest);

// Access the RequestQueue through your singleton class.
//        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    public void sendPostRequest(final String url, @Nullable final HashMap<String, String> params, @Nullable Response.Listener<String> responseListener, @Nullable Response.ErrorListener errorListener){
        sendHttpRequest(Request.Method.POST, url, params, responseListener, errorListener);
    }

    public void sendGetRequest(final String url, @Nullable final HashMap<String, String> params, @Nullable Response.Listener<String> responseListener, @Nullable Response.ErrorListener errorListener){
        sendHttpRequest(Request.Method.GET, url, params, responseListener, errorListener);
    }

    public void sendHttpRequest(final int method, final String url, @Nullable final HashMap<String, String> params, @Nullable final Response.Listener<String> responseListener, @Nullable Response.ErrorListener errorListener){

        StringRequest stringRequest = new StringRequest(method , address + ":" + port  + url, responseListener, errorListener) {
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                String token = Security.getToken();
                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
                if (token != null){
                    headers.put("Authorization", "Bearer " + token);  // Authorization: Bearer <token>
                    return headers;
                }else return super.getHeaders();
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }


    /**
     * Télécharge l'image stockée dans /home/pi/egarden/images/imgUrl
     * @param imgUrl le nom du fichier
     * @param responseListener quoi faire en cas de succès
     * @param errorListener que faire en cas d'échec
     */
    public void downloadImage(String imgUrl, Response.Listener<Bitmap> responseListener, Response.ErrorListener errorListener) {

        boolean absolutePath = imgUrl.startsWith("http");
        String uri;

        if (!absolutePath){
            uri = address + ":" + port + "/" + imgUrl;
        } else {
            uri = imgUrl;
        }

        ImageRequest imageRequest = new ImageRequest(uri, responseListener, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, errorListener);

        imageRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(imageRequest);
    }

    public JSONObject decodeError(VolleyError error){
        NetworkResponse response = error.networkResponse;
        if (error instanceof ServerError && response != null) {
            try {
                String res = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                // Now you can use any deserializer to make sense of data
                JSONObject obj = new JSONObject(res);
                return obj;
//                System.out.println(obj.toString());
            } catch (UnsupportedEncodingException e1) {
                // Couldn't properly decode data to string
                e1.printStackTrace();
            } catch (JSONException e2) {
                // returned data is not JSONObject?
                e2.printStackTrace();
            }
        }
        return null;
    }
}
