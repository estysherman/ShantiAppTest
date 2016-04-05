package webit.android.shanti.general.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import webit.android.shanti.R;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;

/**
 * Created by CRM on 23/02/2015.
 */
public class PutUserMarkerOnMapTask extends AsyncTask<String, Void, Bitmap> {
    Context context;
    User user;
    CallBack callBack;
    boolean isBig;

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

    public PutUserMarkerOnMapTask(Context context, User user, boolean isBig, CallBack callBack) {
        isBig = false;
        this.context = context;
        this.user = user;
        this.callBack = callBack;
        this.isBig = isBig;
    }

    protected Bitmap doInBackground(String... urls) {//מוריד מ url את התמונה ויוצר אותה
        String userImageUrl = String.valueOf(urls[0]);
        try {
            Bitmap bitmap = ImageHelper.getBitmapFromURL(userImageUrl,context);
            Template template;
            if (user.getiUserId() == Common.user.getiUserId())//אם אתה משתמש נוכחי
                template = isBig ? Template.MY_BIG_TEMPLATE : Template.MY_TEMPLATE;
            else template = isBig ? Template.USER_BIG_TEMPLATE : Template.USER_TEMPLATE;
            Bitmap templateBitmap = BitmapFactory.decodeResource(context.getResources(), template.getValue());
            return ImageProcess.cropImageVer2(bitmap, templateBitmap);
        } catch (Exception e) {
            //Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap marker) {
        super.onPostExecute(marker);
        callBack.doAfterTask(marker);
    }
}
