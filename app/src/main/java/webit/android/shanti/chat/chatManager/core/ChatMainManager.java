package webit.android.shanti.chat.chatManager.core;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import org.jivesoftware.smack.SmackException;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import webit.android.shanti.BuildConfig;
import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.chat.chatManager.ApplicationSingleton;
import webit.android.shanti.chat.chatManager.utils.QBUtils;

/**
 * Created by AndroIT on 08/02/2015.
 */
public class ChatMainManager {

    private static QBChatService chatService = null;
    private static ChatMainManager chatMainManager = null;
    private boolean isSessionCreated = false;

    public static enum Mode {PRIVATE, GROUP}

    Context context;

    private ChatMainManager(Context context) {
        this.context = context;
    }

    //singleton
    public static ChatMainManager getInstance(Context context) {
        if (chatMainManager == null) {
            chatMainManager = new ChatMainManager(context);
        }
        return chatMainManager;
    }

    //הגדרות תצורה ל QB
    public void fastConfigInit() {
        QBChatService.setDebugEnabled(true);//Sets the value that indicates whether debugging is enabled
        QBSettings.getInstance().fastConfigInit(QBUtils.getAppId(), QBUtils.getAuthKey(), QBUtils.getAuthSecret());//הגדרות תצורה ל QB
        if (!QBChatService.isInitialized()) {//אם לא אותחל
            QBChatService.init(context);//Init the QuickBlox settings, Returns: QBSettings instance
        }
        chatService = QBChatService.getInstance();

        //returns: An instance of QBRequestCanceler class which can be used to cancel a request
        QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                isSessionCreated = true;
            }

            @Override
            public void onError(List<String> errors) {
                //Connection closed due to timeout. Please check your internet connection.
            }
        });
    }

    public interface ChatCallBack {
        public void doAfterTask(QBUser user);
    }

    //הרשמות לצ'אט - Create new User
    public void signUp(final ChatCallBack callback, final QBUser qbUser) {
        QBUsers.signUp(qbUser, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle args) {
                signIn(qbUser);//כניסה ל QB
                callback.doAfterTask(qbUser);
            }

            @Override
            public void onError(List<String> errors) {
                // error
                Utils.sendToLog(context, "registerOnServer", "error in quickBlox server");

                Toast.makeText(context, context.getString(R.string.quickBloxSignUpError), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //boolean isSignedIn = false;

    //כניסה ל QB
    public void signIn(final QBUser qbUser) {

        final Handler handler = new Handler();
        final Timer timer = new Timer();
        final Runnable SignIn = new Runnable() {
            @Override
            public void run() {
                QBUsers.signIn(qbUser, new QBEntityCallbackImpl<QBUser>() {//Sign In with login & password
                    @Override
                    public void onSuccess(QBUser user, Bundle bundle) {

                        qbUser.setId(user.getId());
                        ((ApplicationSingleton) context).setCurrentUser(qbUser);
                        loginToChat(qbUser);//התחברות ל QB
                    }

                    @Override
                    public void onError(List<String> errors) {

                    }
                });
            }
        };

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (isSessionCreated) {
                    handler.post(SignIn);
                    timer.cancel();
                }
            }
        };
        timer.schedule(task, 0, 1000);


//        Timer timer1 = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//
//            }
//        };
        //timer1.schedule(timerTask, 0, 2000);
    }
    //בודק אם מחובר
    public static boolean isLogin() {
        return !(chatService == null || !chatService.isLoggedIn());
    }
    //התנתקות מQB
    public boolean logout() {
        if (chatService != null && chatService.isLoggedIn())//אם מחובר
            try {
                chatService.logout();//מתנתק
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                return false;
            }
        return true;
    }

    //התחברות ל QB
    public void loginToChat(final QBUser user) {

        if (!chatService.isLoggedIn())//אם לא מחובר
        //To update an Application session to a User session you have to login to QuickBlox:
            chatService.login(user, new QBEntityCallbackImpl() {
                @Override
                public void onSuccess() {
                    try {
                        chatService.startAutoSendPresence(QBUtils.AUTO_PRESENCE_INTERVAL_IN_SECONDS);//כל 30 שניות יבדוק אם אני מחובר
                    } catch (SmackException.NotLoggedInException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(List errors) {
                    Log.d(QBUtils.TAG, "chat login errors: " + errors);
                }
            });
    }
}

