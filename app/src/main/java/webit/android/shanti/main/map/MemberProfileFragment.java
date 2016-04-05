package webit.android.shanti.main.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
//import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.customViews.CustomViewsInitializer;
import webit.android.shanti.customViews.ICallBackCustomViewsData;
import webit.android.shanti.entities.CodeValue;
import webit.android.shanti.entities.User;
import webit.android.shanti.main.MainBaseFragment;

public class MemberProfileFragment extends MainBaseFragment {

    public static String ARG_MEMBER_PROFILE = "MemberProfile";
    private static MemberProfileFragment instance = null;

    public static MemberProfileFragment getInstance(String userAsJson) {
        boolean isActive = instance != null;
        if (instance == null)
            instance = new MemberProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEMBER_PROFILE, userAsJson);
        if (!isActive)
            instance.setArguments(args);
        else
            instance.getArguments().putString(ARG_MEMBER_PROFILE, userAsJson);
        return instance;
    }

    private View mRootView;
    private User mUser;

    public MemberProfileFragment() {
    }


    @Override
    public void changeActionBar(View view, String title) {
        super.changeActionBar(view, title);
        ((TextView) view.findViewById(R.id.profileActionTitle)).setText(title);
    }

    @Override
    public void setActionBarEvents(View view) {
        super.setActionBarEvents(view);
        setBackView(view.findViewById(R.id.profileActionBack));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            mUser = new Gson().fromJson(getArguments().getString(ARG_MEMBER_PROFILE), User.class);
        }

        mRootView = inflater.inflate(R.layout.fragment_member_profile, container, false);
        initViews();
        //getActionBarManager().changeActionBar(ActionBarManager.ActionBarPosition.ACTION_BAR_PROFILE, mUser.getFullName());
        this.setActionBarEvents(mRootView);
        this.changeActionBar(mRootView, mUser.getNvShantiName());
        return mRootView;
    }

    private void initViews() {
//        ImageView mImg = (ImageView) findViewById(R.id.chatUserImage);
//        mImg.setImageBitmap(ImageHelper.getBitmapFromURL(Common.user.getNvImage()));
//        mImg.invalidate();
//        Picasso.with(getActivity()).load(mUser.getNvImage()).into(((ImageView) mRootView.findViewById(R.id.memberProfileImage)));
        new Utils.DownloadImageTask(((ImageView) mRootView.findViewById(R.id.memberProfileImage))).execute(mUser.getNvImage());

        //((ImageView) mRootView.findViewById(R.id.memberProfileImage)).setImageBitmap(ImageHelper.getBitmapFromURL(mUser.getNvImage()));
        ((TextView) mRootView.findViewById(R.id.memberProfileFullName)).setText(mUser.getNvShantiName());
        ((TextView) mRootView.findViewById(R.id.memberProfileInfo)).setText(mUser.getUserInfo(getActivity()));
        ((TextView) mRootView.findViewById(R.id.memberProfileShantiName)).setText(getString(R.string.profileMemberShantiName, mUser.getNvShantiName()));
        CustomViewsInitializer initializer = new CustomViewsInitializer(getActivity());
        initializer.getCodeTable(CodeValue.gender, new ICallBackCustomViewsData() {//שליפת מגדר
            @Override
            public void doCallBack(ArrayList<CodeValue> objects) {
                String gender = "";
                for (CodeValue codeValue : objects) {
                   /*if (Integer.parseInt(codeValue.getiTableId()) == mUser.getiGenderId()) {
                        gender = codeValue.getNvValue();
                    }*/
                }
              /*if (mUser.getiGenderId() == 453) {
                    ((TextView) mRootView.findViewById(R.id.memberProfileGenderAge)).setText(getString(R.string.profileMemberAgeFemale, gender, getAge(mUser.getNvBirthDate())));
                } else {
                    ((TextView) mRootView.findViewById(R.id.memberProfileGenderAge)).setText(getString(R.string.profileMemberAgeMale, gender, getAge(mUser.getNvBirthDate())));
                }*/
            }
        });
        ((TextView) mRootView.findViewById(R.id.memberProfileCountry)).setText(mUser.getoCountry().getNvValue());//ארץ מוצא
        String lang = "";//שליפת שפות מדוברות
        if (mUser.getoLanguages() != null) {
            for (int i = 0; i < mUser.getoLanguages().size(); i++) {
                lang += mUser.getoLanguages().get(i).getNvValue();
                if (i == mUser.getoLanguages().size()) {
                    lang += ", ";
                } else {
                    lang += ".";
                }
            }
        }
        ((TextView) mRootView.findViewById(R.id.memberProfileLanguages)).setText(lang);//שפות מדוברות
//        ((TextView) mRootView.findViewById(R.id.memberProfileReligion)).setText(mUser.getsReligion());
//        ((TextView) mRootView.findViewById(R.id.memberProfileLevelReligion)).setText(mUser.getsReligiousLevel());
        ((TextView) mRootView.findViewById(R.id.memberProfileUserPrefReligion)).setText(mUser.getsReligion());//דת
        ((TextView) mRootView.findViewById(R.id.memberProfileLevelReligion)).setText(mUser.getsReligiousLevel());//יחס לדת
        ((TextView) mRootView.findViewById(R.id.memberProfileJob)).setText(mUser.getNvProfession());
        ((TextView) mRootView.findViewById(R.id.memberProfileHobby)).setText(mUser.getNvHobby());
        ((TextView) mRootView.findViewById(R.id.memberProfileMoreInfoTitle)).setText(getString(R.string.profileMemberMoreInfo, mUser.getUserName()));
        ((TextView) mRootView.findViewById(R.id.memberProfileMoreInfo)).setText("");
    }

    private int getAge(String birthDateStr) {
        int age = 0, curDay, curMonth, curYear;

        Calendar birthDate = Calendar.getInstance();
        curDay = birthDate.get(Calendar.DAY_OF_MONTH);
        curMonth = birthDate.get(Calendar.MONTH);
        curYear = birthDate.get(Calendar.YEAR);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            birthDate.setTime(format.parse(birthDateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        age = curYear - birthDate.get(Calendar.YEAR);
        if ((curMonth < birthDate.get(Calendar.MONTH))
                || ((curMonth == birthDate.get(Calendar.MONTH)) && (curDay < birthDate.get(Calendar.DAY_OF_MONTH)))) {
            --age;
        }

        return age;
    }
}
