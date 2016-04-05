package webit.android.shanti.login.signin;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.chat.chatManager.core.ChatMainManager;
import webit.android.shanti.customViews.CustomViewsInitializer;
import webit.android.shanti.customViews.SwitchView;
import webit.android.shanti.entities.CodeValue;
import webit.android.shanti.entities.User;
import webit.android.shanti.entities.UserQuickBlox;
import webit.android.shanti.entities.UserSearchDef;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.SPManager;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.main.MainActivity;
import webit.android.shanti.main.SearchUsers.SearchUsersFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPreferencesFragment extends SignUpBaseFragment {

    View mRootView;
    String mDoAfterUpdate;
    UserSearchDef mUserSearchDef;
    int iUserId = -1;
    private String DeviceLanguage;
    private int DeviceLanguagete;
	UserSearchDef userSearchDef;

    private RelativeLayout mActionBarRelative;
    private LinearLayout mLinearLayout;
    //private TextView SignInActionSkipTv;

    public enum EDoAfterUpdate {
        FINISH_REGISTRATION,
        GO_BACK
    }

    static SearchPreferencesFragment instance = null;
    private static String ARG_DO_AFTER_UPDATE = "doAfterUpdate";
    private static String ARG_USER_ID = "userId";


    public static SearchPreferencesFragment getInstance(EDoAfterUpdate doAfterUpdate, int iUserId) {
        SearchPreferencesFragment fragment = new SearchPreferencesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DO_AFTER_UPDATE, doAfterUpdate.toString());//save data
        args.putInt(ARG_USER_ID, iUserId);
        fragment.setArguments(args);
        return fragment;


    }

    public SearchPreferencesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDoAfterUpdate = getArguments().getString(ARG_DO_AFTER_UPDATE);
            iUserId = getArguments().getInt(ARG_USER_ID);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        mRootView = inflater.inflate(R.layout.fragment_search_preferences, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        mActionBarRelative = (RelativeLayout) mRootView.findViewById(R.id.groupActionRelative);
        
        if (savedInstanceState == null) {
            if (iUserId != -1) {//exist user

                new GeneralTask(getActivity(), new UseTask() {
                    @Override
                    public void doAfterTask(String result) {
                        if (result != null && !result.equals("")) {//אם יש למשתמש הגדרות חיפוש
                            mUserSearchDef = new Gson().fromJson(result, UserSearchDef.class);
                          //  initSingleSpinners();// או שליפה של  ספינרים V  X
                        } else {//אם אין למשתמש הגדרות חיפוש
                            if (isAdded()) {
                                Toast.makeText(getActivity(), getString(R.string.userPrefLoadingError), Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }
                        }
                    }
                }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"id\":" + iUserId + "}", ConnectionUtil.GetUserSearchDef);//מחזיר הגדרות חיפוש של המשתמש
            } else {//new user
                ((SignUpActivity) getActivity()).setBackgroundForLinear(R.color.purple_home);
                mLinearLayout = (LinearLayout) mRootView.findViewById(R.id.linear_search_setting);
                mLinearLayout.setBackgroundColor(getResources().getColor(R.color.white));

               // initSingleSpinners();//אתחול   של  ספינרים V או X
            }

        } else {

            ArrayList s = savedInstanceState.getIntegerArrayList("Countries");
            mUserSearchDef.setCountries(s);
            s = savedInstanceState.getIntegerArrayList("AgeRange");
            mUserSearchDef.setAgeRangeId(s);
            s = savedInstanceState.getIntegerArrayList("Religion");
            mUserSearchDef.setReligionId(s);
            mUserSearchDef.setiRadiusId(Integer.parseInt(savedInstanceState.get("Radius").toString()));
        }

        SignUpActivity.SignInActionSkipTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPrefOk();
            }
        });
        //האתחול בחוץ בשביל שהמסך יתהפך יהיה אתחול
        initSingleSpinners();//אתחול   של  ספינרים V או X
        addListeners();//מאזין לכפתור אישור
//
        return mRootView;
    }
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("Countries", ((SwitchView) mRootView.findViewById(R.id.searchPrefCountry)).getChooseValues());
        outState.putStringArrayList("AgeRange", ((SwitchView) mRootView.findViewById(R.id.searchPrefAge)).getChooseValues());
        outState.putStringArrayList("Religion", ((SwitchView) mRootView.findViewById(R.id.searchPrefReligion)).getChooseValues());
        outState.putString("Radius", ((SwitchView) mRootView.findViewById(R.id.searchPrefRadius)).getChooseValues().get(0));
 }

    @Override
    public void onPause() {//שמסך אחר מופעל
        super.onPause();
        if (getActivity() instanceof SignUpActivity) {
            ((SignUpActivity) getActivity()).setBackground(R.drawable.bg_enterscreens);
        }
    }

    @Override
    public void onResume() {//שחוזרים חזרה למסך הזה
        super.onResume();
        if (getActivity() instanceof SignUpActivity) {
            ((SignUpActivity) getActivity()).setBackground(R.color.purple_home);
            mActionBarRelative.setVisibility(View.GONE);
        } else
            mActionBarRelative.setVisibility(View.VISIBLE);

    }

    private void searchPrefOk() {
        setUserSearchDef();

        //חייב להיות רדיוס
        int radius = Integer.parseInt(((SwitchView) mRootView.findViewById(R.id.searchPrefRadius)).getChooseValues().get(0));
        if (radius == -1) {
            Toast.makeText(getActivity(), getString(R.string.userPrefRadiusRequired), Toast.LENGTH_SHORT).show();
            return;
        }
        if (mDoAfterUpdate == EDoAfterUpdate.FINISH_REGISTRATION.toString())
            registerOnServer();//ממסך הרשמה
        else setUserSearchDef();//מעריכת פרופיל אישי
    }

    //מאזין לכפתור אישור
    private void addListeners() {
        mRootView.findViewById(R.id.searchPrefOkBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPrefOk();
            }
        });

    }

    //אתחול או שליפה של  ספינרים V או X
    private void initSingleSpinners() {

        if (mUserSearchDef == null)//אין הגדרות חיפוש
            mUserSearchDef = new UserSearchDef();

        CustomViewsInitializer initializer = new CustomViewsInitializer(getActivity());
        initializer.setSwitchView((SwitchView) mRootView.findViewById(R.id.searchPrefAge), CodeValue.ageRange, getString(R.string.userPrefAge), mUserSearchDef.getAgeRangeId());
        //initializer.setSwitchView((SwitchView) mRootView.findViewById(R.id.searchPrefGender), CodeValue.gender, getString(R.string.userPrefGender), mUserSearchDef.getiGenderId());
        initializer.setSwitchView((SwitchView) mRootView.findViewById(R.id.searchPrefReligion), CodeValue.religions, getString(R.string.userPrefReligion), mUserSearchDef.getReligionId());
        //initializer.setSwitchView((SwitchView) mRootView.findViewById(R.id.searchPrefForReligion), "14", getString(R.string.userPrefLevelReligion), -1);
        initializer.setSwitchView((SwitchView) mRootView.findViewById(R.id.searchPrefRadius), CodeValue.radius, getString(R.string.userPrefRadius), mDoAfterUpdate == EDoAfterUpdate.FINISH_REGISTRATION.toString() ? -2 : mUserSearchDef.getiRadiusId());
        //initializer.setSwitchView((SwitchView) mRootView.findViewById(R.id.searchPrefLanguage), CodeValue.language, getString(R.string.userPrefLanguage), mUserSearchDef.getLanguages());
        initializer.setSwitchView((SwitchView) mRootView.findViewById(R.id.searchPrefCountry), CodeValue.countries_name, getString(R.string.userPrefCountry), mUserSearchDef.getCountries());
//        (SwitchView) mRootView.findViewById(R.id.searchPrefCountry).setPeddingFromMulti
//            TextView errorText = (TextView) MultiSpinner.this.getSelectedView();
//            if (errorText != null)
//                errorText.setError(errorS);
//        }
    }

    //הכנסת הגדרות חיפוש משתמשים ושליחה לשרת
    private void setUserSearchDef() {
        userSearchDef = new UserSearchDef();
        //ArrayList<Integer> languages = ((SwitchView) mRootView.findViewById(R.id.searchPrefLanguage)).getChooseValues();
        // int ageRangeId = ((SwitchView) mRootView.findViewById(R.id.searchPrefAge)).getChooseValues().get(0);
        ArrayList<String> ageRangeId = ((SwitchView) mRootView.findViewById(R.id.searchPrefAge)).getChooseValues();
        ArrayList<String> countries = (((SwitchView) mRootView.findViewById(R.id.searchPrefCountry)).getChooseValues());
        //int religionLevel = ((SwitchView) mRootView.findViewById(R.id.searchPrefForReligion)).getChooseValues().get(0);
        //int religion = ((SwitchView) mRootView.findViewById(R.id.searchPrefReligion)).getChooseValues().get(0);
        ArrayList<String> religion = ((SwitchView) mRootView.findViewById(R.id.searchPrefReligion)).getChooseValues();
        //int gender = ((SwitchView) mRootView.findViewById(R.id.searchPrefGender)).getChooseValues().get(0);
        int radius = Integer.parseInt(((SwitchView) mRootView.findViewById(R.id.searchPrefRadius)).getChooseValues().get(0));
        //userSearchDef.setLanguages(languages);
        userSearchDef.setAgeRangeId(getIntegerArray(ageRangeId));
        //userSearchDef.setiGenderId(gender);
        userSearchDef.setCountries(getIntegerArray(countries));
        //userSearchDef.setiReligionLevelId(religionLevel);
        userSearchDef.setReligionId(getIntegerArray(religion));
        userSearchDef.setiRadiusId(radius);
        userSearchDef.setiUserId(Common.user.getiUserId());

        new GeneralTask(getActivity(), new UseTask() {
            @Override
            public void doAfterTask(String result) {
                if (result != null && result.equals("1")) {//אם השינויים נשמרו
                    if (mDoAfterUpdate == EDoAfterUpdate.FINISH_REGISTRATION.toString()) {
                        getActivity().finish();
                        startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));//סיום הרשמה
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.userPrefChangesSaved), Toast.LENGTH_SHORT).show();//מהפרופיל האישי
                        initSingleSpinners();
                    }
                } else//אם השינויים לא נשמרו
                    Toast.makeText(getActivity(), getString(R.string.userPrefChangesNotSaved), Toast.LENGTH_SHORT).show();
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), userSearchDef.toString(), ConnectionUtil.SetUserSearchDef);//שליחת הגדרות חיפוש לשרת
    }


    public static ArrayList<Integer> getIntegerArray(ArrayList<String> stringArray) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (String stringValue : stringArray) {
            try {
                //Convert String to Integer, and store it into integer array list.
                result.add(Integer.parseInt(stringValue));
            } catch (NumberFormatException nfe) {
                //System.out.println("Could not parse " + nfe);
                Log.w("NumberFormat", "Parsing failed! " + stringValue + " can not be an integer");
            }
        }
        return result;
    }

    private void signUpQuickBlox() {//הרשמות לצא'ט
        // final QBUser qbUser = new QBUser(Common.user.getoUserMemberShip().getNvUserName(), "qbUserPassword");

        ChatMainManager.getInstance(getActivity().getApplicationContext()).signUp(new ChatMainManager.ChatCallBack() {
            @Override
            public void doAfterTask(QBUser user) {
                UserQuickBlox quickBlox = new UserQuickBlox(Common.user.getiUserId(), user.getId(), user.getLogin(), "qbUserPassword");
                Common.user.setoUserQuickBlox(quickBlox);
                Common.user.setNvImage("");
                Common.user.setNvTokenId(SPManager.getInstance(getActivity().getApplicationContext()).getString(SPManager.REG_ID));
                //ChatMainManager.getInstance(context.getApplicationContext()).
                // QBChat(user);
                new GeneralTask(getActivity(), new UseTask() {
                    @Override
                    public void doAfterTask(String result) {//כל התחברות לשרת מצריכה סרד חדש כי אי אפשר לפנות לשרת מהסרד הראשי
                        if (result != null && !result.equals("")) {
                            try {
                                User user = new Gson().fromJson(result, User.class);//מקבל גיסון מהשרת
                                if (user.getiUserId() != 0 && user.getiUserId() != -1) {
                                    Common.user.setNvImage(user.getNvImage());
                                    SPManager.getInstance(getActivity().getApplicationContext()).saveUser(Common.user);
                                    // initFragment(SearchPreferencesFragment.getInstance(SearchPreferencesFragment.EDoAfterUpdate.FINISH_REGISTRATION), getString(R.string.userPrefTitle));
                                    setUserSearchDef();//עדכון בשרת
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getActivity().getBaseContext(), getString(R.string.noServer), Toast.LENGTH_LONG).show();
                        }
                    }
                }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), Common.user.toSend(), ConnectionUtil.UpdateUser);//עדכון
            }
        }, new QBUser(Common.user.getoUserMemberShip().getNvUserName(), "qbUserPassword"));
    }

    //שליחת משתמש חדש לשרת
    public void registerOnServer() {
        //Odeya
        //nextPage(new SearchDefFragment());
        // if (1 == 2) {
        if (Common.user.getNvImage() == null)//הרשמה רגילה
            Common.user.setNvImage("");
        String jsonToSend = Common.user.toSend();
        Log.d("setUser", jsonToSend);
        Utils.sendToLog(getActivity(), ConnectionUtil.getServerUrl() + ConnectionUtil.SetUser, jsonToSend);
        DeviceLanguage = Locale.getDefault().getDisplayLanguage();
        if (DeviceLanguage.equals(getString(R.string.DeviceDisplayHebrew)))
            DeviceLanguagete = 1;
        else if (DeviceLanguage.equals(getString(R.string.DeviceDisplayEnglish)))
        DeviceLanguagete = 2;
        new GeneralTask(getActivity(), new UseTask() {
            @Override
            public void doAfterTask(String result) {
                if (result != null && !result.equals("")) {
                    try {
                        User user = new Gson().fromJson(result, User.class);//המשתמש שנשמר עכשיו בשרת
                        if (user.getiUserId() != 0 && user.getiUserId() != -1) {//1- לא הצליח להוסיף משתמש לשרת
                            Common.user.setiUserId(user.getiUserId());
                            Common.user.setNvImage(user.getNvImage());
                            signUpQuickBlox();//רישום ל צ'אט
                            //מאפס מסכי רישום
                            BaseInfoFragment.instance = null;
                            MoreInfoFragment.instance = null;

                            TakePhotoFragment.instance = null;
                            ProfileInfoFragment.instance = null;
                            SearchPreferencesFragment.instance = null;
                            //setIsBackEnable(false);

                        } else
                            Toast.makeText(getActivity().getApplicationContext(), R.string.loginEmailExist, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.loginError), Toast.LENGTH_LONG).show();
                }
            }
        }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(),"{\"newUser\":\"" +  jsonToSend + "\",\"iLanguageId\":\"" + DeviceLanguagete + "\"}", ConnectionUtil.SetUser);//שולח משתמש
    }
}
