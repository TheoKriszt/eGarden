package fr.kriszt.theo.egarden;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
//import butterknife.InjectView;


public class LoginActivity extends AppCompatActivity {

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
    }

    @OnClick(R.id.connectButton)
    public void submit() {
        
        if (validate()){

            Toast.makeText(this, "TODO : Authentification", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DashboardActivity.class);
//        intent.putExtra(MESSAGE, jsonObject.toString());
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
