package com.example.dermascan.User;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dermascan.Application.Analysis;
import com.example.dermascan.R;

public class UserArea extends AppCompatActivity {
    ImageView userIcon;
    TextView nameText;
    TextView emailText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);
        userIcon = (ImageView) findViewById(R.id.icon_user_area_id);
        nameText = (TextView) findViewById(R.id.name_user_area_id);
        emailText = (TextView) findViewById(R.id.email_user_area_id);

        userIcon.setImageBitmap(User.getInstanceUser().getImageProfile() == null ? BitmapFactory.decodeResource(this.getResources(),R.drawable.iconuser) : User.getInstanceUser().getImageProfile());
        nameText.setText(User.getInstanceUser().getName());
        emailText.setText(User.getInstanceUser().getEmail());
    }

    public void toHome(View v){
        this.finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!User.getInstanceUser().getLogged()){
            this.finish();
        }
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
    public void goUserRecords(View v){
        Intent it = new Intent(this, UserRecords.class);
        startActivity(it);
    }
    public void goUserSettings(View v){
        Intent it = new Intent(this, UserSettings.class);
        startActivity(it);
    }
}