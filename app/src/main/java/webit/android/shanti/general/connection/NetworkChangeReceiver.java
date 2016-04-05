package webit.android.shanti.general.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import webit.android.shanti.main.groups.GroupChatFragment;
import webit.android.shanti.main.messages.ChatFragment;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (isOnline(context)) {
            GroupChatFragment.getInstance().changeInternet(true);
            ChatFragment.getInstance().changeInternet(true);
            Toast.makeText(context, "have", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "have not", Toast.LENGTH_LONG).show();
            GroupChatFragment.getInstance().changeInternet(false);
            ChatFragment.getInstance().changeInternet(false);
        }


    }


    public boolean isOnline(Context context) {
        try {
            Thread.sleep(37 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        Toast.makeText(context, (netInfo != null && netInfo.isConnected()) ? "true" : "false", Toast.LENGTH_LONG).show();
        return (netInfo != null && netInfo.isConnected());

    }

}