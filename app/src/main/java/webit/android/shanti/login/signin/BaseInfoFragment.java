package webit.android.shanti.login.signin;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.customViews.CustomViewsInitializer;
import webit.android.shanti.customViews.MultiSpinner;
import webit.android.shanti.entities.CodeValue;
import webit.android.shanti.entities.DialingCode;
import webit.android.shanti.entities.Location;
import webit.android.shanti.entities.User;
import webit.android.shanti.entities.UserMembership;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.CountryToPhonePrefix;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.general.validation.InputValidator;


public class BaseInfoFragment extends SignUpBaseFragment implements View.OnClickListener {
    public static CustomViewsInitializer initializer = null;

    public static BaseInfoFragment instance = null;
    private EditText mShantiName;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mPhone;
    public static String mPhonePerfix;
    MultiSpinner mPhonePerfixMS;
    private boolean isLoginWith = false;
    private View mRootView;
    private ArrayList<DialingCode> mDialingCodes = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    private int selected = 0;
    private String DeviceDisplayLanguage;
    private TextView mProfileInfoUseTermsTxt;
	private static Bundle savedInstanceState1;

    public BaseInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    public static BaseInfoFragment getInstance() {
        /*if this function returns instance, the views values saved after user finish registration and do it again
        instance = new BaseInfoFragment();*/
        if (instance == null) {
            instance = new BaseInfoFragment();
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         // setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
		if (savedInstanceState != null)
            savedInstanceState1 = savedInstanceState;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mRootView = inflater.inflate(R.layout.fragment_base_info, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        mShantiName = (EditText) mRootView.findViewById(R.id.baseInfoShantiName);
        mEmail = (EditText) mRootView.findViewById(R.id.baseInfoEmail);
        mPassword = (EditText) mRootView.findViewById(R.id.baseInfoPassword);
        mFirstName = (EditText) mRootView.findViewById(R.id.baseInfoFirstName);
        mLastName = (EditText) mRootView.findViewById(R.id.baseInfoLastName);
        mPhone = (EditText) mRootView.findViewById(R.id.baseInfoPhone);
        mPhonePerfixMS = (MultiSpinner) mRootView.findViewById(R.id.baseInfoPhonePrefix);
        mProfileInfoUseTermsTxt = (TextView) mRootView.findViewById(R.id.profileInfoUseTermsTxt1);
        initTextViewOnClick();
		mShantiName.setText("ההדסה");
        mEmail.setText("kl@gmail.com");
        mPassword.setText("123");
        mFirstName.setText("הדס");
        mLastName.setText("גולדיש");
        mPhone.setText("533110465");

     
 
          DeviceDisplayLanguage = Locale.getDefault().getDisplayLanguage();
        if (DeviceDisplayLanguage.equals(getActivity().getString(R.string.DeviceDisplayHebrew)))
            mPassword.setGravity(Gravity.CENTER_VERTICAL | View.TEXT_ALIGNMENT_VIEW_START);
        //האם הגיע דרך פיסבוק או גוגל+
        if (Common.user != null && ((Common.user.getNvGoogleUserId() != null && !Common.user.getNvGoogleUserId().equals("")) || (Common.user.getNvFacebookUserId() != null && !Common.user.getNvFacebookUserId().equals("")))) {
            isLoginWith = true;
            mPassword.setVisibility(View.GONE);//שדה סיסמה לא מאופשר
            init();//בכניסה דרך פייסבוק או גוגל מאתחל את השדות בערכים שנשלחו משם
        } else {//הגיע בכניסה רגילה
            isLoginWith = false;
            mPassword.setVisibility(View.VISIBLE);
        }
        if (savedInstanceState1 != null) {

            mShantiName.setText(savedInstanceState1.get("ShantiName").toString());
            mEmail.setText(savedInstanceState1.get("Email").toString());
            mPassword.setText(savedInstanceState1.get("Password").toString());
            mFirstName.setText(savedInstanceState1.get("FirstName").toString());
            mLastName.setText(savedInstanceState1.get("LastName").toString());
            mPhone.setText(savedInstanceState1.get("Phone").toString());
        }

        //אתחול הספינר בקידומת מספר טלפון לפי מיקום המכשיר
        TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        mPhonePerfix = (CountryToPhonePrefix.prefixFor(tMgr.getNetworkCountryIso().toUpperCase()));
        initializer = new CustomViewsInitializer(getActivity());
        if (((MultiSpinner) mRootView.findViewById(R.id.baseInfoPhonePrefix)).getChooseValues().size() > 0)
            selected = Integer.parseInt(((MultiSpinner) mRootView.findViewById(R.id.baseInfoPhonePrefix)).getChooseValues().get(0));

        initializer.setSpinner(mPhonePerfixMS, CodeValue.countries, getString(R.string.signUpPhonePrefix), selected);
 		if (selected!=0) {
            Utils.HideKeyboard(getActivity());
        }        
mRootView.findViewById(R.id.baseInfoCreateUserBtn).setOnClickListener(this);//לחיצה על כפתור המשך
        mShantiName.requestFocus();
        return mRootView;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ShantiName", mShantiName.getText().toString());
        outState.putString("Email", mEmail.getText().toString());
        outState.putString("Password", mPassword.getText().toString());
        outState.putString("FirstName", mFirstName.getText().toString());
        outState.putString("LastName", mLastName.getText().toString());
        outState.putString("Phone", mPhone.getText().toString());
    }

    


    private void InitValidation() {
        InputValidator.collection.clear();//איפוס שדות
        //בדיקת תקינות מייל
        setValidatorEmail(mEmail, "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", getString(R.string.loginErrorEmail));
        if (!isLoginWith) {
            //בדיקת תקינות לסיסמה
            setValidator(mPassword, ".{2,}", getString(R.string.loginErrorAtLeast2Characters));
            setValidator(mPassword, "^(?=.*[A-Z])(?=.*[!@#$&*.?])(?=.*[0-9])(?=.*[a-z]).{8,16}$", getString(R.string.loginErrorAStrongPassword));//סיסמא חייבת להכיל לפחות 8 תווים: אותיות קטנות וגדולות, מספרים וסימני פיסוק
        }
        setValidatorShantiName(mShantiName, ".{2,}", getString(R.string.loginErrorAtLeast2Characters));
        //בדיקת תקינות לטלפון
        //בדיקת תקינות לטלפון - ישן "[-+\\d() ]{8,16}$"
        setValidator(mPhone, "^[0-9]{9}$", getString(R.string.loginErrorPhone));
        setValidator(mFirstName, ".{2,}", getString(R.string.loginErrorRequiredFirstName));
        setValidator(mLastName, ".{2,}", getString(R.string.loginErrorRequiredLastName));
    }

    @Override
    public void onStart() {//שומר קידומת לפי מיקום של משתמש או לפי בחירה בספינר
        super.onStart();
        selected = -1;
        if (Common.user != null && Common.user.getoCountry() != null)//כניסה דרך גוגל או פייסבוק
            selected = Common.user.getoCountry().getiKeyId();
        if (((MultiSpinner) mRootView.findViewById(R.id.baseInfoPhonePrefix)).getChooseValues().size() > 0)//דרך כניסה רגילה ובחרו ערך
            selected = Integer.parseInt(((MultiSpinner) mRootView.findViewById(R.id.baseInfoPhonePrefix)).getChooseValues().get(0));
        isValidPhonePrefix();
    }

    private void init() {//בכניסה דרך פייסבוק או גוגל מאתחל את השדות בערכים שנשלחו משם
        if (Common.user.getNvFirstName() != null)
            mFirstName.setText(Common.user.getNvFirstName());
        if (Common.user.getNvLastName() != null)
            mLastName.setText(Common.user.getNvLastName());
        if (Common.user.getNvEmail() != null)
            mEmail.setText(Common.user.getNvEmail());

    }

    //הצבת שגיאה
    private void setValidator(EditText input, String pattern, String error) {
        InputValidator validator = new InputValidator(pattern, input, error, getActivity());
        input.addTextChangedListener(validator);
        input.setOnFocusChangeListener(validator);
    }

    //הצבת שגיאה למייל לא תקין
    private void setValidatorEmail(EditText input, String pattern, String error) {
        InputValidator validator = new InputValidator(pattern, input, error);
        input.addTextChangedListener(validator);
        onFocusChangeEmail();//ביציאה משדה מייל בדיקה אם קיים במערכת
    }

    //הצבת שגיאה לשם שאנטי
    private void setValidatorShantiName(EditText input, String pattern, String error) {
        InputValidator validator = new InputValidator(pattern, input, error);
        input.addTextChangedListener(validator);
        onFocusChangeShantiName();//בדיקה ששם שאנטי לא קיים במערכת
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void createUser() {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPhone.getWindowToken(), 0);
//Odeya
        //if (1 == 1)
        //    nextPage(new ProfileDefFragment());
        //else {
        //בדיקות תקינות לשדות
        InitValidation();
        final Activity context = getActivity();
//אם אין שדות ריקים והכל תקין
        if (InputValidator.isValidForm(getString(R.string.loginErrorRequired)) && !mEmail.getText().toString().equals("") && !mShantiName.getText().toString().equals("") && isValidPhonePrefix()) {
            if (Common.user == null)//אם זה דרך רישום רגיל
                Common.user = new User();
            String shantiName = mShantiName.getText() + "";
            String email = mEmail.getText() + "";
            Common.user.setoLocation(new Location(0, 0, Common.user.getiUserId(),0));//מאתחל מיקום
            Common.user.setNvShantiName(shantiName);//מציב שם שאנטי
            if (!isLoginWith) {//אם זה דרך רישום רגיל
                UserMembership userMembership = new UserMembership(email, mPassword.getText() + "");
                Common.user.setoUserMemberShip(userMembership);
            } else {//דרך פייסבוק וגוגל
                UserMembership userMembership = new UserMembership(email, "");
                Common.user.setoUserMemberShip(userMembership);
            }
            Common.user.setNvEmail(email);

            Common.user.setNvLastName(mLastName.getText() + "");
            Common.user.setNvFirstName(mFirstName.getText() + "");
            Common.user.setNvPhone(mPhone.getText().toString());
             Common.user.setPhonePrefix(Integer.parseInt(((MultiSpinner) mRootView.findViewById(R.id.baseInfoPhonePrefix)).getChooseValues().get(0)));
            Common.user.setNvPhonePrefix(((MultiSpinner) mRootView.findViewById(R.id.baseInfoPhonePrefix)).getChooseValues().get(0) + "");
            Common.user.setoCountry(((MultiSpinner) mRootView.findViewById(R.id.baseInfoPhonePrefix)).getmCountry());


            new GeneralTask(getActivity(), new UseTask() {
                @Override
                public void doAfterTask(String result) {
                    if (result != null) {
                        switch (result) {
                            case "0"://מייל תקין
                                mEmail.setError(null);
                                mShantiName.setError(null);
                                new GeneralTask(context, new UseTask() {
                                    @Override
                                    public void doAfterTask(String result) {
                                        //nextPage(new ProfileDefFragment());
                                        if (result != null) {
                                            try {
                                                JSONObject jObject = new JSONObject(result);
                                                String jsonString = jObject.getString("nvValue");
                                                //VerifyCodeFragment verifyCodeFragment = new VerifyCodeFragment();
                                                //Bundle bundle = new Bundle();
                                                //bundle.putString(VerifyCodeFragment.ARG_VERIFICATION_CODE, jsonString);
                                                //verifyCodeFragment.setArguments(bundle);
                                                initFragment(VerifyCodeFragment.getInstance(jsonString), getString(R.string.mainLoginSignUp));//קוד אימות
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(context, getString(R.string.loginError), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"mobilePhone\":\"" + mPhone.getText() + "\"}", ConnectionUtil.SendVerificationCode);//שליחת קוד אימות לטלפון
                                break;
                            case "2":
                                mShantiName.setError(
                                        getResources().getString(R.string.loginShantiNameExist));
                                Toast.makeText(getActivity().getApplicationContext(), R.string.loginShantiNameExist, Toast.LENGTH_SHORT).show();
                                break;
                            case "3":
                                mEmail.setError(getResources().getString(R.string.loginEmailExist));
                                Toast.makeText(getActivity().getApplicationContext(), R.string.loginEmailExist, Toast.LENGTH_SHORT).show();
                                break;
                            case "4":
                                mEmail.setError(getResources().getString(R.string.loginEmailExist));
                                mShantiName.setError(getResources().getString(R.string.loginShantiNameExist));
                                Toast.makeText(getActivity().getApplicationContext(), R.string.loginMailShantiNameExist, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } else {
                    }
                }
            }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"nvShantiName\": \"" + shantiName + "\",\"nvEmail\":\"" + email + "\"}", ConnectionUtil.CheckUserDetailsIsFree);

        }
        //}
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.baseInfoCreateUserBtn:
                createUser();
                break;
        }
    }
    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
    public void initTextViewOnClick() {
        String s = getResources().getString(R.string.useTermsText1) + " " + getResources().getString(R.string.useTermsText2) + " " +
                getResources().getString(R.string.useTermsText3) + " " + getResources().getString(R.string.useTermsText4) + " " +
                getResources().getString(R.string.useTermsText6) + " " + getResources().getString(R.string.useTermsText7);
        int start = s.indexOf(getResources().getString(R.string.useTermsText2));
        int end = start + getResources().getString(R.string.useTermsText2).length();
        int start2 = s.indexOf(getResources().getString(R.string.useTermsText4));
        int end2 = start2 + getResources().getString(R.string.useTermsText4).length();
        SpannableString ss = new SpannableString(s);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                initFragment(ArticlesOfAssociationFragment.getInstance("https://media.termsfeed.com/pdf/terms-and-conditions-template.pdf"),
                        getString(R.string.mainLoginSignUp));//קוד אימות
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.WHITE);
            }
        };
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                initFragment(ArticlesOfAssociationFragment.getInstance("https://media.termsfeed.com/pdf/privacy-policy-template.pdf"),
                        getString(R.string.mainLoginSignUp));//קוד אימות
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.WHITE);
            }
        };
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan2, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //TextView textView = (TextView) findViewById(R.id.hello);
        mProfileInfoUseTermsTxt.setText(ss);
        mProfileInfoUseTermsTxt.setTextColor((getResources().getColor(R.color.purple_medium)));
        mProfileInfoUseTermsTxt.setMovementMethod(LinkMovementMethod.getInstance());
        //mProfileInfoUseTermsTxt.setText(Html.fromHtml(ss.toString()), TextView.BufferType.SPANNABLE);
        mProfileInfoUseTermsTxt.setHighlightColor(Color.TRANSPARENT);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    //ביציאה משדה מייל בדיקה אם קיים במערכת
    public void onFocusChangeEmail() {
        mEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isValidPhonePrefix();//בדיקת תקינות קידומת
                if (mEmail.getText().toString().equals(""))//אם לא ריק
                    mEmail.setError(getActivity().getResources().getString(R.string.loginErrorRequiredEmail));
                if (!hasFocus && !mEmail.getText().toString().equals("")) {//אם עבר משדה מייל והוא מלא
                    //put "oo" In order to no result equal "4"
                    String shantiName = mShantiName.getText() + "00";
                    String email = mEmail.getText() + "";
                    new GeneralTask(getActivity(), new UseTask() {
                        @Override
                        public void doAfterTask(String result) {
                            if (result != null && isAdded()) //מחזיר true אם הפרגמנט הצליח להתווסף לאקטביטי = IsAdded למקרה שהפרמנט מתנתק מהאקטיביטי
                                if (result.equals("3") && isAdded()) {//המייל קיים במערכת
                                    mEmail.setError(getActivity().getResources().getString(R.string.loginEmailExist));//שמים error
                                    Toast.makeText(getActivity().getApplicationContext(), R.string.loginEmailExist, Toast.LENGTH_SHORT).show();
                                } else if (isAdded()) {
                                    if (mEmail.getError() != null && mEmail.getError().toString().equals(getResources().getString(R.string.loginEmailExist)))//בודק אם יש error
                                        mEmail.setError(null);//מרוקן את ה error
                                }
                        }
                    }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"nvShantiName\": \"" + shantiName + "\",\"nvEmail\":\"" + email + "\"}", ConnectionUtil.CheckUserDetailsIsFree);//בודק שהשם שאנטי והמייל לא קיימים כבר במערכת
                }

            }
        });
    }

    //בדיקה ששם שאנטי לא קיים במערכת
    public void onFocusChangeShantiName() {
        mShantiName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isValidPhonePrefix();
                if (mShantiName.getText().toString().equals("")) {//השדה ריק
                    if (mShantiName.getError() != null && mShantiName.getError().toString().equals(getResources().getString(R.string.loginErrorRequired)))//אם מוצגת הודעת שגיאה
                        return;
                    mShantiName.setError(getActivity().getResources().getString(R.string.loginErrorAtLeast2Characters));//הצגת הודעת שגיאה
                } else if (!hasFocus && !mShantiName.getText().toString().equals("")) {//אם השדה מלא בערכים
                    //put "oo" In order to no result equal "4"
                    String shantiName = mShantiName.getText().toString();
                    String email = mEmail.getText() + "123456789";
                    new GeneralTask(getActivity(), new UseTask() {
                        @Override
                        public void doAfterTask(String result) {
                            if (result != null && isAdded())//מחזיר true אם הפרגמנט הצליח להתווסף לאקטביטי = IsAdded למקרה שהפרמנט מתנתק מהאקטיביטי
                                if (result.equals("2") && isAdded()) {//שם משתמש קיים
                                    mShantiName.setError(getResources().getString(R.string.loginShantiNameExist));
                                    Toast.makeText(getActivity().getApplicationContext(), R.string.loginShantiNameExist, Toast.LENGTH_SHORT).show();
                                } else if (isAdded()) {
                                    if (mShantiName.getError() != null && mShantiName.getError().toString().equals(getResources().getString(R.string.loginShantiNameExist)))//אם מוצג הודעת שגיאה
                                        mShantiName.setError(null);//יסתיר אותה
                                }
                        }
                    }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"nvShantiName\": \"" + shantiName + "\",\"nvEmail\":\"" + email + "\"}", ConnectionUtil.CheckUserDetailsIsFree);//בודק שהמייל ושם שאנטי לא קיימים במערכת
                }

            }
        });
    }

    //    private void validationCheck(){
//        InputValidator.getCollection().iterator().next()
//    }
    //בדיקת תקינות קידומת
    private boolean isValidPhonePrefix() {
if (Integer.parseInt(((MultiSpinner) mRootView.findViewById(R.id.baseInfoPhonePrefix)).getChooseValues().get(0)) > 0 && Integer.parseInt(((MultiSpinner) mRootView.findViewById(R.id.baseInfoPhonePrefix)).getChooseValues().get(0)) != -1) {//אם בחר ערך ואם הוא תקין
            ((MultiSpinner) mRootView.findViewById(R.id.baseInfoPhonePrefix)).setError(null);
            return true;
        } else {//הצגת שגיאה
            ((MultiSpinner) mRootView.findViewById(R.id.baseInfoPhonePrefix)).setError(getString(R.string.loginErrorRequiredPhonePrefix));
            return false;
        }
    }
}
