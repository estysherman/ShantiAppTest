package webit.android.shanti.main.groups;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.customViews.CustomViewsInitializer;
import webit.android.shanti.customViews.ICallBackCustomViewsData;
import webit.android.shanti.entities.CodeValue;
import webit.android.shanti.entities.Group;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;
import webit.android.shanti.main.MainBaseFragment;


public class GroupProfileFragment extends MainBaseFragment {

    private static final String ARG_GROUP_ID = "mGroup";
    private Group mGroup;
    private List<User> mUsersRequested;
    private List<User> mUsers;
    private View mRootView = null;
    private TextView mGroupInfo;
    private TextView mGroupDescription;
    private TextView mGroupName;
    private ImageView mGroupImage;
    private LinearLayout mGroupMembersLl;
    ListView mMembersListView;

    static GroupProfileFragment instance = null;

    public static GroupProfileFragment getInstance(String groupAsJson) {
        boolean isActive = instance != null;
        if (instance == null)
            instance = new GroupProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_ID, groupAsJson);
        if (!isActive)
            instance.setArguments(args);//
        else
            instance.getArguments().putString(ARG_GROUP_ID, groupAsJson);
        return instance;
    }

    public static GroupProfileFragment getInstance() {
        if (instance == null)
            instance = new GroupProfileFragment();
        return instance;
    }


    public void updateGroup(Group mGroup, Bitmap mGroupBitmap) {
        this.mGroup = mGroup;

        //initViews();
        //changeActionBar(mRootView, "");

        //Picasso.with(getActivity()).load(Group.defultGroupImageUrl).into(mGroupImage);
        mGroupImage.setImageBitmap(mGroupBitmap);
        //Picasso.with(getActivity()).load(Uri.fromFile(new File(""))).into(mGroupImage);
        //Picasso.with(getActivity()).load(mGroup.getNvImage()).skipMemoryCache().into(mGroupImage);
        initViews();
    }


    private void updateGroupViews() {

    }

    public GroupProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void changeActionBar(View view, String title) {
        super.changeActionBar(view, title);
        setActionBarEvents(view);
        setBackView(view.findViewById(R.id.profileGroupActionBack));
        changeVisibility(view.findViewById(R.id.profileGroupSaveChanges), View.GONE);
        if (Common.user.getiUserId() == mGroup.getiMainUserId())
            changeVisibility(view.findViewById(R.id.profileGroupEdit), View.VISIBLE);
        else
            changeVisibility(view.findViewById(R.id.profileGroupEdit), View.GONE);
    }


    @Override
    public void setActionBarEvents(View view) {
        super.setActionBarEvents(view);
        view.findViewById(R.id.profileGroupEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFragment(EditGroupFragment.getInstance(mGroup.getJson()));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mUsers = new ArrayList<>();
        mUsersRequested = new ArrayList<>();
        if (getArguments() != null && getArguments().getString(ARG_GROUP_ID) != null) {
            mGroup = new Gson().fromJson(getArguments().getString(ARG_GROUP_ID), Group.class);
            if (mGroup != null && mGroup.getUsersList() != null)
                for (int i = 0; i < mGroup.getUsersList().size(); i++)
                    if (mGroup.getUsersList().get(i).isbIsActive())//חברי הקבוצה שאישרו את ההשתתפותם
                        mUsers.add(mGroup.getUsersList().get(i));
                    else
                        mUsersRequested.add(mGroup.getUsersList().get(i));
        }

        // Inflate the layout for this fragment
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_group_profile, container, false);
            mGroupImage = (ImageView) mRootView.findViewById(R.id.groupProfileImage);
            mGroupName = ((TextView) mRootView.findViewById(R.id.groupProfileName));
            mGroupInfo = ((TextView) mRootView.findViewById(R.id.groupProfileMoreInfo));
            mGroupDescription = ((TextView) mRootView.findViewById(R.id.groupProfileDesc));
            mMembersListView = (ListView) mRootView.findViewById(R.id.groupProfileFriendsList);
            //mGroupMembersLl = (LinearLayout) mRootView.findViewById(R.id.groupMembers);
        }
        changeActionBar(mRootView, "");
        initViews();


        return mRootView;
    }


    private void initViews() {
        mGroupInfo.setText(mGroup.getNvComment());//ההערות
        mGroupInfo.setMovementMethod(new ScrollingMovementMethod());
        new Utils.DownloadImageTask(mGroupImage).execute(mGroup.getNvImage());

//        Display display = getActivity().getWindowManager().getDefaultDisplay();
//        int screenHeight = display.getHeight();
//        Resources r = getResources();
//        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 232.5f, r.getDisplayMetrics());
//        if (mMembersListView.getHeight() < screenHeight - px) {
//            ViewGroup.LayoutParams params = mGroupMembersLl.getLayoutParams();
//            params.height = (int) (screenHeight - px);
//            mGroupMembersLl.setLayoutParams(params);
//        }

//        Picasso.with(getActivity()).load(mGroup.getNvImage()).skipMemoryCache().into(mGroupImage);
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(false)
//                .resetViewBeforeLoading(true)
//                .showImageForEmptyUri(R.drawable.love)
//                .showImageOnFail(R.drawable.user_photo)
//                .showImageOnLoading(R.drawable.chat_download_image).build();
//
//        MemoryCacheUtils.removeFromCache(mGroup.getNvImage(), ImageLoader.getInstance().getMemoryCache());
//        //DiscCacheUtils.removeFromCache(url, ImageLoader.getInstance().getDiscCache());
//        imageLoader.clearMemoryCache();
//
//        MemoryCacheUtils.removeFromCache(mGroup.getNvImage(), imageLoader.getMemoryCache());
//        imageLoader.displayImage(mGroup.getNvImage(), mGroupImage, options);


        mGroupName.setText(getString(R.string.profileGroupTitle, mGroup.getNvGroupName()));
        CustomViewsInitializer initializer = new CustomViewsInitializer(getActivity());
        initializer.getCodeTable(CodeValue.groupType, new ICallBackCustomViewsData() {//סוג קבוצה
            @Override
            public void doCallBack(ArrayList<CodeValue> objects) {
                String groupType = "";
                for (CodeValue codeValue : objects) {
                    if (codeValue.getiTableId() == mGroup.getiGroupType()) {
                        groupType = codeValue.getNvValue();
                    }
                }
                mGroupDescription.setText(getString(R.string.profileGroupDesc, groupType, mUsers.size(), mUsersRequested.size()));
            }
        });
        mMembersListView.setAdapter(new GroupVerticalMembersAdapter(mUsers, this, false, mGroup.getiMainUserId()));
    }

}
