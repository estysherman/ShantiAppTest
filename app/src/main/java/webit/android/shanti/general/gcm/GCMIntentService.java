package webit.android.shanti.general.gcm;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.entities.Group;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.SPManager;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.main.MainActivity;
import webit.android.shanti.main.groups.ApprovalGroupFragment;
import webit.android.shanti.main.groups.GroupChatFragment;
import webit.android.shanti.main.map.MapFragment;
import webit.android.shanti.main.messages.ChatFragment;
import webit.android.shanti.splash.SplashActivity;

public class GCMIntentService extends IntentService {

    private static final String TAG = GCMIntentService.class.getSimpleName();
    public static int NOTIFICATION_ID = 1;
    Bundle mExtras;
    Context mContext;
    String mMessage;
    AppMode mAppMode;
    Ringtone mRingtone;
    ;
    Intent mNotificationIntent;
    int mNotificationType = -1;
    boolean mIsUserLogin = true;
    private NotificationManager notificationManager;

    public GCMIntentService() {
        super(Consts.GCM_INTENT_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "new push");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
        String messageType = googleCloudMessaging.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            this.mExtras = extras;
            //if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            //    processNotification();
            //} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            //    processNotification();
            //    // If it's a regular GCM message, do some work.
            //} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
                processNotification();
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void putNotificationOnBar(boolean autoCancel) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent contentIntent = null;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Shanti")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(mMessage))
                .setContentText(mMessage).setSmallIcon(R.drawable.shanti_logo).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.shanti_logo)).setSound(alarmSound);
        mNotificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        contentIntent = PendingIntent.getActivity(this, NOTIFICATION_ID,
                mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(autoCancel);        //Odeya
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        NOTIFICATION_ID++;
    }

    protected boolean isRunningInForeground() {
        ActivityManager manager =
                (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
        if (tasks.isEmpty()) {
            return false;
        }
        String topActivityName = tasks.get(0).topActivity.getPackageName();
        return topActivityName.equalsIgnoreCase(getPackageName());
    }

    private void processNotification() {
        mContext = getApplicationContext();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mMessage = mExtras.getString(Consts.MESSAGE);
        if (mExtras.getString(Consts.NOTIFICATION_TYPE) != null) {
            mNotificationType = Integer.parseInt(mExtras.getString(Consts.NOTIFICATION_TYPE));
        }
        mRingtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
//        Typeface myTypeface1 = Typeface.createFromAsset(mContext.getAssets(), "fonts/" + "notification.mp3");
//
//        mRingtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse("file:///android_asset/fonts/notification.mp3"));
        //mRingtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + "asset/ringtone/notification.mp3"));

        if (isRunningInForeground())
            mAppMode = AppMode.FOREGROUND;
        else mAppMode = AppMode.BACKGROUND;
        User user = Common.user;
        if (user == null) {
            mAppMode = AppMode.CLOSED;
            user = SPManager.getInstance(getApplicationContext()).getUser();
            if (user == null)
                mIsUserLogin = false;
        } else if (user.getiUserId() == -1) {
            mIsUserLogin = false;
        }

        switch (mNotificationType) {
            case Consts.NOTIFICATION_TYPE_NEW_MESSAGE_PRIVATE:
                new GeneralTask(mContext, new UseTask() {
                    @Override
                    public void doAfterTask(String result) {
                        privateChatCare(new Gson().fromJson(result, User.class));
                    }
                }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"id\":" + mExtras.getString(Consts.USER_ID_SEND) + "}", ConnectionUtil.GetUser);
                break;
            case Consts.NOTIFICATION_TYPE_NEW_MESSAGE_GROUP:

                new GeneralTask(mContext, new UseTask() {
                    @Override
                    public void doAfterTask(String result) {
                        groupChatCare(new Gson().fromJson(result, Group.class));
                    }
                }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"id\":" + mExtras.getString(Consts.GROUP_ID) + "}", ConnectionUtil.GetGroup);

                break;
            case Consts.NOTIFICATION_TYPE_GLOBAL_MESSAGE:
                new GeneralTask(mContext, new UseTask() {
                    @Override
                    public void doAfterTask(String result) {
                        globalMessageCare(new Gson().fromJson(result, User.class));
                    }
                }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"id\":" + mExtras.getString(Consts.USER_ID_SEND) + "}", ConnectionUtil.GetUser);
                break;
            case Consts.NOTIFICATION_TYPE_APPROVAL_GROUP:
                approvalGroupCare();
                break;
        }
    }

    private void privateChatCare(User user) {
        if (!mIsUserLogin) {
            mNotificationIntent = new Intent(mContext, NotificationUserLogoff.class);
            mNotificationIntent.putExtra(Consts.USER_LOG_OFF_TITLE, getString(R.string.notificationPrivateChatDialogTitle, user.getNvShantiName()));
            mNotificationIntent.putExtra(Consts.USER_LOG_OFF_MESSAGE, getString(R.string.notificationPrivateChatDialogMessage));
            putNotificationOnBar(true);

        } else {
            switch (mAppMode) {
                case FOREGROUND:
                    if (!(MainActivity.getInstance().getCurrentFragment().equals(ChatFragment.class.toString())) || ChatFragment.currentChatUserId != user.getiUserId()) {
                        SPManager.getInstance(mContext).saveInteger(SPManager.NOTIFICATION_COUNTER, SPManager.getInstance(mContext).getInt(SPManager.NOTIFICATION_COUNTER, 0) + 1);
                        MainActivity.getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MapFragment.getInstance().updateNotificationsCounter();
                            }
                        });
                        mRingtone.play();
                    }
                    break;
                case BACKGROUND:
                    if (!MainActivity.getInstance().getCurrentFragment().equals(ChatFragment.class.toString()) || user.getiUserId() == ChatFragment.currentChatUserId) {
                        //MainActivity.getInstance().initFragment(MessagesFragment.getInstance());
                        MainActivity.getInstance().initFragment(ChatFragment.getInstance(getApplicationContext(), user.getJson()));
                    }
                    mNotificationIntent = new Intent(mContext, MainActivity.class);
                    putNotificationOnBar(true);
                    break;
                case CLOSED:
                    mNotificationIntent = new Intent(mContext, SplashActivity.class);
                    mNotificationIntent.putExtras(mExtras);
                    mNotificationIntent.putExtra(Consts.USER_AS_JSON, user.getJson());
                    putNotificationOnBar(true);
                    break;
            }
        }
    }

    private void groupChatCare(Group group) {
        String jsonGroup = group.getJson();
        if (!mIsUserLogin) {
            mNotificationIntent = new Intent(mContext, NotificationUserLogoff.class);
            mNotificationIntent.putExtra(Consts.USER_LOG_OFF_TITLE, getString(R.string.notificationApprovalToGroupDialogTitle));
            mNotificationIntent.putExtra(Consts.USER_LOG_OFF_MESSAGE, getString(R.string.notificationApprovalToGroupDialogMessage));
            putNotificationOnBar(true);
        } else
            switch (mAppMode) {
                case FOREGROUND:
                    if (GroupChatFragment.currentChatUserGroupId == -1 || GroupChatFragment.currentChatUserGroupId != -1 && GroupChatFragment.currentChatUserGroupId != group.getiGroupId())
                        showDialog(getString(R.string.notificationGroupChatDialogTitle, group.getNvGroupName()), getString(R.string.notificationGroupChatDialogMessage), GroupChatFragment.getInstance(getApplicationContext(), group.getJson()));
                    break;
                case BACKGROUND:
                    if (!MainActivity.getInstance().getCurrentFragment().equals(GroupChatFragment.class.toString()) || group.getiGroupId() == GroupChatFragment.currentChatUserGroupId) {
                        {
                            MainActivity.getInstance().initFragment(GroupChatFragment.getInstance(getApplicationContext(), jsonGroup));
                            mNotificationIntent = new Intent(mContext, MainActivity.class);
                            putNotificationOnBar(true);
                        }
                    } else {
                        mNotificationIntent = new Intent(mContext, MainActivity.class);
                        putNotificationOnBar(true);
                    }
                    break;
                case CLOSED:
                    mNotificationIntent = new Intent(mContext, SplashActivity.class);
                    mNotificationIntent.putExtras(mExtras);
                    mNotificationIntent.putExtra(Consts.GROUP_AS_JSON, jsonGroup);
                    putNotificationOnBar(true);
                    break;
            }

    }

    private void approvalGroupCare() {
        if (!mIsUserLogin) {
            mNotificationIntent = new Intent(mContext, NotificationUserLogoff.class);
            mNotificationIntent.putExtra(Consts.USER_LOG_OFF_TITLE, getString(R.string.notificationApprovalToGroupDialogTitle));
            mNotificationIntent.putExtra(Consts.USER_LOG_OFF_MESSAGE, getString(R.string.notificationApprovalToGroupDialogMessage));
            putNotificationOnBar(true);
        } else
            switch (mAppMode) {
                case FOREGROUND:
                    if (MainActivity.getInstance().getCurrentFragment().equals(ApprovalGroupFragment.class.toString())) {
                        ApprovalGroupFragment.getInstance().refresh();
                        //MainActivity.getInstance().onBackPressed();
                        //MainActivity.getInstance().initFragment(ApprovalGroupFragment.getInstance());
                    }
                    foregroundShowDialog(getString(R.string.notificationApprovalToGroupDialogTitle), getString(R.string.notificationApprovalToGroupDialogMessage), ApprovalGroupFragment.getInstance());
                    break;
                case BACKGROUND:
                    if (MainActivity.getInstance() != null) {
                        if (!MainActivity.getInstance().getCurrentFragment().equals(ApprovalGroupFragment.class.toString())) {
                            MainActivity.getInstance().initFragment(ApprovalGroupFragment.getInstance());
                        } else {
                            ;
                            ApprovalGroupFragment.getInstance().refresh();
                            //MainActivity.getInstance().onBackPressed();
                            //MainActivity.getInstance().initFragment(ApprovalGroupFragment.getInstance());

                        }
                        mNotificationIntent = new Intent(mContext, MainActivity.class);
                        putNotificationOnBar(true);
                    }
                    break;
                case CLOSED:
                    mNotificationIntent = new Intent(mContext, SplashActivity.class);
                    mNotificationIntent.putExtras(mExtras);
                    putNotificationOnBar(true);
                    break;
            }
    }

    private void globalMessageCare(User user) {
        switch (mAppMode) {
            case FOREGROUND:
                if (MapFragment.getInstance() != null && user != null && mMessage != null) {
                    MainActivity.getInstance().messageReceivedDialog(user, mMessage);
                }
                break;
            case BACKGROUND:
                if (MapFragment.getInstance() != null) {
                    MainActivity.getInstance().messageReceivedDialog(user, mMessage);
                    mNotificationIntent = new Intent(mContext, MainActivity.class);
                    mNotificationIntent.putExtras(mExtras);
                    putNotificationOnBar(true);
                }
                //backgroundMessageCare(context.getString(R.string.publicMessageMessage) + ", " + message);
                break;
            case CLOSED:
                mNotificationIntent = new Intent(mContext, SplashActivity.class);
                mNotificationIntent.putExtras(mExtras);
                mNotificationIntent.putExtra(Consts.USER_AS_JSON, user.getJson());
                putNotificationOnBar(true);
                break;
        }
    }

    private void showDialog(final String title, final String message, final Fragment fragment) {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(MainActivity.getInstance(), R.style.myDialog).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.notificationApprovalToGroupDialogYes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (!MainActivity.getInstance().getCurrentFragment().equals(fragment.getClass().toString())) {
                            MainActivity.getInstance().initFragment(fragment);
                        }
                    }
                });
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.notificationApprovalToGroupDialogNo),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void foregroundShowDialog(final String title, final String message, final Fragment fragment) {
        //if i create AlertDialog without AsyncTask it's not appear!!!!!!!!?????
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                showDialog(title, message, fragment);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public enum AppMode {
        FOREGROUND,
        BACKGROUND,
        CLOSED
    }
}