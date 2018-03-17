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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;

import fr.kriszt.theo.egarden.utils.CheckForSDCard;
import fr.kriszt.theo.egarden.utils.DownloadTask;
import fr.kriszt.theo.egarden.utils.Urls;

public class DownloadClientPlantsImgs extends AppCompatActivity implements View.OnClickListener {
    private static Button downloadPdf, downloadDoc, downloadZip, downloadVideo, downloadMp3, openDownloadedFolder;
    private static final String TAG = "DownloadClientPlantsImgs Activity";
    private ImageView imgRcvedView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_client_plants_imgs);

        initViews();
        setListeners();
    }

    //Initialize al Views
    private void initViews() {
        downloadZip = (Button) findViewById(R.id.downloadZip);
        downloadVideo = (Button) findViewById(R.id.downloadVideo);
        openDownloadedFolder = (Button) findViewById(R.id.openDownloadedFolder);

    }

    //Set Listeners to Buttons
    private void setListeners() {
        downloadZip.setOnClickListener(this);
        downloadVideo.setOnClickListener(this);
        openDownloadedFolder.setOnClickListener(this);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onClick(View view) {
        //Before starting any download check internet connection availability
        if (ContextCompat.checkSelfPermission(DownloadClientPlantsImgs.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG , "WRITE EXTERNAL STORAGE PERMISSION NOT GRANTED");
            // Permission is not granted
            ActivityCompat.requestPermissions(DownloadClientPlantsImgs.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
            return;

        }
        switch (view.getId()) {
            case R.id.downloadZip:
                if (isConnectingToInternet())
                    new DownloadTask(DownloadClientPlantsImgs.this, downloadZip, Urls.downloadZipImgsUrl);
                else
                    Toast.makeText(DownloadClientPlantsImgs.this, "Oops!! There is no internet connection. Please enable internet connection and try again.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.downloadVideo:
                if (isConnectingToInternet())
                    new DownloadTask(DownloadClientPlantsImgs.this, downloadVideo, Urls.downloadVideoUrl);
                else
                    Toast.makeText(DownloadClientPlantsImgs.this, "Oops!! There is no internet connection. Please enable internet connection and try again.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.openDownloadedFolder:
                //openDownloadedFolder();
                imgRcvedView = findViewById(R.id.iv_element_test);
                imgRcvedView.setImageURI(Uri.parse(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS) + "/"
                        + Urls.downloadDirectory +"/"+"test_img.jpg"));
                break;

        }

    }

    //Open downloaded folder
    private void openDownloadedFolder() {
        //First check if SD Card is present or not
        if (CheckForSDCard.isSDCardPresent()) {

            //Get Download Directory File
            File apkStorage = new File(
                    Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS) + "/"
                            + Urls.downloadDirectory +"/");

            //If file is not present then display Toast
            if (!apkStorage.exists())
                Toast.makeText(DownloadClientPlantsImgs.this, "Right now there is no directory. Please download some file first.", Toast.LENGTH_SHORT).show();

            else {

                //If directory is present Open Folder

                /** Note: Directory will open only if there is a app to open directory like File Manager, etc.  **/

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS) + "/"
                        + Urls.downloadDirectory +"/");
                intent.setDataAndType(uri, "file/*");
                startActivity(Intent.createChooser(intent, "Open Download Folder"));
            }

        } else
            Toast.makeText(DownloadClientPlantsImgs.this, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

    }

    //Check if internet is present or not
    private boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


}
