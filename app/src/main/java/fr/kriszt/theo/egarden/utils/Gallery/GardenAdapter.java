package fr.kriszt.theo.egarden.utils.Gallery;

import android.util.Log;

/**
 * Created by mike on 4/29/18.
 */

public class GardenAdapter
{   private static String TAG = "GardenAdapter";
    public String ImageURL;
    public String ImageTitle;

    public String getImageUrl() {

        return ImageURL;
    }

    public void setImageUrl(String imageServerUrl) {

        this.ImageURL = imageServerUrl;
    }

    public String getImageTitle() {

        return ImageTitle;
    }

    public void setImageTitle(String imagetitlename) {
        imagetitlename = imagetitlename.substring(0,imagetitlename.length()-4);

        String[] parts = imagetitlename.split("_");
        for(int i = 0 ; i < parts.length;i++)
        {
            Log.d(TAG,parts[i]);
        }
        this.ImageTitle = "";
        if(parts.length == 3)
        {
            this.ImageTitle +=parts[1] + "\n" +parts[2] ;
        }
        else if(parts.length == 2)
        {
            this.ImageTitle = "Actuel";
        }
    }

}