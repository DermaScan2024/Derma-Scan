package com.example.dermascan.Application;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;


import androidx.appcompat.app.AppCompatActivity;

import com.example.dermascan.R;
import com.example.dermascan.User.User;
import com.example.dermascan.User.UserArea;


public class Home extends AppCompatActivity {
    FrameLayout frameUpload;
    FrameLayout framePhoto;
    FrameLayout frameInfos;

    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        frameUpload = (FrameLayout) findViewById(R.id.upload_images_id);
        framePhoto = (FrameLayout) findViewById(R.id.take_photo_id) ;
        frameInfos = (FrameLayout) findViewById(R.id.info_cancer_id);
        user = User.getInstanceUser();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!user.getLogged()){
            Intent it = new Intent(this, LoginPage.class);
            startActivity(it);
            this.finish();
        }
    }

    public void uploadAnalysis(View v){
        Intent it = new Intent(this, Analysis.class);
        it.putExtra("typeAnalysis",false);
        startActivity(it);
    }
    public void takeImageAnalysis(View v){
        Intent it = new Intent(this, Analysis.class);
        it.putExtra("typeAnalysis",true);
        startActivity(it);
    }

    public void informationCancer(View v){
        Intent it = new Intent(this, InformationCancer.class);
        startActivity(it);
    }


    public void toUserArea(View v){
        Intent it = new Intent(this, UserArea.class);
        startActivity(it);
    }


}