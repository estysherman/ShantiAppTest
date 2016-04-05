package webit.android.shanti.login.signin;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.customViews.CustomEditText;
import webit.android.shanti.customViews.CustomTextView;
import webit.android.shanti.customViews.CustomViewsInitializer;
import webit.android.shanti.customViews.MultiSpinner;
import webit.android.shanti.entities.CodeValue;
import webit.android.shanti.general.Common;
import webit.android.shanti.main.SearchUsers.SearchUsersFragment;
import webit.android.shanti.main.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */

//דף השלמת פרטים
public class MoreInfoFragment extends SignUpBaseFragment implements View.OnClickListener {

    public static CustomViewsInitializer initializer = null;
    private View mRootView;
    private CustomEditText mMoreTv;
    private EditText mMoreInfoHobbyED;
    private EditText mMoreInfoJobED;
    private View mActionBar;
    //private TextView SignInActionSkipTv;
	private static Bundle savedInstanceState1;
    public MoreInfoFragment() {
        // Required empty public constructor
    }

    public static MoreInfoFragment instance = null;

    public static MoreInfoFragment getInstance() {
      /*  if this function returns instance, the views values saved after user finish registration and do it again
        instance = new MoreInfoFragment();*/
        if (instance == null) {
            instance = new MoreInfoFragment();
        }
        return instance;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
  if (savedInstanceState != null)
            savedInstanceState1 = savedInstanceState;
        mRootView = inflater.inflate(R.layout.fragment_more_info, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        mRootView.findViewById(R.id.moreInfoEndBtn).setOnClickListener(this);
        mMoreTv = (CustomEditText) mRootView.findViewById(R.id.moreInfoArea);
        mMoreInfoHobbyED = (EditText) mRootView.findViewById(R.id.moreInfoHobby);
        mMoreInfoJobED= (EditText) mRootView.findViewById(R.id.moreInfoJob);
 		mMoreTv.setText("הדס");
        mMoreInfoHobbyED.setText("קריאה");
        mMoreInfoJobED.setText("תכנות");

//        mActionBar = inflater.inflate(R.layout.signin_action_bar, container, false);
//        SignInActionSkipTv = (TextView) mActionBar.findViewById(R.id.SignInActionSkip);
        SignUpActivity.SignInActionSkipTv.setOnClickListener(this);
//                (new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                initFragment(SearchPreferencesFragment.getInstance(SearchPreferencesFragment.EDoAfterUpdate.FINISH_REGISTRATION, -1), getString(R.string.userPrefTitle));
//            }
//        });
//        setViews();//אתחול ספינר שפות לפי השפה שנבחרה
        if (savedInstanceState1 != null) {
            mMoreTv.setText(savedInstanceState1.get("moreInfoArea").toString());
            mMoreInfoHobbyED.setText(savedInstanceState1.get("MoreInfoHobby").toString());
            mMoreInfoJobED.setText(savedInstanceState1.get("MoreInfoJob").toString());

        }
        return mRootView;
    }
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("MoreInfoHobby", mMoreInfoHobbyED.getText().toString());
        outState.putString("MoreInfoJob", mMoreInfoJobED.getText().toString());
        outState.putString("moreInfoArea", mMoreTv.getText().toString());
    }
    //אתחול ספינר שפות לפי השפה שנבחרה
//    private void setViews() {
//        initializer = new CustomViewsInitializer(getActivity());
//        initializer.setSpinner((MultiSpinner) mRootView.findViewById(R.id.moreInfoLanguages), CodeValue.language, getString(R.string.moreInfoLanguages), CodeValue.getKeys(Common.user.getoLanguages()));
//    }

    @Override
    public void onClick(View v) {
        //switch (v.getId()) {
            //case R.id.moreInfoEndBtn:
                boolean isValid = true;
                //registerOnServer();
                //מכניס לרשימה את השפות שנבחרו
//                List<CodeValue> Languages = CodeValue.getCodeValues(((MultiSpinner) mRootView.findViewById(R.id.moreInfoLanguages)).getChooseValues());
//                if (Languages != null && Languages.size() == 1 && Languages.get(0).getiKeyId() == -1) {//אם לא בחר שפה
//                    ((MultiSpinner) mRootView.findViewById(R.id.moreInfoLanguages)).setError(getResources().getString(R.string.moreInfoLanguages));//מציג error
//                    isValid = false;
//                } else {
//                    ((MultiSpinner) mRootView.findViewById(R.id.moreInfoLanguages)).setError(null);//מנקה error
//                }
                if (isValid) {//אם תקין
                    //מציב את השדות
                    Common.user.setNvHobby(mMoreInfoHobbyED.getText() + "");
                    Common.user.setNvProfession(mMoreInfoJobED.getText() + "");
//                    Common.user.setoLanguages(CodeValue.getCodeValues(((MultiSpinner) mRootView.findViewById(R.id.moreInfoLanguages)).getChooseValues()));
                    if (mMoreTv.getText().toString() != null)//אם מלאו פרטים נוספים
                        Common.user.setNvAboutMe(mMoreTv.getText().toString());
                    initFragment(SearchPreferencesFragment.getInstance(SearchPreferencesFragment.EDoAfterUpdate.FINISH_REGISTRATION, -1), getString(R.string.userPrefTitle));

                }
                //break;
        //}
    }
}
