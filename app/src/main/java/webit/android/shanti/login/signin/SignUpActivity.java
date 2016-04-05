package webit.android.shanti.login.signin;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.quickblox.chat.model.QBDialogType;
import java.util.Stack;
import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.chat.chatManager.ApplicationSingleton;
import webit.android.shanti.main.MainBaseFragment;
import webit.android.shanti.main.SearchUsers.SearchUsersFragment;
import webit.android.shanti.main.groups.ApprovalGroupFragment;
import webit.android.shanti.main.groups.EditGroupFragment;
import webit.android.shanti.main.info.InfoMainFragment;
import webit.android.shanti.main.info.PlaceDetailsFragment;
import webit.android.shanti.main.messages.ChatFragment;

public class SignUpActivity extends FragmentActivity implements View.OnTouchListener {
    private boolean isBackEnuble = true;
    private RelativeLayout mActionBar;
    private TextView mActionBarTitle;
    private String TAG = "SignUpActivity";
    private Tracker mTracker;
    private LinearLayout mLinearLayout;
    private Stack mBackSt = new Stack();
    private Fragment saveFragment;
    private ScrollView sv;
    public static TextView SignInActionSkipTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_up);

//        sv = (ScrollView) findViewById(R.id.scroll);
//        sv.setEnabled(false);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//סגירת מקלדת

        mLinearLayout = (LinearLayout) findViewById(R.id.linearSignUp);
        //mLinearLayout.setBackgroundResource(R.drawable.bg_enterscreens);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.mainContainer);
        frameLayout.setOnTouchListener(this);
        ApplicationSingleton application = (ApplicationSingleton) getApplication();
        //בשביל הסטטיסטיקה
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_enterscreens));
        mActionBar = (RelativeLayout) findViewById(R.id.signInActionBar);
        mActionBarTitle = (TextView) findViewById(R.id.SignInActionTitle);
        SignInActionSkipTv = (TextView) findViewById(R.id.SignInActionSkip);

        if (savedInstanceState != null) {
            switch ((String) savedInstanceState.get("saved_Fragment")) {//יש לשים לב שלא נמחק אותיות מהstring שבcase
                case "LoginFragment":
                    initFragment(new LoginFragment(), null);
                    break;
                case "BaseInfoFragment":
                    initFragment(new BaseInfoFragment(), null);
                    break;
                case "ProfileInfoFragment":
                    initFragment(new ProfileInfoFragment(), null);
                    break;
                case "TakePhotoFragment":
                    initFragment(new TakePhotoFragment(), null);
                    break;
                case "MoreInfoFragment":
                    initFragment(new MoreInfoFragment(), null);
                    break;
                case "SearchPreferencesFragment":
                    initFragment(new SearchPreferencesFragment(), null);
                    break;
                case "ForgetPasswordFragment":
                    initFragment(new ForgetPasswordFragment(), null);
                    break;
                default:
                    break;
            }
        } else
        initFragment(new MainLoginFragment(), null);
    }
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("saved_Fragment", saveFragment.getClass().getSimpleName().toString());

    }
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//    }

    public void setBackgroundForLinear(int id) {
        mLinearLayout.setBackgroundResource(id);
    }

    public void setIsBackEnable(boolean isBackEnuble) {
        this.isBackEnuble = isBackEnuble;
    }

    //בלחיצה על כפתור 'חזור'
    public void backClick(View view) {
        if (isBackEnuble) {
            getSupportFragmentManager().popBackStack();//שליפת הפרגמנט הקודם
            String previousTitle = mActionBarTitle.getText().toString();
            String currentTitle = (String) mBackSt.pop();//שולף את הכותרת הקודמת
            String title = currentTitle;
            if (currentTitle.equals(previousTitle)) {//לחיצה ראשונה על back
                if (mBackSt.empty() == false) {
                    String titleBefore = (String) mBackSt.peek();//שולף את הכותרת הקודמת
                    if (!currentTitle.equals(titleBefore))//2 דפים עם אותו שם
                        title = (String) mBackSt.pop();

                }
            }

            if (title.equals("")) {
                mActionBar.setVisibility(View.INVISIBLE);
            } else {
                mActionBar.setVisibility(View.VISIBLE);
                setActionBarTitle(title);
                if (title.equals(getString(R.string.moreInfoTitle)) || title.equals(getString(R.string.userPrefTitle)))
                    SignInActionSkipTv.setVisibility(View.VISIBLE);
                else
                    SignInActionSkipTv.setVisibility(View.GONE);
            }
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                //אם הגיע למסך האחרון - סוגר את ה activity

                finish();
            }
            Utils.HideKeyboard(this);
        }
    }

    public void initFragment(Fragment fragment, String title) {
        saveFragment = fragment;
        setBackgroundForLinear(R.drawable.bg_enterscreens);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer, fragment, fragment.getClass().toString());
        fragmentTransaction.addToBackStack("");
        fragmentTransaction.commit();
        if (title == null) {//אם אין כותרת
            mBackSt.push("");//שמירת הכותרת של ה actionbar במחסנית
            mActionBar.setVisibility(View.INVISIBLE);//הסתרת הכותרת
        } else {
//            if (!title.equals(getString(R.string.userPrefTitle))) //את הדף האחרון - לא דוחף
                mBackSt.push(title);//שמירת הכותרת של ה actionbar במחסנית
            mActionBar.setVisibility(View.VISIBLE);//מראה את הכותרת
            setActionBarTitle(title);//מציב אותה ב actionbar
            if (title.equals(getString(R.string.moreInfoTitle)) || title.equals(getString(R.string.userPrefTitle)))
                SignInActionSkipTv.setVisibility(View.VISIBLE);
            else
                SignInActionSkipTv.setVisibility(View.GONE);
        }

    }


    public void setBackground(int backgroundID) {
        findViewById(R.id.signUpLayout).setBackgroundResource(backgroundID);
    }

    public void setActionBarTitle(String title) {
        mActionBarTitle.setText(title);
    }

    @Override   //בלחיצה על כפתור 'חזור'
    public void onBackPressed() {
        setBackgroundForLinear(R.drawable.bg_enterscreens);
        backClick(null);
        //avoid duplicate register on server
//        List<Fragment> fragments = getSupportFragmentManager().getFragments();
//        if (fragments.size() > 1 && !fragments.get(fragments.size() - 1).getClass().equals(SearchDefFragment.class))
//            super.onBackPressed();
    }

    //ChatMainManager.getInstance(this.getApplicationContext())
    public void createDialog(QBDialogType dialogType) {

    }

   /* public void nextVerificationCode(View view) {
        if (InputValidator.isValidForm(getString(R.string.lpErrorRequired))) {
            nextPage(new VerificationCodeFragment());
        } else
            Toast.makeText(this, getString(R.string.lpRequiredFields), Toast.LENGTH_SHORT).show();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Utils.HideKeyboard(SignUpActivity.this);
        return true;
    }

    public enum ESignUpStep {
        baseInfo, verifyCode, profileInfo, takePhoto, moreInfo, searchPreferences
    }

}
