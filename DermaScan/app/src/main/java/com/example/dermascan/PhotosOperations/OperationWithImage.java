package com.example.dermascan.PhotosOperations;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class OperationWithImage {
    //HELP
    public  static Bitmap getResizedBitmap(Uri imagePath, int targetWidth, int targetHeight, Context context,float cornerRadius) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(imagePath)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);

            // Calcular escala de redução
            int scaleFactor = Math.min(options.outWidth / targetWidth, options.outHeight / targetHeight);

            options.inJustDecodeBounds = false;
            options.inSampleSize = scaleFactor;
            options.inPurgeable = true;

            try (InputStream secondInputStream = context.getContentResolver().openInputStream(imagePath)) {
                Bitmap resizedBitmap = BitmapFactory.decodeStream(secondInputStream, null, options);
                Bitmap outputBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(outputBitmap);

                Paint paint = new Paint();
                paint.setAntiAlias(true);
                RectF rectF = new RectF(0, 0, targetWidth, targetHeight);
                canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);

                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(resizedBitmap, null, rectF, paint);
                return outputBitmap;
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            return null;
        }
    }

    //HELP
    public static Bitmap getResizedBitmap(Bitmap originalBitmap, int targetWidth, int targetHeight, float cornerRadius) {

        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();
        float scaleFactor = Math.min((float) targetWidth / originalWidth, (float) targetHeight / originalHeight);
        int resizedWidth = Math.round(originalWidth * scaleFactor);
        int resizedHeight = Math.round(originalHeight * scaleFactor);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, resizedWidth, resizedHeight, true);

        Bitmap outputBitmap = Bitmap.createBitmap(resizedWidth, resizedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0, 0, resizedWidth, resizedHeight);
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(resizedBitmap, 0, 0, paint);

        return outputBitmap;
    }


}
