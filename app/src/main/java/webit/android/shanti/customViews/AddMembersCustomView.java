package webit.android.shanti.customViews;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;

/**
 * Created by Android2 on 28/05/2015.
 */
public class AddMembersCustomView extends RelativeLayout {
    public void setChangeMode(clickCallBack clickCallBack) {
        mClickCallBack = clickCallBack;
    }

    public interface clickCallBack {
        public void doCallBack(boolean isFocus);
    }

    private enum MembersListType {
        ALL_SHANTI_USERS,
        ADDED_USERS
    }

    Context context;
    clickCallBack mClickCallBack = null;
    ScrollView mPageScrollView = null;
    View mGoneView = null;
    private boolean mIsFocus = false;
    ExpandableHeightListView mAllMembersList;
    ExpandableHeightListView mAddedMembersList;
    TextView mSearchButton;
    EditText mSearchText;
    TextView mMembersCount;
    LinearLayout mSearchLinear;
    MembersAdapter mMembersAdapter;

    List<User> mUsersGroupListItems;
    List<User> mUsersGroup;
    List<User> mUsersGroupRequested;
    List<Integer> mUsersExistInGroup = new ArrayList<>();
    List<Integer> mUsersNotExistInGroup = new ArrayList<>();
    int mGroupId;


    public AddMembersCustomView(Context context, AttributeSet attrs) {////נכנס  לפה3 פעמים בלחיצה על קבוצה
        super(context, attrs);
        init(context, attrs);
    }

    public AddMembersCustomView(Context context) {
        super(context);
        init(context, null);
    }

    public void setmPageScrollView(ScrollView mPageScrollView) {
        this.mPageScrollView = mPageScrollView;
    }

    public void setmUsersExistInGroup(List<Integer> mUsersExistInGroup) {
        this.mUsersExistInGroup = mUsersExistInGroup;
    }

    public void setGroupId(int groupId) {
        this.mGroupId = groupId;
        if (mMembersAdapter != null)
            mMembersAdapter.setGroupId(groupId);
    }
    //what the meaning of setmGoneView?
    public void setmGoneView(View mGoneView) {
        this.mGoneView = mGoneView;
    }

    public List<User> getmUsersGroup() {
        return mUsersGroup;
    }

    public void init(Context context, AttributeSet attrs) {

        View.inflate(context, R.layout.add_members_custom_view, this);
        mUsersGroupListItems = new ArrayList<>();
        mUsersGroup = new ArrayList<>();
        this.context = context;
        this.mAllMembersList = (ExpandableHeightListView) findViewById(R.id.addMembersAllMembersList);
        this.mAddedMembersList = (ExpandableHeightListView) findViewById(R.id.addMembersAddedMembersList);
        mMembersAdapter = new MembersAdapter(mUsersGroupListItems, MembersListType.ADDED_USERS, mGroupId);
        this.mAddedMembersList.setAdapter(mMembersAdapter);//Add Members found (Adapter) to addMembersAddedMembersList
        this.mSearchButton = (TextView) findViewById(R.id.addMembersSearch);
        this.mSearchText = (EditText) findViewById(R.id.addMembersSearchText);
        this.mMembersCount = (TextView) findViewById(R.id.addMembersMembersCount);
        this.mSearchLinear = (LinearLayout) findViewById(R.id.addMembersSearchTextContainer);
        this.mSearchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });

        findViewById(R.id.addMembersAdd).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {//פותח תיבת חיפוש עם כפתור חיפוש או סוגר
                if (mClickCallBack != null) {
                    mClickCallBack.doCallBack(mIsFocus);
                }
                if (mSearchLinear.getVisibility() == View.GONE) {
                    mSearchLinear.setVisibility(VISIBLE);
                    mIsFocus = true;
                    mAddedMembersList.setVisibility(View.VISIBLE);
                    mAllMembersList.setVisibility(VISIBLE);
                    if (mGoneView != null)
                        mGoneView.setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.addMembersAdd)).setTextColor(getResources().getColor(R.color.purple_home));
                } else {
                    mIsFocus = false;
                    mAddedMembersList.setVisibility(View.GONE);
                    mSearchLinear.setVisibility(View.GONE);
                    mAllMembersList.setVisibility(GONE);
                }
//                ((TextView) findViewById(R.id.addMembersAddIcon)).setTextColor(getResources().getColor(R.color.purple_home));
            }
        });
        if (mGoneView != null)
            mGoneView.setVisibility(VISIBLE);
    }

    public void callOnclickToAllBTn() {
        findViewById(R.id.addMembersAdd).callOnClick();
    }

    public void search() {//חיפוש חברים להוספה

        if (mSearchText.getText().toString().equals(""))
            return;
        mSearchButton.setClickable(true);
        new GeneralTask(context, new UseTask() {
            @Override
            public void doAfterTask(String result) {
                Type programsListType = new TypeToken<List<User>>() {
                }.getType();
                final List<User> users = new Gson().fromJson(result, programsListType);
                for (int i = 0; i < users.size(); i++) {
                    if (mUsersExistInGroup.contains(users.get(i).getiUserId()) || (Common.user.getiUserId() == users.get(i).getiUserId())
                            || mUsersGroupListItems.contains(users.get(i).getiUserId()) ) {//(users.get(i).isbIsActive())
                        users.remove(i);
                    }
                }
                mMembersAdapter = new MembersAdapter(users, MembersListType.ALL_SHANTI_USERS, mGroupId);
                mAllMembersList.setAdapter(mMembersAdapter);
                mSearchButton.setClickable(true);
                Utils.HideKeyboard(context);
            }
        }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"SearchText\":\"" + mSearchText.getText() + "\"}", ConnectionUtil.GetUsersBySearchText);
    }


    private class MembersAdapter extends BaseAdapter {//שייך לתוצאות החיפוש של הוספת חברים לקבוצה


        private List<User> mUsers;
        private int mGroupId;
        private MembersListType mMembersListType;
        public MembersAdapter(List<User> mUsers, MembersListType membersListType, int groupId) {
            this.mUsers = mUsers;
            this.mGroupId = groupId;
            this.mMembersListType = membersListType;
        }

        public void setGroupId(int groupId) {
            this.mGroupId = groupId;
        }

        @Override
        public int getCount() {
            return mUsers.size();
        }

        @Override
        public Object getItem(int i) {
            return mUsers.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override//why this function returns Again and again?
        public View getView(int position, View convertView, ViewGroup viewGroup) {//initialization every item in AddMemberList
            final ViewHolder holder;
            if (convertView == null) {
                //convertView = LayoutInflater.from(context).inflate(R.layout.group_add_member_details, null);
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.group_add_member_details1, viewGroup, false);
                holder = new ViewHolder();
                holder.memberImage = (ImageView) convertView.findViewById(R.id.addMembersUserImage);
                holder.memberName = (TextView) convertView.findViewById(R.id.addMembersUserName);
                holder.memberShantiName = (TextView) convertView.findViewById(R.id.addMembersShantiName);
                holder.memberAddress = (TextView) convertView.findViewById(R.id.addMembersUserCountry);
                holder.sendAsk = (TextView) convertView.findViewById(R.id.addMembersSendAsk);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final User user = mUsers.get(position);
            holder.memberName.setText(user.getNvShantiName());
            holder.memberAddress.setText(user.getoCountry().getNvValue());
            Picasso.with(getContext()).load(user.getNvImage()).into(holder.memberImage);
            //if member is added - INVISIBLE 'send ask' button
            if (mMembersListType == MembersListType.ADDED_USERS)
                holder.sendAsk.setVisibility(VISIBLE);
            else
                holder.sendAsk.setVisibility(INVISIBLE);
            holder.memberAddress.setVisibility(GONE);
            holder.sendAsk.setTag(user);
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user.isbIsActive())
                        mUsersExistInGroup.add(user.getiUserId());
                    else
                        mUsersNotExistInGroup.add(user.getiUserId());
                    mUsersGroupListItems.add(user);//ADDED_USERS
                    mUsers.clear();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                            ((BaseAdapter) mAddedMembersList.getAdapter()).notifyDataSetChanged();
                        }
                    });
                    //scroll to bottom of page
                    if (mPageScrollView != null)
                        mPageScrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                mPageScrollView.fullScroll(View.FOCUS_DOWN);
                            }
                        });
                    mSearchText.setText("");

                }
            });
            holder.sendAsk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {//שליחת בקשה
//                    if (user.isbIsActive())
//                        mUsersExistInGroup.add(user.getiUserId());
//                    else
//                        mUsersNotExistInGroup.add(user.getiUserId());
                    new GeneralTask(context, new UseTask() {
                        @Override
                        public void doAfterTask(String result) {
                            Type programsListType = new TypeToken<Boolean>() {
                            }.getType();
                            final Boolean isSucsses = new Gson().fromJson(result, programsListType);
                            if (isSucsses) {
                                mUsersGroup.add(user);//מוסיף את המשתמש לחברי הקבוצה
//                                mUsersGroupListItems.add(user);//ADDED_USERS
//                                mUsers.clear();
//                                ((Activity) context).runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        notifyDataSetChanged();
//                                        ((BaseAdapter) mAddedMembersList.getAdapter()).notifyDataSetChanged();
//                                    }
//                                });
//                                //scroll to bottom of page
//                                if (mPageScrollView != null)
//                                    mPageScrollView.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            mPageScrollView.fullScroll(View.FOCUS_DOWN);
//                                        }
//                                    });
//                                mSearchText.setText("");
//
//                                mUsersGroup.add(user);//מוסיף את המשתמש לחברי הקבוצה

                                mMembersCount.setText(context.getString(R.string.addMembersAttachNum, mUsersGroup.size())+"");
                                holder.sendAsk.setText("");
                                holder.sendAsk.setBackgroundResource(R.color.transparent);
                            } else {
                                Toast.makeText(AddMembersCustomView.this.context, AddMembersCustomView.this.context.getString(R.string.addMembersTheUserDoesntReceivedARequestToJoin), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"iUserId\":" + user.getiUserId() + ",\"iGroupId\":" + mGroupId + ",\"bIsMainUser\":" + false + "}", ConnectionUtil.AddUserToGroup);


                }
            });
//

            //not working because ExpandableHeightListView override method
            if (mMembersListType == MembersListType.ADDED_USERS && mUsersGroupListItems.size() > 3 || mMembersListType == MembersListType.ALL_SHANTI_USERS && mUsers.size() > 7) {
                final View finalConvertView = convertView;
                //((Activity) context).runOnUiThread(new Runnable() {
                //    @Override
                //    public void run() {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mAddedMembersList.getLayoutParams();
                lp.height = finalConvertView.getHeight() * (mMembersListType == MembersListType.ADDED_USERS ? 3 : 7) + 10;
                Log.d("lp.height", String.valueOf(lp.height));
                mAddedMembersList.setLayoutParams(lp);
                //   }
                //});
            }
            //scroll page to bottom after member added, cause keyboard not write
            /*if (mPageScrollView != null)
                mPageScrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        mPageScrollView.fullScroll(View.FOCUS_DOWN);
                    }
                })*/
            ;
            return convertView;
        }
    }

    private static class ViewHolder {
        ImageView memberImage;
        TextView memberName;
        TextView memberShantiName;
        TextView memberAddress;
        TextView sendAsk;
    }


}
