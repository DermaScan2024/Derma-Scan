package com.example.dermascan.Application;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.dermascan.Connection.ConnectBD;
import com.example.dermascan.R;
import com.example.dermascan.RecoveryPassword.RecoveryKey;
import com.example.dermascan.User.User;


public class LoginPage extends AppCompatActivity {

    TextView textNewAccont;
    TextView recoveryKeyText;
    Button btn_entrar;
    EditText name_camp;
    EditText password_camp;
    String informationUser;
    ProgressBar progressLoding;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textNewAccont = (TextView) findViewById(R.id.create_id);
        btn_entrar = (Button) findViewById(R.id.entrar_id);
        name_camp = (EditText) findViewById(R.id.login_email_id);
        password_camp = (EditText) findViewById(R.id.login_password_camp_id);
        progressLoding = (ProgressBar) findViewById(R.id.progressBar_Login);
        recoveryKeyText = (TextView) findViewById(R.id.recoverKey_id);
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    private class NetWorkBd extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... voids) {
            return ConnectBD.Companion.GetUser(name_camp.getText().toString(), password_camp.getText().toString());
        }

        @Override
        protected void onPostExecute(String result) {
            progressLoding.setVisibility(View.INVISIBLE);
            if(result.equals("UserNot")){
                textNewAccont.setText("Usuario não encontrado");
                btn_entrar.setVisibility(View.VISIBLE);
            }else{
                String [] infoUser =result.split(";");
                User user = User.getInstanceUser(infoUser[0],infoUser[1],Integer.parseInt(infoUser[2]),infoUser[4]);
                Intent it_home= new Intent(LoginPage.this, Home.class);
                startActivity(it_home);
                LoginPage.this.finish();
            }

        }
    }

    public void pageUser(View V){
        new NetWorkBd().execute();
        btn_entrar.setVisibility(View.INVISIBLE);
        progressLoding.setVisibility(View.VISIBLE);
    }
    public void recoveryKey(View v){
        Intent it = new Intent(this, RecoveryKey.class);
        startActivity(it);
    }
    public void pageNewAccount(View V){

        Intent it_CreateAccount = new Intent(this, CreateLogin.class);
        startActivity(it_CreateAccount);

    }
}
