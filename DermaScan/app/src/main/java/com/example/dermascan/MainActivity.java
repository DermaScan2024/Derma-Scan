package com.example.dermascan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dermascan.Application.LoginPage;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonStart = (Button) findViewById(R.id.btnStart_id);
    }

    public void nextPage(View V){
        Intent it_Login = new Intent(this, LoginPage.class);
        startActivity(it_Login);
        this.finish();
    }



}