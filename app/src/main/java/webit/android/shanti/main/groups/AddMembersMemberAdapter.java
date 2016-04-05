/*
package webit.android.shanti.main.groups;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.entities.User;

*/
/**
 * Created by CRM on 20/03/2015.
 *//*

public class AddMembersMemberAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<User> dataSource;
    private Fragment fragment;
    LinearLayout linearLayoutFriends;
    CallBackFunction callBackFunction;

    public interface CallBackFunction {
        void doCallBack(User user);
    }


    public AddMembersMemberAdapter(List<User> dataSource, Fragment fragment, CallBackFunction callBackFunction) {
        this.inflater = LayoutInflater.from(fragment.getActivity());
        this.dataSource = dataSource;
        this.fragment = fragment;
        this.callBackFunction = callBackFunction;
        this.linearLayoutFriends = linearLayoutFriends;
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
            convertView = inflater.inflate(R.layout.group_add_member_details, null);
            holder = new ViewHolder();
            holder.memberImage = (ImageView) convertView.findViewById(R.id.groupAddMemberImage);
            holder.memberName = (TextView) convertView.findViewById(R.id.groupAddMemberName);
            holder.memberAddress = (TextView) convertView.findViewById(R.id.groupAddMemberAddress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User user = dataSource.get(position);
        holder.memberName.setText(user.getFullName());
        holder.memberAddress.setText(user.getNvShantiName());
        //try {
            //Bitmap processBitmap = new RoundCornerImageTask(fragment.getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user.getNvImage()).get();
        Picasso.with(fragment.getActivity()).load(user.getNvImage()).into(holder.memberImage);
            //holder.memberImage.setImageBitmap(processBitmap);
            //user.setProcessedBitmap(processBitmap);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //} catch (ExecutionException e) {
        //    e.printStackTrace();
        //}

        final View finalConvertView = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                linearLayoutFriends.removeAllViews();
//                linearLayoutFriends.addView(finalConvertView);
                final LinearLayout linearLayoutFriends = (LinearLayout) fragment.getActivity().findViewById(R.id.addGroupAddFriends);
                //linearLayoutFriends.addView(finalConvertView);
                callBackFunction.doCallBack(dataSource.get(position));
                dataSource.clear();
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView memberImage;
        TextView memberName;
        TextView memberAddress;
    }


}

*/
