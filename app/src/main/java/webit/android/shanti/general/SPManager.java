package webit.android.shanti.general;

import android.content.Context;
import android.content.SharedPreferences;

import webit.android.shanti.entities.User;
import webit.android.shanti.entities.UserMembership;
import webit.android.shanti.entities.UserQuickBlox;

/**
 * Created by new on 1/18/2015.
 */
public class SPManager {

    public static final String NOTIFICATION_COUNTER = "notificationCounter";
    public static final String QB_ID = "qbId";
    public static final String QB_LOGIN = "qbLogin";
    public static final String QB_PASSWORD = "qbPassword";
    public static final String USER_ID = "userId";
    public static final String USER_FACEBOOK_ID = "userFacebookId";
    public static final String USER_GOOGLE_ID = "userGoogleId";
    public static final String USER_NAME = "userName";
    public static final String USER_FULL_NAME = "userFullName";
    public static final String USER_PASSWORD = "userPassword";
    public static final String USER_EMAIL = "userEmail";
    public static final String USER_IMAGE = "userImage";
    public static final String REG_ID = "registrationId";
    public static final String APP_VERSION = "appVersion";
    public static final String ERROR_LOGIN_END_TIME = "errorLoginEndTime";

    private static SPManager instance = null;
    private static SharedPreferences sPreferences = null;


    private SPManager(Context context) {
        sPreferences = context.getSharedPreferences("MyShared", 0);
    }

    public static SPManager getInstance(Context context) {
        if (instance == null) {
            instance = new SPManager(context);
        }
        return instance;

    }

    public void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void saveInteger(String key, int value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void saveLong(String key, long value) {
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public String getString(String key) {
        return sPreferences.getString(key, "");
    }

    public int getInt(String key) {
        return sPreferences.getInt(key, -1);
    }

    public int getInt(String key, int i) {
        return sPreferences.getInt(key, i);
    }

    public long getLong(String key) { return sPreferences.getLong(key, -1);}

    public boolean getBoolean(String key, boolean defultValue) {
        return sPreferences.getBoolean(key, defultValue);
    }

    public void saveUser(User user) {
        saveInteger(USER_ID, user.getiUserId());
        saveString(USER_FULL_NAME, user.getNvShantiName());
        saveString(USER_IMAGE, user.getNvImage());
        saveString(USER_FACEBOOK_ID, user.getNvFacebookUserId());
        saveString(USER_GOOGLE_ID, user.getNvGoogleUserId());
        if (user.getoUserMemberShip() != null) {
            saveString(USER_NAME, user.getoUserMemberShip().getNvUserName());
            saveString(USER_PASSWORD, user.getoUserMemberShip().getNvUserPassword());
        }
        if (user.getoUserQuickBlox() != null) {
            saveInteger(QB_ID, user.getoUserQuickBlox().getID());
            saveString(QB_LOGIN, user.getoUserQuickBlox().getLogin());
            saveString(QB_PASSWORD, user.getoUserQuickBlox().getPassword());
        }
        saveString(USER_EMAIL, user.getNvEmail());
    }

    public User getUser() {
        User user = new User();
        user.setiUserId(getInt(USER_ID));
        user.setNvImage(getString(USER_IMAGE));
        user.setNvFacebookUserId(getString(USER_FACEBOOK_ID));
        user.setNvGoogleUserId(getString(USER_GOOGLE_ID));

        UserMembership userMembership = new UserMembership();
        userMembership.setNvUserName(getString(USER_NAME));
        userMembership.setNvUserPassword(getString(USER_PASSWORD));
        user.setoUserMemberShip(userMembership);

        UserQuickBlox userQuickBlox = new UserQuickBlox();
        userQuickBlox.setID(getInt(QB_ID));
        userQuickBlox.setiUserId(getInt(USER_ID));
        userQuickBlox.setLogin(getString(QB_LOGIN));
        userQuickBlox.setPassword(getString(QB_PASSWORD));

        user.setoUserQuickBlox(userQuickBlox);
        return user;
    }

    public void logoffUser() {
        saveInteger(USER_ID, -1);
        saveString(USER_FULL_NAME, null);
        saveString(USER_NAME, null);
        saveString(USER_PASSWORD, null);
        saveString(USER_EMAIL, null);
    }
}
