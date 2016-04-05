package webit.android.shanti.chat.chatManager.core;

import com.quickblox.chat.QBChat;
import com.quickblox.chat.model.QBChatMessage;

/**
 * Created by AndroIT on 09/02/2015.
 */
public interface GetQBMessageCallback {
    public void getQBMessageCallback(QBChat chat, QBChatMessage message);
}
