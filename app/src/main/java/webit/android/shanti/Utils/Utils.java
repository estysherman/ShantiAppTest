package webit.android.shanti.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;

/**
 * Created by crm on 09/07/2015.
 */
public class Utils {

    public static Date convertToJsonDate(String timeString) {
        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(timeString);
        } catch (ParseException e) {
            try {
                date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(timeString);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return date;
    }

    public static void sendToLog(Context context, final String FunctionName, final String Json) {//שליחת נתונים לשרת
        String urlLog = "http://qa.webit-track.com/WebitLogs/LogService.svc/WriteLog";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ProjectName", "shanti android");
            jsonObject.put("FunctionName", FunctionName);
            jsonObject.put("Json", Json);
        } catch (Exception ex) {
        }
        new GeneralTask(context, urlLog, new UseTask() {
            @Override
            public void doAfterTask(String result) {
                Log.d("sendToLog", FunctionName + " : " + Json);
            }
        }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), jsonObject.toString());

    }

   public static String language;

    public static String getDefaultLocale() {
        switch (Locale.getDefault().toString()) {
            //todo change to israel
            case "iw_IL":
				language = "iw";
                return "2";
            case "en_ZA":
            case "en_US":
				language = "en";
                return "1";
            default:
                return "1";

        }
    }


    public static int getIntDefaultLocale() {
        switch (Locale.getDefault().toString()) {
            //change to israel
            case "iw_IL":
                return 2;
            case "en_ZA":
            case "en_US":
                return 1;
            default:
                return 1;

        }
    }

    public static void HideKeyboard(Context context) {
        if (context instanceof Activity) {
            if (((Activity) context).getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
            }

            ((Activity) context).getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

    }

    public static boolean isOnLine(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting())
            return true;
        return false;
    }

    public static void showKeyBoard(EditText ed, Context context) {
        InputMethodManager inputManager = (InputMethodManager) ((Activity) context).getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        inputManager.showSoftInput(ed, InputMethodManager.SHOW_FORCED);
        ed.requestFocus();
    }

    public static String convertJsonDateToString(String timeString) {
        if (timeString != null && timeString.length() > 0) {
            java.sql.Date time = convertToJsonDate1(timeString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time.getTime());

            return calendar.get(Calendar.DAY_OF_MONTH) + "/"
                    + ((calendar.get(Calendar.MONTH)) + 1) + "/"
                    + calendar.get(Calendar.YEAR);
        }
        return "";
    }

    public static java.sql.Date convertToJsonDate1(String timeString) {
        timeString = timeString.substring(timeString.indexOf("(") + 1,
                timeString.indexOf(")"));
        String[] timeSegments = timeString.split("\\+");
        long timeZoneOffSet = 0;
        if (timeSegments.length == 2)
            timeZoneOffSet = Long.valueOf(timeSegments[1]) * 36000;
        long millis = 0;
        if (timeSegments.length > 0)
            millis = Long.valueOf(timeSegments[0]);
        java.sql.Date time = new java.sql.Date(millis + timeZoneOffSet);
        return time;
    }

    public static String convertStringToDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        Date date1 = new Date(date);
        try {
            date1 = dateFormat.parse(date);
            return ("/Date(" + String.valueOf(date1.getTime()) + ")/");
        } catch (ParseException e) {
            try {
                date1 = dateFormat1.parse(date);
                return ("/Date(" + String.valueOf(date1.getTime()) + ")/");
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return ("/Date(" + String.valueOf(date1.getTime()) + ")/");
    }


    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];

            Bitmap mIcon11 = null;
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in, null, options);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
