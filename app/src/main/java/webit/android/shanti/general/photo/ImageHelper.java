package webit.android.shanti.general.photo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import webit.android.shanti.Utils.Utils;

/**
 * Created by CRM on 22/02/2015.
 */
public class ImageHelper {

    private static Bitmap codec(Bitmap src, Bitmap.CompressFormat format,
                                int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);
        src.recycle();

        byte[] array = os.toByteArray();
        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int dp) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        int quality = (int) (((float) 200 / output.getWidth()) * 200);
        if (quality < 0)
            quality = 100;
//        output = codec(output, Bitmap.CompressFormat.PNG, quality);
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        output.compress(Bitmap.CompressFormat.PNG, quality, out);
//        output = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = (int) (dp * Resources.getSystem().getDisplayMetrics().density);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

//        paint.setColor(Color.BLACK);
//        paint.setStrokeWidth(50);
//        canvas.drawRect(30, 30, 80, 80, paint);
//        paint.setStrokeWidth(50);
//        paint.setColor(Color.CYAN);
//        canvas.drawRect(33, 60, 77, 77, paint );
//        paint.setColor(Color.YELLOW);
//        canvas.drawRect(33, 33, 77, 60, paint );


        paint.setColor(color);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    public static Bitmap getBitmapFromURL(String src, Context context) {
//        try {
//            if (Utils.isOnLine(context)) {
//                URL url = new URL(src);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                connection.setDoInput(true);
//                connection.connect();
//                InputStream input = connection.getInputStream();
//                Bitmap myBitmap = BitmapFactory.decodeStream(input);
//                return myBitmap;
//            } else
//                return null;
//        } catch (IOException e) {
//            return null;
//        }
        try {
            return new RetrieveFeedTask(context, src).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    static class RetrieveFeedTask extends AsyncTask<String, Void, Bitmap> {
        Context context;
        String src = "";

        public RetrieveFeedTask(Context context, String src) {
            this.context = context;
            this.src = src;
        }

        private Exception exception;

        protected Bitmap doInBackground(String... urls) {
            if (Utils.isOnLine(context)) {
                URL url = null;
                try {
                    url = new URL(src);
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                connection.setDoInput(true);
                try {
                    connection.connect();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                InputStream input = null;
                try {
                    input = connection.getInputStream();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } else
                return null;
        }

    }

    protected void onPostExecute(Bitmap bitmap) {

    }


    public static String getBase64FromImage(ImageView imageView) {
       /* ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);

        return imageEncoded;*/


        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        //Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        return getBase64FromBitmap(bitmap);
    }


    public static String getBase64FromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String imageEncoded = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT);
            return imageEncoded;
        } else {
            return "";
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int color, int cornerDips, int borderDips, Context context, ImageView imageView) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) borderDips,
                context.getResources().getDisplayMetrics());
        final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) cornerDips,
                context.getResources().getDisplayMetrics());
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        // prepare canvas for transfer
        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        // draw bitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        // draw border
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) borderSizePx);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        return output;
    }
}
