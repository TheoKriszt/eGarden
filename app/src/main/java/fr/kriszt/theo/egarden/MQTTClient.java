package fr.kriszt.theo.egarden;

import android.provider.SyncStateContract;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * Created by wizehunt on 26/02/18.
 */

public class MQTTClient {

/*    private MqttConnectOptions getMqttConnectionOption() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(true);
//        mqttConnectOptions.setWill(SyncStateContract.Constants.PUBLISH_TOPIC, "I am going offline".getBytes(), 1, true);
        //mqttConnectOptions.setUserName("username");
        //mqttConnectOptions.setPassword("password".toCharArray());
        return mqttConnectOptions;
    }

    private  pahoMqttClient = new PahoMqttClient();
    private mqttAndroidClient = pahoMqttClient.getMqttClient(
    getApplicationContext(), Constants.MQTT_BROKER_URL, SyncStateContract.Constants.CLIENT_ID);

      mqttAndroidClient.setCallback(new MqttCallbackExtended() {
        @Override
        public void connectComplete(boolean b, String s) {

        }
        @Override
        public void connectionLost(Throwable throwable) {

        }
        @Override
        public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
            setMessageNotification(s, new String(mqttMessage.getPayload()));
        }
        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        }
    });


*/
}
