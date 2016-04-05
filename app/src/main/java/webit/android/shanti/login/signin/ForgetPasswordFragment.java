package webit.android.shanti.login.signin;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.customViews.CustomEditText;
import webit.android.shanti.customViews.CustomViewsInitializer;
import webit.android.shanti.customViews.ICallBackLoadFinish;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.general.validation.InputValidator;


public class ForgetPasswordFragment extends SignUpBaseFragment {


    private View mRootView;
    private CustomEditText mBirthDateTV;
    private String DeviceLanguage;
    private int DeviceLanguageCode;
    private EditText mEmail;
    private EditText mPassword;

    public static ForgetPasswordFragment getInstance() {
        ForgetPasswordFragment instance = new ForgetPasswordFragment();
        return instance;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_forget_password, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        DeviceLanguage = Locale.getDefault().getDisplayLanguage();


        mEmail = (EditText) mRootView.findViewById(R.id.forgetPasswordRegisteredMail);
        mPassword = (EditText) mRootView.findViewById(R.id.generalPassword);
        initDeviceLanguageCode();
        InputValidator.collection.clear();//איפוס שדות להזכרת סיסמה
        setValidator(mEmail, "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", getString(R.string.loginErrorEmail));
        if(savedInstanceState!=null)
        {

            mEmail.setText(savedInstanceState.get("mail").toString());
        }
//        final CustomViewsInitializer initializer = new CustomViewsInitializer(getActivity(), 0, new ICallBackLoadFinish() {
//            @Override
//            public void doCallBack() {
//            }
//        });
//        initializer.setDatePicker(mRootView.findViewById(R.id.forgetPasswordCalendar), mBirthDateTV, "");//הצבת ערך ריק כברירת מחדל בלוח שנה למציאת תאריך

        mRootView.findViewById(R.id.forgetPasswordOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//כפתור אישור

                if (InputValidator.isValidForm(getString(R.string.forgetPasswordMailEmpty)))
                        new GeneralTask(getActivity(), new UseTask() {
                            @Override
                            public void doAfterTask(String result) {
                                if (result != null) {
                                    if (result.equals("true")) {//הפרטים ששלח נכונים
                                        getActivity().onBackPressed();//חזרה למסך כניסה
                                        //initializer.setDatePicker(mRootView.findViewById(R.id.forgetPasswordCalendar), mBirthDateTV, "");//איפוס שדה תאריך
                                        Toast.makeText(getActivity(), getString(R.string.forgetPasswordSentTo, mEmail.getText()), Toast.LENGTH_SHORT).show();
                                        mEmail.setText("");//איפוס שדה מייל

                                    } else
                                        Toast.makeText(getActivity(), getString(R.string.forgetPasswordNoExistMail), Toast.LENGTH_SHORT).show();

                                } else {

                                }
                            }
//todo change to live
//                        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"nvUserName\":\"" + mEmail.getText().toString() + "\", \"dtBirthDate\":\"" + Utils.convertStringToDate(mBirthDateTV.getText().toString()) + "\"}", ConnectionUtil.ForgotPassword);
                        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"nvUserName\":\"" + mEmail.getText().toString() + "\", \"iLanguageId\":\"" + DeviceLanguageCode + "\"}", ConnectionUtil.ForgotPassword);//שליחת מייל ותאריך לשחזור סיסמה
               }
        });

        return mRootView;
    }
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mail", mEmail.getText().toString());
    }

    //כאשר המייל לא תקין יציב שגיאה
    private void setValidator(EditText input, String pattern, String error) {
        InputValidator validator = new InputValidator(pattern, input, error);
        input.addTextChangedListener(validator);
        input.setOnFocusChangeListener(validator);
    }

    private void initDeviceLanguageCode() {
        if (DeviceLanguage.equals(getActivity().getString(R.string.DeviceDisplayHebrew))) {
            mPassword.setGravity(Gravity.CENTER_VERTICAL | View.TEXT_ALIGNMENT_VIEW_START);
            DeviceLanguageCode = 1;
        }
        else if (DeviceLanguage.equals(getActivity().getString(R.string.DeviceDisplayEnglish)))
                 DeviceLanguageCode = 2;
//            else  if (DeviceLanguage.equals(getActivity().getString(R.string.DeviceDisplaySpanish)))
//                      DeviceLanguageCode = 3;
//                else  if (DeviceLanguage.equals(getActivity().getString(R.string.DeviceDisplayFrench)))
//                          DeviceLanguageCode = 4;
    }

}



