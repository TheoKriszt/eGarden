package fr.kriszt.theo.egarden.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by T.Kriszt on 12/03/18.
 * Singleton utility class providing access to the garden's server
 * once {@link Security} provides a valid JWT <br/>
 * See {@see https://jwt.io/}
 */

public class Connexion {

    private static final String TAG = "eGardenConnexion";
    private static final int TIMEOUT_MS = 5000;
    private Context context;
    private static String port = null;
    private static String address = null;


    private static Connexion O = null;
    private RequestQueue requestQueue;

    /**
     *
     * @param context current {@link Context} for Volley {@link Volley}
     * @param p the server's access port
     * @param addr IPv4 or plain text domain name where the server is reachable
     * @return Singleton instance of Connexion utility
     */
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

    /**
     * @param context {@link Context} current context for {@link Volley}
     * @return Singleton instance of Connexion utility
     * @throws IllegalStateException if instance wasn't properly initialized with {@link Connexion O(Activity activity)} beforehand
     */
    public static synchronized Connexion O(Context context){
        if ((port == null) || (address == null)){
            throw new IllegalStateException(Connexion.class.getSimpleName() + "is not initialized, call O(Context context, String port, String address) first ");
        }
        O.context = context;
        O.requestQueue.stop();

        O.requestQueue = Volley.newRequestQueue(context);
        return O;
    }

    /**
     * @return Singleton instance of Connexion utility
     * @throws IllegalStateException if instance wasn't properly initialized with {@link Connexion O(Activity activity)} beforehand
     */
    public static synchronized Connexion O(){
        if (O == null){
            throw new IllegalStateException(Connexion.class.getSimpleName() + "is not initialized, call O(Context context, String port, String address) first ");
        }
        return O;
    }

    public  boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        Log.d(TAG, "isOnline: "  + (null != netInfo ? netInfo.getExtraInfo() : "False"));
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     *
     * @param url relative url from the server's base url, ex : {@code /weather}
     * @param params HashMap of key/values containing post params
     * @param responseListener callback on what to do with a successful response
     * @param errorListener callback on what to do whith an error, includes a 403 "forbidden" HTTP status
     */
    public void sendPostRequest(final String url, @Nullable final HashMap<String, String> params, @Nullable Response.Listener<String> responseListener, @Nullable Response.ErrorListener errorListener){
        sendHttpRequest(Request.Method.POST, url, params, responseListener, errorListener);
    }

    /**
     * @param url relative url from the server's base url, ex : {@code /weather}
     * @param responseListener callback on what to do with a successful response
     * @param errorListener callback on what to do whith an error, includes a 403 "forbidden" HTTP status
     * @see Security
     */
    public void sendGetRequest(final String url, @Nullable Response.Listener<String> responseListener, @Nullable Response.ErrorListener errorListener){
        sendHttpRequest(Request.Method.GET, url, null, responseListener, errorListener);
    }

    /**
     * @param method {@link Request.Method} (POST | GET)
     * @param url relative url from the server's base url, ex : {@code /weather}
     * @param params HashMap of key/values containing post params
     * @param responseListener callback on what to do with a successful response
     * @param errorListener callback on what to do whith an error, includes a 403 "forbidden" HTTP status
     * Automaticallly adds the JWT token as an authorization HTTP header
     */
    private void sendHttpRequest(final int method, final String url, @Nullable final HashMap<String, String> params, @Nullable final Response.Listener<String> responseListener, @Nullable Response.ErrorListener errorListener){

        StringRequest stringRequest = new StringRequest(method , address + ":" + port  + url, responseListener, errorListener) {
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = Security.getToken();
                Map<String, String> headers = new HashMap<>();

                if (token != null){
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }else return super.getHeaders();
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_MS,                                     // give it some time for heavy requests
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }


    /**
     * Downloads given picture stored in server's pictures folder
     * @param imgUrl picture file name
     * @param responseListener what to do on a success
     * @param errorListener what to do on a failure
     */
    public void downloadImage(String imgUrl, Response.Listener<Bitmap> responseListener, Response.ErrorListener errorListener) {

        boolean absolutePath = imgUrl.startsWith("http");
        String uri;

        if (!absolutePath){
            uri = getServerURL() + imgUrl;
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
                return new JSONObject(res);
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

    /**
     * Cancels all pending requests in the request queue
     *
     * <p>Some requests may be on their way back when the users changes the recipient context to another one
     * Upon receiving such a "lost" request, the app will crash
     * To prevent that, call this method every time you close/destroy an activity or a fragment</p>
     */
    public static void cancellAll() {
        O.requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    /**
     * The server-side timelapses Uri may change depending on where they are stored
     * @param videoFile the video file name, usualy YYYY-MM-DD.mp4
     * @param feed {@link String }(direct | day | week)
     * @return the proper Uri to start downloading the video
     */
    public Uri getTimelapseURI(String videoFile, String feed) {
        if (videoFile == null) return null;
        String path  = getServerURL() + "timelapse/" + feed + "/" +  videoFile;
        return Uri.parse(path);
    }

    /**
     * Calls the download manager to asynchronously download the given picture or video
     * Stores the file in the general-purpose Downloads folder
     * @param uri the source Uri
     * @param filename the file name under wich to store the file on the local storage
     * @param title the optionnal name to display chen downloading the file
     */
    public void downloadFile(Uri uri, String filename, @Nullable String title){

        // Create request for android download manager
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setTitle(title)
                .setDescription(filename)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, filename);
        assert downloadManager != null;
        downloadManager.enqueue(request);
    }

    public String getServerURL()
    {
        return address + ":" + port + "/";
    }
}