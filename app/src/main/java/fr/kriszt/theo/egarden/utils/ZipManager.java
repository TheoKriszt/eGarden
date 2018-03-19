package fr.kriszt.theo.egarden.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by mike on 3/17/18.
 */

public class ZipManager {
    private static String TAG = "ZipManager";
    public static void unzip(Context context, String zipFile, String extractFolder) {

        try {
//            CreateDir();
            int BUFFER = 4096;
            File file = new File(zipFile);
            Log.e(TAG , "Unziping " + file.toString());
            ZipFile zip = new ZipFile(file);
            String newPath = extractFolder;

            new File(newPath).mkdir();
            Enumeration zipFileEntries = zip.entries();

            // Process each entry
            while (zipFileEntries.hasMoreElements()) {
                // grab a zip file entry

                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();

                currentEntry = currentEntry.replace('\\', '/');
                File destFile = new File(newPath, currentEntry);
                // destFile = new File(newPath, destFile.getName());
                File destinationParent = destFile.getParentFile();

                // create the parent directory structure if needed
                destinationParent.mkdirs();

                if (!entry.isDirectory()) {
                    BufferedInputStream is = new BufferedInputStream(
                            zip.getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER];

                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest = new BufferedOutputStream(fos,
                            BUFFER);

                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }
            }
            zip.close();
            Log.e(TAG, "File " + zipFile + " unziped");
            Toast.makeText(context, "Sucess Unziping "+ zipFile , Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "" + e.getMessage());
            Toast.makeText(context, "Unziping failed" , Toast.LENGTH_SHORT).show();
        }

    }
}
