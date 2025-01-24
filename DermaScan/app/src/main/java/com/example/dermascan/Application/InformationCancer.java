package com.example.dermascan.Application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dermascan.Application.BaseInformationCancer.ShowInformation;
import com.example.dermascan.R;
import com.example.dermascan.User.User;
import com.example.dermascan.User.UserArea;

public class InformationCancer extends AppCompatActivity {
    ImageView home;
    ImageView upload;
    ImageView takePhoto;
    ImageView userPerfil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_cancer);
        home = (ImageView) findViewById(R.id.homeImage_Info_button_id);
        upload = (ImageView) findViewById(R.id.uploadImage_Info_button_id);
        takePhoto = (ImageView) findViewById(R.id.takePhotoImage_Info_button_id);
        userPerfil = (ImageView) findViewById(R.id.userImage_Info_button_id);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!User.getInstanceUser().getLogged()){
            this.finish();
        }
    }
    public void toHome(View v){
        this.finish();
    }

    public void uploadAnalysis(View v){
        Intent it = new Intent(this, Analysis.class);
        it.putExtra("typeAnalysis",false);
        startActivity(it);
        this.finish();
    }
    public void takeImageAnalysis(View v){
        Intent it = new Intent(this, Analysis.class);
        it.putExtra("typeAnalysis",true);
        startActivity(it);
        this.finish();
    }

    public void toUserArea(View v){
        Intent it = new Intent(this, UserArea.class);
        startActivity(it);
    }


    public void ShowInformationMela(View v){
        Intent it = new Intent(this, ShowInformation.class);
        it.putExtra("typeInformation",1);
        startActivity(it);
    }

    public void ShowInformationEspino(View v){
        Intent it = new Intent(this, ShowInformation.class);
        it.putExtra("typeInformation",2);
        startActivity(it);
    }

    public void ShowInformationBaso(View v){
        Intent it = new Intent(this, ShowInformation.class);
        it.putExtra("typeInformation",3);
        startActivity(it);
    }

}