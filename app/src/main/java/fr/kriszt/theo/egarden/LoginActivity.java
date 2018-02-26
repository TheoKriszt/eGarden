package fr.kriszt.theo.egarden;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private Context context = getApplicationContext();
    private SharedPreferences sharedPref = context.getSharedPreferences( // shared preferences are our persistence
            getString(R.string.credentials_preference_file), Context.MODE_PRIVATE);

    private String errorMessage = "";

    @BindView(R.id.editIPAddress) EditText _editIPAddress;
    @BindView(R.id.editPort) EditText _editPort;
    @BindView(R.id.editPassword)  EditText _editPassword;
    @BindView(R.id.connectButton)  Button _connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);


        // Load from stored preferences
        int savedPort = sharedPref.getInt(getString(R.string.saved_port_key), 0);
        String savedIP = sharedPref.getString(getString(R.string.saved_ip_key), "192.168.1.");
        String savedPassword = sharedPref.getString(getString(R.string.saved_password_key), "");

        _editIPAddress.setText(savedIP);
        _editPort.setText(savedPort);
        _editPassword.setText(savedPassword);
    }

    @OnClick(R.id.connectButton)
    public void submit() {
        
        if (validate()){

            Toast.makeText(this, "TODO : \"VRAIE\" Authentification", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DashboardActivity.class);
            // intent.putExtra(MESSAGE, jsonObject.toString());

            // Save changes
            SharedPreferences.Editor prefsEditor = sharedPref.edit();

            int port = Integer.parseInt(_editPort.getText().toString());
            String ip = _editIPAddress.getText().toString();
            String password = _editPassword.getText().toString();

            prefsEditor.putInt   (getString(R.string.saved_port_key), port);
            prefsEditor.putString(getString(R.string.saved_ip_key), ip);
            prefsEditor.putString(getString(R.string.saved_password_key), password);

            prefsEditor.apply();

            startActivity(intent);

        }else {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
        


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
