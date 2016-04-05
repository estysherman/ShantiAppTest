package webit.android.shanti.main.SearchUsers;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import webit.android.shanti.Utils.Utils;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;

/**
 * Created by crm on 18/11/2015.
 */
public class GetAllUsersService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GetAllUsersService(String name) {
        super(name);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
//        GetAllUsers();
        //todo get new users
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
 //       GetAllUsers();
    }

    private void GetAllUsers() {

        new GeneralTask(getApplicationContext(), new UseTask() {
            @Override
            public void doAfterTask(String result) {//שולף את רשימת המשתמשים מהשרת
                Type programsListType = new TypeToken<List<User>>() {
                }.getType();
                try {
                    List<User> users = new Gson().fromJson(result, programsListType);//עוד רשומות ל 2 עמודות, מהנקודה שנשלחה
                    for (int i = 0; i < users.size(); i++) {
                        users.get(i).setLastBroadcastDate(Utils.convertToJsonDate(users.get(i).getNvLastBroadcastDate()));
                        Log.d("check", "name: " + users.get(i).getNvShantiName() + " NvLast:" + users.get(i).getNvLastBroadcastDate() + " Last:" + users.get(i).getLastBroadcastDate() +
                                "  " + users.get(i).getiUserId());
                    }
                    for (int i = 0; i < users.size(); i++) {
                        if (users.get(i).getLastBroadcastDate() == null || users.get(i).getNvLastBroadcastDate().length() < 8)//אם תאריך - נראה לאחרונה לא תקין
                            users.remove(users.get(i));//מוחק מהרשימה
                        else {
                            if (Common.getExistUsers().get(users.get(i).getiUserId()) == null) {//אם לא קיים ברשימה שב common
                                Common.getAllUsers().add(users.get(i));//מוסיף ל common
                                Common.getExistUsers().put(users.get(i).getiUserId(), true);
                            }
                        }

                    }

                } catch (Exception e) {
                    Log.d("Json Exception", e.getMessage());
                }
            }
        }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"iAppLanguageId\":\"" + Common.user.getiAppLanguageId() + "\",\"numRow\":\"" + SearchUsersFragment.rowsInPage + "\",\"point\":\"" + SearchUsersFragment.point +  "\"}", ConnectionUtil.GetUsersListToSearch);
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);


        // If we get killed, after returning from here, restart

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
