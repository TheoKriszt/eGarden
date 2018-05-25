package fr.kriszt.theo.egarden.firebaseMessaging;

import android.app.Service;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import fr.kriszt.theo.egarden.utils.Security;


/**
 * Created by T.Kriszt on 23/04/2018.
 */

public class FirebaseGardenIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseGardenIDService";


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        Security.setToken(refreshedToken);

    }

}
