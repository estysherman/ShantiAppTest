package webit.android.shanti.chat.chatManager.utils;

import webit.android.shanti.BuildConfig;

/**
 * Created by AndroIT on 09/02/2015.
 */
////הגירסה של QB////
public class QBUtils {

    //racheli
//    public static final String USER_LOGIN = "chatUser1";
//    public static final String USER_PASSWORD = "chatUser1pass";
//    public static final int USER_ID = 2187704;


    public static String getAppId() {
        if (BuildConfig.DEBUG)
          return "19515";
        else
            return "22214";
    }

    public static String getAuthKey() {
         if (BuildConfig.DEBUG)
         return "aYnhGxHcQc6WFf5";
        else
            return "qZP9GXutF5EdXvS";
    }

    public static String getAuthSecret() {
         if (BuildConfig.DEBUG)
          return "ZtncanqFZzr7MCh";
          else
            return "pcytS4MvD5nF57H";
    }

    public static final String TAG = "ChatMainManager";

    /*public static final String APP_ID = "19515";
    public static final String AUTH_KEY = "aYnhGxHcQc6WFf5";
    public static final String AUTH_SECRET = "ZtncanqFZzr7MCh";*/


    //racheli
//    public static final String APP_ID = "18449";
//    public static final String AUTH_KEY = "SU5SwEf8mMSPFre";
//    public static final String AUTH_SECRET = "8PjspnWP6ARVR6y";
//    public static final String APP_ID = "19769";
//    public static final String AUTH_KEY = "KY3XLZFhNd3rwEE";
//    public static final String AUTH_SECRET = "5Ev86TsuRhFN7-E";
    public static final int AUTO_PRESENCE_INTERVAL_IN_SECONDS = 30;


//    public static final String USER_LOGIN = "hodaya.afargan@gmail.com";
//    public static final String USER_PASSWORD = "12345678";
//    public static final int USER_ID = 2337986;
//    public static final String TAG = "ChatMainManager";
//    public static final String APP_ID = "19515";
//    public static final String AUTH_KEY = "aYnhGxHcQc6WFf5";
//    public static final String AUTH_SECRET = "ZtncanqFZzr7MCh";
//    public static final int AUTO_PRESENCE_INTERVAL_IN_SECONDS = 30;


    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_DIALOG = "dialog";
    public static final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";

}
