package com.example.dermascan.PhotosOperations;

import com.example.dermascan.R;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapterPhotos extends RecyclerView.Adapter<CustomAdapterPhotos.ViewHolder> {
    ArrayList<Photo> photos;
    Context context;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout frameLayout;
        private final FrameLayout framePhoto;
        private final ImageView imageRecord;

        private final TextView labelDate;
        private final TextView valueDate;
        private final TextView labelPercentage;
        private final TextView valuePercentage;

        public ViewHolder(View view) {
            super(view);
            frameLayout = (FrameLayout) view.findViewById(R.id.frame_id);
            framePhoto = (FrameLayout) view.findViewById(R.id.frame_photo_id);
            imageRecord =(ImageView)  view.findViewById(R.id.image_record_id);
            labelDate = (TextView) view.findViewById(R.id.label_date_id);
            labelPercentage = (TextView) view.findViewById(R.id.label_percentage_id);
            valueDate = (TextView) view.findViewById(R.id.value_date_id);
            valuePercentage = (TextView) view.findViewById(R.id.value_percentage_id);
        }

        public FrameLayout getFrameLayout() {
            return frameLayout;
        }

        public FrameLayout getFramePhoto() {
            return framePhoto;
        }

        public ImageView getImageRecord() {
            return imageRecord;
        }

        public TextView getLabelDate() {
            return labelDate;
        }

        public TextView getLabelPercentage() {
            return labelPercentage;
        }

        public TextView getValueDate() {
            return valueDate;
        }

        public TextView getValuePercentage() {
            return valuePercentage;
        }
    }

    public CustomAdapterPhotos(ArrayList<Photo> photos, Context context){

        this.photos = photos;
        this.context = context;
    }


    @Override
    public CustomAdapterPhotos.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_frame, viewGroup, false);
        return  new ViewHolder(view);
    }

    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            return imagePath;
        }
        return null;
    }





    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.getFrameLayout().removeAllViews();
        holder.getFramePhoto().removeAllViews();

        holder.getImageRecord().setImageBitmap(OperationWithImage.getResizedBitmap(photos.get(position).getPath(),300,300,context,30));
        holder.getValueDate().setText(photos.get(position).getDate());
        String valuePercentage = String.valueOf(photos.get(position).getPercentage()) + "%";
        holder.getValuePercentage().setText(valuePercentage);

        holder.getFramePhoto().setBackground(holder.getImageRecord().getDrawable());

        holder.getFrameLayout().addView(holder.getFramePhoto());
        holder.getFrameLayout().addView(holder.getLabelDate());
        holder.getFrameLayout().addView(holder.getLabelPercentage());
        holder.getFrameLayout().addView(holder.getValueDate());
        holder.getFrameLayout().addView(holder.getValuePercentage());

    }

    @Override
    public int getItemCount() {
        return photos.size();
    }
}
