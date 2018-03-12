package fr.kriszt.theo.egarden;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import fr.kriszt.theo.egarden.utils.Connexion;
import fr.kriszt.theo.egarden.utils.PreferencesStorage;
import fr.kriszt.theo.egarden.utils.Security;

public class LoginActivity extends AppCompatActivity {


    private String errorMessage = "";

    @BindView(R.id.editIPAddress) EditText _editIPAddress;
    @BindView(R.id.editPort) EditText _editPort;
    @BindView(R.id.editUsername)  EditText _editUsername;
    @BindView(R.id.editPassword)  EditText _editPassword;
    @BindView(R.id.connectButton)  Button _connectButton;

    RequestQueue MyRequestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyRequestQueue = Volley.newRequestQueue(this);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Load from stored preferences
        String savedPort = PreferencesStorage.O(this).getSavedPort();
        String savedIP = PreferencesStorage.O(this).getSavedIP();
        String savedUsername = PreferencesStorage.O(this).getSavedUsername();
        String savedPassword = PreferencesStorage.O(this).getSavedPassword();

        _editIPAddress.setText(savedIP);
        _editPort.setText(savedPort);
        _editUsername.setText(savedUsername);
        _editPassword.setText(savedPassword);
    }

    @OnClick(R.id.connectButton)
    public void submit() {

        ProgressDialog dialog=new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Tentative de connexion");
        dialog.setIndeterminate(false);
        dialog.setMax(100);
        dialog.setProgress(50);

        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.show();

        if(true){
            return;
        }
        
        if (validate()){ // is form even valid ?
            Toast.makeText(this, "TODO : \"VRAIE\" Authentification", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DashboardActivity.class);
            // intent.putExtra(MESSAGE, jsonObject.toString());

        String port = _editPort.getText().toString();
        String ip = _editIPAddress.getText().toString();
        String username = _editUsername.getText().toString();
        String password = _editPassword.getText().toString();

            PreferencesStorage.O(this).savePreferences(port, ip, username, password);
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
        String username = _editUsername.getText().toString();

        if (!Connexion.checkIP(ip, errorMessage)
                || !Connexion.checkPort(port)
                || !checkCredentials(username, password)){
            return false;
        }else return true;
    }

    private boolean checkCredentials(String username, String password) {



        return true;
    }



    public void testRequest(String url){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("Field", "Value"); //Add the data you'd like to send to the server.
                return MyData;
            }
        };

    MyRequestQueue.add(stringRequest);


    }
}
