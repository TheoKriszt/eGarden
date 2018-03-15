package fr.kriszt.theo.egarden;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import butterknife.BindView;
import butterknife.ButterKnife;
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

//        _progressBar.setVisibility(View.VISIBLE);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//
//        When you are done you can undo this by clearing the flag:
//
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            _progressBar.setBackground(null);
//        }
//        ProgressDialog dialog=new ProgressDialog(LoginActivity.this);
//        dialog.setMessage("Tentative de connexion");
//        dialog.setIndeterminate(false);
//        dialog.setMax(100);
//        dialog.setProgress(50);
//
//        dialog.setCancelable(false);
//        dialog.setInverseBackgroundForced(false);
//        dialog.show();

        String port = _editPort.getText().toString();
        String ip = _editIPAddress.getText().toString();
        String username = _editUsername.getText().toString();
        String password = _editPassword.getText().toString();

        Connexion.O(this, port, ip);

        if(Security.requestToken(username, password)){
            Toast.makeText(this, "Requete envoyee", Toast.LENGTH_SHORT).show();

        }

        if (validate()){ // is form even valid ?
            Toast.makeText(this, "TODO : \"VRAIE\" Authentification", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DashboardActivity.class);
            // intent.putExtra(MESSAGE, jsonObject.toString());

        PreferencesStorage.O(this).savePreferences(port, ip, username, password);
        startActivity(intent);

        }else {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }

    }

    private void addProgressBar() {
//        ProgressBar progressBar = new ProgressBar(LoginActivity.this, null, android.R.attr.progressBarStyleHorizontal);
////        ProgressBar progressBar = new ProgressBar(LoginActivity.this, null, android.R.attr.progressBarStyleLarge);
////        progressBar.setHorizontalScrollBarEnabled(true);
//        progressBar.setMax(100);
//        progressBar.setProgress(25);
//        progressBar.setMessage();
//
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
//        params.addRule(RelativeLayout.CENTER_IN_PARENT);
//        RelativeLayout layout = findViewById(R.id.loginView);
//        layout.addView(progressBar,params);
//        progressBar.show
//        progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar
//        progressBar.setVisibility(View.GONE);     // To Hide ProgressBar
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




}
