package webit.android.shanti.main.groups;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.core.exception.QBResponseException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.customViews.ChatView;
import webit.android.shanti.entities.Group;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.main.MainBaseFragment;
import webit.android.shanti.main.messages.ChatFragment;

/**
 * Created by AndroIT on 11/02/2015.
 */
public class GroupsAdapter extends BaseAdapter implements Filterable {

    private List<Group> dataSource;
    private List<Group> filteredGroups;
    private Fragment fragment;
    private ItemFilter mFilter = new ItemFilter();

    ImageView actionBarDeleteImg;
    static  int beforePosition = -1;
    public static View view = null;

    public GroupsAdapter(List<Group> dataSource, Fragment fragment) {
        this.dataSource = dataSource;
        this.filteredGroups = dataSource;//הרשימה שנשלחה
        this.fragment = fragment;
        actionBarDeleteImg = GroupsListFragment.mDelete;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return filteredGroups.get(position);
    }

    @Override
    public int getCount() {
        if (filteredGroups != null)
            return filteredGroups.size();
        return 0;
    }

    public void updateGroupImage(int iGroupId) {

    }

    public void notifyDataSetChanged(List<Group> filteredGroups) {
        super.notifyDataSetChanged();
        this.filteredGroups.clear();
        this.filteredGroups.addAll(filteredGroups);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.list_item_group, null);
            holder = new ViewHolder();
            holder.groundImage = (ImageView) convertView.findViewById(R.id.groupBg);
            holder.groupName = (TextView) convertView.findViewById(R.id.groupName);
            holder.groupInfo = ((TextView) convertView.findViewById(R.id.groupInfo));
            holder.groupFriendsNum = (TextView) convertView.findViewById(R.id.groupFriendsNum);
            holder.groupMascBg = (TextView) convertView.findViewById(R.id.listItemGroupBg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
final View convertView1 = convertView;
//        Picasso.with(fragment.getActivity()).load(filteredGroups.get(position).getNvImage()).skipMemoryCache().into(holder.groundImage);
        if(filteredGroups.get(position).getiMainUserId() == Common.user.getiUserId())// תמונת קבוצה
        new Utils.DownloadImageTask(holder.groundImage).execute(filteredGroups.get(position).getNvImage());
        else
        Picasso.with(fragment.getActivity()).load(filteredGroups.get(position).getNvImage()).into(holder.groundImage);

        holder.groupName.setText(filteredGroups.get(position).getNvGroupName());
        //holder.groupAddress.setText(dataSource.get(position).getNvAddress());
        holder.groupFriendsNum.setText(filteredGroups.get(position).getiNumOfMembers() + "");
        holder.groupInfo.setText(fragment.getActivity().getResources().getString(R.string.Location) + " | " + Integer.toString(position) +
                fragment.getActivity().getResources().getString(R.string.coupons)
                + " | " + Integer.toString(position) + " " +
                fragment.getActivity().getResources().getString(R.string.events));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.groupMascBg.setBackgroundResource(R.color.transparent);
if (GroupsAdapter.view != null)
                    GroupsAdapter.view.setVisibility(View.GONE);
                actionBarDeleteImg.setVisibility(View.GONE);
                itemClick(position);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
              //  if (v.getVisibility() == View.GONE) {//אם מוסתר
              //      v.findViewById(R.id.contentMyChoose).setVisibility(View.VISIBLE);//מראה
              //  }
               // else {
               //     v.findViewById(R.id.contentMyChoose).setVisibility(View.GONE);//מראה
               // }
               // return true;

actionBarDeleteImg.setVisibility(View.VISIBLE);
                if (beforePosition != position)//לחיצה על קבוצה שונה
                {//הזזת הרקע האפור
                    if (view != null)//שלא יקרוס בפעם הראשונה
                        view.setVisibility(View.GONE);
                    view = convertView1.findViewById(R.id.contentMyChoose);
                    view.setVisibility(View.VISIBLE);
                    beforePosition = position;//שמירת קבוצה שנבחרה
                } else   //הסרת הרקע האפור בלחיצה על אותה קבוצה
                {
                    if (convertView1.findViewById(R.id.contentMyChoose).getVisibility() == View.VISIBLE) {
                        view = convertView1.findViewById(R.id.contentMyChoose);
                        view.setVisibility(View.GONE);
                        actionBarDeleteImg.setVisibility(View.GONE);
                    } else {
                        view = convertView1.findViewById(R.id.contentMyChoose);
                        view.setVisibility(View.VISIBLE);
                        actionBarDeleteImg.setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        });
   actionBarDeleteImg.setOnClickListener(new View.OnClickListener() {//מחיקת קבוצה
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(fragment.getActivity());
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + filteredGroups.get(beforePosition).getNvGroupName());
                adb.setNegativeButton("Cancel", new android.support.v7.app.AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        actionBarDeleteImg.setVisibility(View.GONE);
                        GroupsAdapter.view.setVisibility(View.GONE);
                    }
                });
                adb.setPositiveButton("Ok", new android.support.v7.app.AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new GeneralTask(fragment.getActivity(), new UseTask() {
                            @Override
                            public void doAfterTask(String result) {//מחיקת קבוצה
                                if (result.equals("false"))
                                    Toast.makeText(fragment.getActivity(), R.string.groupEditRemoveError, Toast.LENGTH_SHORT).show();
                                else {
                                    GroupsListFragment group = new GroupsListFragment();
                                    group.removeGroup(filteredGroups.get(beforePosition).getiGroupId());
                                    actionBarDeleteImg.setVisibility(View.GONE);
                                }
                            }
                        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"iGroupId\":" + filteredGroups.get(beforePosition).getiGroupId() + "}", ConnectionUtil.DeleteGroup);
                    }
                });
                adb.show();
            }
        });
        return convertView;
    }
    

   
    private void itemClick(int position) {

        //ctx.startActivity(new Intent(ctx, GroupDetailsActivity1.class));
        new GeneralTask(fragment.getActivity(), new UseTask() {
            @Override
            public void doAfterTask(String result) {
                ((MainBaseFragment) fragment).initFragment(GroupChatFragment.getInstance(fragment.getActivity(),result));
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"id\":" + filteredGroups.get(position).getiGroupId() + "}", ConnectionUtil.GetGroup);
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private static class ViewHolder {
        ImageView groundImage;
        TextView groupName;
        TextView groupInfo;
        TextView groupFriendsNum;
        TextView groupMascBg;

    }
    private class ItemFilter extends Filter {//סינון הקבוצות לפי תיבת החיפוש
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<Group> list = dataSource;
            int count = list.size();
            final ArrayList<Group> nlist = new ArrayList<>(count);

            Group filterableString;

            for (int i = 0; i < count; i++) {//סינון לפי תיבת החיפוש
                filterableString = list.get(i);
                if (filterableString.getNvGroupName().toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }
            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredGroups = (List<Group>) results.values;
            notifyDataSetChanged();//save changes
        }
    }
}
