package fr.kriszt.theo.egarden.firebaseMessaging;

import android.app.Service;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by T.Kriszt on 23/04/2018.
 */

public class FirebaseGardenNotifier extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseMsgService";


    /**
     * Mandatory, but keeps the default behaviour
     * as we just want to bring the user on the login activity when touching a notification
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

    }
}