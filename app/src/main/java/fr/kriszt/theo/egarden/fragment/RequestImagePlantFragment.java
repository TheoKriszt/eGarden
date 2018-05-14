package fr.kriszt.theo.egarden.fragment;

/**
 * Created by mike on 3/16/18.
 */


//import android.app.Fragment;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import fr.kriszt.theo.egarden.R;
import fr.kriszt.theo.egarden.utils.Connexion;

import static android.content.Context.MODE_PRIVATE;

/**
 * @deprecated
 */
public class RequestImagePlantFragment extends Fragment {

    private static final String TAG = "RequestImagePlant";
    private ImageView mImageView;
//    private String mImageURLString = "snapshot";
    // OUTDATED // Câblé automatiquement vers  "/home/pi/egarden/images/<nom_image.jpg>" par NodeRed via [GET]/img/<nom_image.jpg>
    private String mImageURLString = "http://michaelcorp.zzzz.io:1880/garden/observe";

    public RequestImagePlantFragment(){
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the widget reference from XML layout
//        mCLayout = findViewById(R.id.coordinator_layout);
        Button mButtonDo = view.findViewById(R.id.btn_do);
        mImageView = view.findViewById(R.id.iv);


        // Set a click listener for button widget
        mButtonDo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                downloadImage();
            }
        });

    }

    private void downloadImage() {
        Connexion.O().downloadImage(mImageURLString, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                mImageView.setImageBitmap(response);

                // Save this downloaded bitmap to internal storage
                //Uri uri = saveImageToInternalStorage(response);

                // Display the internal storage saved image to image view
                //mImageViewInternal.setImageURI(uri);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + Connexion.O().decodeError(error));
//                Snackbar.make(mCLayout, "Error", Snackbar.LENGTH_LONG).show();

            }
        });

    }

    // Custom method to save a bitmap into internal storage
    protected Uri saveImageToInternalStorage(Bitmap bitmap) {
        // Initialize ContextWrapper
        ContextWrapper wrapper = new ContextWrapper(getView().getContext());

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

    @Override
    public void onStop() {
        super.onStop();
        Connexion.cancellAll();
    }
}
