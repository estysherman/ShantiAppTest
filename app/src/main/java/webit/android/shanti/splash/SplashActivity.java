package webit.android.shanti.splash;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Locale;

import webit.android.shanti.BuildConfig;
import webit.android.shanti.R;
import webit.android.shanti.chat.chatManager.ApplicationSingleton;
import webit.android.shanti.chat.chatManager.core.ChatMainManager;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.SPManager;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.gcm.PlayServicesHelper;
import webit.android.shanti.login.signin.LoginFragment;
import webit.android.shanti.login.signin.MainLoginFragment;
import webit.android.shanti.login.signin.SignUpActivity;
import webit.android.shanti.main.MainActivity;

public class SplashActivity extends Activity {
    private String TAG = "SplashActivity";
    private Tracker mTracker;
    private PlayServicesHelper playServicesHelper;
    private boolean mIsResume = false;
    private static int IS_NOT_ONLINE = 1990;
    private String DeviceDisplayLanguage;
    private int LanguageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//בקשה לרכיב מסוים
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_splash);
        //webit.eztaxi
        BugSenseHandler.initAndStartSession(SplashActivity.this, "aacee323");


        ApplicationSingleton application = (ApplicationSingleton) getApplication();
        //לסטטיסטיקה
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
//מספר ושם גרסה
        String versionName = BuildConfig.VERSION_NAME;
        ((TextView) findViewById(R.id.versionNumber)).setText("version " + versionName);
        DeviceDisplayLanguage = Locale.getDefault().getDisplayLanguage();
        if (DeviceDisplayLanguage.equals(this.getString(R.string.DeviceDisplayHebrew))) {
            LanguageId = 1;
        }
        if (DeviceDisplayLanguage.equals(this.getString(R.string.DeviceDisplayEnglish)))
            LanguageId = 2;
        startLogin();

    }

    private boolean checkIsOnline() {//בדיקת חיבור לאינטרנט
        if (!Common.isNetworkAvailable(this)) {//אם אין חיבור
            AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.myDialog).create();
            alertDialog.setTitle("");
            alertDialog.setMessage(getString(R.string.splashNoInternetConnection));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.splashNoInternetOk),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            onBackPressed();
//                            Intent intent = new Intent(Intent.ACTION_MAIN);
//                            intent.setClassName("com.android.settings", "com.android.settings.wifi.WifiSettings");
//                            startActivityForResult(intent, IS_NOT_ONLINE);
                            startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), IS_NOT_ONLINE);//פתיחת חלון הגדרות של wifi
                        }
                    });
            alertDialog.show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsResume = true;
        if (playServicesHelper != null)
            playServicesHelper.checkPlayServices();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsResume = false;
    }

    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(LoginFragment.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void newUser(final ProgressDialog progressDialog) {
        new CountDownTimer(1000, 2000) {
            @Override
            public void onTick(long l) {
                progressDialog.dismiss();
            }

            @Override
            public void onFinish() {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));//כניסה  למסך רישום
                finish();
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Activity.RESULT_OK) {
            if (resultCode == IS_NOT_ONLINE) {
                startLogin();
            }
        }
    }

    private void startLogin() {
        if (checkIsOnline()) {//אם יש חיבור לאינטרנט
            playServicesHelper = new PlayServicesHelper(this);
            ChatMainManager.getInstance(this.getApplicationContext()).fastConfigInit();
            final ProgressDialog progressDialog = new ProgressDialog(SplashActivity.this);
            if (mIsResume)
                progressDialog.show(SplashActivity.this, getString(R.string.loading), "");

            if (Common.user == null)
                Common.user = SPManager.getInstance(this).getUser();//שליפת משתמש מהזכרון

            final Intent intent = getIntent();//splash

            if (Common.user.getoUserMemberShip() != null && Common.user.getoUserMemberShip().getNvUserName() != null&&(!Common.user.getNvGoogleUserId().equals("")||!Common.user.getNvFacebookUserId().equals(""))) {
                if (!Common.user.getNvGoogleUserId().equals("")) {//נכנס דרך google+
                    MainLoginFragment.updateId(this, ConnectionUtil.UpdateGoogleId, Common.user.getoUserMemberShip().getNvUserName(), Common.user.getNvGoogleUserId());//שליפת נתונים מgoogle+
                } else if (!Common.user.getNvFacebookUserId().equals("")) {//נכנס דרך facebook
                    MainLoginFragment.updateId(this, ConnectionUtil.UpdateFacebookId, Common.user.getoUserMemberShip().getNvUserName(), Common.user.getNvFacebookUserId());//שליפת נתונים מfacebook
                } else
                    newUser(progressDialog);//משתמש חדש
                return;
            }
            //register user
            //כניסה דרך login
            if (Common.user.getiUserId() != -1 && Common.user.getoUserMemberShip().getNvUserName() != null) {
                LoginFragment.setContext(SplashActivity.this);
                LoginFragment.loginToServer(Common.user.getoUserMemberShip().getNvUserName(), Common.user.getoUserMemberShip().getNvUserPassword(),SPManager.getInstance(this).getString(SPManager.REG_ID),Common.user.getiDeviceTypeId(),LanguageId, null, new LoginFragment.LoginCallback() {
                            @Override
                            public void onLogin(User user) {
                                progressDialog.dismiss();
                                if (user != null && user.getiUserId() != -1) {
                                    Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);//פתיחת מסך אנשי קשר
                                    if (intent.getExtras() != null) {
                                        mainIntent.putExtras(intent.getExtras());//העברת הנתונים ממסך הspalsh
                                    }
                                    startActivity(mainIntent);
                                } else {
                                    startActivity(new Intent(getApplicationContext(), SignUpActivity.class));//פתיחת מסך רישום
                                }
                                finish();
                            }
                        }
                );
            }
            //new user
            else
                newUser(progressDialog);


//        Resources res = getResources();
//        // Change locale settings in the app.
//        DisplayMetrics dm = res.getDisplayMetrics();
//        android.content.res.Configuration conf = res.getConfiguration();
//        conf.locale = new Locale("he".toLowerCase());
//        res.updateConfiguration(conf, dm);
        }
    }
}
