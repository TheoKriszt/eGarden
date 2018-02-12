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
            Toast.makeText(this, "Erreur de formulaire", Toast.LENGTH_SHORT).show();
        }
        


    }


    public boolean validate(){
        String ip = _editIPAddress.getText().toString();
        int port = Integer.parseInt(_editPort.getText().toString());
        String password = _editPassword.getText().toString();

        if (!checkIP(ip)
                || !checkPort(port)
                || !checkPassword(password)){
            return false;
        }else return true;
    }

    private boolean checkPassword(String password) {
        return false;
    }

    private boolean checkPort(int port) {
        return port > 0 && port <= 65535;
    }

    private boolean checkIP(String ip) {
        boolean retVal = true;
        String[] matches = ip.split("\\.");

        if (matches.length != 4){
            retVal = false;
        }

        for (String s : matches){
            int i = Integer.parseInt(s);
            if(i < 0 || i > 255){
                retVal =  false;
            }
        }

        if (!retVal){
            Toast.makeText(this, "Erreur : IP invalide", Toast.LENGTH_LONG).show();
        }

        return retVal;
    }
}
