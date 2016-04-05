package webit.android.shanti.main.SearchUsers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.entities.Distance;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;
import webit.android.shanti.main.MainActivity;
import webit.android.shanti.main.map.DistanceCalculator;
import webit.android.shanti.main.messages.ChatFragment;


/**
 * Created by user on 06/08/2015.
 */
public class SearchUsersAdapter extends BaseAdapter implements Filterable {

    public SearchUsersAdapter(Context context, List<User> users, EditText searchText) {
        this.originalUsers = users;
        this.filteredUsers = users;
        this.mContext = context;
        this.searchText = searchText;
    }

    private ItemFilter mFilter = new ItemFilter();
    private Context mContext;
    private List<User> originalUsers;
    private List<User> filteredUsers;
    EditText searchText;

    private boolean isNameStraight = true;

    @Override
    public int getCount() {
        return filteredUsers.size();
    }

    @Override
    public Object getItem(int i) {
        return filteredUsers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void sortListByShantiName() {//מיון לפי שם שאנטי
        isNameStraight = !isNameStraight;//לחיצה אחת מיון מא-ת  לחיצה שניה מיון מת-א
        if (searchText.getText().toString().equals("")) {
            Collections.sort(originalUsers, new UsersComparator(isNameStraight ? UsersComparator.ESortTypes.NAME_STRAIGHT : UsersComparator.ESortTypes.FAR_REVERSE));
            Log.d("originalUsers", originalUsers.toString());
        } else {
            Collections.sort(filteredUsers, new UsersComparator(isNameStraight ? UsersComparator.ESortTypes.NAME_STRAIGHT : UsersComparator.ESortTypes.FAR_REVERSE));
            Log.d("filteredUsers", filteredUsers.toString());
        }
        notifyDataSetChanged();
    }

    private static class UsersComparator implements Comparator<User> {

        public enum ESortTypes {
            NAME_STRAIGHT,
            NAME_REVERSE,
            FAR_STRAIGHT,
            FAR_REVERSE
        }

        private ESortTypes eSortTypes;

        public UsersComparator(ESortTypes eSortTypes) {
            this.eSortTypes = eSortTypes;
        }

        @Override
        public int compare(User object1, User object2) {

            switch (eSortTypes) {
                case NAME_STRAIGHT:
                    return object1.getNvShantiName().compareTo(object2.getNvShantiName());
                case NAME_REVERSE:
                    return object2.getNvShantiName().compareTo(object1.getNvShantiName());
                case FAR_STRAIGHT:
                    return -1;//איפה משתמשים
                case FAR_REVERSE:
                    return -1;
            }
            return -1;
        }
    }

    public void sortListByFar() {//איפה משתמשים
        Collections.sort(originalUsers, new Comparator<User>() {
            @Override
            public int compare(final User object1, final User object2) {
                return object1.getNvShantiName().compareTo(object2.getNvShantiName());
            }
        });
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_search_user, null);
            holder = new ViewHolder();
            holder.searchUserImage = (SelectableRoundedImageView) convertView.findViewById(R.id.searchUserImage);
            holder.searchUserName = (TextView) convertView.findViewById(R.id.searchUserName);
            holder.searchUserTime = (TextView) convertView.findViewById(R.id.searchUserTime);
            holder.searchUserFar = (TextView) convertView.findViewById(R.id.searchUserFar);
            convertView.setTag(holder);
            //מחשב את גובה כל שורה ברשימה
//            convertView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            SearchUsersFragment.RowHeight = convertView.getMeasuredHeight();
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final User user = filteredUsers.get(position);

        //server until update my last Broadcast data
        if (CalcDateToOnlineUsers(user.getNvLastBroadcastDate()) || user.getiUserId() == Common.user.getiUserId())//מחובר לאפליקציה
            holder.searchUserImage.setBorderColor(mContext.getResources().getColor(R.color.green));
        else
            holder.searchUserImage.setBorderColor(mContext.getResources().getColor(R.color.red));//מנותק מהאפליקציה
        if (Common.user.getiUserId() == user.getiUserId())
            new Utils.DownloadImageTask(holder.searchUserImage).execute(user.getNvImage());//שנוכל לשנות תמונה
        else
            Picasso.with(mContext).load(user.getNvImage()).into(holder.searchUserImage);//ישאיר את התמונה המקורית גם אם נערוך אותה

        holder.searchUserName.setText(user.getNvShantiName() + ", " + user.getoCountry().getNvValue());//הצגת שם שאנטי וארץ
        holder.searchUserTime.setText(user.getNvLastBroadcastDate());//הצגת תאריך נראות אחרונה
//        user.setLastBroadcastDate(Utils.convertToJsonDate(user.getNvLastBroadcastDate()));
        if (user.getoLocation().getdLongitude() != 0)
            new Thread(new Runnable() {//הצגת המרחק
                @Override
                public void run() {
//                    try {
                        //final Distance distance = DistanceCalculator.GetDistance(Common.user.getoLocation().getAsLatLng(), user.getoLocation().getAsLatLng());
                    double x = user.getoLocation().getDistance();
                    x = Math.floor(x * 100) / 100;
                    final String distance = String.valueOf(x);
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //holder.searchUserFar.setText(distance.getText());
                                holder.searchUserFar.setText(distance + " " + mContext.getString(R.string.kilometer));
                                //originalUsers.get(position).setDistance(distance.getValue());
                                //double dd = Common.user.getoLocation().getDistance();
                                originalUsers.get(position).setDistance((int)user.getoLocation().getDistance());
                            }
                        });

//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
            }).start();
        else holder.searchUserFar.setText("");
        //final User finalUser = user;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.getiUserId() != Common.user.getiUserId())
                    if (Utils.isOnLine(mContext)) {
                        String jsonString = user.getJson();
                        try {
                            if (jsonString != null)
                                ((MainActivity) mContext).initFragment(ChatFragment.getInstance(mContext, jsonString));
                            else
                                ((MainActivity) mContext).initFragment(ChatFragment.getInstance(mContext, ""));
                        } catch (Exception e) {
                        }
                    } else
                        Toast.makeText(mContext, ((Activity) mContext).getString(R.string.noConnectionNetwork), Toast.LENGTH_LONG).show();
            }
        });

        return convertView;
    }

    class ViewHolder {
        SelectableRoundedImageView searchUserImage;
        TextView searchUserName;
        TextView searchUserTime;
        TextView searchUserFar;
    }


    private boolean CalcDateToOnlineUsers(String lastBroadcastDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        try {

            Date date = formatter.parse(lastBroadcastDate);
            long lastBroadcastDateMilliSeconds = date.getTime();
            long nowDateMilliseconds = new Date().getTime();
            if (nowDateMilliseconds - lastBroadcastDateMilliSeconds <= 1000)
                return true;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<User> list = originalUsers;
            int count = list.size();
            final ArrayList<User> nlist = new ArrayList<>(count);

            User filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getNvShantiName().toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredUsers = (List<User>) results.values;
            notifyDataSetChanged();
        }
    }
}
