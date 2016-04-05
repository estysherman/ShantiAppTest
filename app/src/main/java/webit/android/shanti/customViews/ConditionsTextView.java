package webit.android.shanti.customViews;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import webit.android.shanti.login.signin.MainLoginFragment;

/**
 * Created by AndroIT on 22/01/2015.
 */
//כרגע לא משתמשים במחלקה זו
public class ConditionsTextView extends RelativeLayout {
    public ConditionsTextView(Context context) {
        super(context);
    }

    public ConditionsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConditionsTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    private void init() {

        SpannableString ss = new SpannableString("Android is a Software stack");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                getContext().startActivity(new Intent(getContext(), MainLoginFragment.class));
            }
        };
        ss.setSpan(clickableSpan, 22, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//        TextView textView = (TextView) findViewById(R.id.hello);
//        textView.setText(ss);
//        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private ClickableSpan getClickableSpan(final String word) {
        return new ClickableSpan() {
            final String mWord;
            {
                mWord = word;
            }

            @Override
            public void onClick(View widget) {
                Log.d("tapped on:", mWord);
                Toast.makeText(widget.getContext(), mWord, Toast.LENGTH_SHORT)
                        .show();
            }

            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
            }
        };
    }

}
