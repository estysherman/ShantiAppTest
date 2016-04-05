package webit.android.shanti.main.groups;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.entities.Group;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;

/**
 * Created by CRM on 18/03/2015.
 */
public class ApprovalGroupAdapter extends BaseAdapter {

    //private LayoutInflater inflater;
    private List<Group> dataSource;//רשימת בקשות הצטרפות
    private Fragment fragment;

    public ApprovalGroupAdapter(List<Group> dataSource, Fragment fragment) {
        //this.inflater = LayoutInflater.from(fragment.getActivity());
        this.dataSource = dataSource;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.list_item_approval_group, null);
            holder = new ViewHolder();
            holder.groupImage = (ImageView) convertView.findViewById(R.id.approvalGroupImage);
            holder.groupName = (TextView) convertView.findViewById(R.id.approvalGroupName);
            holder.groupDateCreated = (TextView) convertView.findViewById(R.id.approvalGroupDatedCreate);
            holder.ok = (TextView) convertView.findViewById(R.id.approvalOK);
            holder.cancellation = (TextView) convertView.findViewById(R.id.aprovalCancellation);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Group group = dataSource.get(position);
        holder.groupName.setText(fragment.getString(R.string.approvalGroup) + " " + group.getNvGroupName());
        holder.groupDateCreated.setText(group.getDtCreateDate()+"");
        //try {
        Picasso.with(fragment.getActivity()).load(group.getNvImage()).into(holder.groupImage);
        //holder.groupImage.setImageBitmap(ImageHelper.getBitmapFromURL(group.getNvImage()));
        //holder.groupImage.setImageBitmap(new RoundCornerImageTask(fragment.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, group.getNvImage()).get());

        //} catch (InterruptedException e) {
        //   e.printStackTrace();
        //} catch (ExecutionException e) {
        //   e.printStackTrace();
        //}

        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GeneralTask(fragment.getActivity(), new UseTask() {
                    @Override
                    public void doAfterTask(String result) {
                        dataSource.remove(dataSource.get(position));
                        notifyDataSetChanged();
                        GroupsListFragment.setInstance(null);
                    }
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"iUserId\":" + Common.user.getiUserId() + ",\"iGroupId\":" + dataSource.get(position).getiGroupId() + "}", ConnectionUtil.ApprovalUserGroup);//אישור בקשת הצטרפות
            }
        });

        holder.cancellation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GeneralTask(fragment.getActivity(), new UseTask() {
                    @Override
                    public void doAfterTask(String result) {
                        dataSource.remove(dataSource.get(position));//מחיקת מרישמת בקשות הצטרפות
                        notifyDataSetChanged();
                    }
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"iUserId\":" + Common.user.getiUserId() + ",\"iGroupId\":" + dataSource.get(position).getiGroupId() + "}", ConnectionUtil.RejectUserGroup);
            }
        });

        return convertView;
    }

    public void setData(List<Group> data) {
        this.dataSource = data;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView groupImage;
        TextView groupName;
        TextView groupDateCreated;
        TextView ok;
        TextView cancellation;
    }
}
