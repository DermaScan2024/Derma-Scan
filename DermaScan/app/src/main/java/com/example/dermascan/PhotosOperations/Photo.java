package com.example.dermascan.PhotosOperations;

import android.net.Uri;

public class Photo {
    private Uri path;
    private Float percentage;
    private String date;

    public Photo(Uri path, Float percentage, String date){
        this.path = path;
        this.percentage = percentage;
        this.date = date;
    }
    public Uri getPath() {
        return path;
    }
    public Float getPercentage() {
        return percentage;
    }

    public String getDate() {
        String [] dateFormated = date.split("-");
        return dateFormated[2] + "/" + dateFormated[1];
    }


}
