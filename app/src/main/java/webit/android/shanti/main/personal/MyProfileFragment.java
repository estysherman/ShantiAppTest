package webit.android.shanti.main.personal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.customViews.CustomViewsInitializer;
import webit.android.shanti.customViews.ICallBackLoadFinish;
import webit.android.shanti.customViews.MultiSpinner;
import webit.android.shanti.entities.CodeValue;
import webit.android.shanti.entities.KeyValue;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.SPManager;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.general.photo.ImageHelper;
import webit.android.shanti.general.photo.getimage.CallbackContext;
import webit.android.shanti.general.photo.getimage.CameraLauncher;
import webit.android.shanti.general.photo.getimage.CameraLauncherArguments;
import webit.android.shanti.login.signin.SearchPreferencesFragment;
import webit.android.shanti.main.MainActivity;
import webit.android.shanti.main.MainBaseFragment;
import webit.android.shanti.main.messages.FullScreenChatImageFragment;

public class MyProfileFragment extends MainBaseFragment implements View.OnClickListener {

    public static MyProfileFragment instance = null;
    View mRootView;
    View actionBarView;
    CameraLauncher cameraLauncher;
    CameraLauncherArguments arguments;
    CallbackContext callbackContext = new CallbackContext() {

        @Override
        public void success(Bitmap bitmap) {
            super.success(bitmap);
            mUserOrdinalImage = bitmap;
            mUserImage.setImageBitmap(bitmap);
        }

    };
    private ImageView mUserImage;
    private EditText mFirstName, mLastName, mPhone, mJob, mHobby, mMoreAbout;
    private MultiSpinner mGender, mReligion, mLanguage;
    private View mBirthDateContainer;
    private TextView mBirthDateText;
    private MultiSpinner mPhonePerfixMS;
    private static Context mContext;
    private Bitmap mUserOrdinalImage;
    private String PathUserImage;
    private String UserName;
    ProgressDialog dialog;
    CustomViewsInitializer initializer;
    public MyProfileFragment() {
        // Required empty public constructor
    }

    public static MyProfileFragment getInstance() {
        if (instance == null) {
            instance = new MyProfileFragment();
        }
        return instance;
    }

    @Override
    public void changeActionBar(View view, String title) {
        super.changeActionBar(view, title);
        setActionBarEvents(view);
        setBackView(view.findViewById(R.id.profileGroupActionBack));
        changeVisibility(view.findViewById(R.id.profileGroupSaveChanges), View.VISIBLE);
        changeVisibility(view.findViewById(R.id.profileGroupEdit), View.GONE);
        ((TextView) view.findViewById(R.id.profileGroupActionTitle)).setText(getString(R.string.myProfile) + "");
    }

    @Override
    public void setActionBarEvents(View view) {
        super.setActionBarEvents(view);
        if (view == mRootView) {
            view.findViewById(R.id.profileGroupSaveChanges).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {//set on click - on save changes

                    saveChange();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = MyProfileFragment.getInstance().getActivity();
        actionBarView = inflater.inflate(R.layout.group_profile_action_bar, container, false);
        mRootView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        mRootView.findViewById(R.id.myProfileEditDetails).setOnClickListener(this);
        mRootView.findViewById(R.id.myProfileEditCompletesDetails).setOnClickListener(this);
        mRootView.findViewById(R.id.myProfileEditPersonalProfile).setOnClickListener(this);
        dialog = new ProgressDialog(getActivity());

        changeActionBar(mRootView, "");

        initViews();
        cameraLauncher = new CameraLauncher(this);
         if(savedInstanceState!=null)
         {
            mFirstName.setText(savedInstanceState.get("FirstName").toString());
            mLastName.setText(savedInstanceState.get("LastName").toString());
            mPhone.setText(savedInstanceState.get("Phone").toString());
            mJob.setText(savedInstanceState.get("mJob").toString());
            mHobby.setText(savedInstanceState.get("Hobby").toString());
            mMoreAbout.setText(savedInstanceState.get("MoreAbout").toString());
            mBirthDateText.setText(savedInstanceState.get("Language").toString());

/////////////////////האם זה נכון
            initializer.setSpinner(mPhonePerfixMS, CodeValue.countries, getString(R.string.myProfilePersonalProfile), Integer.valueOf(savedInstanceState.get("PhonePerfixMS").toString()));
            initializer.setSpinner(mGender, CodeValue.gender, getString(R.string.userPrefGender), Integer.valueOf(savedInstanceState.get("Gender").toString()));
            initializer.setSpinner(mLanguage, CodeValue.language, getString(R.string.moreInfoLanguages), Integer.valueOf(savedInstanceState.get("Language").toString()));

         }
        return mRootView;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("FirstName", mFirstName.getText().toString());
        outState.putString("LastName", mLastName.getText().toString());
        outState.putString("Phone", mPhone.getText().toString());
        outState.putString("mJob", mJob.getText().toString());
        outState.putString("Hobby", mHobby.getText().toString());
        outState.putString("BirthDate", mBirthDateText.getText().toString());
        outState.putString("MoreAbout", mMoreAbout.getText().toString());

//////////////////////////////////////
//איזה חלק טוב
        outState.putInt("Gender", mGender.getSelectedItemPosition());
        outState.putInt("PhonePerfixMS", mPhonePerfixMS.getSelectedItemPosition());
        outState.putInt("Language", mLanguage.getSelectedItemPosition());


        outState.putString("Gender", mGender.getSelectedItem().toString());
        outState.putString("PhonePerfixMS", mPhonePerfixMS.getSelectedItem().toString());
        outState.putString("Language", mLanguage.getSelectedItem().toString());



    }

    @Override
    public void onClick(View view) {
        TextView textView = (TextView) view;
        View inputsContainer = ((LinearLayout) textView.getParent().getParent()).getChildAt(1);
        if (inputsContainer.getVisibility() == View.GONE) {
            inputsContainer.setVisibility(View.VISIBLE);
            textView.setText(getString(R.string.myProfileClose));
        } else {
            inputsContainer.setVisibility(View.GONE);
            Utils.HideKeyboard(getActivity());
            textView.setText(getString(R.string.myProfileEdit));
        }

    }

    private void initViews() {
        //dialog.show(getActivity(), "", "");
        initializer = new CustomViewsInitializer(getActivity(), 3, new ICallBackLoadFinish() {
            @Override
            public void doCallBack() {
                dialog.dismiss();
            }
        });
        final User user = Common.user;
        PathUserImage = user.getNvImage();
        UserName = user.getUserName();
        mUserImage = (ImageView) mRootView.findViewById(R.id.myProfileUserImage);//save image profile
        new Thread(new Runnable() {
            @Override
            public void run() {
                mUserOrdinalImage = ImageHelper.getBitmapFromURL(user.getNvImage(), getActivity());
            }
        }).start();

        mFirstName = (EditText) mRootView.findViewById(R.id.myProfileFirstName);
        mLastName = (EditText) mRootView.findViewById(R.id.myProfileLastName);
        mPhone = (EditText) mRootView.findViewById(R.id.myProfilePhone);
        mJob = (EditText) mRootView.findViewById(R.id.myProfileJob);
        mHobby = (EditText) mRootView.findViewById(R.id.myProfileHobby);
        mMoreAbout = (EditText) mRootView.findViewById(R.id.myProfileMoreAbout);
//        mCountry = (MultiSpinner) mRootView.findViewById(R.id.myProfileCountry);
        mGender = (MultiSpinner) mRootView.findViewById(R.id.myProfileGender);
        mPhonePerfixMS = (MultiSpinner) mRootView.findViewById(R.id.myProfilePhonePrefix);
//        mReligion = (MultiSpinner) mRootView.findViewById(R.id.myProfileReligion);
        mLanguage = (MultiSpinner) mRootView.findViewById(R.id.myProfileLanguage);

        mBirthDateContainer = mRootView.findViewById(R.id.myProfileBirthDate);
        mBirthDateText = (TextView) mRootView.findViewById(R.id.myProfileTextBirthDate);
        new Utils.DownloadImageTask(mUserImage).execute(user.getNvImage());

        mFirstName.setText(user.getNvFirstName());
        mLastName.setText(user.getNvLastName());
        mPhone.setText(user.getNvPhone());
        mJob.setText(user.getNvProfession());
        mHobby.setText(user.getNvHobby());
        mMoreAbout.setText(user.getNvAboutMe());

        initializer.setDatePicker(mBirthDateContainer, mBirthDateText, user.getNvBirthDate());
//        initializer.setSpinner(mCountry, CodeValue.countries, getString(R.string.signUpCountry),Common.user.getoCountry().getiKeyId());
        initializer.setSpinner(mGender, CodeValue.gender, getString(R.string.userPrefGender), Common.user.getiGenderId());
//        initializer.setSpinner(mReligion, CodeValue.religions, getString(R.string.userPrefReligion), user.getiReligionId());
        int selected = -1;
        try {
            selected = Integer.valueOf(user.getNvPhonePrefix());
        } catch (Exception e) {
            selected = -1;
        }
        initializer.setSpinner(mPhonePerfixMS, CodeValue.countries, getString(R.string.myProfilePersonalProfile), selected);

         initializer.setSpinner(mLanguage, CodeValue.language, getString(R.string.moreInfoLanguages), SearchPreferencesFragment.getIntegerArray((KeyValue.getKeys(Common.user.getoLanguages()))));
         initEvents();
        dialog.dismiss();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Utils.DownloadImageTask(mUserImage).execute(Common.user.getNvImage());
    }

    private void saveChange() {
        User user = new User();
        user.setNvFirstName(mFirstName.getText().toString());
        user.setNvLastName(mLastName.getText().toString());
        user.setNvPhone(mPhone.getText().toString());
        user.setNvFirstName(mFirstName.getText().toString());
        user.setNvProfession(mJob.getText().toString());
        user.setNvHobby(mHobby.getText().toString());
        user.setNvAboutMe(mMoreAbout.getText().toString());
        user.setNvPhonePrefix(((MultiSpinner) mRootView.findViewById(R.id.myProfilePhonePrefix)).getChooseValues().get(0) + "");
        String sTmp = ImageHelper.getBase64FromBitmap(mUserOrdinalImage);
        user.setNvImage(sTmp);
//        int countryId = mCountry.getChooseValues().get(0);
        int genderId = Integer.parseInt(mGender.getChooseValues().get(0));
        int religionId = Integer.parseInt(mReligion.getChooseValues().get(0));
        ArrayList<Integer> languagesId = SearchPreferencesFragment.getIntegerArray(mLanguage.getChooseValues());

//        user.setoCountry(new CodeValue("", countryId));
        user.setNvBirthDate(mBirthDateText.getText().toString());
        user.setiGenderId(genderId);
//        user.setiReligionId(religionId);
        ArrayList<KeyValue> languages = new ArrayList<>();
        for (Integer key : languagesId) {
            languages.add(new KeyValue("", "key"));
        }
        user.setoLanguages(languages);

//        final UserSearchDef def = new UserSearchDef();
//        def.setLanguages(languagesId);
//        def.setiReligionId(religionId);
//        def.setiGenderId(genderId);
//        def.setCountries(mCountry.getChooseValues());
//        def.setiUserId(Common.user.getiUserId());

        user.setiUserId(Common.user.getiUserId());

        if (mBirthDateText.getText().toString().equals(R.string.dateBeforeToday) || mPhone.getText().toString().equals("") || genderId == -1 || languagesId.get(0) == -1 || religionId == -1)
            Toast.makeText(getActivity(), getString(R.string.myProfileRequiredField), Toast.LENGTH_SHORT).show();
        else {
            new GeneralTask(getActivity(), new UseTask() {
                @Override
                public void doAfterTask(String result) {
                    if (result != null && !result.equals("")) {
                        final User user = new Gson().fromJson(result, User.class);
                        if (user.getiUserId() != 0 && user.getiUserId() != -1) {
                            Common.user = user;
                            SPManager.getInstance(getActivity().getApplicationContext()).saveUser(Common.user);
                            Toast.makeText(getActivity(), getString(R.string.myProfileUpdateSuccess), Toast.LENGTH_SHORT).show();
                            try {
                                ((MainActivity) getActivity()).initNavigationDrawerBackgroundImage();
                            } catch (IOException e) {
                                Log.e("Error", e.getMessage());
                                e.printStackTrace();
                            }

                        }
                    } else
                        Toast.makeText(getActivity(), getString(R.string.myProfileUpdateError), Toast.LENGTH_SHORT).show();
                }
            }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), user.toSend(), ConnectionUtil.UpdateUser);
        }
    }

    private void initEvents() {

        mUserImage.post(new Runnable() {
            @Override
            public void run() {
                arguments = new CameraLauncherArguments(mUserImage.getWidth(), mUserImage.getHeight(), true, null);
            }
        });

        mRootView.findViewById(R.id.myProfileImageReplace)//change profile image from galery
                .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            arguments.eSourceType = CameraLauncherArguments.ESourceType.GALLERY;
                                            cameraLauncher.execute(arguments, callbackContext, getActivity());
                                        }
                                    }

                );
        mRootView.findViewById(R.id.myProfileImageRetake)//change profile image from camera
                .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            arguments.eSourceType = CameraLauncherArguments.ESourceType.CAMERA;
                                            cameraLauncher.execute(arguments, callbackContext, getActivity());
                                        }
                                    }

                );
        mRootView.findViewById(R.id.myProfileDelete)//delete image profile
                .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
//                                            Picasso.with(getActivity()).load(User.defaultUserImageUrl).skipMemoryCache().into(mUserImage);
                                            new Utils.DownloadImageTask(mUserImage)
                                                    .execute(User.defaultUserImageUrl);
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mUserOrdinalImage = ImageHelper.getBitmapFromURL(User.defaultUserImageUrl, getActivity());
                                                }
                                            }).start();

                                        }
                                    }
                );
        mUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mContext).initFragment(FullScreenMyProfileImage.getInstance(mContext, Common.user.getNvImage(), UserName));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//when return from camera
        super.onActivityResult(requestCode, resultCode, data);
        try {
            cameraLauncher.onActivityResult(requestCode, resultCode, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


