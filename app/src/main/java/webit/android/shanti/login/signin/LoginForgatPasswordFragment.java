package webit.android.shanti.login.signin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.regex.Pattern;

import webit.android.shanti.R;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.SPManager;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.general.validation.InputValidator;
import webit.android.shanti.main.MainActivity;

public class LoginForgatPasswordFragment extends SignUpBaseFragment {

    private View mRootView;
    private EditText mNewPasswordEt;
    private EditText mConfirmPasswordEt;
    private Button mContinueLoginBtn;
    private static Activity baseContext;
    private String DeviceLanguage;
    private int DeviceLanguageCode;

    public LoginForgatPasswordFragment() {
        // Required empty public constructor
    }

    public static LoginForgatPasswordFragment getInstance() {
        LoginForgatPasswordFragment fragment = new LoginForgatPasswordFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_login_forgat_password, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        DeviceLanguage = Locale.getDefault().getDisplayLanguage();
        mNewPasswordEt = (EditText) mRootView.findViewById(R.id.NewPassword);
        mConfirmPasswordEt = (EditText) mRootView.findViewById(R.id.ConfirmPassword);
        mContinueLoginBtn = (Button) mRootView.findViewById(R.id.loginBtn);
        mContinueLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContinueLogin();
            }
        });
        baseContext = getActivity();
        initDeviceLanguageCode();
        return mRootView;
    }

    private void ContinueLogin() {
        if (isLoginValid(mNewPasswordEt.getText().toString(),mConfirmPasswordEt.getText().toString())) {
            changePassword();
        }
//        else
//            Toast.makeText(baseContext, getString(R.string.loginErrorAStrongPassword), Toast.LENGTH_LONG).show();
    }

    private boolean isValid(CharSequence s, Pattern sPattern) {
        return sPattern.matcher(s).matches();
    }

    private boolean isLoginValid(String newPassword, String confirmPassword) {
        boolean b = true;
        if (newPassword.equals("")) {//אם שדות ריקים
            mNewPasswordEt.setError(baseContext.getResources().getString(R.string.loginErrorRequired));
            b = false;
        }
        if (confirmPassword.equals("")) {
            mConfirmPasswordEt.setError(baseContext.getResources().getString(R.string.loginErrorRequired));
            b = false;
        }

        setValidator(mNewPasswordEt, "^(?=.*[A-Z])(?=.*[!@#$&*.?])(?=.*[0-9])(?=.*[a-z]).{8,16}$", getString(R.string.loginErrorAStrongPassword));
        final Pattern sPattern = Pattern.compile("^(?=.*[A-Z])(?=.*[!@#$&*.?])(?=.*[0-9])(?=.*[a-z]).{8,16}$");
        b = isValid(mNewPasswordEt.getText(),sPattern);


        //אם הסיסמאות לא תואמות
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(baseContext, baseContext.getResources().getString(R.string.loginErrorPasswordsDontMatch), Toast.LENGTH_LONG).show();
            b = false;
        }

        return b;
    }

    private void initDeviceLanguageCode() {
        if (DeviceLanguage.equals(getActivity().getString(R.string.DeviceDisplayHebrew))) {
            mNewPasswordEt.setGravity(Gravity.CENTER_VERTICAL | View.TEXT_ALIGNMENT_VIEW_START);
            mConfirmPasswordEt.setGravity(Gravity.CENTER_VERTICAL | View.TEXT_ALIGNMENT_VIEW_START);
            DeviceLanguageCode = 1;
        }
        else if (DeviceLanguage.equals(getActivity().getString(R.string.DeviceDisplayEnglish)))
            DeviceLanguageCode = 2;
//            else  if (DeviceLanguage.equals(getActivity().getString(R.string.DeviceDisplaySpanish)))
//                      DeviceLanguageCode = 3;
//                else  if (DeviceLanguage.equals(getActivity().getString(R.string.DeviceDisplayFrench)))
//                          DeviceLanguageCode = 4;
    }

    //הצבת שגיאה
    private void setValidator(EditText input, String pattern, String error) {
        InputValidator validator = new InputValidator(pattern, input, error, getActivity());
        input.addTextChangedListener(validator);
        input.setOnFocusChangeListener(validator);
    }



    private void changePassword() {
        new GeneralTask(baseContext, new UseTask() {
            @Override
            public void doAfterTask(String result) {
                if (result != "true") {
                    startActivity(new Intent(getActivity(), MainActivity.class));//עובר לדף הבית
                    getActivity().finish();//סוגר את login
                }
                else
                     Toast.makeText(baseContext, getString(R.string.loginError), Toast.LENGTH_SHORT).show();

            }
        }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"id\":\"" + Common.user.getiUserId() + "\",\"newName\":\"" + Common.user.getoUserMemberShip().getNvUserName()+ "\",\"password\":\"" + Common.user.getoUserMemberShip().getNvUserPassword() + "\",\"newPassword\":\"" + mNewPasswordEt.getText().toString() + "\",\"iLanguageId\":\"" + DeviceLanguageCode + "\"}", ConnectionUtil.ChangeLogin);//שליחת קוד אימות לטלפון

    }

}
