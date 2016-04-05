package webit.android.shanti.chat.chatManager.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by igorkhomenko on 1/13/15.
 */
public class TimeUtils {
    public final static long ONE_SECOND = 1000;
    public final static long SECONDS = 60;

    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long MINUTES = 60;

    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long HOURS = 24;

    public final static long ONE_DAY = ONE_HOUR * 24;

    private TimeUtils() {
    }

    /**
     * converts time (in milliseconds) to human-readable format
     *  "<w> days, <x> hours, <y> minutes and (z) seconds"
     */
    public static String millisToLongDHMS(long duration) {
        if(duration > 0) {
            duration = new Date().getTime() - duration;
        }
        if(duration < 0){
            duration = 0;
        }

        StringBuffer res = new StringBuffer();
        long temp = 0;
        if (duration >= ONE_SECOND) {
            temp = duration / ONE_DAY;
            if (temp > 0) {
                duration -= temp * ONE_DAY;
                res.append(temp).append(" day").append(temp > 1 ? "s" : "")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_HOUR;
            if (temp > 0) {
                duration -= temp * ONE_HOUR;
                res.append(temp).append(" hour").append(temp > 1 ? "s" : "")
                        .append(duration >= ONE_MINUTE ? ", " : "");
            }

            temp = duration / ONE_MINUTE;
            if (temp > 0) {
                duration -= temp * ONE_MINUTE;
                res.append(temp).append(" minute").append(temp > 1 ? "s" : "");
            }

            if (!res.toString().equals("") && duration >= ONE_SECOND) {
                res.append(" and ");
            }

            temp = duration / ONE_SECOND;
            if (temp > 0) {
                res.append(temp).append(" second").append(temp > 1 ? "s" : "");
            }
            res.append(" ago");
            return res.toString();
        } else {
            return "0 second ago";
        }
    }

    public static String calcTimeToDisplay(long miliSeconds) {
        miliSeconds /= 1000;
        int seconds = (int) (miliSeconds % 60);
        miliSeconds /= 60;
        int minutes = (int) (miliSeconds % 60);
        miliSeconds /= 60;
        int hours = (int) miliSeconds;


        String timeString = "";
        DecimalFormat format = new DecimalFormat("00");
        if (hours > 0) {
            if (hours > 99) {
                timeString = hours + ":" + format.format(minutes) + ":"
                        + format.format(seconds);
            } else {
                timeString = format.format(hours) + ":"
                        + format.format(minutes) + ":" + format.format(seconds);
            }
        } else {
            if (minutes > 0)
                timeString = "00:" + format.format(minutes) + ":"
                        + format.format(seconds);
            else {
                timeString = "00:00:" + format.format(seconds);
            }
        }
        return timeString;
    }


    public static  String getDateFromMilliseconds(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);//באיזה פורמט להציג את התאריך

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();//ממיר זמן ותאריך ב milliSeconds ל date בפורמט הרצוי
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
