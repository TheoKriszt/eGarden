package fr.kriszt.theo.egarden;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.SharedPreferences.*;

public class LoginActivity extends AppCompatActivity {
    private Context context;
    private SharedPreferences sharedPref;
    private String errorMessage = "";

    @BindView(R.id.editIPAddress) EditText _editIPAddress;
    @BindView(R.id.editPort) EditText _editPort;
    @BindView(R.id.editPassword)  EditText _editPassword;
    @BindView(R.id.connectButton)  Button _connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences( // shared preferences are our persistence
                getString(R.string.credentials_preference_file), Context.MODE_PRIVATE);
//        SharedPreferences sharedPref = PreferenceManager
//                .getDefaultSharedPreferences(this);

        if (sharedPref == null)
        Toast.makeText(context, "sgaredPref is NULL !", Toast.LENGTH_LONG).show();

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        // Load from stored preferences
//        String savedPort = String.valueOf(sharedPref.getInt(getString(R.string.saved_port_key), 0));
        String savedPort = sharedPref.getString(getString(R.string.saved_port_key), "22");

        String savedIP = sharedPref.getString(getString(R.string.saved_ip_key), "192.168.1.12");
        String savedPassword = sharedPref.getString(getString(R.string.saved_password_key), "abcdef");

        _editIPAddress.setText(savedIP);
//        _editPort.setText("4242");
        _editPort.setText(savedPort);
        _editPassword.setText(savedPassword);
    }

    @OnClick(R.id.connectButton)
    public void submit() {
        
        if (validate()){

            Toast.makeText(this, "TODO : \"VRAIE\" Authentification", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DashboardActivity.class);
            // intent.putExtra(MESSAGE, jsonObject.toString());


            savePreferences();

            startActivity(intent);

        }else {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
        


    }

    private void savePreferences(){

        sharedPref = context.getSharedPreferences( // shared preferences are our persistence
                getString(R.string.credentials_preference_file), Context.MODE_PRIVATE);
        
        String port = _editPort.getText().toString();
        String ip = _editIPAddress.getText().toString();
        String password = _editPassword.getText().toString();

        Editor prefsEditor = sharedPref.edit();

        prefsEditor.putString   (getString(R.string.saved_port_key), port);
        prefsEditor.putString(getString(R.string.saved_ip_key), ip);
        prefsEditor.putString(getString(R.string.saved_password_key), password);

        prefsEditor.apply();
    }


    public boolean validate(){
        String ip = _editIPAddress.getText().toString();
        int port = -1;
        try{
            port = Integer.parseInt(_editPort.getText().toString());
        }catch (NumberFormatException e){
            errorMessage = "Numéro de port invalide";
        }

        String password = _editPassword.getText().toString();

        if (!checkIP(ip)
                || !checkPort(port)
                || !checkPassword(password)){
            return false;
        }else return true;
    }

    private boolean checkPassword(String password) {

        //TODO : logique métier pour vérification de mdp
        if (password.isEmpty()){
            errorMessage = "Mot de passe manquant";
            return false;
        }

        return true;
    }

    private boolean checkPort(int port) {
        return port > 0 && port <= 65535;
    }

    private boolean checkIP(String ip) {
        boolean retVal = true;

        if (ip.isEmpty()){
            errorMessage = "IP manquante";
            return false;
        }

        String[] matches = ip.split("\\.");

        if (matches.length != 4){
            retVal = false;
            errorMessage = "Format de l'adresse IPv4 invalide (xxx.xxx.xxx.xxx)";
        }

        for (String s : matches){
            try {
                int i = Integer.parseInt(s);
                if(i < 0 || i > 255){
                    retVal =  false;
                    errorMessage = "IP invalide : " + i;
                    break;
                }
            }catch (NumberFormatException e){
                retVal = false;
                errorMessage = "IP invalide : " + s;
                break;
            }

        }

        return retVal;
    }
}
