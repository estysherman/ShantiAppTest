package webit.android.shanti.main.groups;


import android.graphics.Shader;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;

/**
 * Created by AndroIT202 on 12/02/2015.
 */
public class MembersAdapter extends BaseAdapter {

    //private LayoutInflater inflater;
    private List<User> dataSource;
    private Fragment fragment;

    public MembersAdapter(List<User> dataSource, Fragment fragment) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.list_item_member, null);
            holder = new ViewHolder();
            holder.memberImage = (ImageView) convertView.findViewById(R.id.memberImage);
            holder.groupManagerIcon = (ImageView) convertView.findViewById(R.id.groupManagerIcon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == 0)//הצגת ריבועי התמונות של חבקרי הקבוצה
            holder.groupManagerIcon.setVisibility(View.VISIBLE);
        else
            holder.groupManagerIcon.setVisibility(View.GONE);
        //holder.memberImage.setImageBitmap(dataSource.get(position).getProcessedBitmap());
        if(dataSource.get(position).getiUserId()== Common.user.getiUserId())
        new Utils.DownloadImageTask(holder.memberImage).execute(dataSource.get(position).getNvImage());
else
        Picasso.with(fragment.getActivity()).load(dataSource.get(position).getNvImage()).into(holder.memberImage);

        return convertView;
    }

    private static class ViewHolder {
        ImageView memberImage;
        ImageView groupManagerIcon;
    }


}
