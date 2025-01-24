package com.example.dermascan.RecoveryPassword;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dermascan.Connection.ConnectBD;
import com.example.dermascan.R;

public class RecoveryKey extends AppCompatActivity {
    EditText campTextRecovery;
    ProgressBar loading;
    TextView recoverytext;
    Button sendButton;
    String emailUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_key);
        campTextRecovery = (EditText) findViewById(R.id.email_recovery_id);
        loading = (ProgressBar) findViewById(R.id.progressBar_Recovery_id);
        recoverytext = (TextView) findViewById(R.id.RecoveryText_id);
        sendButton = (Button) findViewById(R.id.button_recovery_id);
        emailUser = new String();

    }

    private class NetWorkEmail extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... emails) {
            PasswordRec passwordRec = new PasswordRec(emails[0]);
            passwordRec.sendEmail();
            return passwordRec.getCode();
        }

        @Override
        protected void onPostExecute(String result) {
            recoverytext.setText("Digite o código");
            campTextRecovery.setHint("Código:");
            campTextRecovery.getText().clear();
            loading.setVisibility(View.INVISIBLE);
            sendButton.setText("Verificar");
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String camp = campTextRecovery.getText().toString();
                    if(camp.equals(result)){
                        recoverytext.setText("Nova senha");
                        campTextRecovery.setHint("Senha");
                        campTextRecovery.getText().clear();
                        campTextRecovery.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        sendButton.setText("Salvar");
                        sendButton.setOnClickListener(changePasword());
                    }else{
                        campTextRecovery.getText().clear();
                        campTextRecovery.setHint("Tente novamente");
                    }
                }
            });

        }
    }

    public View.OnClickListener changePasword(){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ConnectBD.Companion.UpdatePassWord(campTextRecovery.getText().toString(),emailUser)){
                    RecoveryKey.this.finish();
                }else{
                    campTextRecovery.setHint("Ocorreu um Erro");
                    campTextRecovery.getText().clear();
                }
            }
        };

    }

    public void SendEmail(View v){
        loading.setVisibility(View.VISIBLE);
        emailUser = campTextRecovery.getText().toString();
        new NetWorkEmail().execute(campTextRecovery.getText().toString());
    }

}