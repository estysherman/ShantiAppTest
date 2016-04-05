package webit.android.shanti.general.gcm;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import webit.android.shanti.R;
import webit.android.shanti.login.signin.SignUpActivity;
import webit.android.shanti.main.MainActivity;

public class NotificationUserLogoff extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_user_logoff);
        if (getIntent().getExtras() != null)
            showNotificationDialog(getIntent().getExtras().getString(Consts.USER_LOG_OFF_TITLE), getIntent().getExtras().getString(Consts.USER_LOG_OFF_MESSAGE));
    }


    private void showNotificationDialog(String title, String message) {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(NotificationUserLogoff.this, R.style.myDialog).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, getString(R.string.notificationApprovalToGroupDialogYes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                    }
                });
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, getString(R.string.notificationApprovalToGroupDialogNo),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.show();
    }
}
