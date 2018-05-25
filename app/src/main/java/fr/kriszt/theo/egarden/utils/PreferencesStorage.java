package fr.kriszt.theo.egarden.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import fr.kriszt.theo.egarden.R;

/**
 * Created by T.Kriszt on 12/03/18.
 * Singleton preferences manager
 * Keeps Last valid credentials entered
 */

public class PreferencesStorage {

    private static final String TAG = "Preferences";
    private SharedPreferences sharedPref;
    private Context context;
    private static PreferencesStorage O = null;

    private PreferencesStorage(Activity activity){
        context = activity.getApplicationContext();

        String creds = String.valueOf(R.string.credentials_preference_file);
        sharedPref = context.getSharedPreferences(creds, Context.MODE_PRIVATE);

        if (sharedPref == null) {
            Log.wtf(TAG, "sharedPref is NULL !"); // shouldn't happen
        }
    }

    /**
     * @param activity current context
     * @return PreferencesStorage singleton instance
     */
    public static PreferencesStorage O(Activity activity){
        if (O == null){
            O = new PreferencesStorage(activity);
        }
        return O;
    }

    /**
     * Shortcut for {@link PreferencesStorage O(Activity activity)}
     * @return PreferencesStorage singleton instance
     * @throws IllegalStateException if instance wasn't properly initialized with {@link PreferencesStorage O(Activity activity)} beforehand
     */
    public static PreferencesStorage O(){
        if(O == null){
            throw new IllegalStateException(Connexion.class.getSimpleName() + "is not initialized, call O(Activity activity) first ");
        }
        return O;
    }

    public String getSavedPort() {
        return sharedPref.getString(String.valueOf(R.string.saved_port_key), "");

    }

    public String getSavedIP() {
        return sharedPref.getString(String.valueOf(R.string.saved_ip_key), "");
    }

    public String getSavedUsername() {
        return sharedPref.getString(String.valueOf(R.string.saved_username_key), "");
    }

    public String getSavedPassword() {
        return sharedPref.getString(String.valueOf(R.string.saved_password_key), "");
    }

    public void savePreferences(String port, String ip, String username, String password) {

        SharedPreferences.Editor prefsEditor = sharedPref.edit();

        prefsEditor.putString   (String.valueOf((R.string.saved_port_key)), port);
        prefsEditor.putString(String.valueOf((R.string.saved_ip_key)), ip);
        prefsEditor.putString(String.valueOf((R.string.saved_username_key)), username);
        prefsEditor.putString(String.valueOf((R.string.saved_password_key)), password);

        prefsEditor.apply();
    }
}
