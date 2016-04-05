package webit.android.shanti.general;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.HashMap;
import java.util.List;

import webit.android.shanti.entities.User;

/**
 * Created by AndroIT on 19/01/2015.
 */
public class Common {

    public static String CrashTag = "CrashTag";

    public static User user;

    public static List<User> allUsers;

    public static HashMap<Integer, Boolean> existUsers = new HashMap<>();

    //public static String currentActivity = null;


    public static HashMap<Integer, Boolean> getExistUsers() {
        return existUsers;
    }

    public static void setExistUsers(HashMap<Integer, Boolean> existUsers) {
        Common.existUsers = existUsers;
    }

    public static List<User> getAllUsers() {
        return allUsers;
    }

    public static void setAllUsers(List<User> allUsers) {
        Common.allUsers = allUsers;

    }

    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return !(networkInfo == null || !networkInfo.isConnected());
        }
        return true;
    }


}