package webit.android.shanti.main.groups;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

import webit.android.shanti.R;
import webit.android.shanti.customViews.CustomButton;
import webit.android.shanti.customViews.CustomTextView;
import webit.android.shanti.main.MainBaseFragment;

/**
 * Created by Android2 on 26/05/2015.
 */
public class GroupMainFragment extends MainBaseFragment {

   /* public GroupMainFragment() {
        initFragment(GroupsListFragment.getInstance());
    }*/

    public static MainBaseFragment getInstance() {
        return GroupsListFragment.getInstance();
    }

    @Override
    public void setActionBarEvents(View view) {
        super.setActionBarEvents(view);

        view.findViewById(R.id.groupActionAddNew).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFragment(NewGroupFragment.getInstance());
 				if (GroupsAdapter.view != null)
                    GroupsAdapter.view.setVisibility(View.GONE);
                GroupsListFragment.mDelete.setVisibility(View.GONE);
            }
        });

        view.findViewById(R.id.groupActionApproval).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFragment(ApprovalGroupFragment.getInstance());
               if (GroupsAdapter.view != null)
                    GroupsAdapter.view.setVisibility(View.GONE);
                GroupsListFragment.mDelete.setVisibility(View.GONE);
            }
        });
        view.findViewById(R.id.groupActionBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupsListFragment.setInstance(null);
                initFragment(GroupsListFragment.getInstance());
                setBackView(v);
            }
        });
//        view.findViewById(R.id.groupActionBackIcon).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GroupsListFragment.setInstance(null);
//                initFragment(GroupsListFragment.getInstance());
//                setBackView(v);
//            }
//        });


        //setBackView(view.findViewById(R.id.groupActionBack));

//        view.findViewById(R.id.groupDeleteImg).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {//יקרא לפונקציה מ GroupListFragment
//                initFragment(ApprovalGroupFragment.getInstance());
//            }
//        });

        setBackView(view.findViewById(R.id.groupActionBack));

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
    public void changeActionBar(View view, String title, String leftTitle, int visibility, String fragmentName) {
        super.changeActionBar(view, title, leftTitle, visibility);
        boolean existInStack = false;
        setActionBarEvents(view);
        for (int i = 0; i < getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++) {
            if (getActivity().getSupportFragmentManager().getBackStackEntryAt(i).getName().equals(fragmentName)) {
                existInStack = true;
                break;
            }
        }
        if (existInStack) {
            ((CustomTextView) view.findViewById(R.id.groupActionBackText)).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.groupActionBackText)).setText(leftTitle);
        } else {
            ((CustomTextView) view.findViewById(R.id.groupActionBackText)).setVisibility(View.INVISIBLE);
        }

        if (title.length() > 13)
            title = title.substring(0, 11) + "...";
        ((TextView) view.findViewById(R.id.groupActionTitle)).setText(title);

        view.findViewById(R.id.groupActionAddNew).setVisibility(visibility);
        //view.findViewById(R.id.groupActionRemove).setVisibility(View.VISIBLE);
        view.findViewById(R.id.groupActionApproval).setVisibility(visibility);
    }

    public void changeActionBar(View view, String title, String leftTitle, int visibility) {
        super.changeActionBar(view, title, leftTitle, visibility);
        setActionBarEvents(view);
        ((TextView) view.findViewById(R.id.groupActionBackText)).setText(leftTitle);
        ((CustomTextView) view.findViewById(R.id.groupActionBackIcon)).setVisibility(View.VISIBLE);
        ((LinearLayout) view.findViewById(R.id.groupActionBack)).setVisibility(View.VISIBLE);

        ((TextView) view.findViewById(R.id.groupActionTitle)).setText(title);
        view.findViewById(R.id.groupActionAddNew).setVisibility(visibility);
        //view.findViewById(R.id.groupActionRemove).setVisibility(View.VISIBLE);
        view.findViewById(R.id.groupActionApproval).setVisibility(visibility);
    }
}
