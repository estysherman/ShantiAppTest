/*
package webit.android.shanti.main.notifications;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.users.model.QBUser;
import webit.android.shanti.R;
import webit.android.shanti.chat.chatManager.ApplicationSingleton;

import java.util.List;

*/
/**
 * Created by AndroIT on 11/02/2015.
 *//*

public class NotificationsAdapter extends BaseAdapter {
    private List<QBDialog> dataSource;
    private LayoutInflater inflater;
    private Activity ctx;

    public NotificationsAdapter(List<QBDialog> dataSource, Activity ctx) {
        this.dataSource = dataSource;
        this.inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
    }

    public List<QBDialog> getDataSource() {
        return dataSource;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_room, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.roomName);
            holder.lastMessage = (TextView) convertView.findViewById(R.id.lastMessage);
            holder.groupType = (TextView) convertView.findViewById(R.id.textViewGroupType);
            holder.imageView = ((ImageView) convertView.findViewById(R.id.roomFriendImage));
            holder.imageView.setImageResource(android.R.color.transparent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        QBDialog dialog = dataSource.get(position);
        if (dialog.getType().equals(QBDialogType.GROUP)) {
            holder.name.setText(dialog.getName());
        } else {
            // get opponent name for private dialog
            //
            Integer opponentID = ((ApplicationSingleton) ctx.getApplication()).getOpponentIDForPrivateDialog(dialog);
            QBUser user = ((ApplicationSingleton) ctx.getApplication()).getDialogsUsers().get(opponentID);
            if (user != null) {
                holder.name.setText(user.getLogin() == null ? user.getFullName() : user.getLogin());
            }
        }



        holder.lastMessage.setText(dialog.getLastMessage());
        holder.groupType.setText(dialog.getType().toString());

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView name;
        TextView lastMessage;
        TextView groupType;
    }
}
*/
