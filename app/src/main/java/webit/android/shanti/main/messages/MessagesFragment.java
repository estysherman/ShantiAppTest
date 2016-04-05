package webit.android.shanti.main.messages;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.chat.chatManager.utils.TimeUtils;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.SPManager;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.main.MainActivity;
import webit.android.shanti.main.MainBaseFragment;
import webit.android.shanti.main.groups.GroupsListFragment;
import webit.android.shanti.main.map.MapFragment;

//import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends MainBaseFragment {

    public static MessagesFragment instance = null;


    public static MessagesFragment getInstance() {
        if (instance == null) {
            instance = new MessagesFragment();
        }
        return instance;

    }

    private View mRootView;
    private ListView dialogsListView;
    private LinearLayout mChatRetryArea;
    private RelativeLayout emptyView;
    private TextView mChatRetryLoad;

    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void setActionBarEvents(View view) {
        super.setActionBarEvents(view);

        view.findViewById(R.id.messagesActionMapBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFragment(MapFragment.getInstance());
            }
        });

        setBackView(view.findViewById(R.id.messagesActionBackBtn));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_messages, container, false);
            setActionBarEvents(mRootView);
            this.dialogsListView = (ListView) mRootView.findViewById(R.id.notificationsList);
            this.mChatRetryArea = (LinearLayout) mRootView.findViewById(R.id.chatRetryArea);
            this.emptyView = (RelativeLayout) mRootView.findViewById((R.id.messagesEmptyList));
            this.mChatRetryLoad = (TextView) mRootView.findViewById(R.id.chatRetryLoad);
            this.mChatRetryLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mChatRetryArea.setVisibility(View.GONE);
                    dialogsListView.setVisibility(View.GONE);
                    getAllDialogs();

                }
            });
            getAllDialogs();
        }
        SPManager.getInstance(getActivity()).saveInteger(SPManager.NOTIFICATION_COUNTER, 0);
        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MapFragment.getInstance().updateNotificationsCounter();
            }
        });
        return mRootView;
    }

    public void getAllDialogs() {

        final ProgressDialog dialog;
        dialog = ProgressDialog.show(getActivity(), "", "");
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);

        QBChatService.getChatDialogs(QBDialogType.PRIVATE, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBDialog> dialogs, Bundle args) {
                mChatRetryArea.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                dialogsListView.setVisibility(View.VISIBLE);

                final List<Integer> ListQBIds = new ArrayList<Integer>();
                for (QBDialog dialog : dialogs) {
                    Integer id = dialog.getOccupants().get(0);
                    if (id == Common.user.getoUserQuickBlox().getID())
                        ListQBIds.add(dialog.getOccupants().get(1));
                    else ListQBIds.add(dialog.getOccupants().get(0));
                }

                new GeneralTask(getActivity(), new UseTask() {
                    @Override
                    public void doAfterTask(String result) {
                        Type programsListType = new TypeToken<List<User>>() {
                        }.getType();
                        List<User> users = new Gson().fromJson(result, programsListType);
                        buildListView(dialogs, users);
                        if (dialog != null && dialog.isShowing())
                            dialog.dismiss();

                    }
                }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), new Gson().toJson(new ListQBIdsToSend(ListQBIds)).toString(), ConnectionUtil.GetUsersByQBIdList);
            }

            @Override
            public void onError(List<String> errors) {
                //AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                //dialog.setMessage("get dialogs errors: " + errors).create().show();
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                mChatRetryArea.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                dialogsListView.setVisibility(View.GONE);
            }
        });
    }

    void buildListView(final List<QBDialog> dialogs, final List<User> users) {
        //final NotificationsAdapter adapter = new NotificationsAdapter(dialogs, getActivity());
        dialogsListView.setEmptyView(emptyView);
        dialogsListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return dialogs.size();
            }

            @Override
            public Object getItem(int position) {
                return dialogs.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_room, null);
                    holder = new ViewHolder();
                    holder.name = (TextView) convertView.findViewById(R.id.messagesUserName);
                    holder.lastMessage = (TextView) convertView.findViewById(R.id.messagesLastMessage);
                    holder.time = (TextView) convertView.findViewById(R.id.messagesTime);
                    holder.imageView = ((ImageView) convertView.findViewById(R.id.messagesUserImage));
                    //holder.imageView.setImageResource(android.R.color.transparent);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                QBDialog dialog = dialogs.get(position);
                User user = null;
                for (User item : users) {
                    if (dialog.getOccupants().contains(item.getoUserQuickBlox().getID())) {
                        user = item;
                        break;
                    }
                }
                if (user == null) {
                    //convertView.setVisibility(View.GONE);
                    dialogs.remove(position);
                    notifyDataSetChanged();

                } else {
                    if (user.getiUserId() == Common.user.getiUserId())
                        new Utils.DownloadImageTask(holder.imageView).execute(user.getNvImage());
                    else
                        Picasso.with(getActivity()).load(user.getNvImage()).into(holder.imageView);

                    holder.lastMessage.setText(dialog.getLastMessage());
                    holder.time.setText(TimeUtils.getDateFromMilliseconds(dialog.getLastMessageDateSent() * 1000, "dd/MM/yyyy HH:mm"));
                    holder.name.setText(user.getNvShantiName());

                    final User finalUser = user;
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            initFragment(ChatFragment.getInstance(getActivity(), finalUser.getJson()));
                            /*Intent intent = new Intent();
                            intent.putExtra(ChatFragment.ARG_USER, finalUser.getJson());
                            intent.setClass(getActivity(), ChatFragment.class);
                            getActivity().startActivity(intent);*/
                        }
                    });
                }

                return convertView;
            }

            class ViewHolder {
                ImageView imageView;
                TextView name;
                TextView lastMessage;
                TextView time;
            }
        });
    }

    class ListQBIdsToSend {
        List<Integer> ListQBIds;

        ListQBIdsToSend(List<Integer> listQBIds) {
            ListQBIds = listQBIds;
        }
    }

}
