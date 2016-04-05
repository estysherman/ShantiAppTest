package webit.android.shanti.general;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by user on 12/07/2015.
 */
public class FilesHelper {


    //שמירת תמונה
    public static File saveImageFile(Bitmap bitmap, String fileName, boolean quality) {
        try {
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/ShantiChatFiles";
            File dir = new File(file_path);
            if (!dir.exists())
                dir.mkdirs();//יצירת תיקיה
            File file = new File(dir, fileName + ".png");//שומר תמונות בתיקיה שנוצרה
           if (!quality) {
                Matrix m = new Matrix();
                m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, 50, 50), Matrix.ScaleToFit.CENTER);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

            }
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            return file;//מחזיר קובץ
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("FilesHelperExcaption", e.getMessage());
        } finally {
            //
        }
        return null;
    }


    public static File saveImageFileV1(Bitmap bitmap, String fileName, boolean quality) {
        try {
            //quality = false;
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/ShantiChatFiles";
            File dir = new File(file_path);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dir, fileName + ".png");
            FileOutputStream fOut = new FileOutputStream(file);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (!quality) {
                Matrix m = new Matrix();
                m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, 50, 50), Matrix.ScaleToFit.CENTER);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            fOut.write(stream.toByteArray(), 0, stream.toByteArray().length);
            fOut.flush();
            fOut.close();
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void writeToFile(Context context, Bitmap bitmap, String fileName, int quality) {
        //String FILENAME = "hello_file";
        //String string = "hello world!";
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/ShantiChatFiles";

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream);
        byte[] byteArray = stream.toByteArray();

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(file_path + fileName);
    }

    //פענוח קובץ
    public static Bitmap decodeFile(String path) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            final int REQUIRED_SIZE = 100;

            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE
                    && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }
}
