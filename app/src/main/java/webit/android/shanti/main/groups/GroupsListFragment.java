package webit.android.shanti.main.groups;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.customViews.CustomEditText;
import webit.android.shanti.entities.Group;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;

import webit.android.shanti.main.MainActivity;
import webit.android.shanti.main.map.MapFragment;

public class GroupsListFragment extends GroupMainFragment implements AbsListView.OnScrollListener {

    public static GroupsListFragment instance = null;
    public static boolean isFromNewGroupFragment;

    public static void setInstance(GroupsListFragment instance) {//הצגת תמונות חברי הקבוצה ברשימה
        GroupsListFragment.instance = instance;
    }

    public static GroupsListFragment getInstance() {
        if (instance == null) {
            instance = new GroupsListFragment();
        }
        return instance;
    }


   static ArrayList<Group> groups;

    public void updateGroup(Group group, Bitmap mGroupBitmap) {
        Log.d(Common.CrashTag, groups.toString());
        for (Group item : groups) {
            if (item.getiGroupId() == group.getiGroupId()) {
                item.setNvImage(group.getNvImage());
                item.setNvComment(group.getNvComment());
                item.setNvGroupName(group.getNvGroupName());
                updateAdapter(group);
                break;
            }
        }
    }

//    private void scrollMyListViewToBottom() {
//        groups.post(new Runnable() {
//            @Override
//            public void run() {
//                // Select the last row so it will scroll into view...
//                myListView.setSelection(myListAdapter.getCount() - 1);
//            }
//        });
//    }

    public void addGroup(Group group) {
        groups.add(group);
        updateAdapter(group);
    }

    public void removeGroup(int groupId) {
        for (Group group : groups)
            if (group.getiGroupId() == groupId) {
                groups.remove(group);
                updateAdapter();
                return;
            }
    }

    GroupsAdapter groupsAdapter;
    private static View mRootView;
    private CustomEditText mSearchET;
    private static LinearLayout mEmptyListLinear;
    private static LinearLayout mEmptyFullLinear;
    private static ListView groupsListView;
public static Fragment fragment;

    public List<View> Views;
    public static int oldSelectedKey = -1;
    public HashMap<Integer, Boolean> selected = new HashMap<>();
    ImageView actionBarDeleteImg;
    public static ImageView mDelete;

    public GroupsListFragment() {
    }


    //    public void notifyDataSetChanged() {
//        //groupsAdapter.notifyDataSetChanged();
//        updateAdapter();
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (mRootView == null) {
            groups = new ArrayList<>();
            // Inflate the layout for this fragment
            mRootView = inflater.inflate(R.layout.fragment_groups_list, container, false);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            mEmptyListLinear = (LinearLayout) mRootView.findViewById(R.id.LinearEmptyList);
            mEmptyFullLinear = (LinearLayout) mRootView.findViewById(R.id.LinearFullList);
            mSearchET = (CustomEditText) mRootView.findViewById(R.id.groupListSearch);
            mDelete = (ImageView) mRootView.findViewById(R.id.deleteImg);
            groupsListView = (ListView) mRootView.findViewById(R.id.groupList);
fragment = this;
            mSearchET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (groupsAdapter != null)
                        groupsAdapter.getFilter().filter(s);
 if (GroupsAdapter.view != null)
                        GroupsAdapter.view.setVisibility(View.GONE);
                    mDelete.setVisibility(View.GONE);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            if (savedInstanceState != null) {
                mSearchET.setText(savedInstanceState.get("Search").toString());
            }
//            super.changeActionBar(mRootView, getString(R.string.groupsTitle), "", View.VISIBLE);
            setGroupList();//איתחול רשימה

           
            mRootView.findViewById(R.id.groupListCreateNew).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initFragment(NewGroupFragment.getInstance());
                }
            });
        }
        this.groupsListView.setOnScrollListener(this);
        return mRootView;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Search", mSearchET.getText().toString());
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.changeActionBar(mRootView, getString(R.string.groupsTitle), getString(R.string.groupsMap), View.VISIBLE, MapFragment.class.toString());
//        groupsAdapter.getFilter().filter("");
        if (isFromNewGroupFragment == true) {//אם מגיע מקבוצה חדשה - נגלל לסוף הרשימה כדי להראות את הקבוצה שנוצרה
            groupsListView.setSelection(groupsAdapter.getCount() - 1);
            isFromNewGroupFragment = false;
        }

    }


    public void updateAdapter(Group group) {
        groupsAdapter = new GroupsAdapter(groups, fragment);
        groupsListView.setEmptyView(mRootView.findViewById(R.id.empty_view));
        groupsListView.setAdapter(groupsAdapter);
        if (groups == null || groups.size() == 0) {//בטעינה הראשונה
            mEmptyListLinear.setVisibility(View.VISIBLE);
            mEmptyFullLinear.setVisibility(View.GONE);
        } else {
            mEmptyListLinear.setVisibility(View.GONE);
            mEmptyFullLinear.setVisibility(View.VISIBLE);
        }
        if (group != null) {//בטעינה השניה

            for (int i = 0; i < groups.size(); i++) {
                if (groups.get(i).getiGroupId() == group.getiGroupId()) {
                    if (i >= groupsListView.getFirstVisiblePosition() && i <= groupsListView.getLastVisiblePosition()) {//
                        //Picasso.with(getActivity()).load(group.getNvImage()).skipMemoryCache().into((ImageView) groupsListView.getChildAt(i).findViewById(R.id.groupBg));
                        if (group.getiMainUserId() == Common.user.getiUserId()) {//אם אני מנהל הקבוצה
                            new Utils.DownloadImageTask((ImageView) groupsListView.getChildAt(i).findViewById(R.id.groupBg)).execute(Group.defultGroupImageUrl);
                            new Utils.DownloadImageTask((ImageView) groupsListView.getChildAt(i).findViewById(R.id.groupBg)).execute(groups.get(i).getNvImage());
                        } else {
                            Picasso.with(getActivity()).load(Group.defultGroupImageUrl).into((ImageView) groupsListView.getChildAt(i).findViewById(R.id.groupBg));
                            Picasso.with(getActivity()).load(groups.get(i).getNvImage()).into((ImageView) groupsListView.getChildAt(i).findViewById(R.id.groupBg));
                        }
                    }
                }
            }


//
//            int wantedPosition = i; // Whatever position you're looking for
//            int firstPosition = groupsListView.getFirstVisiblePosition() - groupsListView.getHeaderViewsCount(); // This is the same as child #0
//            int wantedChild = wantedPosition - firstPosition;
//            View wantedView = groupsAdapter.getView(wantedChild, null, null);
// Say, first visible position is 8, you want position 10, wantedChild will now be 2
// So that means your view is child #2 in the ViewGroup:
            //if (wantedChild < 0 || wantedChild >= groupsListView.getChildCount()) {
            //   Log.w("odeya", "Unable to get view for desired position, because it's not being displayed on screen.");
            //    return;
            ///}
// Could also check if wantedPosition is between listView.getFirstVisiblePosition() and listView.getLastVisiblePosition() instead.
            //View wantedView = groupsListView.getChildAt(wantedChild);


//            Picasso.with(getActivity()).load(group.getNvImage()).skipMemoryCache().into((ImageView) wantedView.findViewById(R.id.groupBg));
        }
    }

    private void updateAdapter() {
        updateAdapter(null);
    }

    private void setGroupList() {//איתחול רשימה
        new GeneralTask(getActivity(), new UseTask() {
            @Override
            public void doAfterTask(String result) {
                if (result != null) {
                    Log.d("get user group", result);
                    Type programsListType = new TypeToken<List<Group>>() {//המרת אוביקט הגיסון יותר מהר
                    }.getType();
                    groups = new Gson().fromJson(result, programsListType);//המרת הגיסון לסוג המתאים
                    updateAdapter();
                } else {
                    Toast.makeText(GroupsListFragment.this.getActivity(), GroupsListFragment.this.getActivity().getString(R.string.noConnectionNetwork), Toast.LENGTH_LONG).show();
                }
            }

        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"id\":" + Common.user.getiUserId() + "}", ConnectionUtil.GetUserGroups);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (view.getId()) {
            case R.id.groupList: {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL)
                    Utils.HideKeyboard(getActivity());
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
    public void showDeleteImg() {

    }

    public void hideDeleteImg() {

    }

    @Override
    public void onStart() {
        super.onStart();
        mSearchET.setText("");
    }
}
