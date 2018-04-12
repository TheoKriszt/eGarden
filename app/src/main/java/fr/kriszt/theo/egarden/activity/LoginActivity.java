package fr.kriszt.theo.egarden.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.kriszt.theo.egarden.R;
import fr.kriszt.theo.egarden.utils.Connexion;
import fr.kriszt.theo.egarden.utils.PreferencesStorage;
import fr.kriszt.theo.egarden.utils.Security;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "eGardenLogin";
    private String errorMessage = "";

    @BindView(R.id.editIPAddress) EditText _editIPAddress;
    @BindView(R.id.editPort) EditText _editPort;
    @BindView(R.id.editUsername)  EditText _editUsername;
    @BindView(R.id.editPassword)  EditText _editPassword;
    @BindView(R.id.connectButton)  Button _connectButton;
    @BindView(R.id.connexionProgressBar) ProgressBar _progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        final String port = _editPort.getText().toString();
        final String ip = _editIPAddress.getText().toString();
        final String username = _editUsername.getText().toString();
        final String password = _editPassword.getText().toString();

        Connexion.O(this, port, ip);


        if(!Connexion.O().isOnline()){
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            return;
        }

        if (validateForm(port, ip, username, password)){
            _progressBar.setVisibility(View.VISIBLE);
            Security.requestToken(username, password, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    PreferencesStorage.O(LoginActivity.this).savePreferences(port, ip, username, password);
                    _progressBar.setVisibility(View.GONE);
                    startDashboardActivity();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: ", error);
                    Toast.makeText(LoginActivity.this, "Erreur " + " : \"" + error.getClass().getSimpleName() + "\"", Toast.LENGTH_LONG).show();
                    _progressBar.setVisibility(View.GONE);
                }
            });
        }else { // invalid form
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }


    }

    private void startDashboardActivity() {
            Intent intent = new Intent(this, MainActivity.class);
            // intent.putExtra(MESSAGE, jsonObject.toString());

        startActivity(intent);
    }

    public boolean validateForm(String port, String ip, String username, String password){

        if (port.isEmpty()){
            port = "80";
            _editPort.setText(port);
        }

        if (ip.isEmpty() || password.isEmpty() || username.isEmpty()){
            errorMessage = "Un champ est manquant";
            return false;
        }

        return true;
    }

}
