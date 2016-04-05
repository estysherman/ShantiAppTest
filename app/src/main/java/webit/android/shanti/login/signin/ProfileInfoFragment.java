package webit.android.shanti.login.signin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.customViews.CustomTextView;
import webit.android.shanti.customViews.CustomViewsInitializer;
import webit.android.shanti.customViews.ICallBackLoadFinish;
import webit.android.shanti.customViews.MultiSpinner;
import webit.android.shanti.customViews.SwitchView;
import webit.android.shanti.entities.CodeValue;
import webit.android.shanti.entities.KeyValue;
import webit.android.shanti.entities.Location;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;


public class ProfileInfoFragment extends SignUpBaseFragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static CustomViewsInitializer initializer = null;
    public View mRootView = null;

    private ProgressDialog mDialog;
    private String mParam1;
    private MultiSpinner mReligionTxt;
    private MultiSpinner mGenderTxt;
    private MultiSpinner mCityTxt;
    private MultiSpinner mLanguageseTxt;
    private TextView mBirthDateTxt;

    private CustomTextView mCountryTv;
    private String mParam2;
    private static boolean flag = true;
	private static Bundle savedInstanceState1;
    private OnFragmentInteractionListener mListener;
    private int coutry = -1;
    private int religion = -1;
    private int selectGender = -1;
    private ArrayList languagese = null;
    private String birthDate = "";

    public static ProfileInfoFragment instance = null;

    public static ProfileInfoFragment getInstance() {
        //if (instance == null) {
//        instance = new ProfileInfoFragment();
        //}
        if (instance == null) {
            instance = new ProfileInfoFragment();
        }
        return instance;
    }

    public ProfileInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
 if (savedInstanceState != null)
        savedInstanceState1 = savedInstanceState;
 
            mRootView = inflater.inflate(R.layout.fragment_profile_info, container, false);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            mBirthDateTxt = ((TextView) mRootView.findViewById(R.id.profileInfoTextBirthDate));
            mReligionTxt = (MultiSpinner) mRootView.findViewById(R.id.profileInfoReligion);
            mGenderTxt = ((MultiSpinner) mRootView.findViewById(R.id.profileInfoGender));
            mCityTxt = (MultiSpinner) mRootView.findViewById(R.id.profileInfoCountry);
            mLanguageseTxt = ((MultiSpinner) mRootView.findViewById(R.id.profileInfoLanguages));
            //todo
//            if (Common.user != null && Common.user.getNvBirthDate() != null && !Common.user.getNvBirthDate().equals(""))
//                mBirthDateTxt.setText(Common.user.getNvBirthDate());

//            mCountryTv = (CustomTextView) mRootView.findViewById(R.id.profileInfoCountry);
//            if (Common.user.getoCountry() != null)
//                mCountryTv.setText(Common.user.getoCountry().getNvValue() + "");
        // mRootView = inflater.inflate(R.layout.fragment_profile_info, container, false);
        mDialog = ProgressDialog.show(getActivity(), "", "");
        //setUi();
           
        mDialog.dismiss();
//        }
        mRootView.findViewById(R.id.profileInfoRegisterBtn).setOnClickListener(this);
        //      }
        if (savedInstanceState1 != null) {//×œ×‘×“×•×§ ××™×–×” ×—×œ×§ ×˜×•×‘
                     /* initializer.setSpinner(mCityTxt, CodeValue.countries_name, getString(R.string.userPrefCountry), Integer.valueOf(savedInstanceState1.get("Countries").toString()));
            initializer.setSpinner(mLanguageseTxt, CodeValue.language, getString(R.string.moreInfoLanguages), Integer.valueOf(savedInstanceState1.get("Languages").toString()));
            initializer.setSpinner(mReligionTxt, CodeValue.religions, getString(R.string.signUpReligion), Integer.valueOf(savedInstanceState1.get("Religion").toString()));
            initializer.setSpinner(mGenderTxt, CodeValue.gender, getString(R.string.userPrefGender), Integer.valueOf(savedInstanceState1.get("gender").toString()));
*/

            coutry = Integer.parseInt(savedInstanceState1.get("Countries").toString());
            religion = Integer.parseInt(savedInstanceState1.get("Religion").toString());
            selectGender = Integer.parseInt(savedInstanceState1.get("gender").toString());
            languagese = savedInstanceState1.getIntegerArrayList("Languages");
            birthDate = savedInstanceState1.get("BirthDate").toString();
     /*
           mGenderTxt.setSelection((Integer) savedInstanceState1.get("gender"));
            mReligionTxt.setSelection((Integer) savedInstanceState1.get("Religion"));
            mLanguageseTxt.setSelection((Integer) savedInstanceState1.get("Languages"));
*/
            //mBirthDateTxt.setText(savedInstanceState1.get("BirthDate").toString());
        }
        setViews();        //×ž××ª×—×œ ×¡×¤×™× ×¨×™× ×©×œ ×ž×’×“×¨ ×•×ž×§×•× ×ž×’×•×¨×™×, ×‘× ×•×¡×£ ×ž××ª×—×œ ×ž×™×§×•×  - ×× ××™×Ÿ ×•×“×™××œ×•×’ ×©×œ ×ª××¨×™×š ×œ×™×“×”
        return mRootView;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//למה  נותן תמיד פוזישן 0
        outState.putInt("Countries", mCityTxt.getSelectedItemPosition());
        outState.putInt("gender", mGenderTxt.getSelectedItemPosition());
 outState.putString("Countries", ((MultiSpinner) mRootView.findViewById(R.id.profileInfoCountry)).getChooseValues().get(0));
        outState.putString("gender", ((MultiSpinner) mRootView.findViewById(R.id.profileInfoGender)).getChooseValues().get(0));
        outState.putString("Religion", ((MultiSpinner) mRootView.findViewById(R.id.profileInfoReligion)).getChooseValues().get(0));
        outState.putStringArrayList("Languages", ((MultiSpinner) mRootView.findViewById(R.id.profileInfoLanguages)).getChooseValues());
        outState.putString("BirthDate", mBirthDateTxt.getText().toString());
       /* int a=mCityTxt.oldSelectedKey;
        outState.putInt("Countries", a);
        outState.putInt("gender", (Integer) mGenderTxt.getSelectedItem());
        outState.putInt("Religion", mReligionTxt.getSelectedItemPosition());
        outState.putInt("Languages", mLanguageseTxt.getSelectedItemPosition());
*/
//×œ×‘×“×•×§ ××™×–×” ×—×œ×§ ×˜×•×‘
//×œ×ž×”  × ×•×ª×Ÿ ×ª×ž×™×“ ×¤×•×–×™×©×Ÿ 0
       /*   outState.putString("Countries",(Integer.valueOf(mCityTxt.getSelectedItemPosition()).toString()));
        outState.putString("gender", mGenderTxt.getSelectedItem().toString());
        outState.putString("Religion",mReligionTxt.getSelectedItem().toString());
        outState.putString("Languages", mLanguageseTxt.getSelectedItem().toString());
        outState.putString("BirthDate", mBirthDateTxt.getText().toString());*/
    }

    @Override
    public void onDestroyView() {
        if (mRootView.getParent() != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }
        super.onDestroyView();
    }

    //מאתחל ספינרים של מגדר ומקום מגורים, בנוסף מאתחל מיקום  - אם אין ודיאלוג של תאריך לידה
    private void setViews() {
        if (flag) {
            if (Common.user == null) {//אם משתמש בהרשמה רגילה
                Common.user = new User();//יוצר משתמש חדש
                Common.user.setoLocation(new Location(0, 0, Common.user.getiUserId()));//מציב לו מיקום
            }
            User user = Common.user;
            initializer = new CustomViewsInitializer(getActivity(), 2, new ICallBackLoadFinish() {
                @Override
                public void doCallBack() {
                    mDialog.dismiss();//סוגר dialog
                }
            });

//        mRootView.findViewById(R.id.odeyaClick).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ((TextView) mRootView.findViewById(R.id.odeya)).setText("odeya katz!!!@#$%^&*()");
//            }
//        });

            initializer.setDatePicker(mRootView.findViewById(R.id.profileInfoBirthDate), mBirthDateTxt, birthDate);//מאתחל גיאלוג - תאריך
//        initializer.setSpinner((MultiSpinner) mRootView.findViewById(R.id.profileInfoCountry), CodeValue.countries, getString(R.string.signUpCountry), -1);
     
            if (Common.user != null && (Common.user.getiGenderId() == -3) || (Common.user.getiGenderId() == -4))//אם בחר
                selectGender = Common.user.getiGenderId();//מציב את הבחירה
            else//אם לא בחר
                selectGender = -1;
            initializer.setSpinner(mGenderTxt, CodeValue.gender, getString(R.string.userPrefGender), selectGender);
//        initializer.setSpinner((MultiSpinner) mRootView.findViewById(R.id.profileInfoReligion), CodeValue.religions, getString(R.string.userPrefReligion), -1);
            flag = false;
        }
        
        if (Common.user != null && Common.user.getoCountry() != null)//אם יש לו מדינה
            coutry = Common.user.getoCountry().getiKeyId();
			//selected = Common.user.getoCountry().getiKeyId();
        //מסמן את המדינה בספינר
        initializer.setSpinner(mCityTxt, CodeValue.countries_name, getString(R.string.userPrefCountry), coutry);
        initializer.setSpinner(mLanguageseTxt, CodeValue.language, getString(R.string.moreInfoLanguages), languagese);
        initializer.setSpinner(mReligionTxt, CodeValue.religions, getString(R.string.signUpReligion), religion);

        // / initializer.setSpinner(mReligionTxt, CodeValue.religions, getString(R.string.signUpReligion), Common.user.getiReligionId());

    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {

        }
    }

    //מציב את הנתונים ב Common.user
    public void register() {
        boolean isValid = true;
         int gender = -1, religion = -1, forReligion = -1, country = -1, language =-1;
//                country = -1;        //×©×œ×™×¤×ª × ×ª×•× ×™× ×ž×”×ª×™×‘×•×ª ×˜×§×¡×˜
        gender = Integer.parseInt(((MultiSpinner) mRootView.findViewById(R.id.profileInfoGender)).getChooseValues().get(0));
        religion = Integer.parseInt(((MultiSpinner) mRootView.findViewById(R.id.profileInfoReligion)).getChooseValues().get(0));
        country = Integer.parseInt(((MultiSpinner) mRootView.findViewById(R.id.profileInfoCountry)).getChooseValues().get(0));
        //forReligion = ((MultiSpinner) mRootView.findViewById(R.id.profileInfoLevelReligion)).getChooseValues().get(0);
        List<KeyValue> Languages = MultiSpinner.Languages;
        if (Languages != null && Languages.size() == 1 && Languages.get(0).getNvKey().equals(-1)) {//×× ×œ× ×‘×—×¨ ×©×¤×”
            ((MultiSpinner) mRootView.findViewById(R.id.profileInfoLanguages)).setError(getResources().getString(R.string.moreInfoLanguages));//×ž×¦×™×’ error
            isValid = false;
        } else {
            ((MultiSpinner) mRootView.findViewById(R.id.profileInfoLanguages)).setError(null);//×ž× ×§×” error
        }
        String birthDate = mBirthDateTxt.getText() + "";
        //×‘×“×™×§×ª ×ª×§×™× ×•×ª ×œ birthDate
        if (birthDate.equals("null") || birthDate.equals(getActivity().getString(R.string.dateBeforeToday)) || birthDate.equals(getActivity().getString(R.string.signUpBirthDate))) {
            mBirthDateTxt.setError(getResources().getString(R.string.loginErrorRequired));
            isValid = false;
        } else {
            mBirthDateTxt.setError(null);

        }
        //×‘×“×™×§×ª ×ª×§×™× ×•×ª ×œ gender
        if (gender==-1) {
            ((MultiSpinner) mRootView.findViewById(R.id.profileInfoGender)).setError(getResources().getString(R.string.loginErrorRequired));
            isValid = false;
        } else {
            ((MultiSpinner) mRootView.findViewById(R.id.profileInfoGender)).setError(null);
        }
        //×‘×“×™×§×ª ×ª×§×™× ×•×ª ×œ country
        if (country==-1) {
            ((MultiSpinner) mRootView.findViewById(R.id.profileInfoCountry)).setError(getResources().getString(R.string.loginErrorRequired));
            isValid = false;
        } else {
            ((MultiSpinner) mRootView.findViewById(R.id.profileInfoCountry)).setError(null);
        }
//        if (religion == -1) {
//            ((MultiSpinner) mRootView.findViewById(R.id.profileInfoReligion)).setError(getResources().getString(R.string.loginErrorRequired));
//            isValid = false;
//        }
//        else {
//            ((MultiSpinner) mRootView.findViewById(R.id.profileInfoReligion)).setError(null);
//        }


        //Odeya
        //isValid = true;
        if (!isValid) {//×× ×™×© ×”×–× ×ª × ×ª×•× ×™× ×©×’×•×™×”
            return;//×—×•×–×¨
        }


//        Common.user.setoCountry(new CodeValue("", country));
        //×× ×”× ×ª×•× ×™× ×ª×§×™× ×™× - ×ž×¦×™×‘ ××•×ª× ×‘  Common.user
        Common.user.setiReligionId(religion);
        Common.user.setiReligiousLevelId(forReligion);
        Common.user.setNvBirthDate(birthDate);
        Common.user.setiGenderId(gender);
        Common.user.setoLanguages(Languages);

        initFragment(TakePhotoFragment.getInstance(Common.user.getNvFirstName(), ""), "");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profileInfoRegisterBtn:
                register();    //מציב את הנתונים ב Common.user
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * ק
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
