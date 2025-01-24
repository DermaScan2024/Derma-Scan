package com.example.dermascan.PhotosOperations;

import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PhotosManager{
    private static final int PICK_IMAGE = 1;

    private static PhotosManager photosManager;
    private  PhotosManager(){};

    public void openImagePicker(AppCompatActivity activity, ProgressBar progressAnalysis) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        progressAnalysis.setVisibility(View.VISIBLE);
        activity.startActivityForResult(intent, PICK_IMAGE);
    }

    public void openCamera(AppCompatActivity activity, ProgressBar progressAnalysis){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(activity.getPackageManager()) != null){
            activity.startActivityForResult(takePicture, PICK_IMAGE);
            progressAnalysis.setVisibility(View.VISIBLE);
        }else{
            progressAnalysis.setVisibility(View.INVISIBLE);
            Toast.makeText(activity,"Não foi encontrao um app de camera", Toast.LENGTH_SHORT);
        }
    }

    public void openImagePicker(AppCompatActivity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activity.startActivityForResult(intent, PICK_IMAGE);
    }

    public void openCamera(AppCompatActivity activity){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(activity.getPackageManager()) != null){
            activity.startActivityForResult(takePicture, PICK_IMAGE);
        }else{
            Toast.makeText(activity,"Não foi encontrao um app de camera", Toast.LENGTH_SHORT);
        }
    }

    public static PhotosManager getInstancePhotoManager(){
        if(photosManager == null){
             photosManager = new PhotosManager();
        }
        return photosManager;
    }








}
