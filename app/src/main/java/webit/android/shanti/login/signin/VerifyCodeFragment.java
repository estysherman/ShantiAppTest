package webit.android.shanti.login.signin;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import webit.android.shanti.R;
import webit.android.shanti.entities.Group;


public class VerifyCodeFragment extends SignUpBaseFragment implements View.OnClickListener {


    public static String ARG_VERIFICATION_CODE = "verification_code";

    int count = 0;


    private String mVerifyCode;
    private String mVerifyCodeUserInput = "";

    private View mRootView;
    View status1;
    View status2;
    View status3;
    View status4;
   public View[] views = new View[4];
//   static VerifyCodeFragment instance = new VerifyCodeFragment();

    public VerifyCodeFragment() {
        // Required empty public constructor
    }


    public static VerifyCodeFragment getInstance(String verifyCode) {
//        if(instance == null)
        VerifyCodeFragment  instance= new VerifyCodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VERIFICATION_CODE, verifyCode);
        instance.setArguments(args);
        return instance;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVerifyCode = getArguments().getString(VerifyCodeFragment.ARG_VERIFICATION_CODE);
        }
        ((SignUpActivity)getActivity()).setBackgroundForLinear(R.color.purple_home);
    }

    public View[] getViews() {
        return views;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        resetData();//איפוס מונה וקוד אימות שמוקש
        //mVerifyCode = getArguments().getString(VerifyCodeFragment.ARG_VERIFICATION_CODE);
        mRootView = inflater.inflate(R.layout.fragment_verify_code, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        GridView gridViewVerifyCode = (GridView) mRootView.findViewById(R.id.verifyGridView);
        VerifyCodeAdapter adapter = new VerifyCodeAdapter(getActivity(), mVerifyCode);
        gridViewVerifyCode.setAdapter(adapter);

        status1 = mRootView.findViewById(R.id.verify_code_square1);
        status2 = mRootView.findViewById(R.id.verify_code_square2);
        status3 = mRootView.findViewById(R.id.verify_code_square3);
        status4 = mRootView.findViewById(R.id.verify_code_square4);
        views[0] = status1;
        views[1] = status2;
        views[2] = status3;
        views[3] = status4;

//        mRootView.findViewById(R.id.verify_code_digit_0).setOnClickListener(this);
//        mRootView.findViewById(R.id.verify_code_digit_1).setOnClickListener(this);
//        mRootView.findViewById(R.id.verify_code_digit_2).setOnClickListener(this);
//        mRootView.findViewById(R.id.verify_code_digit_3).setOnClickListener(this);
//        mRootView.findViewById(R.id.verify_code_digit_4).setOnClickListener(this);
//        mRootView.findViewById(R.id.verify_code_digit_5).setOnClickListener(this);
//        mRootView.findViewById(R.id.verify_code_digit_6).setOnClickListener(this);
//        mRootView.findViewById(R.id.verify_code_digit_7).setOnClickListener(this);
//        mRootView.findViewById(R.id.verify_code_digit_8).setOnClickListener(this);
//        mRootView.findViewById(R.id.verify_code_digit_9).setOnClickListener(this);
//        mRootView.findViewById(R.id.verify_code_back_btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (count > 0) {
//                    count--;
//                    if (mVerifyCodeUserInput.length() > 0) {
//                        mVerifyCodeUserInput = mVerifyCodeUserInput.substring(0, mVerifyCodeUserInput.length() - 1);
//                    }
//                }
//                views[count].setAlpha((float) 0.5);
//            }
//        });

        return mRootView;
    }


    @Override
    public void onClick(View v) {
        mVerifyCodeUserInput += ((TextView) v).getText().toString();

        views[count].setAlpha((float) 1);
        count++;

        if (count == 4) {
            if (mVerifyCode.equals(mVerifyCodeUserInput)) {
                getActivity().onBackPressed();
                initFragment(ProfileInfoFragment.getInstance(), getString(R.string.mainLoginSignUp));

            } else {
                Toast.makeText(getActivity(), getString(R.string.verifyCodeNoMatch), Toast.LENGTH_SHORT).show();
                resetData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetData();
                        setAlphaViews();
                    }
                }, 1000);
            }
        }
    }
    //איפוס מונה וקוד אימות שמוקש
    private void resetData() {
        count = 0;
        mVerifyCodeUserInput = "";
    }

    @Override
    public void onPause() {
        super.onPause();
        ((SignUpActivity) getActivity()).setBackground(R.drawable.bg_enterscreens);
    }

    @Override
    public void onResume() {
        super.onResume();
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ((SignUpActivity) getActivity()).setBackground(R.color.purple_home);
    }
    //הנקודות - בשקיפות צבע
    public void setAlphaViews(){
        views[0].setAlpha((float) 0.5);
        views[1].setAlpha((float) 0.5);
        views[2].setAlpha((float) 0.5);
        views[3].setAlpha((float) 0.5);
    }
}
