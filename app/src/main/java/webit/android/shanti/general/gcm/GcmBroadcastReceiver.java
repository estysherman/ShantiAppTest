package webit.android.shanti.general.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(), GCMIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//        //ComponentName componentInfo = taskInfo.get(0).topActivity;
//        if (taskInfo.get(0) != null)
//            intent.putExtra(Consts.CURRENT_ACTIVITY, taskInfo.get(0).topActivity.getClassName());
        //if(Common.currentActivity!=null)

//        if (!ChatMainManager.getInstance(context).isLogin()) {
//            Log.d("FastConfig", "fast config");
//            BugSenseHandler.initAndStartSession(context, "aacee323");
//            ChatMainManager.getInstance(context).fastConfigInit();
//        }

        startWakefulService(context, (intent.setComponent(comp)));
        //intent.putExtra("isBackground", isApplicationBroughtToBackground(context));
        setResultCode(Activity.RESULT_OK);


    }



//    private boolean isApplicationBroughtToBackground(Context context) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
//        if (!tasks.isEmpty()) {
//            ComponentName topActivity = tasks.get(0).topActivity;
//            if (!topActivity.getPackageName().equals(context.getPackageName())) {
//                return true;
//            }
//        }
//
//        return false;
//    }
}