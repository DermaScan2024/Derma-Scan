package com.example.dermascan.User;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;

import android.widget.Toast;

import com.example.dermascan.Connection.ConnectBD;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;


public class User implements Serializable {
    private String name;
    private String email;
    private String password;
    private final int id;
    private Bitmap imageProfile;
    private static User user;
    private Boolean isLogged;

    private User(String name, String email, int id, String password){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isLogged = true;
        new GetIconNetWork().execute();
    }

    public Boolean getLogged() {
        return isLogged;
    }

    public void setLogged(Boolean logged) {
        isLogged = logged;
    }

    public Bitmap getImageProfile() {
        return imageProfile;
    }
    public void setImageProfile(Bitmap imageProfile) {
        this.imageProfile = imageProfile;
    }

    public int getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static User getInstanceUser(String name, String email, int id,String password){
        user = new User(name,email,id, password);
        return user;
    }

    public static User getInstanceUser(){
        if(user == null){
            user = new User("name","email",0,"null");
        }
        return user;
    }


    public void SetIconUserNetWork(Context context) {
        new SetIconNetWork().execute(context);
    }

    public boolean updateInformations(String name, String email, String password){
        Boolean result;
        try{
            result = new UpdateInformationsNetWork().execute(name,email,password).get();
            setEmail(email);
            setName(name);
            setPassword(password);
            return  result;
        }catch (Exception e){
            return false;
        }

    }
    private static class UpdateInformationsNetWork extends AsyncTask<String,Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... values) {
            String [] oldIformations = {user.getName(),user.getEmail(),user.getPassword()};
            return  ConnectBD.Companion.UpdateUser(oldIformations,values);
        }

        @Override
        protected void onPostExecute(Boolean result) {

        }
    }
    private static class SetIconNetWork extends AsyncTask<Context,Void, Boolean> {
        @SuppressLint("StaticFieldLeak")
        private Context context;
        @Override
        protected Boolean doInBackground(Context... contexts) {
            this.context = contexts[0];
            return ConnectBD.Companion.InsertIconUser(user.getName(), user.getEmail(),user.getImageProfile(),user.getId());
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                Toast.makeText(context, "Imagem alterada com sucesso", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "Não foi possível alterar a imagem", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static class GetIconNetWork extends AsyncTask<Void,Void, ByteArrayOutputStream> {
        @Override
        protected ByteArrayOutputStream doInBackground(Void... voids) {
            return ConnectBD.Companion.GetIconUser(user.getName(), user.getEmail());
        }

        @Override
        protected void onPostExecute(ByteArrayOutputStream iconUser) {
            if(iconUser.toByteArray().length == 0){
                user.setImageProfile(null);
            }else{
                Bitmap bitmapImage = BitmapFactory.decodeByteArray(iconUser.toByteArray(),0,iconUser.size());
                user.setImageProfile(bitmapImage);
            }

        }

    }
}
