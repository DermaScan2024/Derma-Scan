package com.example.dermascan.User;




import android.content.ContentResolver;
import android.content.ContentValues;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.dermascan.Application.Analysis;
import com.example.dermascan.Connection.ConnectBD;
import com.example.dermascan.PhotosOperations.CustomAdapterPhotos;
import com.example.dermascan.PhotosOperations.Photo;
import com.example.dermascan.Connection.PhotosInfo;
import com.example.dermascan.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class UserRecords extends AppCompatActivity {
    ArrayList<Photo> photos;
    ProgressBar progressBar;
    TextView informationRecordText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_records);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_loading_id);
        informationRecordText = (TextView) findViewById(R.id.information_non_id);
        progressBar.setVisibility(View.VISIBLE);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    photos = new findHistoryNetWork().execute().get();
                    //HELP
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(photos !=null){
                                progressBar.setVisibility(View.INVISIBLE);
                                RecyclerView recyclerView = findViewById(R.id.list_records_id);
                                recyclerView.setLayoutManager(new LinearLayoutManager(UserRecords.this));
                                recyclerView.setAdapter(new CustomAdapterPhotos(photos,UserRecords.this));

                            }else{
                                progressBar.setVisibility(View.INVISIBLE);
                                informationRecordText.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }catch (Exception e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            informationRecordText.setText("Nenhum registro encontrado");
                            progressBar.setVisibility(View.INVISIBLE);
                            informationRecordText.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!User.getInstanceUser().getLogged()){
            this.finish();
        }
    }

    private class findHistoryNetWork extends AsyncTask<Void,Void, ArrayList<Photo>> {
        String pathName = Environment.DIRECTORY_PICTURES + "/History_" + User.getInstanceUser().getId();
        Socket socket;
        OutputStream outputStream;
        InputStream inputStream;
        BufferedInputStream in;
        ByteArrayOutputStream bufferArmazen;
        int countPhotos;
        ArrayList<Uri> photosPath;
        List<PhotosInfo> photosInfos;
        ArrayList<Photo> photos;


        @Override
        protected ArrayList<Photo> doInBackground(Void... values) {
            countPhotos = 0;
            photosPath = new ArrayList<Uri>();
            photos = new ArrayList<Photo>();
            photosInfos = new ArrayList<PhotosInfo>();

            try {
                photosInfos = ConnectBD.Companion.GetPhotoRecord(User.getInstanceUser().getId());
                startConnection();
                findRecords();
                for(int i =0; i < photosInfos.size(); i++){
                    photos.add(new Photo(photosPath.get(i),photosInfos.get(i).getPercentage(),photosInfos.get(i).getDate()));
                }
                return photos;
            }catch (Exception e){
                return null;
            }
        }

        public void startConnection() throws Exception{
            File file = new File(pathName);
            if (!file.exists()) {
                file.mkdir();
            }
            deleteOldFiles(UserRecords.this);
            socket = new Socket("rngfm-2804-2488-9080-e110-59-4d81-c161-f64f.a.free.pinggy.link", 41099);
            String indicador = User.getInstanceUser().getId() + ";" + "END_OF_FILE";
            outputStream = socket.getOutputStream();
            outputStream.write(indicador.getBytes());

            inputStream = socket.getInputStream();
            in = new BufferedInputStream(inputStream);

            bufferArmazen = new ByteArrayOutputStream();
            int byteReadConeection;
            byte[] buffer = new byte[1024];
            while ((byteReadConeection = in.read(buffer)) != -1) {
                bufferArmazen.write(buffer, 0, byteReadConeection);
            }

            UserRecords.this.informationRecordText.setText("Error ao tentar recuperar os registros");


            in.close();
            inputStream.close();
            outputStream.close();
        }

        public ArrayList<Uri> findRecords(){
            try{

                if (bufferArmazen.size() > 100) {
                    int byteReadArmazen = bufferArmazen.size();
                    byte[] bufferReaderArmazen = bufferArmazen.toByteArray();
                    byte[] delimiter = "DELIMITER".getBytes();
                    int startingPoint = 0;
                    int index = 0;


                    for (int i = 0; i < byteReadArmazen; i++) {
                        if (bufferReaderArmazen[i] == delimiter[index]) {
                            index++;

                            if (index == delimiter.length) {
                                writerPhoto(bufferReaderArmazen,startingPoint,i,delimiter);
                                startingPoint = i + 1;
                                index = 0;
                            }
                        } else {
                            index = 0;
                        }
                    }
                } else {
                    return null;
                }

                bufferArmazen.close();
                socket.close();
                return photosPath;
            }catch (Exception e){
                return null;
            }
        }

        //HELP
        public void deleteOldFiles(Context context) {
            Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver resolver = context.getContentResolver();
            String selection = MediaStore.Images.Media.DATA + " LIKE ?";
            String[] selectionArgs = new String[]{"%/History_"+ User.getInstanceUser().getId()+"/%"};
            resolver.delete(collection, selection, selectionArgs);
        }

        public void writerPhoto(byte[] bufferReaderArmazen, int startingPoint, int i,byte[] delimiter) throws Exception{
            //HELPO
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, countPhotos + ".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, pathName);
            Uri uri = UserRecords.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            photosPath.add(uri);
            //

            BufferedOutputStream out = new BufferedOutputStream(UserRecords.this.getContentResolver().openOutputStream(uri));
            out.write(bufferReaderArmazen, startingPoint, (i - startingPoint - delimiter.length) + 1);
            out.close();
            countPhotos++;
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