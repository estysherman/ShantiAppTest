//package webit.android.shanti.general.gcm;
//
//import android.app.ActivityManager;
//import android.app.IntentService;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.BitmapFactory;
//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.NotificationCompat;
//import android.util.Log;
//
//import com.google.android.gms.gcm.GoogleCloudMessaging;
//import com.google.gson.Gson;
//
//import java.util.List;
//
//import webit.android.shanti.R;
//import webit.android.shanti.chat.chatManager.ui.ChatFragment;
//import webit.android.shanti.entities.User;
//import webit.android.shanti.general.Common;
//import webit.android.shanti.general.SPManager;
//import webit.android.shanti.general.connection.ConnectionUtil;
//import webit.android.shanti.general.connection.GeneralTask;
//import webit.android.shanti.general.connection.UseTask;
//import webit.android.shanti.main.MainActivity;
//import webit.android.shanti.main.groups.ApprovalGroupFragment;
//import webit.android.shanti.main.groups.GroupChatFragment;
//import webit.android.shanti.main.groups.GroupsListFragment;
//import webit.android.shanti.main.map.MapFragment;
//import webit.android.shanti.main.messages.MessagesFragment;
//import webit.android.shanti.splash.SplashActivity;
//
//public class GCMIntentService1 extends IntentService {
//
//    public enum AppMode {
//        FOREGROUND,
//        BACKGROUND,
//        CLOSED
//    }
//
//    public static int NOTIFICATION_ID = 1;
//    //public static String ARG_NOTIFICATION_ID = "notificationId";
//    Bundle extras;
//    Context context;
//
//    private static final String TAG = GCMIntentService1.class.getSimpleName();
//
//    private NotificationManager notificationManager;
//
//    public GCMIntentService1() {
//        super(Consts.GCM_INTENT_SERVICE);
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        Log.i(TAG, "new push");
//        Bundle extras = intent.getExtras();
//        GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
//        String messageType = googleCloudMessaging.getMessageType(intent);
//
//        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
//            this.extras = extras;
//            if (GoogleCloudMessaging.
//                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//                processNotification(Consts.GCM_SEND_ERROR, extras);
//            } else if (GoogleCloudMessaging.
//                    MESSAGE_TYPE_DELETED.equals(messageType)) {
//                processNotification(Consts.GCM_DELETED_MESSAGE, extras);
//                // If it's a regular GCM message, do some work.
//            } else if (GoogleCloudMessaging.
//                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
//                // Post notification of received message.
//                processNotification(Consts.GCM_RECEIVED, extras);
//                Log.i(TAG, "Received: " + extras.toString());
//            }
//        }
//        // Release the wake lock provided by the WakefulBroadcastReceiver.
//        GcmBroadcastReceiver.completeWakefulIntent(intent);
//    }
//
//    protected boolean isRunningInForeground() {
//        ActivityManager manager =
//                (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
//        if (tasks.isEmpty()) {
//            return false;
//        }
//        String topActivityName = tasks.get(0).topActivity.getPackageName();
//        return topActivityName.equalsIgnoreCase(getPackageName());
//    }
//
//    private void processNotification(String type, final Bundle extras) {
//        context = getApplicationContext();
//
//        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        final String message = extras.getString(Consts.MESSAGE);
//        int userId = Integer.parseInt(extras.getString(Consts.USER_ID_SEND) == null ? "0" : extras.getString(Consts.USER_ID_SEND));
//
//
//        new GeneralTask(context, new UseTask() {
//            @Override
//            public void doAfterTask(String result) {
//                User writeUser = new Gson().fromJson(result, User.class);
//                if (writeUser.getiUserId() == writeUser.getiUserId()) {
//                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                    Intent dialogIntent;
//                    int notificationType;
//                    try {
//                        notificationType = Integer.parseInt(extras.getString(Consts.NOTIFICATION_TYPE));
//                    } catch (NumberFormatException e) {
//                        notificationType = -1;
//                    }
//
//                    AppMode mode;
//                    User user;
//                    boolean isUserLogin = true;
//                    if (isRunningInForeground())
//                        mode = AppMode.FOREGROUND;
//                    else mode = AppMode.BACKGROUND;
//                    user = Common.user;
//                    if (user == null) {
//                        mode = AppMode.CLOSED;
//                        user = SPManager.getInstance(getApplicationContext()).getUser();
//                        if (user == null)
//                            isUserLogin = false;
//                    } else if (user.getiUserId() == -1) {
//                        isUserLogin = false;
//                    }
//                    switch (notificationType) {
//
//                        case Consts.NOTIFICATION_TYPE_NEW_MESSAGE_PRIVATE:
//
//                            if (!isUserLogin) {
//                                dialogIntent = new Intent(context, NotificationUserLogoff.class);
//                                dialogIntent.putExtra(Consts.USER_LOG_OFF_TITLE, getString(R.string.notificationPrivateChatDialogTitle, writeUser.getNvShantiName()));
//                                dialogIntent.putExtra(Consts.USER_LOG_OFF_MESSAGE, getString(R.string.notificationPrivateChatDialogMessage));
//                                putNotificationOnBar(dialogIntent, message, true);
//
//                            } else {
//                                switch (mode) {
//                                    case FOREGROUND:
//                                        if (!(MainActivity.getInstance().getCurrentFragment().equals(ChatFragment.class.toString())) || ChatFragment.currentChatUserId != writeUser.getiUserId()) {
//                                            SPManager.getInstance(context).saveInteger(SPManager.NOTIFICATION_COUNTER, SPManager.getInstance(context).getInt(SPManager.NOTIFICATION_COUNTER, 0) + 1);
//                                            MainActivity.getInstance().runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    MapFragment.getInstance().updateNotificationsCounter();
//                                                }
//                                            });
//                                            r.play();
//                                        }
//                                        break;
//                                    case BACKGROUND:
//                                        backgroundMessageCare(message, MessagesFragment.getInstance(), 0);
//                                        break;
//                                    case CLOSED:
//                                        dialogIntent = new Intent(context, SplashActivity.class);
//                                        dialogIntent.putExtras(extras);
//                                        putNotificationOnBar(dialogIntent, message, true);
//                                        break;
//                                }
//                            }
//                            break;
//
//                        case Consts.NOTIFICATION_TYPE_GLOBAL_MESSAGE:
//                            switch (mode) {
//                                case FOREGROUND:
//                                    if (MapFragment.getInstance() != null) {
//                                        MainActivity.getInstance().messageReceivedDialog(user, message);
//                                    }
//                                    break;
//                                case BACKGROUND:
//                                    if (MapFragment.getInstance() != null) {
//                                        MainActivity.getInstance().messageReceivedDialog(user, message);
//                                        dialogIntent = new Intent(context, MainActivity.class);
//                                        dialogIntent.putExtras(extras);
//                                        putNotificationOnBar(dialogIntent, context.getString(R.string.publicMessageMessage) + ", " + message, true);
//                                    }
//                                    //backgroundMessageCare(context.getString(R.string.publicMessageMessage) + ", " + message);
//                                    break;
//                                case CLOSED:
//                                    dialogIntent = new Intent(context, SplashActivity.class);
//                                    dialogIntent.putExtras(extras);
//                                    putNotificationOnBar(dialogIntent, context.getString(R.string.publicMessageMessage) + ", " + message, true);
//                                    break;
//                            }
//                            break;
//
//                        case Consts.NOTIFICATION_TYPE_APPROVAL_GROUP:
//                            if (!isUserLogin) {
//                                dialogIntent = new Intent(context, NotificationUserLogoff.class);
//                                dialogIntent.putExtra(Consts.USER_LOG_OFF_TITLE, getString(R.string.notificationApprovalToGroupDialogTitle));
//                                dialogIntent.putExtra(Consts.USER_LOG_OFF_MESSAGE, getString(R.string.notificationApprovalToGroupDialogMessage));
//                                putNotificationOnBar(dialogIntent, message, true);
//                            } else
//                                switch (mode) {
//                                    case FOREGROUND:
//                                        foregroundShowDialog(getString(R.string.notificationApprovalToGroupDialogTitle), getString(R.string.notificationApprovalToGroupDialogMessage), ApprovalGroupFragment.getInstance());
//                                        break;
//                                    case BACKGROUND:
//                                        if (MainActivity.getInstance() != null) {
//                                            if (!MainActivity.getInstance().getCurrentFragment().equals(ApprovalGroupFragment.class.toString())) {
//                                                MainActivity.getInstance().initFragment(ApprovalGroupFragment.getInstance());
//                                            }
//                                            dialogIntent = new Intent(context, MainActivity.class);
//                                            putNotificationOnBar(dialogIntent, message, true);
//                                        }
//                                        break;
//                                    case CLOSED:
//                                        dialogIntent = new Intent(context, SplashActivity.class);
//                                        dialogIntent.putExtras(extras);
//                                        putNotificationOnBar(dialogIntent, message, true);
//                                        break;
//                                }
//                            break;
//
//                        case Consts.NOTIFICATION_TYPE_NEW_MESSAGE_GROUP:
//                            if (!isUserLogin) {
//                                dialogIntent = new Intent(context, NotificationUserLogoff.class);
//                                dialogIntent.putExtra(Consts.USER_LOG_OFF_TITLE, getString(R.string.notificationApprovalToGroupDialogTitle));
//                                dialogIntent.putExtra(Consts.USER_LOG_OFF_MESSAGE, getString(R.string.notificationApprovalToGroupDialogMessage));
//                                putNotificationOnBar(dialogIntent, message, true);
//                            } else
//                                switch (mode) {
//                                    case FOREGROUND:
//                                        new GeneralTask(context, new UseTask() {
//                                            @Override
//                                            public void doAfterTask(String result) {
//                                                User user = new Gson().fromJson(result, User.class);
//                                                if (user.getiUserId() != -1) {
//                                                    foregroundShowDialog(getString(R.string.notificationApprovalToGroupDialogTitle), getString(R.string.notificationApprovalToGroupDialogMessage), GroupChatFragment.getInstance());
//                                                }
//                                            }
//                                        }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"id\":" + Integer.parseInt(extras.getString(Consts.USER_ID_SEND) + "") + "}", ConnectionUtil.GetUser);
//                                        break;
//                                    case BACKGROUND:
//                                        backgroundMessageCare(message, GroupsListFragment.getInstance(), 0);
//                                        break;
//                                    case CLOSED:
//                                        dialogIntent = new Intent(context, SplashActivity.class);
//                                        dialogIntent.putExtras(extras);
//                                        putNotificationOnBar(dialogIntent, message, true);
//                                        break;
//                                }
//                    }
//                }
//            }
//        }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"id\":" + userId + "}", ConnectionUtil.GetUser);
//
//    }
//
//    private void foregroundShowDialog(final String title, final String message, final Fragment fragment) {
//        //if i create AlertDialog without AsyncTask it's not appear!!!!!!!!?????
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//
//                android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(MainActivity.getInstance(), R.style.myDialog).create();
//                alertDialog.setTitle(title);
//                alertDialog.setMessage(message);
//                alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.notificationApprovalToGroupDialogYes),
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                                if (!MainActivity.getInstance().getCurrentFragment().equals(fragment.getClass().toString())) {
//                                    MainActivity.getInstance().initFragment(fragment);
//                                }
//                            }
//                        });
//                alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.notificationApprovalToGroupDialogNo),
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                alertDialog.show();
//            }
//        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//
//    }
//
//    private void backgroundMessageCare(final String message, final Fragment fragment, final int currentUserIdInChat) {
//        if (!MainActivity.getInstance().getCurrentFragment().equals(fragment.getClass().toString()) || currentUserIdInChat != -1 && currentUserIdInChat != Integer.parseInt(extras.getString(Consts.USER_ID_SEND)))
//            MainActivity.getInstance().initFragment(fragment);
//        Intent dialogIntent = new Intent(context, MainActivity.class);
//        putNotificationOnBar(dialogIntent, message, true);
//    }
//
//    private void putNotificationOnBar(Intent dialogIntent, String message, boolean autoCancel) {
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        PendingIntent contentIntent = null;
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                .setContentTitle("Shanti")
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText(message))
//                .setContentText(message).setSmallIcon(R.drawable.shanti_logo).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.shanti_logo)).setSound(alarmSound);
//
//        if (dialogIntent != null) {
//            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            contentIntent = PendingIntent.getActivity(this, NOTIFICATION_ID,
//                    dialogIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//            mBuilder.setContentIntent(contentIntent);
//        }
//        mBuilder.setAutoCancel(autoCancel);        //Odeya
//        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        mBuilder.setSound(uri);
//
//        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//        NOTIFICATION_ID++;
//    }
//}