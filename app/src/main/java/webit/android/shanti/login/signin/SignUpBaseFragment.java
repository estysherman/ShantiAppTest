package webit.android.shanti.login.signin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Field;

import webit.android.shanti.Utils.Utils;

/**
 * Created by user on 14/05/2015.
 */
public class SignUpBaseFragment extends Fragment implements View.OnTouchListener{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void initFragment(Fragment fragment, String title) {
        ((SignUpActivity) getActivity()).initFragment(fragment, title);
    }

    public void setIsBackEnable(boolean isBackEnable) {
        ((SignUpActivity) getActivity()).setIsBackEnable(isBackEnable);
    }
    @Override
    public void onDetach() {//ניתוק
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Utils.HideKeyboard(getActivity());
        return true;
    }


}
