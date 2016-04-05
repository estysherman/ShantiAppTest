package webit.android.shanti.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;


/**
 * Created by user on 19/05/2015.
 */
public class MainBaseFragment extends Fragment implements View.OnTouchListener {


    /*public ActionBarManager getActionBarManager() {
        if (mActionBarManager == null) {
            mActionBarManager = ((MainActivity) getActivity()).getActionBarManager();
        }
        return mActionBarManager;
    }*/

    public void changeVisibility(View view, int visibility){
        view.setVisibility(visibility);
    }

    public void setActionBarEvents(View view){}

    public void changeActionBar(View view, String title){}

    public void changeActionBar(View view,String title, String leftTitle, int visibility){}
    public void changeActionBar(View view,String title, String leftTitle, int visibility, String fragmentName){}

    public void setBackView(View backView){
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).backClick(view);
            }
        });
    }

    public void initFragment(Fragment fragment) {
        ((MainActivity) getActivity()).initFragment(fragment);
    }

    public void initFragment(Fragment fragment, boolean addToBackStack) {
        ((MainActivity) getActivity()).initFragment(fragment, addToBackStack);
    }
    @Override
    public void onDetach() {
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
