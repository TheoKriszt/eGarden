package fr.kriszt.theo.egarden;

/**
 * Created by mike on 3/16/18.
 */


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import fr.kriszt.theo.egarden.utils.Connexion;

public class RequestImagePlant extends AppCompatActivity {
//    private Context mContext;
//    private Activity mActivity;

//    private CoordinatorLayout mCLayout;
    private ImageView mImageView;
//    private ImageView mImageViewInternal;
    //private String mImageURLString = "http://michaelcorp.zzzz.io/photos/";
    private String mImageURLString = "raspi.jpg"; // Câblé automatiquement vers  "/home/pi/egarden/images/<nom_image.jpg>" par NodeRed via [GET]/img/<nom_image.jpg>
//    private String mImageURLString = "http://michaelcorp.zzzz.io:1880/img/test_user";
    //private String mImageURLString = "https://cdn.nurserylive.com/images/stories/virtuemart/product/nurserylive-top-five-plants-to-attract-money.jpg";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view);

        // Get the widget reference from XML layout
//        mCLayout = findViewById(R.id.coordinator_layout);
        Button mButtonDo = findViewById(R.id.btn_do);
        mImageView = findViewById(R.id.iv);


        // Set a click listener for button widget
        mButtonDo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                downloadImage();
            }
        });
    }

    private void downloadImage() {
        Connexion.O(this, "1880", "sparklife.freeboxos.fr"); //TODO : virer quand cablé derrière MainActivity

        Connexion.O().downloadImage(mImageURLString, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Toast.makeText(RequestImagePlant.this, "Image téléchargée", Toast.LENGTH_SHORT).show();
                mImageView.setImageBitmap(response);

                // Save this downloaded bitmap to internal storage
                //Uri uri = saveImageToInternalStorage(response);

                // Display the internal storage saved image to image view
                //mImageViewInternal.setImageURI(uri);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RequestImagePlant.this, "Erreur de telechargement pour l'image", Toast.LENGTH_SHORT).show();
//                Snackbar.make(mCLayout, "Error", Snackbar.LENGTH_LONG).show();

            }
        });

    }

    // Custom method to save a bitmap into internal storage
    protected Uri saveImageToInternalStorage(Bitmap bitmap) {
        // Initialize ContextWrapper
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());

        // Initializing a new file
        // The bellow line return a directory in internal storage
        File file = wrapper.getDir("Images", MODE_PRIVATE);

        // Create a file to save the image
        file = new File(file, "UniqueFileName" + ".jpg");

        try {
            // Initialize a new OutputStream
            OutputStream stream = null;

            // If the output file exists, it can be replaced or appended to it
            stream = new FileOutputStream(file);

            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            // Flushes the stream
            stream.flush();

            // Closes the stream
            stream.close();

        } catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }

        // Parse the gallery image url to uri
        Uri savedImageURI = Uri.parse(file.getAbsolutePath());

        // Return the saved image Uri
        return savedImageURI;
    }
}
