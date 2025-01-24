package com.example.dermascan.User;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;


import android.graphics.BitmapFactory;
import android.net.Uri;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dermascan.Application.Analysis;
import com.example.dermascan.PhotosOperations.PhotosManager;
import com.example.dermascan.R;

import java.io.InputStream;

public class UserSettings extends AppCompatActivity {
    ImageView imageIconUser;

    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int PICK_IMAGE = 1;
    private PhotosManager photosManager;
    private Uri selecetedIconUri;
    private User user;
    private ProgressBar progressBarIcon;
    private EditText passwordCamp;
    private EditText emailCamp;
    private EditText nameCamp;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        imageIconUser = (ImageView) findViewById(R.id.icon_user_image_id);
        photosManager = PhotosManager.getInstancePhotoManager();
        progressBarIcon = (ProgressBar) findViewById(R.id.progressBar_icon_user);
        nameCamp = (EditText) findViewById(R.id.Name_update_id);
        emailCamp = (EditText) findViewById(R.id.Email_update_id);
        passwordCamp = (EditText) findViewById(R.id.Password_update_id);
        button = (Button) findViewById(R.id.Button_save_update);
        user = User.getInstanceUser();
        Intent it = getIntent();

        imageIconUser.setImageBitmap(user.getImageProfile() == null ? BitmapFactory.decodeResource(this.getResources(),R.drawable.iconuser) : user.getImageProfile());
        nameCamp.setText(user.getName());
        emailCamp.setText(user.getEmail());
        passwordCamp.setText(user.getPassword());

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!User.getInstanceUser().getLogged()){
            this.finish();
        }
    }

    public void setIconUser(View v){
        //if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
           // requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
       // }else{
            photosManager.openImagePicker(this);
       // }
    }

    public void setInformationsUser(View v){
        progressBarIcon.setVisibility(View.VISIBLE);
        button.setVisibility(View.INVISIBLE);

        if(user.updateInformations(nameCamp.getText().toString(),emailCamp.getText().toString(),passwordCamp.getText().toString())){
            Toast.makeText(this,"Informações alteradas com sucessos!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Não foi possível alterar as informações", Toast.LENGTH_SHORT).show();
        }

        button.setVisibility(View.VISIBLE);
        progressBarIcon.setVisibility(View.INVISIBLE);
    }
    public void LogOff(View v){
        user.setLogged(false);
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    photosManager.openImagePicker(this);
            }else{
                    Toast.makeText(this,"Permissão necessária para abrir a galeria.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null){
                selecetedIconUri = data.getData();
                try{
                    imageIconUser.setImageURI(selecetedIconUri);
                    InputStream iconSelected = getContentResolver().openInputStream(selecetedIconUri);
                    user.setImageProfile(BitmapFactory.decodeStream(iconSelected));
                    user.SetIconUserNetWork(this);

                } catch (Exception e){
                    Toast.makeText(this, "Falha ao abrir a imagem", Toast.LENGTH_SHORT).show();
                }
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
        this.finish();
    }

}