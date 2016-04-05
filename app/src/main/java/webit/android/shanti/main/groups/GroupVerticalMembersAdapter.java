package webit.android.shanti.main.groups;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestUpdateBuilder;

import org.jivesoftware.smack.util.XmppDateTime;

import java.util.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.chat.chatManager.utils.TimeUtils;
import webit.android.shanti.entities.Group;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;

/**
 * Created by Android2 on 27/05/2015.
 */
public class GroupVerticalMembersAdapter extends BaseAdapter {

    private List<User> mUsers;
    private Fragment fragment;
    private boolean mIsBeRemoved;
    private int mMainUserId;
    public static int deletePosition;
    public static List<Integer> usersToDelete;


    public GroupVerticalMembersAdapter(List<User> Users, Fragment fragment, boolean isBeRemoved, int mMainUserId) {
        mUsers = new ArrayList<>();
        for (int i = 0; i < Users.size(); i++)
            if (Users.get(i).isbIsActive())
                mUsers.add(Users.get(i));
        this.fragment = fragment;
        this.mIsBeRemoved = isBeRemoved;
        this.mMainUserId = mMainUserId;
        usersToDelete = new ArrayList<>();
    }

    public List<User> getmUsers() {
        return mUsers;
    }


    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.list_item_group_profile_member, null);
            holder = new ViewHolder();
            holder.mMemberImage = (ImageView) convertView.findViewById(R.id.itemGroupProfileMemberImage);
            holder.mMemberIsGroupManager = (ImageView) convertView.findViewById(R.id.itemGroupProfileMemberGroupManager);
            holder.mMemberName = (TextView) convertView.findViewById(R.id.itemGroupProfileMemberName);
            holder.mMemberAddress = (TextView) convertView.findViewById(R.id.itemGroupProfileMemberAddress);
            holder.mMemberAttach = (TextView) convertView.findViewById(R.id.itemGroupProfileMemberAttach);
            holder.mIsBeRemoved = (TextView) convertView.findViewById(R.id.itemGroupProfileMemberRemove);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final User member = mUsers.get(position);
        GroupProfileFragment groupProfileFragment = new GroupProfileFragment();

        if (member.getiUserId() == mMainUserId) {
            holder.mMemberIsGroupManager.setVisibility(View.VISIBLE);
            holder.mMemberAttach.setText(fragment.getResources().getString(R.string.profileGroupGroupManager));
            holder.mIsBeRemoved.setVisibility(View.GONE);
        }
        else {
            Date CreateDate = Utils.convertToJsonDate(member.getNvCreateDate());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String date = sdf.format(CreateDate);
            holder.mMemberIsGroupManager.setVisibility(View.GONE);//אם הוא לא המנהל לא שם עליו כדור פורח
            holder.mMemberAttach.setText(fragment.getResources().getString(R.string.profileGroupAttachIn) + " " + date);
            if (fragment.getClass().toString().equals(groupProfileFragment.getClass().toString()))//אם מגיע מדף פרופיל
                holder.mIsBeRemoved.setVisibility(View.GONE);
            else
                holder.mIsBeRemoved.setVisibility(View.VISIBLE);
        }

        if(Common.user.getiUserId() == mMainUserId)
        new Utils.DownloadImageTask(holder.mMemberImage).execute(member.getNvImage());

//        Picasso.with(fragment.getActivity()).load(member.getNvImage()).into(holder.mMemberImage);

        holder.mMemberName.setText(member.getNvFirstName());
holder.mMemberAttach.setText(fragment.getResources().getString(R.string.profileGroupAttachIn) + member.getNvCreateDate());
        holder.mMemberAddress.setText(member.getoCountry().getNvValue());
        if (mIsBeRemoved && member.getiUserId() != Common.user.getiUserId()) {
            holder.mIsBeRemoved.setVisibility(View.VISIBLE);//מראה את הסימן של מחיקת חבר מהקבוצה
            holder.mIsBeRemoved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deletePosition = position;
                    usersToDelete.add(member.getiUserId());
                    mUsers.remove(member);
                    notifyDataSetChanged();
                }
            });
        }


        return convertView;
    }

    private static class ViewHolder {
        ImageView mMemberImage;
        ImageView mMemberIsGroupManager;
        TextView mMemberName;
        TextView mMemberAddress;
        TextView mMemberAttach;
        TextView mIsBeRemoved;
    }


}
