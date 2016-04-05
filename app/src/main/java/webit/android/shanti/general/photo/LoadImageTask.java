
package webit.android.shanti.general.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by AndroIT203 on 09/11/2014.
 */
public class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap bitmap;
        try {
            URL imageURL = new URL(strings[0]);
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            Log.e("Shanti", "Error get profile picture", e);
            return null;
        } catch (IOException e) {
            Log.e("Shanti", "Error get profile picture", e);
            return null;
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        Log.d("Haifa", bitmap.toString());
    }
}
