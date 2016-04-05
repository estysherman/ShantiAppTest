package webit.android.shanti.chat.chatManager;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.users.model.QBUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import webit.android.shanti.R;
//מחלקה זו מטפלת בדיאלוג - צ'אט
public class ApplicationSingleton extends MultiDexApplication {

    private QBUser currentUser;

    private Map<Integer, QBUser> dialogsUsers = new HashMap<Integer, QBUser>();

    @Override
    public void onCreate() {
        super.onCreate();
    }
    private Tracker mTracker;
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);//analytics = ניתוחי
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    public QBUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(QBUser currentUser) {
        this.currentUser = currentUser;
    }

    public Map<Integer, QBUser> getDialogsUsers() {
        return dialogsUsers;
    }

    public void setDialogsUsers(List<QBUser> setUsers) {
        dialogsUsers.clear();

        for (QBUser user : setUsers) {
            dialogsUsers.put(user.getId(), user);
        }
    }

    //מוסיף לרשימת משתמשי הדיאלוג - משתמשים חדשים
    public void addDialogsUsers(List<QBUser> newUsers) {
        for (QBUser user : newUsers) {
            dialogsUsers.put(user.getId(), user);
        }
    }

    //מקבל - דיאלוג פרטי, ומחזיר - קוד של השותף לשיחה שלי
    //opponentID = Users IDs who chat in this dialog
    public Integer getOpponentIDForPrivateDialog(QBDialog dialog){
        Integer opponentID = -1;
        for(Integer userID : dialog.getOccupants()){
            if(!userID.equals(getCurrentUser().getId())){//אם זה לא CurrentUser
                opponentID = userID;
                break;
            }
        }
        return opponentID;
    }



    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */

}
