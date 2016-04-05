package webit.android.shanti.main.messages;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.quickblox.chat.model.QBDialogType;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.customViews.ChatView;
import webit.android.shanti.entities.User;
import webit.android.shanti.main.MainActivity;
import webit.android.shanti.main.MainBaseFragment;
import webit.android.shanti.main.map.MemberProfileFragment;


public class ChatFragment extends MainBaseFragment {

    public static int currentChatUserId = -1;

    private View mRootView;
    public static final String ARG_USER = "userArg";
    private static Context mContext;
    private User mUser;
    private ChatView mChatView;
    public static ImageView mDelete = null;
    private EditText messege;

    public ChatFragment() {

    }

    List<User> mUsers;

    public static ChatFragment instance = null;

    public static ChatFragment getInstance() {
        if (instance == null)
            instance = new ChatFragment();
        return instance;

    }

    public void changeInternet(boolean b) {
        if (mChatView != null)
            if (b)
                mChatView.enable();
            else
                mChatView.disEnable();
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

    public static ChatFragment getInstance(Context context, String userAsJson) {
        if (userAsJson == null)
            userAsJson = "";
        boolean isActive = instance != null;
        ChatFragment fragment = ChatFragment.getInstance();
        Bundle args = new Bundle();
        if (args != null)
            args.putString(ARG_USER, userAsJson);
        if (!isActive) {
            fragment.setArguments(args);
            Utils.sendToLog(context, "ChatFragment getInstance", "fragment:" + fragment + "args:" + args);
        } else {
            fragment.getArguments().putString(ARG_USER, userAsJson);
            Utils.sendToLog(context, "ChatFragment getInstance", "fragment:" + fragment + "args:" + args);
        }

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = ChatFragment.getInstance().getActivity();

        if (getArguments() != null) {
            mUser = new Gson().fromJson(getArguments().getString(ARG_USER), User.class);
            currentChatUserId = mUser.getiUserId();
        }

        mRootView = inflater.inflate(R.layout.chat_fragment, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        changeActionBar(mRootView, "");

        mDelete = (ImageView) mRootView.findViewById(R.id.deleteImg);
        mUsers = new ArrayList<>();
        mUsers.add(mUser);

        mChatView = ((ChatView) mRootView.findViewById(R.id.chatView));
        mChatView.startChat(mUsers, QBDialogType.PRIVATE, "", this);

        mRootView.findViewById(R.id.chatUserImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View actionBarView = mRootView.findViewById(R.id.chatActionBar);
                ((MainActivity) mContext).initFragment(FullScreenChatImageFragment.getInstance(mContext, mUser.getNvImage(), mUser.getNvShantiName(), ChatFragment.getInstance().getClass().toString(), actionBarView));
            }
        });
        initTitleChat();

        TextView mMemberInGroup = (TextView) mRootView.findViewById(R.id.memberInGroup);
        mMemberInGroup.setText(getString(R.string.userInfoText, mUser.getiNumGroupAsMemberUser(), mUser.getiNumGroupAsMainUser()));
        TextView mMoreAbout = (TextView) mRootView.findViewById(R.id.moreAbout);
        mMoreAbout.setText(getString(R.string.profileMemberMoreInfo, mUser.getUserName()));
        mMoreAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFragment(MemberProfileFragment.getInstance(getArguments().getString(ARG_USER)));
            }
        });

        if (savedInstanceState != null) {
            ChatView.mChatMessageText.setText(savedInstanceState.get("messesage").toString());
        }

        return mRootView;
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("messesage", ChatView.mChatMessageText.getText().toString());
    }

    private void initTitleChat() {
        final ImageView mImg = (ImageView) mRootView.findViewById(R.id.chatUserImage);
        Picasso.with(getActivity()).load(mUser.getNvImage()).into(mImg);
        //((TextView) mRootView.findViewById(R.id.chatTitleName)).setText(mUser.getNvFirstName() + " " + mUser.getNvLastName());
        ((TextView) mRootView.findViewById(R.id.chatTitleName)).setText(mUser.getNvShantiName());
        mRootView.findViewById(R.id.chatBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
				Utils.HideKeyboard(getActivity());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        currentChatUserId = -1;
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


}
