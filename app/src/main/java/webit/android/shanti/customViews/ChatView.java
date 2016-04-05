package webit.android.shanti.customViews;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.core.request.QBRequestUpdateBuilder;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.chat.chatManager.ApplicationSingleton;
import webit.android.shanti.chat.chatManager.core.ChatManager;
import webit.android.shanti.chat.chatManager.core.GetQBMessageCallback;
import webit.android.shanti.chat.chatManager.core.GroupChatManagerImpl;
import webit.android.shanti.chat.chatManager.core.PrivateChatManagerImpl;
import webit.android.shanti.chat.chatManager.utils.QBUtils;
import webit.android.shanti.entities.KeyValue;
import webit.android.shanti.entities.User;
import webit.android.shanti.entities.UserQuickBlox;
import webit.android.shanti.general.CallBackFunction;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.FilesHelper;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.general.gcm.Consts;
import webit.android.shanti.general.gcm.MessageToSend;
import webit.android.shanti.general.photo.getimage.CallbackContext;
import webit.android.shanti.general.photo.getimage.CameraLauncher;
import webit.android.shanti.general.photo.getimage.CameraLauncherArguments;
import webit.android.shanti.main.messages.ChatFragment;

/**
 * Created by Android2 on 28/05/2015.
 */
public class ChatView extends RelativeLayout {

    private static String logTag = "ChatView";

    private Context mContext;

    private LinearLayout mChatRetryArea;
    private TextView mChatRetryLoad;
    private ListView mChatHistoryList;
    private ImageView mChatSendClick;
    public static EditText mChatMessageText;
    private ImageView mChatAttachmentBtn;
    private RelativeLayout mChatLoadingPanel;

    private List<QBChatMessage> mChatMessages;
    private List<User> mUsers;
    private QBDialogType mDialogType;
    private ChatAdapter mChatAdapter;
    private String mDialogName;
    private int mGroupId;
    private QBDialog mDialog;
    private ChatManager mChat;
    private CallBackFunction mSuccessCallBack;
    private CallBackFunction mErrorCallBack;
    private long mLittleImgDate = 0;
    ImageView actionBarDeleteIcon;
ImageView actionBarDeleteImg;
    private CameraLauncher cameraLauncher;
    private CameraLauncherArguments arguments;
    private static android.app.AlertDialog.Builder adb = null;
    public HashMap<Integer, Boolean> selected = new HashMap<>();
    public List<Integer> positions;
    public List<View> Views;
    public static int oldSelectedKey = -1;
    CallbackContext callbackContext = new CallbackContext() {//שליחת קובץ מצורף

        @Override
        public void success(Bitmap bitmap) {
            super.success(bitmap);
            sendAttachment(bitmap);
        }
    };


    public CameraLauncher getCameraLauncher() {
        return cameraLauncher;
    }

    public void startChat(List<User> mUsers, QBDialogType mDialogType, String mDialogName, Fragment fragment) {

        this.startChat(mUsers, mDialogType, null, mDialogName, fragment, -1);
    }

    public void startChat(List<User> mUsers, QBDialogType mDialogType, QBDialog mDialog, String mDialogName, Fragment fragment, int groupId) {
        this.mUsers = mUsers;
        this.mDialogType = mDialogType;
        this.mDialog = mDialog;
        this.mDialogName = mDialogName;
        this.mGroupId = groupId;

        cameraLauncher = new CameraLauncher(fragment);
        arguments = new CameraLauncherArguments();
        initChat();//אתחול צ'אט
    }


    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);//איתחול משתנים

    }

    public ChatView(Context context) {
        super(context);
        init(context, null);//איתחול משתנים
    }


    public void init(Context context, AttributeSet attrs) {//איתחול משתנים

        View.inflate(context, R.layout.chat_view, this);
        this.mContext = context;
        this.mChatRetryArea = (LinearLayout) findViewById(R.id.chatRetryArea);
        this.mChatRetryLoad = (TextView) findViewById(R.id.chatRetryLoad);
        this.mChatHistoryList = (ListView) findViewById(R.id.chatHistoryList);
        this.mChatSendClick = (ImageView) findViewById(R.id.chatSendClick);
        this.mChatMessageText = (EditText) findViewById(R.id.chatMessageText);
        this.mChatAttachmentBtn = (ImageView) findViewById(R.id.chatAttachmentBtn);
        this.mChatLoadingPanel = (RelativeLayout) findViewById(R.id.chatLoadingPanel);
        mChatAttachmentBtn.setVisibility(GONE);

        Configuration config = getResources().getConfiguration();//לנראות משמאל לימין
        if(config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
        this.mChatSendClick.setBackgroundResource(R.drawable.send);//שינוי כיוון כפתור שליחת הודעה
        }
                initEvents();
    }

    //play callcack function
    private void initEvents() {
        //הסטורית ההודעות  נטענה
        this.mSuccessCallBack = new CallBackFunction() {//הסטורית ההודעות  נטענה
            @Override
            public void doCallBack(Objects objects) {//when load is finish
                mChatRetryArea.setVisibility(View.GONE);//"try again" is false
                mChatHistoryList.setVisibility(View.VISIBLE);//history chat list
                mChatLoadingPanel.setVisibility(GONE);//load icon false
                mChatAttachmentBtn.setVisibility(VISIBLE);//+icon

            }
        };
//הסטורית ההודעות לא נטענה
        this.mErrorCallBack = new CallBackFunction() {//when load is not success
            @Override
            public void doCallBack(Objects objects) {
                mChatRetryArea.setVisibility(View.VISIBLE);
                mChatAttachmentBtn.setVisibility(GONE);
                mChatHistoryList.setVisibility(View.GONE);
                mChatLoadingPanel.setVisibility(GONE);
            }
        };

        this.mChatSendClick.setOnClickListener(new OnClickListener() {//בלחיצה על שליחת הודעה
            @Override
            public void onClick(View view) {
                sendMessage(null, "");//שליחת הודעה
            }
        });
//הוספת קובץ מצורף
        mChatAttachmentBtn.setOnClickListener(new OnClickListener() {//when on click + icon
            @Override
            public void onClick(View view) {//פותח דיאלוג של בחירת תמונה
                AlertDialog.Builder getImageFrom = new AlertDialog.Builder(mContext, R.style.myDialog);
                getImageFrom.setTitle(mContext.getString(R.string.chooseImage));
                final CharSequence[] opsChars = {mContext.getString(R.string.chooseImageCamera), mContext.getString(R.string.chooseImageGallery), mContext.getString(R.string.chooseImageCancel)};
                getImageFrom.setItems(opsChars, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                arguments.eSourceType = CameraLauncherArguments.ESourceType.CAMERA;
                                cameraLauncher.execute(arguments, callbackContext, mContext);
                                break;
                            case 1:
                                arguments.eSourceType = CameraLauncherArguments.ESourceType.GALLERY;
                                cameraLauncher.execute(arguments, callbackContext, mContext);
                                break;
                        }
                        dialog.dismiss();
                    }
                }).show();
            }

        });

        this.mChatRetryLoad.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {//לחיצה על נסה שנית-לטעון את ההודעות
                mChatRetryArea.setVisibility(View.GONE);
                mChatHistoryList.setVisibility(View.GONE);
                mChatLoadingPanel.setVisibility(VISIBLE);//אייקון טעינה
                initChat();//אתחול צ'אט

            }
        });


    }

    //שליחת הודעה
    private void sendMessage(QBAttachment qbAttachment, String attachmentId) {
        if (getContext() != null) {
            String messageText = mChatMessageText.getText() + "";//get text message
            if (qbAttachment == null && TextUtils.isEmpty(messageText)) {//  error - אם גוף ההודעה ריק ואין קובץ מצורף
                return;
            }
            QBChatMessage chatMessage = new QBChatMessage();//new QBChatMessage()
            chatMessage.setBody(messageText);// set text message
            chatMessage.setProperty(QBUtils.PROPERTY_SAVE_TO_HISTORY, "1");
//            if (qbAttachment != null && qbAttachment.getUrl() != null && qbAttachment.getUrl().equals("0000") && mLittleImgDate > 0)
//                chatMessage.setDateSent(mLittleImgDate);
//            else {
            chatMessage.setDateSent(new Date().getTime() / 1000);// time send
//                mLittleImgDate = 0;
//            }
            //אם יש קובץ מצורף
            if (qbAttachment != null) {// if attachment file
                chatMessage.addAttachment(qbAttachment);// get attachment file
                chatMessage.setBody(attachmentId);//get attachment id
            }
            //todo add message to adapter
            showMessage(chatMessage);// showing message
            Log.d("after  showMyMe!!age", new Date().toString());

            try {

                mChat.sendMessage(chatMessage);
                //אם יש רק טקסט בהודעה ואין קובץ מצורף
                if (!chatMessage.getBody().equals("null") && !chatMessage.getBody().equals("") && (chatMessage.getAttachments() == null || chatMessage.getAttachments().size() > 0))
                    sendNotificationMessage(messageText);//שליחת נוטיפיקציות לכל המשתמשים
            } catch (XMPPException e) {
                Log.e(QBUtils.TAG, "failed to send a message", e);
                //todo show to usr no send
            } catch (SmackException.NotConnectedException e) {
                //todo
                ////
            } catch (SmackException sme) {
                //todo show to usr no send
                Log.e(QBUtils.TAG, "failed to send a message", sme);
            }
            Log.d("enter onSuccess li!!le", new Date().toString());
            if (qbAttachment == null)//אם קובץ מצורף
                mChatMessageText.setText("");//מאפס את תיבת טקסט - כתיבת הודעה בניראות
//        if (mDialogType == QBDialogType.PRIVATE)
//            showMessage(chatMessage);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                scrollDown();//גולל את הסטוריית הודעות, כך שיציג את ההודעה האחרונה

            }
        }).start();
    }

    //שליחת נוטיפיקציות לכל המשתמשים
    private void sendNotificationMessage(String messageText) {//sent message to group or one from group
        List<KeyValue> payload = new ArrayList<>();
        if (mDialogType == QBDialogType.PRIVATE)//אם שיחה פרטית
            payload.add(new KeyValue(Consts.NOTIFICATION_TYPE, String.valueOf(Consts.NOTIFICATION_TYPE_NEW_MESSAGE_PRIVATE)));//מוסיף ל payload נוטיפיקציה - של הודעה פרטית
        else {
            payload.add(new KeyValue(Consts.NOTIFICATION_TYPE, String.valueOf(Consts.NOTIFICATION_TYPE_NEW_MESSAGE_GROUP)));//מוסיף ל payload נוטיפיקציה - של הודעת קבוצה
            payload.add(new KeyValue(Consts.GROUP_ID, String.valueOf(this.mGroupId)));//מוסיף קוד קבוצה
        }
        payload.add(new KeyValue(Consts.USER_ID_SEND, Integer.toString(Common.user.getiUserId())));//קוד שולח
        payload.add(new KeyValue(Consts.MESSAGE, Common.user.getNvShantiName() + ": " + messageText));//שם שולח + גוף הודעה

        List<User> usersId = new ArrayList<>();//רשימת משתמשים שצריך לשלוח להם את ההודעה
        for (User user : mUsers)
            if (user.getiUserId() != Common.user.getiUserId())
                usersId.add(user);
        MessageToSend toSend = new MessageToSend(Common.user.getNvShantiName() + ": " + messageText, payload, User.getUsersId(usersId));//אוביקט הודעה לשליחה
        //שולח את אוביקט  tosend לשרת
        new GeneralTask(mContext, new UseTask() {
            @Override
            public void doAfterTask(String result) {

            }
        }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), new Gson().toJson(toSend), ConnectionUtil.PushUpdates);
    }

    //אתחול צ'אט
    private void initChat() {
        if (mDialogType == QBDialogType.PRIVATE) {//אם שיחה עם משתמש פרטי
            final QBDialog dialogToCreate = new QBDialog();
            dialogToCreate.setName(mDialogName);//שם שיחה
            dialogToCreate.setType(mDialogType);//סוג שיחה
            dialogToCreate.setOccupantsIds(UserQuickBlox.getQbUsersIdsList(mUsers));
            if (QBChatService.getInstance().getGroupChatManager() != null)//אם  יש מנהל לקבוצה
                createDialog(dialogToCreate);//יצירת צא'ט - פרטי
            else {
                // final ProgressDialog progressDialog = ProgressDialog.show(mContext, "", "");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // if (progressDialog != null && progressDialog.isShowing())
                        //    progressDialog.dismiss();
                        if (QBChatService.getInstance().getGroupChatManager() != null)//this return because sometimes the first return null and after 3 second ask again this if ,and not the first
                            createDialog(dialogToCreate);//יצירת צא'ט - פרטי
                        else mErrorCallBack.doCallBack(null);
                    }
                }, 3000);
            }
        } else {//אם השיחה עם קבוצה
            //בתוך GroupChatManagerImpl יהיה callBack ואקטיביטי
            mChat = new GroupChatManagerImpl(new GetQBMessageCallback() {//בונה צ'אט קבוצתי
                @Override
                public void getQBMessageCallback(QBChat chat, QBChatMessage message) {//כאשר פונקציה זו מסתיימת היא מודיעה למי שקרא לה - GroupChatManagerImpl
                    //              showMessage(message);////////////////
                }
            }, (Activity) mContext);
            ((GroupChatManagerImpl) mChat).joinGroupChat(mDialog, new QBEntityCallbackImpl() {//יוצר צ'אט קבוצתי חדש
                @Override
                public void onSuccess() {
                    loadChatHistory();//טוען הסטוריית הודעות
                }

                @Override
                public void onError(List list) {
                    mErrorCallBack.doCallBack(null);
                }
            });
        }
    }

    //גולל את הסטוריית הודעות, כך שיציג את ההודעה האחרונה
    private void scrollDown() {
        mChatHistoryList.post(new Runnable() {//הסטוריית הודעות
            @Override
            public void run() {
                mChatHistoryList.setSelection(mChatHistoryList.getCount() - 1);//גולל את ההודעות ,שיציג את ההודעה האחרונה
                mChatAttachmentBtn.setVisibility(VISIBLE);
            }
        });
    }

//    public void showMyMessage(QBChatMessage message) {
//        if (message.getSenderId() != Common.user.getiUserId()) {
//            mChatAdapter.add(message);
//
//            ((Activity) mContext).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mChatAdapter.notifyDataSetChanged();
//                    scrollDown();
//                }
//            });
//        }
//    }

    //מוסיף את ההודעה לניראות
    public void showMessage(QBChatMessage message) {
        mChatAdapter.add(message);//sent to adapter to add function
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mChatAdapter.notifyDataSetChanged();
                    scrollDown();//גולל את הסטוריית הודעות, כך שיציג את ההודעה האחרונה
                } catch (Exception e) {
                    Log.d("error group chat", e.toString());
                }
            }
        });
    }

//    public void setEnableds(boolean b) {
//        mChatAttachmentBtn.setEnabled(b);
//        mChatSendClick.setEnabled(b);
//    }

    //טוען הסטוריית הודעות
    private void loadChatHistory() {
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setPagesLimit(100);
        customObjectRequestBuilder.sortDesc("date_sent");//מיון לפי תאריך שליפה
        QBChatService.getDialogMessages(mDialog, customObjectRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                mChatMessages = messages;//שולף רשימת הודעות
                Collections.reverse(messages);
                mChatAdapter = new ChatAdapter(mContext, mChatMessages, mUsers);//שלוח לאדפטר את רשימת ההודעות
                mChatHistoryList.setAdapter(mChatAdapter);
                scrollDown();//גולל את הסטוריית הודעות, כך שיציג את ההודעה האחרונה
                mChatMessageText.setEnabled(true);
                mChatAttachmentBtn.setEnabled(true);
                mSuccessCallBack.doCallBack(null);
                for (QBChatMessage qbChatMessage : mChatMessages)
                    selected.put(mChatMessages.indexOf(qbChatMessage), false);
                positions = new ArrayList<Integer>();
                Views = new ArrayList<View>();
                actionBarDeleteIcon = ChatFragment.mDelete;
            }

            @Override
            public void onError(List<String> errors) {
                mErrorCallBack.doCallBack(null);
            }
        });


        mChatHistoryList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                position = (int) mChatAdapter.getOrginalId(position);
                mChatAdapter.setPosition(position);
                mChatAdapter.setSelectedView(view);
                Views.add(view);
                for (View view1 : Views) {
                    if (view1.findViewById(R.id.contentMyChoose) != null)
                        view1.findViewById(R.id.contentMyChoose).setVisibility(GONE);//תיבה ריקה
                }
                if (oldSelectedKey != -1) {//כבר נבחרה הודעה
                    selected.put(oldSelectedKey, false);//הפיכת הערך הקודם שנבחר לfalse
                    if (view.findViewById(R.id.contentMyChoose) != null && view.findViewById(R.id.contentMe) != null) {
                        view.findViewById(R.id.contentMyChoose).setMinimumHeight(view.findViewById(R.id.contentMe).getHeight());
                        view.findViewById(R.id.contentMyChoose).setVisibility(VISIBLE);//סמן V
                    }
                    if (actionBarDeleteIcon != null)//אם יש פח ואין פריטים בחורים
                        actionBarDeleteIcon.setVisibility(VISIBLE);
                }
                if (oldSelectedKey != position) {//אם לא לוחצים על אותו ערך
                    selected.put(position, true);//שם vעל הבחירה החדשה
                    oldSelectedKey = position;//הבחירה החדשה
                    if (view.findViewById(R.id.contentMyChoose) != null && view.findViewById(R.id.contentMe) != null) {
                        view.findViewById(R.id.contentMyChoose).setMinimumHeight(view.findViewById(R.id.contentMe).getHeight());
                        view.findViewById(R.id.contentMyChoose).setVisibility(VISIBLE);//סמן V
                    }
                    if (actionBarDeleteIcon != null)
                        actionBarDeleteIcon.setVisibility(VISIBLE);
                } else {
                    oldSelectedKey = -1;//הערך הוא לא מוגדר
                    if (actionBarDeleteIcon != null)
                        actionBarDeleteIcon.setVisibility(GONE);
                    if (view.findViewById(R.id.contentMyChoose) != null)
                        view.findViewById(R.id.contentMyChoose).setVisibility(GONE);//סמן V
                }
                mChatAdapter.notifyDataSetChanged();//שמירת השינויים בספינר
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                            if (dialog != null && dialog.isShowing())
//                                dialog.dismiss();
                    }
                }, 200);
                mChatAdapter.setSelected(selected);//שליחת הרשימה
//                position = (int) mChatAdapter.getOrginalId(position);
//                selected.put(position, !selected.get(position));//מוסיף לרשימת הבחורים ומשנה את true ל false ולהפך
//                positions.add(position);
//                if (selected.get(position)) {//אם הפריט בחור
//                    if (view.findViewById(R.id.contentMyChoose) != null && view.findViewById(R.id.contentMe) != null)
//                        view.findViewById(R.id.contentMyChoose).setMinimumHeight(view.findViewById(R.id.contentMe).getHeight());
//                        view.findViewById(R.id.contentMyChoose).setVisibility(VISIBLE);//סמן V
//                    if (actionBarDeleteIcon != null)
//                        actionBarDeleteIcon.setVisibility(VISIBLE);
//                }
//                else {
//                    if (view.findViewById(R.id.contentMyChoose) != null)
//                        view.findViewById(R.id.contentMyChoose).setVisibility(GONE);//תיבה ריקה
//                    boolean isSelect = false;
//                    if (selected != null)
//                        for (Map.Entry<Integer, Boolean> entry : selected.entrySet()) {//עובר על הרשימה של הפריטים שנבחרו
//                            if (entry.getValue())//אם נבחר
//                                isSelect = true;
//                        }
//                    if (actionBarDeleteIcon != null && !isSelect)//אם יש פח ואין פריטים בחורים
//                            actionBarDeleteIcon.setVisibility(GONE);
//                }
//                mChatAdapter.setSelected(selected);//שליחת הרשימה בחורים
//                mChatAdapter.setPositin(positions);//רשימת מיקומים
                return true;
            }
        });
    }

    //בונה דיאלוג - פרטי
    private void createDialog(QBDialog dialogToCreate) {

//        QBChatService.getInstance().getGroupChatManager().updateDialog()
        QBChatService.getInstance().getGroupChatManager().createDialog(dialogToCreate, new QBEntityCallbackImpl<QBDialog>() {
            @Override
            public void onSuccess(QBDialog dialog, Bundle args) {
                mDialog = dialog;
                Integer opponentID = ((ApplicationSingleton) mContext.getApplicationContext()).getOpponentIDForPrivateDialog(dialog);
                mChat = new PrivateChatManagerImpl(new GetQBMessageCallback() {//בונה צ'אט פרטי חדש
                    @Override
                    public void getQBMessageCallback(QBChat chat, QBChatMessage message) {
                        showMessage(message);//מוסיף את ההודעה לניראות
                    }
                }, opponentID);
                loadChatHistory();//טוען הסטוריית הודעות
            }

            @Override
            public void onError(List<String> errors) {
                mErrorCallBack.doCallBack(null);
            }
        });
    }

    File mLittleFile;

    //שליחת קובץ מצורף
    private void sendAttachment(final Bitmap bitmap) { //on click on + icon: dwomload image that send to me!!
        mChatSendClick.setEnabled(false);
        mChatAttachmentBtn.setEnabled(false);
        final File littleFile = FilesHelper.saveImageFile(bitmap, "Temporary", false);//קובץ בשם Temporary
        mLittleFile = littleFile;
        final String s = littleFile.getPath();//שומר נתיב של קובץ Temporary
        final Boolean fileIsPublic = true;
        Log.d(ChatView.logTag, "little file start upload");
        QBChatMessage qbChatMessage = new QBChatMessage();
        qbChatMessage.setBody("");
        QBAttachment attachment = new QBAttachment(QBAttachment.PHOTO_TYPE);// new QBAttachment for photo
        attachment.setId(s);//set id
        attachment.setUrl("1");
        qbChatMessage.addAttachment(attachment);
        qbChatMessage.setDateSent(new Date().getTime() / 1000);//מעדכן תאריך שליחה
        Log.d("01-09 send temp", new Date().toString());
        showMessage(qbChatMessage);//send small image to me!!!//מציג אצלי תמונה קטנה
//        new uploadAttachmentTask().execute(bitmap);
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                QBContent.uploadFileTask(littleFile, fileIsPublic, null, new QBEntityCallbackImpl<QBFile>() {//מעלה קבצים
                    @Override
                    public void onSuccess(QBFile file, Bundle params) {
                        mChatSendClick.setEnabled(true);
                        mChatAttachmentBtn.setEnabled(true);
                        final String fileId = file.getId().toString();//set name file from ids file
                        mChatAdapter.addStorageFile(bitmap, Integer.parseInt(fileId));//update adapter - הוספת לקבצים מאוחסנים במכשיר
                        QBAttachment attachment = new QBAttachment(QBAttachment.PHOTO_TYPE);// new QBAttachment for photo
                        attachment.setId(file.getId().toString());//set id
                        Log.d("01-09 send little", new Date().toString());
                        sendMessage(attachment, "");//send message- small image to my friend and big image to me
                        File bigFile = FilesHelper.saveImageFile(bitmap, "Temporary", true);//שומר על אותו שם בו נשמרה התמונה הקטנה את התמונה הגדולה
                        Log.d(ChatView.logTag, "big file start upload");//מתחיל לטעון תמונה גדולה
                        QBContent.uploadFileTask(bigFile, fileIsPublic, null, new QBEntityCallbackImpl<QBFile>() {
                            @Override
                            public void onSuccess(QBFile file, Bundle params) {//אם הצליח לטעון תמונה גדולה
                                Log.d("enter onSuccess big!!", new Date().toString());

                                if (getContext() != null) {
                                    Log.d(ChatView.logTag, "big file end upload");
                                    QBAttachment attachment = new QBAttachment(QBAttachment.PHOTO_TYPE);
                                    attachment.setId(file.getId().toString());
                                    attachment.setUrl("0000");
                                    Log.d("01-09 send big", new Date().toString());
                                    sendMessage(attachment, fileId);//sent big image to my friend
                                }
                            }

                            @Override
                            public void onError(List<String> errors) {
                                Toast.makeText(mContext, mContext.getString(R.string.chatAttachmentError), Toast.LENGTH_SHORT).show();
                            }

                        });

                    }

                    @Override
                    public void onError(List<String> errors) {
                        Toast.makeText(mContext, mContext.getString(R.string.chatAttachmentError), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public void removeUserFormGroups(final QBDialog qbDialog, final int userId) {
        QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();
        requestBuilder.pullAll("occupants_ids", userId); // Remove yourself (user with ID 22)
        QBChatService.getInstance().getGroupChatManager().updateDialog(qbDialog, requestBuilder, new QBEntityCallbackImpl<QBDialog>() {
            @Override
            public void onSuccess(QBDialog dialog, Bundle args) {
                ChatView.this.mDialog = dialog;
                new GeneralTask(mContext, new UseTask() {
                    @Override
                    public void doAfterTask(String result) {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mChatAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{iGroupId:" + qbDialog.getDialogId() + "", "iUserId:" + userId + "}", ConnectionUtil.RemoveUserFromGroup);

            }

            @Override
            public void onError(List<String> errors) {
                if (errors != null && errors.get(0) != null)
                    Utils.sendToLog(mContext, "removeUserFormGroups", errors.get(0));
                Toast.makeText(mContext, mContext.getString(R.string.errorRemoveUserFromGroupQb), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void enable() {
        mChatSendClick.setEnabled(true);
    }

    public void disEnable() {
        mChatSendClick.setEnabled(false);
    }
}
