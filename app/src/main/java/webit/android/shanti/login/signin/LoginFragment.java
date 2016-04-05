package webit.android.shanti.login.signin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.chat.chatManager.core.ChatMainManager;
import webit.android.shanti.entities.User;
import webit.android.shanti.entities.UserMembership;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.SPManager;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.main.MainActivity;
import webit.android.shanti.splash.SplashActivity;

//הקוד של הכפתור כניסה
public class LoginFragment extends SignUpBaseFragment {

    public static Activity baseContext;
    private View mRootView;

    private static EditText mEmailET;
    public static EditText mPasswordEt;
    public static TextView mForgotPassword;
    private static Button mEnterBtn;
    private String DeviceDisplayLanguage;
    private int LanguageId;
    public static TextView timerValue;

    private static int errorCount = 0;


    public static void setContext(Activity context) {
        baseContext = context;
    }
    //שליפת ה USER
    public static void loginToServer(final String userName, final String userPassword, final String tokenId, final int DeviceId, final int iLanguageId ,final EditText passwordView, final LoginCallback callback) {
        Utils.HideKeyboard(baseContext);
        if (isLoginValid(userName, userPassword)) {//אם השדות מלאים
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", userName);
                jsonObject.put("id", userPassword);//שומר ב JSON שם משתמש וסיסמא
                jsonObject.put("tokenId", tokenId);
                jsonObject.put("DeviceId", DeviceId);
                jsonObject.put("iLanguageId", iLanguageId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new GeneralTask(baseContext, new UseTask() {
                @Override
                public void doAfterTask(String result) {
                    User u = new Gson().fromJson(result, User.class);//מקבל את המשתמש מהשרת
                    if (u == null || u.getiUserId() == -1) {//אם משתמש לא קיים
                        hideKeyboard(baseContext);
                        if (passwordView != null)
                            passwordView.setText("");//מנקה את שדה סיסמא

                        Toast.makeText(baseContext, baseContext.getString(R.string.mainLoginError), Toast.LENGTH_LONG).show();

                        //שמירת שעת שגיאה + 15 דקות
                        Calendar errorTime= Calendar.getInstance();
                        errorTime.add(Calendar.MINUTE, 3);
                        String time = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").format(errorTime.getTime());
                        //שמירת שעת שגיאה ב SPManager
                        SPManager.getInstance(baseContext).saveString(SPManager.getInstance(baseContext).ERROR_LOGIN_END_TIME, time);
                        //errorCount = 0;
                        errorCount = errorCount + 1;
                        if (errorCount == 5)
                            loginErrorCare();
                        callback.onLogin(u);

                    } else if (u.getiUserId() == -2) {
                        Toast.makeText(baseContext.getApplicationContext(),baseContext.getString(R.string.loginErrorDuplicateDevices), Toast.LENGTH_LONG).show();
                    } else  {//אם משתמש קיים
                            UserMembership userMembership = new UserMembership();//שומר את הנתונים
                            userMembership.setNvUserName(userName);
                            userMembership.setNvUserPassword(userPassword);
                            u.setoUserMemberShip(userMembership);
                            Common.user = u;//מציב את המשתמש שקיבלנו כמשתמש נוכחי
                            ChatMainManager.getInstance(baseContext).signIn(Common.user.getQBUserToChat());
                            SPManager.getInstance(baseContext).saveUser(u);
                            updateRegIdInServer();//בכל LOGIN שולח עדכון של קוד משתמש, מספר מכשיר וסוג מכשיר - משום שלפעמים הקוד מכשיר משתנה
                            /////////////////
//                            new PutMarkerOnMapTask(baseContext, Common.user, PutMarkerOnMapTask.Template.MY_TEMPLATE, new PutMarkerOnMapTask.CallBack() {
//                                @Override
//                                public void doAfterTask(Bitmap croppedImg) {
//                                    //my process image
//                                    Common.user.setProcessedBitmap(croppedImg);
//                                }
//                            }
//                            ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Common.user.getNvImage());

                            callback.onLogin(Common.user);
                        }

                    }

            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), jsonObject.toString(), ConnectionUtil.LoginUser);
        } else if (baseContext instanceof SplashActivity)//מתי קורה???????????????????????????
            baseContext.startActivity(new Intent(baseContext, SignUpActivity.class));
    }

    //בודק אם עדיין לא עברו 15 דקות מהשגיאה
    private static long EndTimeError() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        try {
            cal.setTime(sdf.parse(SPManager.getInstance(baseContext).getString(SPManager.getInstance(baseContext).ERROR_LOGIN_END_TIME)));
            if (Calendar.getInstance().getTime().before(cal.getTime())) { //אם שעה נוכחית קטנה משעת שגיאה + 15 דקות
                return cal.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();//מחזיר זמן שנשאר לטיימר
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //כל זמן שלא נגמרו ה 15 דקות יציג טיימר
    private static void loginErrorCare() {
        if (EndTimeError() != -1) {
            new CountDownTimer(EndTimeError(), 1000) {//1440000, 1000 - 15 דקות

                public void onTick(long millisUntilFinished) {
                    mEmailET.setEnabled(false);
                    mEmailET.setAlpha(0.4f);
                    mPasswordEt.setEnabled(false);
                    mPasswordEt.setAlpha(0.4f);
                    mForgotPassword.setEnabled(false);
                    mForgotPassword.setAlpha(0.6f);
                    mEnterBtn.setEnabled(false);
                    mEnterBtn.setAlpha(0.6f);
                    timerValue.setVisibility(View.VISIBLE);
                    long minutesRemaining = millisUntilFinished / 60000 + 1;
                    timerValue.setText(baseContext.getString(R.string.errorPasswordTryAgainIn)+ "  " +minutesRemaining + " " + baseContext.getString(R.string.errorPasswordRemaindMinutes));//timerValue.setText( "דקות שנותרו: " +millisUntilFinished / 90000);
                    if(millisUntilFinished / 60000 < 1)
                        timerValue.setText(baseContext.getString(R.string.errorPasswordTryAgainIn)+ "  " + (millisUntilFinished / 1000)+ " " + baseContext.getString(R.string.errorPasswordRemaindSecond));
                }

                public void onFinish() {
                    mEmailET.setEnabled(true);
                    mEmailET.setAlpha(1f);
                    mPasswordEt.setEnabled(true);
                    mPasswordEt.setAlpha(1f);
                    mForgotPassword.setEnabled(true);
                    mForgotPassword.setAlpha(1f);
                    mEnterBtn.setEnabled(true);
                    mEnterBtn.setAlpha(1f);
                    timerValue.setVisibility(View.GONE);
                    errorCount = 0;
                    SPManager.getInstance(baseContext).saveString(SPManager.getInstance(baseContext).ERROR_LOGIN_END_TIME,"");
                }
            }.start();
        }
    }

    //מסתיר מקלדת
    private static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    //שולח לשרת קוד משתמש, מספר מכשיר וסוג מכשיר
    private static void updateRegIdInServer() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("iUserId", Common.user.getiUserId());
            jsonObject.put("nvTokenId", SPManager.getInstance(baseContext).getString(SPManager.REG_ID));
            jsonObject.put("iDeviceType", Common.user.getiDeviceTypeId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        new GeneralTask(baseContext, new UseTask() {
            @Override
            public void doAfterTask(String result) {

            }
        }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), jsonObject.toString(), ConnectionUtil.UpdateTokenAndDevice);
    }

    //בודק עם שדות שם משתמש וסיסמא ריקים
    private static boolean isLoginValid(String userName, String userPassword) {
        if (userName.equals("") || userPassword.equals("")) {//אם שדות ריקים
            Toast.makeText(baseContext, baseContext.getResources().getString(R.string.loginErrorRequired), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        mEmailET = (EditText) mRootView.findViewById(R.id.loginEmail);
        mPasswordEt = (EditText) mRootView.findViewById(R.id.loginPassword);
        timerValue = (TextView) mRootView.findViewById(R.id.timerValue);
        mForgotPassword = (TextView) mRootView.findViewById(R.id.loginForgetPassword);
        mEmailET.setText(SPManager.getInstance(getActivity().getApplicationContext()).getString(SPManager.USER_NAME));
        mPasswordEt.setText(SPManager.getInstance(getActivity().getApplicationContext()).getString(SPManager.USER_PASSWORD));
        DeviceDisplayLanguage = Locale.getDefault().getDisplayLanguage();
        if (DeviceDisplayLanguage.equals(getActivity().getString(R.string.DeviceDisplayHebrew))) {
            mPasswordEt.setGravity(Gravity.CENTER_VERTICAL | View.TEXT_ALIGNMENT_VIEW_START);
            LanguageId = 1;
        }
        if (DeviceDisplayLanguage.equals(getActivity().getString(R.string.DeviceDisplayEnglish)))
            LanguageId = 2;
//        final TextWatcher textWatcher = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (mEmailET.getText().toString().length() > 0 && mPasswordEt.getText().toString().length() > 0) {
//                    mEnterBtn.setEnabled(true);
//                } else
//                    mEnterBtn.setEnabled(false);
//            }
//        };
        mEnterBtn = (Button) mRootView.findViewById(R.id.loginBtn);
        mEnterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        loginErrorCare();//בודק אם צריך להפעיל את הטיימר

        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//כפתור שכחתי סיסמה
                errorCount = 0;
                initFragment(ForgetPasswordFragment.getInstance(), getString(R.string.mainLoginSignIn));
            }
        });
        baseContext = getActivity();
        sendInKeyboard();//בעת לחיצה על DONE במקלדת - יעביר אותי לדף הבא
        return mRootView;
    }



    @Override
    public void onStart() {
        Utils.showKeyBoard(mEmailET,getActivity());
        mEmailET.callOnClick();
        super.onStart();
        mEmailET.callOnClick();

    }

    //כניסה עם user קיים
    public void signIn() {
        mEnterBtn.setEnabled(false);
        final String userName = ((EditText) mRootView.findViewById(R.id.loginEmail)).getText() + "";
        final String userPassword = ((EditText) mRootView.findViewById(R.id.loginPassword)).getText() + "";
        loginToServer(userName, userPassword, SPManager.getInstance(baseContext).getString(SPManager.REG_ID), Common.user.getiDeviceTypeId(), LanguageId, mPasswordEt, new LoginCallback() {//שליפת ה USER
            @Override
            public void onLogin(User user) {
                if (user != null && user.getiUserId() != -1) {//user קיים
                    if (user.getbIsLocked() == true) {//אם מגיע משכחתי סיסמה
                        initFragment(LoginForgatPasswordFragment.getInstance(), getString(R.string.mainLoginPasswordRecovery));
                    } else {
                        startActivity(new Intent(getActivity(), MainActivity.class));//עובר לדף הבית
                        mEnterBtn.setEnabled(true);//מאפשר לחיצה
                        getActivity().finish();//סוגר את login
                    }
                }
            }
        });
        mEnterBtn.setEnabled(true);
    }


    public interface LoginCallback {
        public void onLogin(User user);
    }
    //בעת לחיצה על DONE במקלדת - יעביר אותי לדף הבא
    private void sendInKeyboard() {
        mPasswordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {//בעת פעולה בתיבת טקסט
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {//בהזזת הסמן = KeyEvent.KEYCODE_MOVE_END
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_MOVE_END)) || (i == EditorInfo.IME_ACTION_DONE)) {//בעת הזזת הסמן לסוף או לחיצה על DONE
                    signIn();//כניסה עם user קיים
                }
                return false;
            }
        });
    }

}



