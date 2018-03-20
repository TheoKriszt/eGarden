package fr.kriszt.theo.egarden;

/**
 * Created by mike on 3/17/18.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;

import fr.kriszt.theo.egarden.utils.CheckForSDCard;
import fr.kriszt.theo.egarden.utils.Connexion;
import fr.kriszt.theo.egarden.utils.DownloadTask;
import fr.kriszt.theo.egarden.utils.Urls;
import fr.kriszt.theo.egarden.utils.ZipManager;

public class DownloadClientPlantsImgs extends AppCompatActivity implements View.OnClickListener {
    private static Button downloadZip, openDownloadedFolder,unzipFile;
    private static final String TAG = "DownloadPlantsImgs";
    private ImageView imgRcvedView ;

//    Attributs to display large set of data images
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_client_plants_imgs);

        initViews();
        setListeners();
    }

    //Initialize all Views
    private void initViews() {
        downloadZip = findViewById(R.id.downloadZip);
        unzipFile = findViewById(R.id.unZipFolder);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    //Set Listeners to Buttons
    private void setListeners() {
        downloadZip.setOnClickListener(this);

        unzipFile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // TODO : refactorer dans CheckForSDCard et renommer en SDUtils
        if (ContextCompat.checkSelfPermission(DownloadClientPlantsImgs.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG , "WRITE EXTERNAL STORAGE PERMISSION NOT GRANTED");
            // Permission is not granted
            ActivityCompat.requestPermissions(DownloadClientPlantsImgs.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
            return;

        }
        //Before starting any download check internet connection availability
        switch (view.getId()) {
            case R.id.downloadZip:
                if (Connexion.O().isOnline())
                {
                    Log.e(TAG , "Download started from "+Urls.downloadZipImgsUrl+"...");
                    new DownloadTask(DownloadClientPlantsImgs.this, downloadZip, Urls.downloadZipImgsUrl);

                }
                else
                    Toast.makeText(DownloadClientPlantsImgs.this, "Oops!! There is no internet connection. Please enable internet connection and try again.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.unZipFolder:
                Log.e(TAG , "Unzip started ...");
                ZipManager.unzip(DownloadClientPlantsImgs.this,Urls.downloadDirectory + "/" + "plants_images.zip",
                        Urls.downloadDirectory + "/" + "plants_images");
                break;
        }

    }

}
