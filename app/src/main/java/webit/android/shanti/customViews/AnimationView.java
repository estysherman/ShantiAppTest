package webit.android.shanti.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import webit.android.shanti.R;

/**
 * Created by AndroIT on 10/02/2015.
 */
//הפס הציבעוני בתחתית האפליקציה
public class AnimationView extends LinearLayout {

//        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.animationLayout);
//        relativeLayout.post(new Runnable() {
//            public void run() {
//                startAnimation(relativeLayout.getWidth());
//            }
//        });

    public AnimationView(Context context) {
        super(context);
    }

    public AnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    //אתחול
    private void init() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.line_animation, this);
        //setBackgroundResource(R.drawable.color_line_animate);
        View v = (View) getParent();
        startAnimation();
    }
    //אנימציה
    private void startAnimation() {
        Animation animation1 = AnimationUtils.loadAnimation(getContext(), R.anim.line_anim);
        Animation animation2 = AnimationUtils.loadAnimation(getContext(), R.anim.line_anim2);
        animation1.setInterpolator(new LinearInterpolator());
        animation2.setInterpolator(new LinearInterpolator());
        int width = 800;
        final View img_animation1 = findViewById(R.id.img_animation1);
        final View img_animation2 = findViewById(R.id.img_animation2);
        img_animation1.startAnimation(animation1);
        img_animation2.startAnimation(animation2);

//        ObjectAnimator mEnterAnim1;
//        ObjectAnimator mEnterAnim2;
//        ObjectAnimator mExitAnim1;
//        ObjectAnimator mExitAnim2;
//
//        mEnterAnim1 = ObjectAnimator.ofFloat(img_animation1, "translationX", width, 0f);
//        mEnterAnim1.setRepeatCount(AlphaAnimation.INFINITE);
//        mEnterAnim1.setDuration(7000);
//        mEnterAnim1.setInterpolator(new LinearInterpolator());
//        mEnterAnim2 = ObjectAnimator.ofFloat(img_animation2, "translationX", width, 0f);
//        mEnterAnim2.setRepeatCount(AlphaAnimation.INFINITE);
//        mEnterAnim2.setDuration(7000);
//        mEnterAnim2.setInterpolator(new LinearInterpolator());
//        mExitAnim1 = ObjectAnimator.ofFloat(img_animation1, "translationX", 0f, -width);
//        mExitAnim1.setRepeatCount(AlphaAnimation.INFINITE);
//        mExitAnim1.setDuration(7000);
//        mExitAnim1.setInterpolator(new LinearInterpolator());
//        mExitAnim2 = ObjectAnimator.ofFloat(img_animation2, "translationX", 0f, -width);
//        mExitAnim2.setRepeatCount(AlphaAnimation.INFINITE);
//        mExitAnim2.setDuration(7000);
//        mExitAnim2.setInterpolator(new LinearInterpolator());
//        mExitAnim1.start();
//        mEnterAnim2.start();

    }
}
