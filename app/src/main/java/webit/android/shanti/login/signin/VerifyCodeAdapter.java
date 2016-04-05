package webit.android.shanti.login.signin;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.customViews.CustomTextView;

/**
 * Created by crm on 06/12/2015.
 */
public class VerifyCodeAdapter extends BaseAdapter implements View.OnClickListener {
    Context mContext;
    List<Integer> mList = new ArrayList<>();
    int count = 0;
    private String mVerifyCodeUserInput = "";
    private String mVerifyCode = "";

    public VerifyCodeAdapter(Context context, String mVerifyCode) {
        mContext = context;
        for (int i = 1; i <= 12; i++)
            mList.add(i);
        this.mVerifyCode = mVerifyCode;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(R.layout.verify_code_adapter, null);
        CustomTextView digitTv = (CustomTextView) convertView.findViewById(R.id.verifyDigit);
        CustomTextView cancelTv = (CustomTextView) convertView.findViewById(R.id.verifyCancel);
        RelativeLayout relativeLayoutLine = (RelativeLayout) convertView.findViewById(R.id.line);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);//יצירת RelativeLayout בקוד - לטבלת מספרים
        switch (position) {//מיקום במטריצה
            case 0:
                params.setMargins(0, 0, 1, 1);//ליצירת קווי אורך / רוחב לטבלה
                break;
            case 1:
                params.setMargins(1, 0, 1, 1);
                break;
            case 2:
                params.setMargins(1, 0, 0, 1);
                break;
            case 3:
            case 6:
                params.setMargins(0, 1, 1, 1);
                break;
            case 4:
            case 7:
                params.setMargins(1, 1, 1, 1);
                break;
            case 5:
            case 8:
                params.setMargins(1, 1, 0, 1);
                break;
            case 9:
                params.setMargins(0, 1, 1, 0);
                break;
            case 10:
                params.setMargins(1, 1, 1, 0);
                break;
            case 11:
                params.setMargins(1, 1, 0, 0);
                break;


        }
        relativeLayoutLine.setLayoutParams(params);
        if (position <= mList.size() - 2) {//כל המספרים
            digitTv.setVisibility(View.VISIBLE);//תציג מספר
            digitTv.setOnClickListener(this);
            cancelTv.setVisibility(View.INVISIBLE);//אל תציג Cancel
            if (position == mList.size() - 2)//במיקום עשירי - הצב ערך '0'
                digitTv.setText("0");
            else if (position == mList.size() - 3) {//במיקום התשיעי
                cancelTv.setVisibility(View.INVISIBLE);//אל תציג כלום - ריק
                digitTv.setVisibility(View.INVISIBLE);
            } else
                digitTv.setText(mList.get(position) + "");//הצב ערך מספרי

        } else {//במיקום ה - 12
            cancelTv.setVisibility(View.VISIBLE);// תציג Cancel
            cancelTv.setOnClickListener(this);
            digitTv.setVisibility(View.INVISIBLE);//אל תציג מספר
        }


        return convertView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verifyDigit: {//אם לחצו על ספרה
                mVerifyCodeUserInput += ((TextView) v).getText().toString();//משרשר למחרוזת את הקוד
                ((VerifyCodeFragment) ((SignUpActivity) mContext).getSupportFragmentManager().findFragmentByTag(VerifyCodeFragment.class.toString())).getViews()[count].setAlpha((float) 1);//ניגש לעיגול
                count++;//מעלה את מונה המספרים שהוקשו לקוד

                if (count == 4) {//סיים להקיש קוד אימות
                    if (mVerifyCode.equals(mVerifyCodeUserInput)) {//אם שווה לקוד אימות שנשלח
                        ((Activity) mContext).onBackPressed();//יוצא מהמסך
                        ((SignUpActivity) mContext).initFragment(ProfileInfoFragment.getInstance(), mContext.getString(R.string.mainLoginSignUp));//עובר למסך הבא

                    } else {//הקוד אינו תואם את הקוד אימות שנשלח
                        Toast.makeText(mContext, mContext.getString(R.string.verifyCodeNoMatch), Toast.LENGTH_SHORT).show();
                        resetData();//מאפס מונה ומחרוזת קוד
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                resetData();//מאפס מונה ומחרוזת קוד
                                //מאפס שקיפות צבע עיגולים
                                ((VerifyCodeFragment) ((SignUpActivity) mContext).getSupportFragmentManager().findFragmentByTag(VerifyCodeFragment.class.toString())).setAlphaViews();
                            }
                        }, 1000);
                    }
                }
                break;
            }
            case R.id.verifyCancel: {//אם לחצו על Cancel
                if (count > 0) {//אם כבר נלחץ מספר
                    count--;//מוריד אותו מהמונה
                    if (mVerifyCodeUserInput.length() > 0) {//אם יש במחרוזת מספר
                        mVerifyCodeUserInput = mVerifyCodeUserInput.substring(0, mVerifyCodeUserInput.length() - 1);//מסיר אותו
                    }
                }
                ((VerifyCodeFragment) ((SignUpActivity) mContext).getSupportFragmentManager().findFragmentByTag(VerifyCodeFragment.class.toString())).getViews()[count].setAlpha((float) 0.5);
            }
            break;
        }
    }

    //איפוס מונה וקוד אימות שמוקש
    private void resetData() {
        count = 0;
        mVerifyCodeUserInput = "";
    }
}
