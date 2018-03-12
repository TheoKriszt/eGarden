package fr.kriszt.theo.egarden;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
//import android.view.View;
//import android.widget.Toast;
//
//import org.eclipse.paho.android.service.MqttAndroidClient;
//import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
//import org.eclipse.paho.client.mqttv3.IMqttActionListener;
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
//import org.eclipse.paho.client.mqttv3.IMqttToken;
//import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;

public class DashboardActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

//    MqttAndroidClient mqttAndroidClient;

//    final String serverUri = "tcp://iot.eclipse.org:1883";

//    String clientId = "ExampleAndroidClienttkri2136PT07";
//    final String subscriptionTopic = "exampleAndroidTopic";
//    final String publishTopic = "exampleAndroidPublishTopic";
//    final String publishMessage = "Hello World!";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);


        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });


//        setupMQTTClient();





    }

//    public void subscribeToTopic(){
//        try {
//            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    Toast.makeText(getApplicationContext(), "Subscribed", Toast.LENGTH_SHORT).show();
//
////                    addToHistory("Subscribed!");
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
////                    addToHistory("Failed to subscribe");
//                    Toast.makeText(getApplicationContext(), "Failed to subscribe", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            // THIS DOES NOT WORK!
//            mqttAndroidClient.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
//                @Override
//                public void messageArrived(String topic, MqttMessage message) throws Exception {
//                    // message Arrived!
//                    System.out.println("Message: " + topic + " : " + new String(message.getPayload()));
//                }
//            });

//        } catch (MqttException ex){
//            System.err.println("Exception whilst subscribing");
//            ex.printStackTrace();
//        }
//    }

//    public void publishMessage(){

//        try {
//            MqttMessage message = new MqttMessage();
//            message.setPayload(publishMessage.getBytes());
//            mqttAndroidClient.publish(publishTopic, message);
////            addToHistory("Message Published");
//            if(!mqttAndroidClient.isConnected()){
////                addToHistory(mqttAndroidClient.getBufferedMessageCount() + " messages in buffer.");
//            }
//        } catch (MqttException e) {
//            System.err.println("Error Publishing: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    public void setupMQTTClient(){
//        clientId = clientId + System.currentTimeMillis();
//        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
//        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
//            @Override
//            public void connectComplete(boolean reconnect, String serverURI) {
//
//                if (reconnect) {
//                    Toast.makeText(getApplicationContext(), "Reconnected to : " + serverURI, Toast.LENGTH_SHORT).show();
////                    addToHistory("Reconnected to : " + serverURI);
//                    // Because Clean Session is true, we need to re-subscribe
//                    subscribeToTopic();
//                } else {
////                    addToHistory("Connected to: " + serverURI);
//                    Toast.makeText(getApplicationContext(), "Connected to : " + serverURI, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void connectionLost(Throwable cause) {
//                Toast.makeText(getApplicationContext(), "The Connection was lost.", Toast.LENGTH_SHORT).show();
////                addToHistory("The Connection was lost.");
//            }
//
//            @Override
//            public void messageArrived(String topic, MqttMessage message) throws Exception {
//                Toast.makeText(getApplicationContext(), "Incoming message: " + new String(message.getPayload()), Toast.LENGTH_SHORT).show();
////                addToHistory("Incoming message: " + new String(message.getPayload()));
//            }
//
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken token) {
//                Toast.makeText(getApplicationContext(), "Delivery complete.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
//        mqttConnectOptions.setAutomaticReconnect(true);
//        mqttConnectOptions.setCleanSession(false);
//
//
//        try {
//            //addToHistory("Connecting to " + serverUri);
//            Toast.makeText(getApplicationContext(), "Connecting to " + serverUri, Toast.LENGTH_SHORT).show();
//            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken asyncActionToken) {
//                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
//                    disconnectedBufferOptions.setBufferEnabled(true);
//                    disconnectedBufferOptions.setBufferSize(100);
//                    disconnectedBufferOptions.setPersistBuffer(false);
//                    disconnectedBufferOptions.setDeleteOldestMessages(false);
//                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
//                    subscribeToTopic();
//                }
//
//                @Override
//                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
//                    Toast.makeText(getApplicationContext(), "Failed to Connect to " + serverUri, Toast.LENGTH_SHORT).show();
////                    addToHistory("Failed to connect to: " + serverUri);
//                }
//            });
//
//
//        } catch (MqttException ex){
//            ex.printStackTrace();
//        }
//    }

}
