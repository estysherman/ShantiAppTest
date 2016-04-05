package webit.android.shanti.main.groups;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.customViews.AddMembersCustomView;
import webit.android.shanti.customViews.ChatView;
import webit.android.shanti.customViews.CustomButton;
import webit.android.shanti.customViews.slideuppanel.SlidingUpPanelLayout;
import webit.android.shanti.entities.Group;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.general.horizontallistview.HorizontalListView;
import webit.android.shanti.main.map.MapFragment;
import webit.android.shanti.main.messages.ChatFragment;

public class GroupChatFragment extends GroupMainFragment {

    public static String ARG_GROUP = "mGroup";

    public static int currentChatUserGroupId = -1;

    public static GroupChatFragment instance = null;

    public static GroupChatFragment getInstance(Context context, String groupAsJson) {
        GroupChatFragment groupChatFragment = new GroupChatFragment();
        if (groupAsJson == null)
            groupAsJson = "";
        if (instance == null)
            instance = groupChatFragment;
        Bundle args = new Bundle();
        args.putString(ARG_GROUP, groupAsJson);
        Utils.sendToLog(context, "GroupChatFragment getInstance", "instance:" + instance + " args:" + args + " groupAsJson:" + groupAsJson);
        groupChatFragment.setArguments(args);

        return groupChatFragment;
    }

    public static GroupChatFragment getInstance() {
        if (instance == null)
            instance = new GroupChatFragment();
        return instance;
    }

    public void changeInternet(boolean b) {
        if (mChatView != null)
            if (b)
                mChatView.enable();
            else
                mChatView.disEnable();
    }

    View mRootView;
    Group mGroup;
    ChatView mChatView;
    HorizontalListView membersList;
    MembersAdapter membersAdapter;
    LinearLayout mHaveMembersLinear;
    LinearLayout mHaveNoMembersLinear;
    LinearLayout mDetailsGroupLinear;
    CustomButton mAddMembersBtn;
    AddMembersCustomView mAddMembersCustomView;
    AddMembersCustomView mAddMembersCustomViewBtn;
    SlidingUpPanelLayout mSlidingUpPanelLayout;
    private int mDetailsGroupLinearHeight = 0;
    private float mDetailsGroupLinearMarginTop = 0;
    private int mDetailsGroupLinearWidth = 0;
    private List<User> mUserInGroup;
    private List<User> mIUserRequested;

    public void updateGroup(Group mGroup, Bitmap mGroupBitmap) {
        this.mGroup = mGroup;
        if (mRootView!= null)
        ((ImageView) mRootView.findViewById(R.id.groupImage)).setImageBitmap(mGroupBitmap);
    }


    public GroupChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_group_chat, container, false);
        }
        mDetailsGroupLinear = (LinearLayout) mRootView.findViewById(R.id.linear_details_group);
        mHaveMembersLinear = (LinearLayout) mRootView.findViewById(R.id.linear_have_members);
        mHaveNoMembersLinear = (LinearLayout) mRootView.findViewById(R.id.linear_have_no_members);
        mAddMembersBtn = (CustomButton) mRootView.findViewById(R.id.groupDetailsContentAdd);
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) mRootView.findViewById(R.id.mainLoginSliding);
        mDetailsGroupLinearWidth = mDetailsGroupLinear.getWidth();
        mDetailsGroupLinearHeight = mDetailsGroupLinear.getHeight();
        mDetailsGroupLinearMarginTop = getActivity().getResources().getDimension(R.dimen.chat_group_details_marginTop);
       ChatFragment.mDelete = (ImageView) inflater.inflate(R.layout.chat_fragment, container, false).findViewById(R.id.deleteImg);
//        if (mGroup == null) {
//            return mRootView;
//        }
//        if (mGroup.getUsersList().size() == 1) {
//            mHaveMembersLinear.setVisibility(View.GONE);
//            mHaveNoMembersLinear.setVisibility(View.VISIBLE);
//            mSlidingUpPanelLayout.setPanelHeight(3);
//        }
//        if (mGroup.getUsersList().size() > 1) {
//            mSlidingUpPanelLayout.setPanelHeight(120);
//            membersAdapter = new MembersAdapter(mGroup.getUsersList(), this);
//            membersList = (HorizontalListView) mRootView.findViewById(R.id.membersList);
//            membersList.setAdapter(membersAdapter);
//            mHaveMembersLinear.setVisibility(View.VISIBLE);
//            mHaveNoMembersLinear.setVisibility(View.GONE);
//        }
//
//
//        changeActionBar(mRootView, mGroup.getNvGroupName(), getString(R.string.groupsTitle), View.VISIBLE, GroupsListFragment.class.toString());
//        paintStatusBar();
//        initGroupDetailsViews();
//        mChatView = (ChatView) mRootView.findViewById(R.id.chatView);
//        if (!mGroup.getNvQBDialogId().equals("") && !mGroup.getNvQBRoomJid().equals("")) {
//            QBDialog qbDialog = new QBDialog();
//            qbDialog.setDialogId(mGroup.getNvQBDialogId());
//            qbDialog.setRoomJid(mGroup.getNvQBRoomJid());
//            mChatView.startChat(mGroup.getUsersList(), QBDialogType.GROUP, qbDialog, mGroup.getNvGroupName(), this, mGroup.getiGroupId());
//        }
//        if (mGroup == null) {
//            return mRootView;
//        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            mGroup = new Gson().fromJson(getArguments().getString(ARG_GROUP), Group.class);
            if (mGroup != null)
                currentChatUserGroupId = mGroup.getiGroupId();
            mAddMembersCustomViewBtn = (AddMembersCustomView) mRootView.findViewById(R.id.groupDetailsContentAddMembers);
            mAddMembersCustomViewBtn.setGroupId(mGroup.getiGroupId());
            mAddMembersCustomViewBtn.setVisibility(View.VISIBLE);
            mAddMembersCustomView = (AddMembersCustomView) mRootView.findViewById(R.id.groupEditAddMembers);
            mAddMembersCustomView.setGroupId(mGroup.getiGroupId());
			//mAddMembersCustomView.setVisibility(View.VISIBLE);
            mAddMembersCustomView.setVisibility(View.GONE);

        }

        updateCurrentLayout();

        mAddMembersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//הכפתור מופיע שאין בככלל חברים
                v.setVisibility(View.INVISIBLE);
                mAddMembersCustomViewBtn.callOnclickToAllBTn();
            }
        });
        if (mGroup != null) {
            mUserInGroup = new ArrayList<>();
            mIUserRequested = new ArrayList<>();
            for (int i = 0; i < mGroup.getUsersList().size(); i++)
                if (mGroup.getUsersList().get(i).isbIsActive())//בודק אם נשלח לו בקשה או שהוא קייים כבר
                    mUserInGroup.add(mGroup.getUsersList().get(i));
                else
                    mIUserRequested.add(mGroup.getUsersList().get(i));

            membersAdapter = new MembersAdapter(mUserInGroup, this);
            membersList = (HorizontalListView) mRootView.findViewById(R.id.membersList);
            membersList.setAdapter(membersAdapter);
            if (mGroup.getiNumOfMembers() == 1) {
                mHaveMembersLinear.setVisibility(View.GONE);
                mHaveNoMembersLinear.setVisibility(View.VISIBLE);
                mSlidingUpPanelLayout.setPanelHeight(3);
            }
            if (mGroup.getiNumOfMembers() > 1) {
                mSlidingUpPanelLayout.setPanelHeight(120);
                mHaveMembersLinear.setVisibility(View.VISIBLE);
                mHaveNoMembersLinear.setVisibility(View.GONE);
            }


            changeActionBar(mRootView, mGroup.getNvGroupName(), getString(R.string.groupsTitle), View.VISIBLE, GroupsListFragment.class.toString());
            paintStatusBar();
            initGroupDetailsViews();
            mChatView = (ChatView) mRootView.findViewById(R.id.chatView);
            if (!mGroup.getNvQBDialogId().equals("") && !mGroup.getNvQBRoomJid().equals("")) {
                QBDialog qbDialog = new QBDialog();
                qbDialog.setDialogId(mGroup.getNvQBDialogId());
                qbDialog.setRoomJid(mGroup.getNvQBRoomJid());

                mChatView.startChat(mUserInGroup, QBDialogType.GROUP, qbDialog, mGroup.getNvGroupName(), this, mGroup.getiGroupId());
            }
        }
    }

    private void paintStatusBar() {//שינוי העיצוב של הפס שמופיע במכשיר למעלה
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.purple_home));
        }
    }

    private void initGroupDetailsViews() {
//        Picasso.with(getActivity()).load(mGroup.getNvImage()).skipMemoryCache().into((ImageView) mRootView.findViewById(R.id.groupImage));
        new Utils.DownloadImageTask((ImageView) mRootView.findViewById(R.id.groupImage)).execute(mGroup.getNvImage());

        ((TextView) mRootView.findViewById(R.id.groupMoreAbout)).setText(getString(R.string.groupsMoreAbout) + " " + mGroup.getNvGroupName());
        (mRootView.findViewById(R.id.groupMoreAbout)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFragment(GroupProfileFragment.getInstance(mGroup.getJson()));
            }
        });
        //why we need groupMoreAbout and groupMoreAbout1 - isn't it duplicate?
        ((TextView) mRootView.findViewById(R.id.groupMoreAbout1)).setText(getString(R.string.groupsMoreAbout) + " " + mGroup.getNvGroupName());
        (mRootView.findViewById(R.id.groupMoreAbout1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFragment(GroupProfileFragment.getInstance(mGroup.getJson()));
            }
        });
        //if Common.user is management of the group
        if (mGroup.getiMainUserId() == Common.user.getiUserId()) {
            mRootView.findViewById(R.id.groupMeetingPoint).setVisibility(View.VISIBLE);//נקודת מפגש
            mRootView.findViewById(R.id.groupMeetingPoint).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initFragment(MapFragment.getInstance(mGroup.getiGroupId(), true));
                    //MapFragment.getInstance().setmGroupIdToCreateMeetingPoint(mGroup.getiGroupId());
                }
            });
        } else {
            mRootView.findViewById(R.id.groupMeetingPoint).setVisibility(View.INVISIBLE);
//            mRootView.findViewById(R.id.addGroupMeetingPointIcon).setVisibility(View.INVISIBLE);
        }
        ((TextView) mRootView.findViewById(R.id.groupInfo)).setText(mGroup.getNvComment() + "");
        for (User user : mGroup.getUsersList()) {
            //if user is management of the group
            if (user.getiUserId() == mGroup.getiMainUserId()) {
                ((TextView) mRootView.findViewById(R.id.groupAddress)).setText(user.getoCountry().getNvValue());
                break;
            }
        }

//        final AddMembersCustomView addMembersCustomView = ((AddMembersCustomView) mRootView.findViewById(R.id.groupAddFriends));
//        addMembersCustomView.setmGoneView(mRootView.findViewById(R.id.groupHeader));
//        addMembersCustomView.setmUsersExistInGroup(User.getUsersId(mGroup.getUsersList()));
//        addMembersCustomView.setGroupId(mGroup.getiGroupId());
//        mRootView.findViewById(R.id.groupAddSave).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                List<Integer> usersId = new ArrayList<>();
//                for (User user : addMembersCustomView.getmUsersGroup())
//                    usersId.add(user.getiUserId());
//                UpdateGroupToSend groupToSend = new UpdateGroupToSend(mGroup, usersId, null);
//                new GeneralTask(getActivity(), new UseTask() {
//                    @Override
//                    public void doAfterTask(String result) {
//                        Group group = new Gson().fromJson(result, Group.class);
//                        if (group == null || group.getiGroupId() == -1)
//                            Toast.makeText(getActivity(), getString(R.string.groupEditUpdateError), Toast.LENGTH_SHORT).show();
//                        else addMembersCustomView.init(getActivity(), null);
//                    }
//                }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), groupToSend.getJson(), ConnectionUtil.UpdateGroup);
//            }
//        });
    }


    @Override
    public void onPause() {
        super.onPause();
        currentChatUserGroupId = -1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            mChatView.getCameraLauncher().onActivityResult(requestCode, resultCode, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateCurrentLayout() {//מעדכנת פריסה נוכחית
        if (mAddMembersCustomView != null)
            mAddMembersCustomView.setChangeMode(new AddMembersCustomView.clickCallBack() {
                @Override
                public void doCallBack(boolean isFocus) {
                    mDetailsGroupLinear.setBackgroundResource(R.color.white);
                    if (!isFocus) {
                        mDetailsGroupLinearWidth = mDetailsGroupLinear.getWidth();
                        mDetailsGroupLinearHeight = mDetailsGroupLinear.getHeight();
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        params.setMargins(0, 0, 0, 0);
                        mDetailsGroupLinear.setLayoutParams(params);
                    } else {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mDetailsGroupLinearWidth, mDetailsGroupLinearHeight);
                        int helpConvertToInt = (int) mDetailsGroupLinearMarginTop;
                        params.setMargins(0, helpConvertToInt, 0, 0);
                        mDetailsGroupLinear.setLayoutParams(params);
                    }
                }
            });
    }
}
