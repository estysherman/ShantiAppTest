package webit.android.shanti.general.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import webit.android.shanti.R;
import webit.android.shanti.entities.User;

/**
 * Created by CRM on 23/02/2015.
 */
public class PutMarkerOnMapTask extends AsyncTask<String, Void, Bitmap> {
    Context context;
    User user;
    Template template;
    CallBack callBack;

    public static enum Template {
        MY_TEMPLATE(R.drawable.marker_me),
        USER_TEMPLATE(R.drawable.marker_user),
        MY_BIG_TEMPLATE(R.drawable.marker_me_big),
        USER_BIG_TEMPLATE(R.drawable.marker_big_user);

        private final int id;

        Template(int id) {
            this.id = id;
        }

        public int getValue() {
            return id;
        }
    }

    public interface CallBack {
        public void doAfterTask(Bitmap marker);
    }

    public PutMarkerOnMapTask(Context context, User user, Template template, CallBack callBack) {
        this.context = context;
        this.user = user;
        this.template = template;
        this.callBack = callBack;
    }

    protected Bitmap doInBackground(String... urls) {
        String userImageUrl = String.valueOf(urls[0]);
        try {
            Bitmap bitmap = ImageHelper.getBitmapFromURL(userImageUrl,context);
            Bitmap templateBitmap = BitmapFactory.decodeResource(context.getResources(), template.getValue());
            return ImageProcess.cropImageVer2(bitmap, templateBitmap);
        } catch (Exception e) {
            if (e != null) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap marker) {
        super.onPostExecute(marker);
        callBack.doAfterTask(marker);
    }
}
