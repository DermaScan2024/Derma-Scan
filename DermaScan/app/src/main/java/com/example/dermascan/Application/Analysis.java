package com.example.dermascan.Application;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.dermascan.Connection.ConnectBD;
import com.example.dermascan.PhotosOperations.OperationWithImage;
import com.example.dermascan.PhotosOperations.PhotosManager;
import com.example.dermascan.R;
import com.example.dermascan.User.User;
import com.example.dermascan.User.UserArea;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLConnection;

public class Analysis extends AppCompatActivity {

    private boolean typeAnalysis;
    private Uri selecetedImageUri;
    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int PICK_IMAGE = 1;
    private TextView resultAnalysis;
    private ProgressBar progressAnalysis;
    private ImageView userIcon;
    private ImageView imageSelected;
    private PhotosManager photosManager;
    private FrameLayout frameImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        resultAnalysis = (TextView) findViewById(R.id.result);
        progressAnalysis = (ProgressBar) findViewById(R.id.progressBar_analysis_id);
        imageSelected = new ImageView(this);
        frameImage = (FrameLayout) findViewById(R.id.frame_selected_id);
        userIcon = (ImageView) findViewById(R.id.icon_user_analysis_id);
        photosManager = PhotosManager.getInstancePhotoManager();

        Intent it = getIntent();
        typeAnalysis = it.getBooleanExtra("typeAnalysis",false);
        progressAnalysis.setVisibility(View.INVISIBLE);

        userIcon.setImageBitmap(User.getInstanceUser().getImageProfile());
        try {
        if(typeAnalysis){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CODE);
            }else{
                    photosManager.openCamera(this, progressAnalysis);
            }
        }else{
            //if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
              //  requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            //}else{
                photosManager.openImagePicker(this,progressAnalysis);
            //}
        }
        }catch (Exception e){
            Toast.makeText(this, "Falha ao abrir a imagem", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(!User.getInstanceUser().getLogged()){
            this.finish();
        }
    }

    private class NetworkTask extends AsyncTask<InputStream,Void, String> {
        @Override
        protected String doInBackground(InputStream... input) {
            InputStream imagemStream = input[0];
            return loadingResult(imagemStream);
        }

        @Override
        protected void onPostExecute(String result) {
            resultAnalysis.setText("Analise...");
            progressAnalysis.setVisibility(View.INVISIBLE);
            try{
                if(ConnectBD.Companion.InsertPhotoRecord(Float.parseFloat(result),User.getInstanceUser().getId())){
                    resultAnalysis.setText(result + "%");
                }else{
                    resultAnalysis.setText(result + "%" + "\n Não foi possivel salvar o resultado");
                }
            } catch (Exception e){
            Toast.makeText(Analysis.this, "Falha ao analisar a imagem", Toast.LENGTH_SHORT).show();
                resultAnalysis.setText("Falha ao analisar a imagem");
        }


        }
    }

        private String loadingResult(InputStream PhotoStream) {
            String extensionImage = "";

            try {
                Socket socket = new Socket("rngfm-2804-2488-9080-e110-59-4d81-c161-f64f.a.free.pinggy.link", 41099);

                if(typeAnalysis){
                    extensionImage = URLConnection.guessContentTypeFromStream(PhotoStream).split("/")[1];
                }else{
                    extensionImage = getPathFromUri(selecetedImageUri);
                }

                String indicador = "DIVISOR" + User.getInstanceUser().getId() + "DIVISOR"+ extensionImage +"DIVISOREND_OF_FILE";


                BufferedInputStream bufferInputStream = new BufferedInputStream(PhotoStream);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int byteRead;
                while ((byteRead = bufferInputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, byteRead);
                }

                byte[] file = byteArrayOutputStream.toByteArray();
                bufferInputStream.close();
                PhotoStream.close();

                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(file);
                outputStream.write(indicador.getBytes());

                InputStream inputStream = socket.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader in = new BufferedReader(inputStreamReader);
                String line = in.readLine();


                inputStream.close();
                outputStream.close();
                socket.close();
                return line;
            } catch (Exception e) {
                return "ERROR IN " + e.getMessage();
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null){
                selecetedImageUri = data.getData();
                if(selecetedImageUri == null){
                    Bitmap bitmapImage = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytesFileImage = new ByteArrayOutputStream();
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, bytesFileImage);
                    byte[] fileImage = bytesFileImage.toByteArray();

                    imageSelected.setImageBitmap(OperationWithImage.getResizedBitmap(bitmapImage,300,300,30f));
                    frameImage.setBackground(imageSelected.getDrawable());
                    try{
                        InputStream ImagemSelectd = new ByteArrayInputStream(fileImage);
                        new NetworkTask().execute(ImagemSelectd);
                    } catch (Exception e){
                        Toast.makeText(this, "Falha ao abrir a imagem", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    try{
                        InputStream ImagemSelectd = getContentResolver().openInputStream(selecetedImageUri);
                        imageSelected.setImageBitmap(OperationWithImage.getResizedBitmap(selecetedImageUri,300,300,this,30));
                        frameImage.setBackground(imageSelected.getDrawable());
                        new NetworkTask().execute(ImagemSelectd);
                        } catch (Exception e){
                            Toast.makeText(this, "Falha ao abrir a imagem", Toast.LENGTH_SHORT).show();
                        }
                  }

            }
        }


        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if(requestCode == REQUEST_PERMISSION_CODE){
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    try {
                        if (typeAnalysis) {
                            photosManager.openCamera(this, progressAnalysis);
                        } else {
                            photosManager.openImagePicker(this, progressAnalysis);
                        }
                    }catch (Exception e){
                        Toast.makeText(this, "Falha ao abrir a imagem", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(typeAnalysis){
                        Toast.makeText(this,"Permissão necessária para abrir a Camera.", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this,"Permissão necessária para abrir a galeria.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            return imagePath.split("/")[imagePath.split("/").length - 1].split("\\.")[1];
        }
        return null;
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

}