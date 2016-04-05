package webit.android.shanti.chat.chatManager.core;

import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListenerImpl;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatMessage;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

//מנהל צ'אט פרטי
public class PrivateChatManagerImpl extends QBMessageListenerImpl<QBPrivateChat> implements ChatManager, QBPrivateChatManagerListener {

    private static final String TAG = "PrivateChatManagerImpl";

    private GetQBMessageCallback mGetQBMessageCallback;
    private QBPrivateChatManager mPrivateChatManager;
    private QBPrivateChat mPrivateChat;

    public PrivateChatManagerImpl(GetQBMessageCallback getQBMessageCallback, Integer opponentID) {
        this.mGetQBMessageCallback = getQBMessageCallback;
        mPrivateChatManager = QBChatService.getInstance().getPrivateChatManager();//בונה צ'אט פרטי
        mPrivateChatManager.addPrivateChatManagerListener(this);//מאזין להודעות בצ'אט פרטי

        // init private chat
        //שולח בקשה עם קוד משתמש משתתף
        //ומחזיר את הצ'אט שיש איתו, אם אין מחזיר NULL
        mPrivateChat = mPrivateChatManager.getChat(opponentID);
        if (mPrivateChat == null) {//אם אין צ'אט אם משתמש זה
            mPrivateChat = mPrivateChatManager.createChat(opponentID, this);//יוצר צ'אט חדש
        } else {
            mPrivateChat.addMessageListener(this);//מאזין להודעות נכנסות
        }
    }

    @Override//שולח הודעה למשתתף השני
    public void sendMessage(QBChatMessage message) throws XMPPException, SmackException.NotConnectedException {
        mPrivateChat.sendMessage(message);
    }

    @Override//לשחרר צ'אט פרטי
    public void release() {
        Log.w(TAG, "release private chat");
        mPrivateChat.removeMessageListener(this);//מוחק מאזין להודעות נכנסות
        mPrivateChatManager.removePrivateChatManagerListener(this);//מסיר מאזין, לא יהיו עוד התראות הודעות נכנסות
    }

    @Override//קבלת הודעות
    public void processMessage(QBPrivateChat chat, QBChatMessage message) {
        Log.w(TAG, "new incoming message: " + message);
        if (mGetQBMessageCallback != null) {
            mGetQBMessageCallback.getQBMessageCallback(chat, message);
        }
    }

    @Override
    public void processError(QBPrivateChat chat, QBChatException error, QBChatMessage originChatMessage) {

    }

    @Override//כשנוצר צ'אט פרטי
    public void chatCreated(QBPrivateChat incomingPrivateChat, boolean createdLocally) {
        if (!createdLocally) {
            mPrivateChat = incomingPrivateChat;
            mPrivateChat.addMessageListener(PrivateChatManagerImpl.this);//מאזין להודעות נכנסות
        }

        Log.w(TAG, "private chat created: " + incomingPrivateChat.getParticipant() + ", createdLocally:" + createdLocally);
    }
}
