package webit.android.shanti.login.signin;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.photo.ImageHelper;
import webit.android.shanti.general.photo.getimage.CallbackContext;
import webit.android.shanti.general.photo.getimage.CameraLauncher;
import webit.android.shanti.general.photo.getimage.CameraLauncherArguments;

//import com.squareup.picasso.Picasso;


public class TakePhotoFragment extends SignUpBaseFragment implements View.OnClickListener {


    public static final int TACK_PICTURE = 1;
    public static final int SELECT_FILE = 2;
    public static final int CROP_FROM_CAMERA = 3;
    private static final String USER_NAME = "userName";
    private static final String ARG_PARAM2 = "param2";
    public static TakePhotoFragment instance;
    private boolean isUseLoginImage = false;
    public static Bitmap bitmap1 = null;
    public static Bundle savedInstanceState1 = null;

    CameraLauncher cameraLauncher;
    CameraLauncherArguments arguments;
    CallbackContext callbackContext = new CallbackContext() {

        @Override
        public void success(Bitmap bitmap) {
            super.success(bitmap);
            useImageFromResult(bitmap);//כשחוזרת תמונה ממצלמה או מגלריה

        }
    };
    private View mRootView;
    private String userName = "";
    ImageView imageView;

    public TakePhotoFragment() {
        // Required empty public constructor
    }

    public static TakePhotoFragment getInstance(String param1, String param2) {
        if (instance == null)
            instance = new TakePhotoFragment();
        Bundle args = new Bundle();
        args.putString(USER_NAME, param1);
        args.putString(ARG_PARAM2, param2);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString(USER_NAME);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null)
            if (savedInstanceState.get("picture") != null)
                savedInstanceState1 = savedInstanceState;
        mRootView = inflater.inflate(R.layout.fragment_take_photo, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        ((TextView) mRootView.findViewById(R.id.takePhotoWelcomeTitle)).setText(getString(R.string.takePhotoWelcome) + " " + Common.user.getUserName() + "!");//מציב כותרת: welcome + שם משתמש
        imageView = (ImageView) mRootView.findViewById(R.id.takePhotoImageProfile);
        imageView.setOnClickListener(this);
        if (Common.user != null && Common.user.getLoginImage() != null) {//אם נכנס דרך פייסבוק או גוגל+
            isUseLoginImage = true;
//            Picasso.with(getActivity()).load(Common.user.getLoginImage()).into(imageView);
          if (bitmap1 == null)
                new Utils.DownloadImageTask(imageView).execute(Common.user.getLoginImage());//שם תמונה מתוך פייסבוק/גוגל+
            else
                imageView.setImageBitmap(bitmap1);

            mRootView.findViewById(R.id.TakePhotoOpenCameraBtn).setVisibility(View.GONE);
            mRootView.findViewById(R.id.takePhotoChooseFileBtn).setVisibility(View.GONE);
            mRootView.findViewById(R.id.takePhotoSkipBtn).setVisibility(View.GONE);
            mRootView.findViewById(R.id.takePhotoFinishBtn).setVisibility(View.VISIBLE);//המשך הרשמה
            mRootView.findViewById(R.id.takePhotoAgainBtn).setVisibility(View.VISIBLE);//צלם שוב
            ((TextView) mRootView.findViewById(R.id.takePhotoComment)).setText(getString(R.string.takePhotoCommit));
        } else {//אם נכנס דרך הרשמה רגילה
            isUseLoginImage = false;
            mRootView.findViewById(R.id.TakePhotoOpenCameraBtn).setVisibility(View.VISIBLE);//צלם תמונה
            mRootView.findViewById(R.id.takePhotoChooseFileBtn).setVisibility(View.VISIBLE);//בחר מתוך מאגר
            mRootView.findViewById(R.id.takePhotoSkipBtn).setVisibility(View.VISIBLE);//הוסף מאוחר יותר
            mRootView.findViewById(R.id.takePhotoFinishBtn).setVisibility(View.GONE);
            mRootView.findViewById(R.id.takePhotoAgainBtn).setVisibility(View.GONE);
            ((TextView) mRootView.findViewById(R.id.takePhotoComment)).setText(getString(R.string.takePhotoComment));
//            Picasso.with(getActivity()).load(User.defaultUserImageUrl).into(imageView);
             if (bitmap1 == null)
                new Utils.DownloadImageTask(imageView).execute(User.defaultUserImageUrl);//שם תמונת ברירת מחדל
            else
                imageView.setImageBitmap(bitmap1);

        }
  
        final Fragment fragment = this;
        //if (Common.user != null && Common.user.getProcessedBitmap() != null)
        //    imageView.setImageBitmap(Common.user.getProcessedBitmap());
        imageView.post(new Runnable() {
            @Override
            public void run() {
                cameraLauncher = new CameraLauncher(fragment);
                arguments = new CameraLauncherArguments(imageView.getWidth(), imageView.getHeight(), true, null);
            }
        });
        //מאזינים לכל הכפתורים על המסך
        addListeners();
//        }
        return mRootView;
    }

 
    //מאזינים לכל הכפתורים על המסך
    private void addListeners() {
        mRootView.findViewById(R.id.takePhotoSkipBtn).setOnClickListener(this);
        mRootView.findViewById(R.id.TakePhotoOpenCameraBtn).setOnClickListener(this);
        mRootView.findViewById(R.id.takePhotoChooseFileBtn).setOnClickListener(this);
        mRootView.findViewById(R.id.takePhotoAgainBtn).setOnClickListener(this);
        mRootView.findViewById(R.id.takePhotoFinishBtn).setOnClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override//לא עובד- -נופל
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            cameraLauncher.onActivityResult(requestCode, resultCode, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //כשחוזרת תמונה ממצלמה או מגלריה
    public void useImageFromResult(Bitmap bitmap) {
        //Common.user.setProcessedBitmap(bitmap);
        Bitmap b = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
        bitmap1 = bitmap;
     
//        float rotation = 270;
        imageView.setImageBitmap(bitmap);//שם את התמונה שהתקבלה
//        imageView.setRotation(rotation);
//            if (!uriFile.contains("http"))
//                uriFile = "file://" + uriFile;
        mRootView.findViewById(R.id.TakePhotoOpenCameraBtn).setVisibility(View.GONE);
        mRootView.findViewById(R.id.takePhotoChooseFileBtn).setVisibility(View.GONE);
        mRootView.findViewById(R.id.takePhotoSkipBtn).setVisibility(View.GONE);
        mRootView.findViewById(R.id.takePhotoFinishBtn).setVisibility(View.VISIBLE);//השלם הרשמה
        mRootView.findViewById(R.id.takePhotoAgainBtn).setVisibility(View.VISIBLE);//צלם שוב
        ((TextView) mRootView.findViewById(R.id.takePhotoComment)).setText(getString(R.string.takePhotoCommit));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePhotoSkipBtn://הוסף מאוחר יותר
                Common.user.setNvImage(null);
                initFragment(MoreInfoFragment.getInstance(), getString(R.string.moreInfoTitle));//עובר למסך הבא
                break;
            case R.id.takePhotoImageProfile://בלחיצה על תמונת פרופיל
            case R.id.TakePhotoOpenCameraBtn://בלחיצה על כפתור מצלמה
                isUseLoginImage = false;
                arguments.eSourceType = CameraLauncherArguments.ESourceType.CAMERA;//מפעיל את המצלמה
                cameraLauncher.execute(arguments, callbackContext, getActivity());
                break;
            case R.id.takePhotoChooseFileBtn://בלחיצה על בחירת תמונה מגלריה
                isUseLoginImage = false;
                arguments.eSourceType = CameraLauncherArguments.ESourceType.GALLERY;//פותח גלריה
                cameraLauncher.execute(arguments, callbackContext, getActivity());
                break;
            case R.id.takePhotoAgainBtn://בלחיצה על צלם שוב
                if (Common.user != null && Common.user.getLoginImage() != null)//אם נכנס מפייסבוק או גוגל+
                    isUseLoginImage = true;
                else//הרשמה רגילה
                    isUseLoginImage = false;
                mRootView.findViewById(R.id.TakePhotoOpenCameraBtn).setVisibility(View.VISIBLE);//צלם
                mRootView.findViewById(R.id.takePhotoChooseFileBtn).setVisibility(View.VISIBLE);//גלריה
                mRootView.findViewById(R.id.takePhotoSkipBtn).setVisibility(View.VISIBLE);//דלג
                mRootView.findViewById(R.id.takePhotoFinishBtn).setVisibility(View.GONE);
                mRootView.findViewById(R.id.takePhotoAgainBtn).setVisibility(View.GONE);
                ((TextView) mRootView.findViewById(R.id.takePhotoComment)).setText(getString(R.string.takePhotoComment));
//                Picasso.with(getActivity()).load(User.defaultUserImageUrl).into(imageView);
                new Utils.DownloadImageTask(imageView).execute(User.defaultUserImageUrl);

                break;
            case R.id.takePhotoFinishBtn://השלם הרשמה
                if (isUseLoginImage && Common.user != null && Common.user.getLoginImage() != null) {//הגיע מפייסבוק או גוגל+ ויש לו תמונה
                    Bitmap bitmap = ImageHelper.getBitmapFromURL(Common.user.getLoginImage(), getActivity());//שולף תמונה
                    Common.user.setNvImage(ImageHelper.getBase64FromBitmap(bitmap));//מכניס למשתמש המרה למחרוזת  - של התמונה
                } else {
                    Common.user.setNvImage(ImageHelper.getBase64FromImage(imageView));//מכניס למשתמש המרה למחרוזת  - של התמונה
                }
                initFragment(MoreInfoFragment.getInstance(), getString(R.string.moreInfoTitle));//עובר למסך הבא
                break;
        }
    }
}
