package webit.android.shanti.chat.chatManager.core;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListenerImpl;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//מנהל צ'אט קבוצתי
public class GroupChatManagerImpl extends QBMessageListenerImpl<QBGroupChat> implements ChatManager {
    private static final String TAG = "GroupChatManagerImpl";

    private Activity activity;
    GetQBMessageCallback getQBMessageCallback;

    private QBGroupChatManager groupChatManager;
    private QBGroupChat groupChat;

    public GroupChatManagerImpl(GetQBMessageCallback getQBMessageCallback, Activity activity) {
        this.activity = activity;
        this.getQBMessageCallback = getQBMessageCallback;

        //QBGroupChatManager is used to build a group chat.
        //Returns: The QBGroupChatManager instance or null if not logged in.
        groupChatManager = QBChatService.getInstance().getGroupChatManager();
        if (groupChatManager == null) {//אם חוזר ריק
            new Handler().postDelayed(new Runnable() {//מנסה שוב אחרי 3 שניות
                @Override
                public void run() {
                    groupChatManager = QBChatService.getInstance().getGroupChatManager();
                }
            }, 3000);
        }
    }

    //יוצר צ'אט קבוצתי חדש
    public void joinGroupChat(final QBDialog dialog, final QBEntityCallback callback) {
        Log.d("groupChatManager", "groupChatManager" + groupChatManager);
        //getRoomJid() = זה משמש להצטרף לצ'אט קבוצתי בזמן אמת.
        if (groupChatManager == null || groupChatManager.createGroupChat(dialog.getRoomJid()) == null) {
            new Handler().postDelayed(new Runnable() {
                @Override//בודק שוב, אולי עכשיו יצליח
                public void run() {
                    //createGroupChat - join יוצר צ'אט קבוצתי חדש ומחזיר אותו. זה רק מופע מודל. כדי להתחיל שיחה עליך להפעיל את פונקציה
                    if (groupChatManager == null || groupChatManager.createGroupChat(dialog.getRoomJid()) == null) {//אם חוזר ריק
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(activity, "chat manager is not available", Toast.LENGTH_SHORT).show();
                                callback.onError(new ArrayList<String>());
                            }
                        });
                    } else {
                        groupChat = groupChatManager.createGroupChat(dialog.getRoomJid());//מציב את ה צ'אט שיצר ב  groupChat
                        join(groupChat, callback);//מצרף את הקבוצה
                    }
                }
            }, 3000);
        } else {//אם חזר מלא
            groupChat = groupChatManager.createGroupChat(dialog.getRoomJid());
            join(groupChat, callback);//מצרף את הקבוצה
        }
    }

    //מצרף את הקבוצה
    private void join(final QBGroupChat groupChat, final QBEntityCallback callback) {
        DiscussionHistory history = new DiscussionHistory();//ההסטוריה של ההודעות
        history.setMaxStanzas(0);

        //Joins the group chat. - מצרף את הצ'אט הקבוצתי
        groupChat.join(history, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {//כשמצליח

                groupChat.addMessageListener(GroupChatManagerImpl.this);//מוסיף מאזין להודעות נכנסות

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess();

                        // Toast.makeText(activity, "Join successful", Toast.LENGTH_LONG).show();
                    }
                });
                Log.w("Chat", "Join successful");
            }

            @Override
            public void onError(final List list) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(list);
                    }
                });
                Log.w("Could not join chat", Arrays.toString(list.toArray()));
            }
        });
    }

    //לשחרר
    @Override
    public void release() throws XMPPException {
        if (groupChat != null) {//אם מלא
            try {
                groupChat.leave();//עוזב את הצ'אט הקבוצתי
            } catch (SmackException.NotConnectedException nce) {
                nce.printStackTrace();
            }

            groupChat.removeMessageListener(this);//מבטל את המאזין להודעות נכנסות
        }
    }

    @Override//שולח הודעה לצ'אט הקבוצתי
    public void sendMessage(QBChatMessage message) throws XMPPException, SmackException.NotConnectedException {
        if (groupChat != null) {//אם יש צ'אט קבוצתי
            try {
                groupChat.sendMessage(message);//שולח הודעת צ'אט לצ'אט הקבוצתי
            } catch (SmackException.NotConnectedException nce) {
                nce.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();

                Toast.makeText(activity, "You are still joining a group chat, please white a bit", Toast.LENGTH_LONG).show();
            }

        } else {
            //Toast.makeText(activity, "Join unsuccessful", Toast.LENGTH_LONG).show();
        }
    }

    @Override//קבלת הודעות
    public void processMessage(QBGroupChat groupChat, QBChatMessage chatMessage) {
        // Show message
        Log.w(TAG, "new incoming message: " + chatMessage);
        //activity.showMessage(chatMessage);
        if (getQBMessageCallback != null) {
            getQBMessageCallback.getQBMessageCallback(groupChat, chatMessage);
        }
    }

    @Override
    public void processError(QBGroupChat groupChat, QBChatException error, QBChatMessage originMessage) {

    }
}
