package fr.kriszt.theo.egarden.utils;

/**
 * Created by mike on 3/17/18.
 */
import android.os.Environment;

public class CheckForSDCard {
    //Check If SD Card is present or not method
    public static boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(

                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }
}