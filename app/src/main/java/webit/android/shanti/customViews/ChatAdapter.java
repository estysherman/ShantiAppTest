package webit.android.shanti.customViews;

/**
 * Created by user on 15/07/2015.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.GestureDetector;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ActionMenuView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.content.QBContent;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smackx.carbons.packet.CarbonExtension;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.chat.chatManager.ApplicationSingleton;
import webit.android.shanti.chat.chatManager.utils.TimeUtils;
import webit.android.shanti.customViews.slideuppanel.SlidingUpPanelLayout;
import webit.android.shanti.entities.CodeValue;
import webit.android.shanti.entities.User;
import webit.android.shanti.entities.UserQuickBlox;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.FilesHelper;
import webit.android.shanti.main.MainActivity;
import webit.android.shanti.main.messages.ChatFragment;

public class ChatAdapter extends BaseAdapter {

    private static String logTag = "ChatAdapterTag";

    private Context mContext;
    private List<QBChatMessage> mChatMessages;
    private List<User> mUsers;
    private Bitmap emptyImage;
    private Bitmap mBitmap;
    private static AlertDialog.Builder adb = null;
    private GestureDetector gestureDetector;
    int mPosition;
    View mConvertView;
    public View onTouchView;
    private float x1, x2, y1, y2;
    static final int MIN_DISTANCE = 150;
    ImageView actionBarDeleteImg;
    private static List<Integer> selectedList;
    HashMap<Integer, Boolean> selected = new HashMap<>();
    List<Integer> positions = new ArrayList<Integer>();
    int mPositionView;
    View mSelectedView;
    private static List<View> selectedListView;

    //private Bitmap mMyProceedBitmap;
    private HashMap<Integer, Bitmap> littleImages = new HashMap<>();
    //             <little, big>
    private HashMap<String, String> mapFiles = new HashMap<>();
    //         <fileId, path>
    private Map<Integer, String> storedFiles = new HashMap<>();

    private LayoutInflater mInflater;

    public ChatAdapter(Context mContext, List<QBChatMessage> mChatMessages, List<User> mUsers) {
        this.mContext = mContext;
        this.mChatMessages = mChatMessages;//הסטוריית הודעות
        this.mUsers = mUsers;//רשימת משתמשים
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        filterMessages();//סינון הודעות
        loadStoredFiles();//שליפת הודעות שמאוכסנות במכשיר
        emptyImage = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.place_empty_image);
        actionBarDeleteImg = ChatFragment.mDelete;
        selectedList = new ArrayList<Integer>();
        selectedListView = new ArrayList<View>();



        //gestureDetector = new GestureDetector(mContext, new GestureListener());
    }

    //הוספת לקבצים מאוחסנים במכשיר
    public void addStorageFile(Bitmap bitmap, int fileId) {
        File file = FilesHelper.saveImageFile(bitmap, fileId + "", true);//שמירת תמונה
        if (file == null)
//            Toast.makeText(mContext,mContext.getString(R.string.do))
            return;

        storedFiles.put(fileId, file.getPath());
        updateStoredFiles();//עדכון קבצים מאוחסנים
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
        Log.d("file added", String.valueOf(fileId));

        ////////bitmap
    }

    @Override
    public int getCount() {
        return mChatMessages.size();
    }

    @Override
    public QBChatMessage getItem(int i) {
        return mChatMessages.get(i);
    }

    public void filterMessages() {//סינון הודעות
        for (int i = 0; i < mChatMessages.size(); i++) {
            //big Image
            QBChatMessage chatMessage = mChatMessages.get(i);//copy all message to mapFiles and remove them from mChatMessages in order to not be mesaage 2 time and more
            if (!chatMessage.getBody().equals("null") && !chatMessage.getBody().equals("") && chatMessage.getAttachments() != null && chatMessage.getAttachments().size() > 0) {//אם יש תוכן בהודעה
                mapFiles.put(chatMessage.getBody(), chatMessage.getAttachments().iterator().next().getId());
                mChatMessages.remove(chatMessage);//מרוקן את הסטורית הודעות ל mapFiles
            }
        }
    }

    public void add(QBChatMessage message) {//enter this func 3 times:
        if (mChatMessages.size() > 0)
            // remove small image
            if (mChatMessages.get(mChatMessages.size() - 1).getAttachments() != null && mChatMessages.get(mChatMessages.size() - 1).getAttachments().iterator().hasNext() && mChatMessages.get(mChatMessages.size() - 1).getAttachments().iterator().next().getUrl() != null && mChatMessages.get(mChatMessages.size() - 1).getAttachments().iterator().next().getUrl().equals("1")) {
                remove(mChatMessages.get(mChatMessages.size() - 1));
            }
        //מגיע לפה כל פעם שנכנסים לפונקציה (3 פעמים)
        //פעם ראשונה - מוסיף לניראות שלי תמונה קטנה
        //פעם שניה - מוסיף לניראות שלי תמונה גדולה
        //פעם שלישית - מוסיף תמונה גדולה לניראות של השותף לשיחה ולניראות שלי
        mChatMessages.add(message);//1.add small image to me 2. add big image to me 3.  add big image to my friend
        //save image in my files on my phone and delete second big Image
        if (!message.getBody().equals("null") && !message.getBody().equals("") && message.getAttachments() != null && message.getAttachments().size() > 0) {
            mapFiles.put(message.getBody(), message.getAttachments().iterator().next().getId());//מוסיף לmapFiles את ההודעה בגירסה האחרונה (תמונה גדולה לי ולשותפים)
            mChatMessages.remove(message);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    //שליפת הודעות שמאוכסנות במכשיר
    public void loadStoredFiles() {
        try {
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ShantiChatFiles/storedFiles.map";
            FileInputStream fileInputStream = new FileInputStream(file_path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            storedFiles = (HashMap) objectInputStream.readObject();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //עדכון קבצים מאוחסנים
    private void updateStoredFiles() {
        try {
            FileOutputStream fileOutputStream = null;
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ShantiChatFiles/storedFiles.map";
            fileOutputStream = new FileOutputStream(file_path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(storedFiles);
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //מוריד קבצים מ QB
    private void downloadFileFromQuickBlox(final Integer fileId, final ImageView downloadIcon, final ProgressBar progressDownload) {
        //big image still uploaded
        if (mapFiles.get(fileId.toString()) == null)
            return;
        downloadIcon.setVisibility(View.GONE);//הורדת תמונה
        progressDownload.setVisibility(View.VISIBLE);
        QBContent.downloadFileTask(Integer.parseInt(mapFiles.get(fileId.toString())), new QBEntityCallbackImpl<InputStream>() {
            @Override
            public void onSuccess(InputStream inputStream, Bundle params) {
                mBitmap = BitmapFactory.decodeStream(inputStream);//התמונה שירדה מ QB
                progressDownload.setVisibility(View.GONE);
                Log.d("bitmap isn null?", mBitmap == null ? "yes" : "no");
                addStorageFile(mBitmap, fileId);//הוספת לקבצים מאוחסנים במכשיר
                ////////bitmap

                //File file = FilesHelper.saveImageFile(bitmap, fileId + "", true);
                //storedFiles.put(fileId, file.getPath());

            }
        });
    }

    //טיפול בקבצים מצורפים - מוריד אותם ושם בניראות של הצ'אט
    private void careAttachments(final QBChatMessage chatMessage, final ViewHolder holder, int position) {
        //אם גוף ההודעה ריק ויש קובץ מצורף
        if (chatMessage.getBody().equals("null") || chatMessage.getBody().equals("") && chatMessage.getAttachments() != null && chatMessage.getAttachments().size() > 0) {
            final QBAttachment attachment = chatMessage.getAttachments().iterator().next();//שומר את משתנה העזר
            if (chatMessage.getAttachments().iterator().hasNext() && chatMessage.getAttachments().iterator().next().getUrl() != null && chatMessage.getAttachments().iterator().next().getUrl().equals("1")) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBitmap = FilesHelper.decodeFile(attachment.getId());//שליפת קובץ
                        holder.attachImageView.setImageBitmap(mBitmap);//מוסיף את הקובץ המצורף למסך ההודעות
                    }
                });

                holder.attachment.setVisibility(View.VISIBLE);
                holder.attachmentProgressDownload.setVisibility(View.VISIBLE);
                holder.attachmentDownloadIcon.setVisibility(View.GONE);
                holder.txtMessage.setVisibility(View.GONE);
                holder.txtMessageBg.setVisibility(View.GONE);
                return;
            }
//            else if (mChatMessages.size() - 1 == position) {
//                for (int j = 0; j < mChatMessages.size(); j++) {
//                    if (mChatMessages.get(j).getAttachments() != null && mChatMessages.get(j).getAttachments().iterator().hasNext() && mChatMessages.get(j).getAttachments().iterator().next().getUrl() != null && mChatMessages.get(j).getAttachments().iterator().next().getUrl().equals("1")) {
//                        remove(mChatMessages.get(j));
//                    }
//                }
//
//            }
            final Integer fileId = Integer.valueOf(attachment.getId());//get id int pf attachment
            holder.attachment.setVisibility(View.VISIBLE);// set visibility to attachment
            final boolean fileDownloaded = storedFiles.get(fileId) != null;//if exist in file on storage - אם כבר קיים בקבצים שלי שבמכשיר
            holder.attachment.setOnClickListener(new View.OnClickListener() {//בלחיצה על קובץ מצורף
                @Override
                public void onClick(View view) {
                    if (storedFiles.get(fileId) == null) {//אם לא קיים אצלי במכשיר
                        downloadFileFromQuickBlox(fileId, holder.attachmentDownloadIcon, holder.attachmentProgressDownload);////מוריד קבצים מ QB
                    } else {//אם קיים אצלי במכשיר
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri uri = Uri.fromFile(new File(storedFiles.get(fileId)));
                        intent.setDataAndType(uri, "image/*");
                        mContext.startActivity(intent);
                    }
                }
            });
            holder.attachImageView.setImageBitmap(emptyImage);
            holder.txtMessage.setVisibility(View.GONE);
            holder.txtMessageBg.setVisibility(View.GONE);
            if (fileDownloaded) {//אם התמונה כבר הורדה אלי
                final File imgFile = new File(storedFiles.get(fileId));//שולף אותה

                if (imgFile.exists()) {//אם הצליח לשלוף
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBitmap = FilesHelper.decodeFile(imgFile.getAbsolutePath());
                            holder.attachImageView.setImageBitmap(mBitmap);//שם את התמונה (הגדולה)  בניראות שלי
                        }
                    });

                    Log.d("01-09 show big", new Date().toString());
                    holder.attachmentProgressDownload.setVisibility(View.GONE);
                    holder.attachmentDownloadIcon.setVisibility(View.GONE);
                }
            } else {//אם התמונה עוד לא הורדה אלי
                if (littleImages.get(fileId) != null) {//אם יש תמונות ברשימה של תמונות קטנות
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.attachImageView.setImageBitmap(littleImages.get(fileId));//שם את התמונה (הקטנה) בניראות שלי
                        }
                    });
                    Log.d("01-09 show little", new Date().toString());

                    holder.attachmentProgressDownload.setVisibility(View.GONE);
                    holder.attachmentDownloadIcon.setVisibility(View.VISIBLE);
                } else {//אם יש תמונות ברשימה של התמונות הקטנות
                    holder.attachmentProgressDownload.setVisibility(View.VISIBLE);
                    holder.attachmentDownloadIcon.setVisibility(View.GONE);
                    QBContent.downloadFileTask(fileId, new QBEntityCallbackImpl<InputStream>() {//מוריד את התמונות מ QB
                        @Override
                        public void onSuccess(final InputStream inputStream, Bundle params) {//כשמצליח...
                            ((Activity) mContext).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mBitmap = BitmapFactory.decodeStream(inputStream);
                                    holder.attachImageView.setImageBitmap(mBitmap);//שם  את התמונה הקטנה
                                }
                            });
                            littleImages.put(fileId, mBitmap);//מוסיף אותה לרשימה של תמונות קטנות
                            Log.d("01-09 show big", new Date().toString());
                            holder.attachmentProgressDownload.setVisibility(View.GONE);
                            holder.attachmentDownloadIcon.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        } else {//אם אין קובץ מצורף
            holder.attachment.setVisibility(View.GONE);
            holder.txtMessage.setVisibility(View.VISIBLE);
            holder.txtMessageBg.setVisibility(View.VISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        mConvertView = convertView;
        mPosition = position;
        final ViewHolder holder;
        QBChatMessage chatMessage = mChatMessages.get(position);

        QBUser currentUser = ((ApplicationSingleton) mContext.getApplicationContext()).getCurrentUser();//get current user
        final boolean isOutgoing = chatMessage.getSenderId() == null || chatMessage.getSenderId().equals(currentUser.getId());// who sender - אם השולח ריק או שהשולח הוא אני
        User user = isOutgoing ? Common.user : UserQuickBlox.getUserByQbUserId(mUsers, chatMessage.getSenderId());//שם את השולח ב user: אותי או את השולח

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_message_container, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        holder.deleteMessageBtn.setOnClickListener(new View.OnClickListener() {//בלחיצה על כפתור מחיקת הודעה
//            @Override
//            public void onClick(View v) {
//                deleteMessage(mChatMessages.get(position).getId(), position);//מוחק את ההודעה
//                holder.deleteMessageBtn.setVisibility(View.GONE);
//            }
//        });


        actionBarDeleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adb = new AlertDialog.Builder(mContext);
                adb.setTitle(mContext.getString(R.string.chatDeleteMessageAlert));
                adb.setIcon(android.R.drawable.ic_dialog_alert);

                adb.setPositiveButton(mContext.getString(R.string.chatDeleteMessage), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMessage(mChatMessages.get(mPositionView).getId(), mPositionView);//מוחק את ההודעה
                        mSelectedView.findViewById(R.id.contentMyChoose).setVisibility(View.GONE);
                        actionBarDeleteImg.setVisibility(View.GONE);
                    }
                });

                adb.setNegativeButton(mContext.getString(R.string.chatCancelDeleteMessage), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        actionBarDeleteImg.setVisibility(View.GONE);
                        mSelectedView.findViewById(R.id.contentMyChoose).setVisibility(View.GONE);
                    }
                });
                adb.show();
                ChatView.oldSelectedKey = -1;
            }
        });
        Picasso.with(mContext).load(user.getNvImage()).into(holder.imageView);//שם את התמונה של השולח (אני או משתתף אחר בשיחה)
        final float scale = mContext.getResources().getDisplayMetrics().density;
        int atmntMarginEnd = (int) (85 * scale + 0.5f);///
        int WidthHeight = (int) (150 * scale + 0.5f);///
        int TxtMarginEnd = (int) (85 * scale + 0.5f);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams paramsTxt = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams paramsAtmnt = new RelativeLayout.LayoutParams(WidthHeight, WidthHeight);
        RelativeLayout.LayoutParams paramsBg = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        //אתחול הודעה בסיסית
        if (isOutgoing) {// if message from me - אם אני שולח את ההודעה
            holder.container.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);//כיוון הניראות
            //holder.container.setRotation(180);
            holder.txtMessageBg.setImageResource(R.drawable.chat_purple);//תמונת צ'אט סגולה להודעות טקסט
            //txtMassege - ImageBackground
            paramsBg.addRule(RelativeLayout.ALIGN_LEFT, R.id.txtMessageMe);
            paramsBg.addRule(RelativeLayout.ALIGN_RIGHT, R.id.txtMessageMe);
            paramsBg.addRule(RelativeLayout.ALIGN_TOP, R.id.txtMessageMe);
            paramsBg.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.txtMessageMe);
            holder.txtMessageBg.setLayoutParams(paramsBg);
            holder.txtMessage.setPadding(50, 50, 50, 50);
            //holder.txtMessage.setBackground(mContext.getResources().getDrawable(R.drawable.chat_purple));
            holder.attachment.setBackground(mContext.getResources().getDrawable(R.drawable.chat_purple));//תמונת צ'אט סגולה לקבצים מצורפים
            holder.attachment.setGravity(Gravity.RIGHT);
            paramsAtmnt.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);///
            paramsAtmnt.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);///
            paramsAtmnt.addRule(RelativeLayout.LEFT_OF, R.id.linear_image_outer);///משמאל ל outer
            paramsAtmnt.setMarginEnd(atmntMarginEnd);///
            paramsAtmnt.setMarginStart(10);///
            holder.attachment.setLayoutParams(paramsAtmnt);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
            holder.linearImgOuter.setLayoutParams(params);
            paramsTxt.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);///
            paramsTxt.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);///
            paramsTxt.addRule(RelativeLayout.LEFT_OF, R.id.linear_image_outer);///
            paramsTxt.setMarginEnd(TxtMarginEnd);///
            paramsTxt.setMarginStart(10);///
            holder.linearMessage.setLayoutParams(paramsTxt);
            holder.linearImg.setGravity(Gravity.RIGHT);
            holder.linearImgOuter.setGravity(Gravity.RIGHT);
            holder.linearMessage.setGravity(Gravity.RIGHT);///

        } else {//if message to me - אם ההודעה נשלחה אלי
            holder.container.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

            holder.txtMessageBg.setImageResource(R.drawable.chat_white);
            //txtMassege - ImageBackground
            paramsBg.addRule(RelativeLayout.ALIGN_LEFT, R.id.txtMessageMe);
            paramsBg.addRule(RelativeLayout.ALIGN_RIGHT, R.id.txtMessageMe);
            paramsBg.addRule(RelativeLayout.ALIGN_TOP, R.id.txtMessageMe);
            paramsBg.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.txtMessageMe);
            holder.txtMessageBg.setLayoutParams(paramsBg);
            holder.txtMessage.setGravity(Gravity.CENTER_HORIZONTAL);
            holder.txtMessage.setPadding(50, 50, 50, 50);
            //holder.txtMessage.setBackground(mContext.getResources().getDrawable(R.drawable.chat_white));
            holder.attachment.setBackground(mContext.getResources().getDrawable(R.drawable.chat_white));
            holder.attachment.setGravity(Gravity.LEFT);
            paramsAtmnt.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);///
            paramsAtmnt.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);///
            paramsAtmnt.addRule(RelativeLayout.RIGHT_OF, R.id.linear_image_outer);///
            paramsAtmnt.setMarginEnd(atmntMarginEnd);///
            //paramsAtmnt.setMarginStart(10);///
            holder.attachment.setLayoutParams(paramsAtmnt);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
            holder.linearImgOuter.setLayoutParams(params);
            paramsTxt.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            paramsTxt.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
            paramsTxt.addRule(RelativeLayout.RIGHT_OF, R.id.linear_image_outer);///
            paramsTxt.setMarginEnd(TxtMarginEnd);///
            paramsTxt.setMarginStart(10);///
            holder.linearMessage.setLayoutParams(paramsTxt);
            holder.linearImg.setGravity(Gravity.LEFT);
            holder.linearImgOuter.setGravity(Gravity.LEFT);
            holder.linearMessage.setGravity(Gravity.LEFT);

        }
        //set data of sender
//        if (user.getNvImage() != null)
//            if (Common.user.getiUserId() == user.getiUserId())
//                new Utils.DownloadImageTask(holder.imageView).execute(user.getNvImage());
//            else


        holder.txtUserName.setText(user.getNvFirstName());
        if (chatMessage.getAttachments() == null || chatMessage.getAttachments().size() == 0)//אם אין קובץ מצורף
            holder.txtMessage.setText(chatMessage.getBody());
        holder.txtInfo.setText(TimeUtils.getDateFromMilliseconds(chatMessage.getDateSent() * 1000, "dd/MM/yyyy hh:mm:ss"));//שם את זמן שליחת ההודעה

        // if Attachments
        careAttachments(chatMessage, holder, position);//טיפול בקבצים מצורפים - מוריד אותם ושם בניראות של הצ'אט




        return convertView;

    }
    public long getOrginalId(int i) {
        int pos = 0;
        for (QBChatMessage qbChatMessage : mChatMessages) {
            if (pos == i) {
                long l = Long.parseLong(String.valueOf(mChatMessages.indexOf(qbChatMessage)));
                return l;
            }
            pos++;
        }
        return 0;
    }
    public void setSelected(HashMap<Integer, Boolean> selected) {
        this.selected = selected;
    }
    public void setPosition(int position) {
        this.mPositionView = position;
    }
    public void setSelectedView(View v) {
        this.mSelectedView = v;
    }
    private void SlideOutAnimation(View view, final int position) {
        Animation slideOut = AnimationUtils.loadAnimation(mContext, R.anim.slide_out);

        if (slideOut != null) {
            slideOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    Log.i("onAnimationStart", "onAnimationStart");
                    Toast.makeText(mContext, "onAnimationStart", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Log.i("onAnimationEnd", "onAnimationEnd");
                    Toast.makeText(mContext, "onAnimationEnd", Toast.LENGTH_SHORT).show();
                    mChatMessages.remove(position);
                    notifyDataSetChanged();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

            });

            view.setAnimation(slideOut);
            slideOut.start();
//        slideOut.startNow();
        }
    }

    //אתחול משתנים
    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        //holder.meContainer = (LinearLayout) v.findViewById(R.id.contentMe);
        holder.linearImg = (LinearLayout) v.findViewById(R.id.linear_img);
        holder.deleteMessageBtn = (Button) v.findViewById(R.id.deleteMessage);//כפתור מחיקה
        holder.linearImgOuter = (LinearLayout) v.findViewById(R.id.linear_image_outer);
        holder.container = (RelativeLayout) v.findViewById(R.id.contentMe);
        holder.containerDeleteRightToLeft = (RelativeLayout) v.findViewById(R.id.contentMeDeleteRightToLeft);
        holder.containerDeleteLeftToRight = (RelativeLayout) v.findViewById(R.id.contentMeDeleteLeftToRight);
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessageMe);
        //holder.linearMessage = (LinearLayout) v.findViewById(R.id.linear_message);
        holder.linearMessage = (RelativeLayout) v.findViewById(R.id.linear_message);
        holder.txtInfo = (TextView) v.findViewById(R.id.itemMessageTxtInfoMe);
        holder.imageView = (ImageView) v.findViewById(R.id.friendImageMe);
        holder.attachImageView = (ImageView) v.findViewById(R.id.attachmentImageMe);
        holder.attachmentDownloadIcon = (ImageView) v.findViewById(R.id.attachmentImageDownloadMe);
        holder.attachmentProgressDownload = (ProgressBar) v.findViewById(R.id.attachmentProgressDownloadMe);
        holder.attachment = (RelativeLayout) v.findViewById(R.id.attachmentMe);
        holder.txtUserName = (TextView) v.findViewById(R.id.itemMessageFriendNameMe);
        holder.Choose = (RelativeLayout) v.findViewById(R.id.contentMyChoose);
        holder.isSelected = false;
        holder.position = mPosition;
        holder.txtMessageBg = (ImageView) v.findViewById(R.id.txtMessageBg);

        return holder;
    }



    //הגדרת משתנים
    //משתמשים ב holder כדי לחסוך את הזמן של findViewById  כל פעם כשגוללים את הדף מחדש
    private static class ViewHolder {
        public LinearLayout meContainer;
        public RelativeLayout container;
        public RelativeLayout containerDeleteRightToLeft;
        public RelativeLayout containerDeleteLeftToRight;
        public TextView txtUserName;
        public TextView txtMessage;
        //public LinearLayout linearMessage;
        public RelativeLayout linearMessage;
        public TextView txtInfo;
        public LinearLayout content;
        public ImageView imageView;
        public ImageView attachImageView;
        public ImageView attachmentDownloadIcon;
        public ProgressBar attachmentProgressDownload;
        public RelativeLayout attachment;
        public LinearLayout linearImg;
        public LinearLayout linearImgOuter;
        public Button deleteMessageBtn;
        public RelativeLayout Choose;
        public boolean isSelected;
        public int position;
        public ImageView txtMessageBg;
    }

    //מוחק את אוביקט  ההודעה שנשלח אליו מרשימת ההודעות
    public void remove(QBChatMessage object) {
        if (mChatMessages != null) {
            synchronized (new Exception()) {
                mChatMessages.remove(object);
            }
        } else {
            mChatMessages.remove(object);
        }
//        notifyDataSetChanged();
    }

    //מקבל קוד הודעה (מרשימת ההודעות) ומיקום - ומוחק מהניראות את ההודעה
    public void deleteMessage(final String messageId, final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    QBChatService.getInstance().deleteMessage(messageId);//מוחק מהניראות
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mChatMessages.get(position) != null) {
                                mChatMessages.remove(mChatMessages.get(position));//מוחק את אוביקט  ההודעה שנשלח אליו מרשימת ההודעות
                                ChatAdapter.this.notifyDataSetChanged();//שומר את השינויים
                            }
                        }
                    });
                } catch (QBResponseException e) {
                    Log.d("QBContent.deleteFile", e.toString());

                }
            }
        }).start();

    }
}