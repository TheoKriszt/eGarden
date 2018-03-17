package fr.kriszt.theo.egarden.utils;

import android.os.Environment;

/**
 * Created by mike on 3/16/18.
 */

public class Urls {
    //String Values to be Used in App
    public static final String downloadDirectory = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS) + "/"+ "eGardenDownloads";
    public static final String mainImageUrl = "http://michaelcorp.zzzz.io";
    public static final String downloadZipImgsUrl = "http://michaelcorp.zzzz.io:1880/img/test_user";
    public static final String downloadVideoUrl = "http://androhub.com/demo/demo.mp4";
}
