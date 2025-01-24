package com.example.dermascan.Application;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dermascan.Connection.ConnectBD;
import com.example.dermascan.R;


public class CreateLogin extends AppCompatActivity {
    Button btn_criar;
    TextView returnLogin;
    EditText camp_name;
    EditText camp_email;
    EditText camp_password;
    ProgressBar progressBar_create;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_login);
        returnLogin = (TextView) findViewById(R.id.return_login);
        btn_criar = (Button) findViewById(R.id.btnCadastrar_id);
        camp_name = (EditText) findViewById(R.id.camp_name_Create_id);
        camp_email = (EditText) findViewById(R.id.camp_email_Create_id);
        camp_password = (EditText) findViewById(R.id.Password_camp_Create_id);
        progressBar_create = (ProgressBar) findViewById(R.id.progressBar_create_login);

    }

    private class NetWorkBd extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            if(!camp_email.getText().toString().equals("") || !camp_email.getText().toString().equals("") || !camp_password.getText().toString().equals("")){
                return ConnectBD.Companion.InsertUser(camp_email.getText().toString(),camp_name.getText().toString(),camp_password.getText().toString());
            }else{
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressBar_create.setVisibility(View.INVISIBLE);
            if(result){
                CreateLogin.this.finish();
            }else{
                btn_criar.setVisibility(View.VISIBLE);
                returnLogin.setText("Erro ao cadastrar");
            }
        }
    }


    public void register(View v){
        new NetWorkBd().execute();
        btn_criar.setVisibility(View.INVISIBLE);
        progressBar_create.setVisibility(View.VISIBLE);
    }

    public void ReturnLogin(View v){
        //Intent it_ReturnLogin = new Intent(this, LoginPage.class);
       // startActivity(it_ReturnLogin);
        this.finish();
    }

}