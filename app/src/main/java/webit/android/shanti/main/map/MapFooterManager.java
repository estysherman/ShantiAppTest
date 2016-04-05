package webit.android.shanti.main.map;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.w3c.dom.Document;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;

import webit.android.shanti.R;
import webit.android.shanti.Utils.Utils;
import webit.android.shanti.customViews.CustomEditText;
import webit.android.shanti.entities.BaseMarker;
import webit.android.shanti.entities.Group;
import webit.android.shanti.entities.KeyValue;
import webit.android.shanti.entities.MeetingPoint;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.CallBackFunction;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.SPManager;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.general.gcm.Consts;
import webit.android.shanti.general.gcm.MessageToSend;
import webit.android.shanti.main.MainActivity;
import webit.android.shanti.main.groups.GroupChatFragment;
import webit.android.shanti.main.groups.GroupsListFragment;
import webit.android.shanti.main.groups.NewGroupFragment;
import webit.android.shanti.main.messages.ChatFragment;
import webit.android.shanti.main.messages.MessagesFragment;

/**
 * Created by 1 on 04/05/15.
 */
public class MapFooterManager extends LinearLayout {

    public enum MapFooterPosition {
        MAP_FOOTER_USER(0),
        MAP_FOOTER_ME(1),
        MAP_FOOTER_CREATE_MEETING_POINT(2),
        MAP_FOOTER_MEETING_POINT_DETAILS(3);

        private int value;

        MapFooterPosition(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private MapFooterPosition mPosition;
    private ViewFlipper mFooterFlipper;
    private Activity mActivity;
    private Polyline mPolyline;
    private LinearLayout mUpLine, mGroupFooter, mChooseGroup;
    private TextView mUserAddress, mUserName, mUserInfo, mNotificationCounter, mGroupName, mMeetingTime;
    private ImageView mOpenChatBtn, mMeetingPointBtn, mNavigateBtn;
    private Button mMeetingCreateBtn;
    private EditText mMeetingSetting, mMeetingComments;
    private int mGroupNameIndex = 0;
    private Marker mMeetingPointMarker = null;
    private User mUser;
    private boolean isCreateMeetingPoint = false;
    private MeetingPoint mMeetingPoint;
    private int mGroupIdToCreateMeetingPoint = -1;
    private LayoutInflater layoutInflater;


    public boolean isCreateMeetingPoint() {
        return isCreateMeetingPoint;
    }

    public void setIsCreateMeetingPoint(boolean isCreateMeetingPoint) {
        this.isCreateMeetingPoint = isCreateMeetingPoint;
    }

    public void setUserInfoText(String info) {
        if (mUser.getiUserId() == Common.user.getiUserId())
            mUserInfo.setText(info);
    }

    public void setmUser(User mUser) {
        this.mUser = mUser;
    }

    public User getmUser() {
        return mUser;
    }

    public MeetingPoint getmMeetingPoint() {
        return mMeetingPoint;
    }

    public void setmMeetingPoint(MeetingPoint mMeetingPoint) {
        this.mMeetingPoint = mMeetingPoint;
    }

    public MapFooterManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MapFooterManager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init(AttributeSet attrs) {

        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.map_footer, this);
        mActivity = MapFragment.getInstance().getActivity();

        mFooterFlipper = (ViewFlipper) findViewById(R.id.map_footer_view_flipper);
        mGroupFooter = (LinearLayout) findViewById(R.id.map_footer_groups_footer);
        setPurpleBottomFooter();//ניהול לחיצה על אח הכפתוריםצ בשורה הסגולה - למטה
        //changeFooter(mPosition, Common.mUser);
    }

    private void setPurpleBottomFooter() {
        //בעת לחיצה על שליחת הודעה לקבוצה
        findViewById(R.id.map_footer_purple_public_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPublicMessageClick();

            }
        });
        //בעת לחיצה על '+' ליצירת קבוצה חדשה
        findViewById(R.id.map_footer_purple_add_group).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) MapFragment.getInstance().getActivity()).initFragment(NewGroupFragment.getInstance(), true);
            }
        });
        //פתיחת פרגמנט קבוצות
        findViewById(R.id.map_footer_purple_groups).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) MapFragment.getInstance().getActivity()).initFragment(GroupsListFragment.getInstance());
            }
        });

    }

    public void setCreateMeetingPoint() {
        if (mMeetingCreateBtn != null)
            mMeetingCreateBtn.callOnClick();
    }

    public void sendPublicMessageClick() {//פתיחת דיאלוג של שליחת הודעה לקבוצה
        // custom dialog
        final Dialog dialog = new Dialog(mActivity, R.style.Base_Theme_AppCompat_Dialog_FixedSize);
        dialog.setContentView(R.layout.public_message_dialog);
        dialog.setTitle("");

        dialog.findViewById(R.id.public_message_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Utils.HideKeyboard(mActivity);
            }
        });
        CustomEditText editText = (CustomEditText) dialog.findViewById(R.id.public_message_text);
        dialog.findViewById(R.id.public_message_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isOnLine(getContext())) {
                    Utils.HideKeyboard(MapFooterManager.this.getContext());
                    TextView textView = (TextView) dialog.findViewById(R.id.public_message_text);
                    if (textView.getText().equals(""))
                        return;
                    sendPublicMessage(textView.getText().toString());
                    dialog.dismiss();
                } else
                    Toast.makeText(getContext(), getResources().getString(R.string.noConnection), Toast.LENGTH_LONG).show();
            }
        });
        Utils.showKeyBoard(editText, getContext());

        dialog.show();
    }


    private void sendPublicMessage(String messageText) {

        List<KeyValue> payload = new ArrayList<>();//הגדרות על הנוטיפיקציה שתשלח
        payload.add(new KeyValue(Consts.NOTIFICATION_TYPE, String.valueOf(Consts.NOTIFICATION_TYPE_GLOBAL_MESSAGE)));//סוג
        payload.add(new KeyValue(Consts.USER_ID_SEND, Integer.toString(Common.user.getiUserId())));//של המשתמש שולח ID
        payload.add(new KeyValue(Consts.MESSAGE, Common.user.getNvShantiName() + ": " + messageText));//ההודעה
        payload.add(new KeyValue(Consts.NOTIFICATION_USER_FULL_NAME, Common.user.getNvShantiName()));//שם מלא של השולח

        MessageToSend toSend = new MessageToSend(Common.user.getNvShantiName() + ": " + messageText, payload, BaseMarker.getUsersIdWithoutMe(MapFragment.getInstance().getmAllMarkers()));
        //שולח את ההודעה
        new GeneralTask(mActivity, new UseTask() {
            @Override
            public void doAfterTask(String result) {
                if (result != null) {
                    Log.d("send message", result);
                    if (result.contains("TokenId or DeviceType is undefine")) {
                        Toast.makeText(getContext(), ((Activity) getContext()).getString(R.string.mapFooterPublicMessageNoSend), Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(getContext(), ((Activity) getContext()).getString(R.string.mapFooterPublicMessageSend), Toast.LENGTH_LONG).show();

                }

            }
        }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), new Gson().toJson(toSend), ConnectionUtil.PushUpdates);
    }

    private void setViews() {
        switch (this.mPosition) {
            case MAP_FOOTER_USER://כשלוחצים על משתמש על המפה
                TextView moreAboutTextView = (TextView) findViewById(R.id.mapFooterMoreAbout);
                moreAboutTextView.setText(getContext().getString(R.string.mapFooterUserMoreAbout) + " " + mUser.getNvShantiName());
                moreAboutTextView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) MapFragment.getInstance().getActivity())
                                .initFragment(MemberProfileFragment
                                        .getInstance(mUser.getJson()));
                    }
                });
                mUserAddress = (TextView) findViewById(R.id.mapFooterUserAddress);
                mUserName = (TextView) findViewById(R.id.mapFooterUserName);
                mUserInfo = (TextView) findViewById(R.id.mapFooterUserInfo);
                mOpenChatBtn = (ImageView) findViewById(R.id.mapFooterUserOpenChat);
                mNavigateBtn = (ImageView) findViewById(R.id.mapFooterUserNavigateToUser);
                mNavigateBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        navigateToLatLng(mUser.getoLocation().getAsLatLng());//יצירת מסלול
                    }
                });
                break;
            case MAP_FOOTER_ME://נתונים עלי

                mUserName = (TextView) findViewById(R.id.map_footer_me_name);
                mUserInfo = (TextView) findViewById(R.id.map_footer_me_info);
                mNotificationCounter = (TextView) findViewById(R.id.map_footer_me_notification_counter);
                mOpenChatBtn = (ImageView) findViewById(R.id.map_footer_me_open_chat);
                mMeetingPointBtn = (ImageView) findViewById(R.id.map_footer_me_create_meeting_point);
//                if (!mUser.isbIsMainUser())
//                    mMeetingPointBtn.setVisibility(GONE);
//                else
                mMeetingPointBtn.setVisibility(VISIBLE);
                mMeetingPointBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createMeetingPoint();
                        setIsCreateMeetingPoint(true);
                    }
                });
                break;
            case MAP_FOOTER_CREATE_MEETING_POINT://קביעת נקודת מפגש

                MapFragment.getInstance().getmMap().animateCamera(CameraUpdateFactory.newLatLngZoom(Common.user.getoLocation().getAsLatLng(), 15));

//                MapFragment.getInstance().getmMap().moveCamera(CameraUpdateFactory.newLatLngZoom(
//                        new LatLng(44.797283, 20.460663), 25));

//                MapFragment.getInstance().getmMap().animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(44.797283, 20.460663), 15));
//
//                MapFragment.getInstance().getmMap().addMarker(new MarkerOptions()
//                        .position(new LatLng(44.797283, 20.460663))
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                if (mMeetingPointMarker != null) {//אם קיים  - רק משנים מיקום
                    mMeetingPointMarker.setPosition(new LatLng(Common.user.getoLocation().getdLatitude() + 0.0001, Common.user.getoLocation().getdLongitude() + 0.0001));
                } else {//אם לא קיים, יוצרים חדש
                    MarkerFactory markerFactory = new MarkerFactory();
                    mMeetingPointMarker = MapFragment.getInstance().getmMap().addMarker(markerFactory.getMeetingPointCreateMarker(mActivity, new MeetingPoint(-1, -1, "", "", "", Common.user.getoLocation().getAsLatLng(),Common.user.getoLocation().getDistance())));
                    mMeetingPointMarker.showInfoWindow();
                }
                MapFragment.getInstance().getmMap().animateCamera(CameraUpdateFactory.newLatLngZoom(mMeetingPointMarker.getPosition(), 25));

                mGroupName = (TextView) findViewById(R.id.map_footer_meeting_groups_list);
                new GeneralTask(mActivity, new UseTask() {//מחזיר את כל הקבוצות שבהם אני נמצא. לכל קבוצה: שם מנהל, קוד קבוצה, ומספר חברים בקבוצה
                    @Override
                    public void doAfterTask(String result) {
                        mGroupNameIndex = 0;
                        Type programsListType = new TypeToken<List<Group>>() {
                        }.getType();
                        final ArrayList<Group> groups = new Gson().fromJson(result, programsListType);

                        if (groups == null) {//שגיאה בעת טעינת הקבצים
                            Toast.makeText(mActivity, mActivity.getString(R.string.meetingPointCreateErrorLoading), Toast.LENGTH_SHORT).show();
                            changeFooter(MapFooterPosition.MAP_FOOTER_ME, Common.user);
                            return;
                        }
                        if (groups.size() == 0) {//אין למשתמש קבוצות
                            Toast.makeText(mActivity, mActivity.getString(R.string.groupsEmptyList), Toast.LENGTH_SHORT).show();
                            changeFooter(MapFooterPosition.MAP_FOOTER_ME, Common.user);
                            return;
                        }
                        mGroupName.setText(mActivity.getString(R.string.meetingPointToGroup) + " " + groups.get(mGroupNameIndex).getNvGroupName());
                        if (mGroupIdToCreateMeetingPoint != -1) {//אם נבחרה  קבוצה
                            for (int i = 0; i < groups.size(); i++)
                                if (groups.get(i).getiGroupId() == mGroupIdToCreateMeetingPoint) {
                                    mGroupName.setText(mActivity.getString(R.string.meetingPointToGroup) + " " + groups.get(i).getNvGroupName());//שומר את שם הקבוצה
                                    mGroupNameIndex = i;//שומר את אינדקס הקבוצה
                                    break;
                                }
                        }

                        mMeetingSetting = (EditText) findViewById(R.id.map_footer_meeting_setting);
                        mMeetingComments = (EditText) findViewById(R.id.map_footer_meeting_comments);
                        mMeetingCreateBtn = (Button) findViewById(R.id.map_footer_meeting_create);
                        mMeetingTime = (TextView) findViewById(R.id.map_footer_meeting_time);
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.MINUTE, 10);
                        final int year = c.get(Calendar.YEAR);
                        int mHour = c.get(Calendar.HOUR_OF_DAY);
                        int mMinute = c.get(Calendar.MINUTE);
                        final int month = c.get(Calendar.MONTH);

                        mMeetingTime.setText((mHour < 10 ? "0" + mHour : mHour) + ":" + (mMinute < 10 ? "0" + mMinute : mMinute));
                        mMeetingTime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {//נפתח דיאלוג לבחירת שעה
                                TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        mMeetingTime.setText((hourOfDay < 10 ? "0" + hourOfDay : hourOfDay) + ":" + (minute < 10 ? "0" + minute : minute));
                                        //mMeetingTime.setTag(date);
                                    }
                                }, year, month, true);
                                timePickerDialog.show();
                            }
                        });

                        mChooseGroup = (LinearLayout) findViewById(R.id.meeting_point_choose_group);
                        mChooseGroup.setOnTouchListener(new OnSwipeTouchListener(mActivity) {

                            public void onSwipeRight() {//בהחלקה לימין - הקבוצה הבאה
                                mGroupNameIndex++;
                                if (mGroupNameIndex > groups.size())
                                    mGroupNameIndex = 0;
                                mGroupName.setText(mActivity.getString(R.string.meetingPointToGroup) + " " + groups.get(mGroupNameIndex).getNvGroupName());
                            }

                            public void onSwipeLeft() {//בהחלקה לשמאל - הקבוצה הקודמת
                                mGroupNameIndex--;
                                if (mGroupNameIndex == -1)
                                    mGroupNameIndex = groups.size() - 1;
                                mGroupName.setText(mActivity.getString(R.string.meetingPointToGroup) + " " + groups.get(mGroupNameIndex).getNvGroupName());
                            }
                        });

                        mMeetingCreateBtn.setOnClickListener(new OnClickListener() {//לחיצה על 'צור נקודת מפגש'
                            @Override
                            public void onClick(View v) {
                                setIsCreateMeetingPoint(false);
                                final MeetingPoint point = new MeetingPoint(-1, groups.get(mGroupNameIndex).getiGroupId(), mMeetingTime.getText().toString(), mMeetingSetting.getText().toString(), mMeetingComments.getText().toString(), mMeetingPointMarker.getPosition());
                                new GeneralTask(mActivity, new UseTask() {
                                    @Override
                                    public void doAfterTask(String result) {
                                        MeetingPoint pointResult = new Gson().fromJson(result, MeetingPoint.class);
                                        if (pointResult == null || pointResult.getiMeetingPointId() == -1) {
                                            Toast.makeText(mActivity, mActivity.getString(R.string.meetingPointCreateError) + " " + groups.get(mGroupNameIndex).getNvGroupName(), Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        //Load the meeting point from the server
                                        SendCurrentLocationTimer.getInstance(mActivity).sendLocation();
                                        changeFooter(MapFooterPosition.MAP_FOOTER_ME);
                                        mMeetingComments.setText("");
                                        mGroupNameIndex = 0;
                                        mMeetingSetting.setText("");
                                        mMeetingTime.setText("");
                                        if (mGroupIdToCreateMeetingPoint != -1) {//אתחול ל 1-
                                            //mActivity.onBackPressed();
                                            MapFragment.getInstance().setmGroupIdToCreateMeetingPoint(-1);
                                            SendCurrentLocationTimer.getInstance(mActivity).startSendLocation();
                                        }
                                    }
                                }, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), point.getJson(), ConnectionUtil.CreateNewMeetingPoint);
                            }
                        });
                    }
                }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"id\":\"" + mUser.getiUserId() + "\"}", ConnectionUtil.GetUserGroupsAsMain);

                break;
            case MAP_FOOTER_MEETING_POINT_DETAILS://בלחיצה על מרקר של מפגש על המפה
                final TextView meetingGroupName = (TextView) findViewById(R.id.meetingDetailsGroupName);
                TextView meetingTime = (TextView) findViewById(R.id.meetingDetailsTime);
                TextView meetingComment = (TextView) findViewById(R.id.meetingDetailsComments);
                TextView meetingSetting = (TextView) findViewById(R.id.meetingDetailsSetting);
                final TextView meetingTimeToArrived = (TextView) findViewById(R.id.meetingDetailsTimeToArrived);
                ImageView meetingNavigate = (ImageView) findViewById(R.id.meetingDetailsNavigateToMeetingPoint);
                ImageView meetingChat = (ImageView) findViewById(R.id.meetingDetailsOpenChat);
                meetingTime.setText(mMeetingPoint.GetMeetingShortTime());
                meetingSetting.setText(mActivity.getString(R.string.meetingDetailsSetting) + " " + mMeetingPoint.getNvTitle());
                meetingComment.setText(mActivity.getString(R.string.meetingDetailsComments) + " " + mMeetingPoint.getNvComment());

                android.location.Location locationA = new android.location.Location("mUser");
                locationA.setLatitude(Common.user.getoLocation().getAsLatLng().latitude);
                locationA.setLongitude(Common.user.getoLocation().getAsLatLng().longitude);
                android.location.Location locationB = new android.location.Location("point");
                locationB.setLatitude(mMeetingPoint.getoLocation().getAsLatLng().latitude);
                locationB.setLongitude(mMeetingPoint.getoLocation().getAsLatLng().longitude);
                float distanceInMeters = locationB.distanceTo(locationA);//מרחק בין A לB
                int speedIs10MetersPerMinute = 50;
                final float estimatedDriveTimeInMinutes = distanceInMeters / speedIs10MetersPerMinute;
                // meetingTimeToArrived.setText(mActivity.getString(R.string.meetingDetailsTimeToArrived) + " " + String.valueOf(estimatedDriveTimeInMinutes) + " " + mActivity.getString(R.string.meetingDetailsMinutes));

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            final String distance = DistanceCalculator.GetDuration(Common.user.getoLocation().getAsLatLng(), mMeetingPoint.getoLocation().getAsLatLng());
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    meetingTimeToArrived.setText(mActivity.getString(R.string.meetingDetailsTimeToArrived) + " " + distance);
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                //נווט מהמיקום שלי לנקודת מפגש
                meetingNavigate.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mNavigateBtn to point
                        navigateToLatLng(mMeetingPoint.getoLocation().getAsLatLng());//יצירת מסלול
                    }
                });

                //צ'אט קבוצתי
                meetingChat.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //open group chat
                        //Toast.makeText(mActivity, "לא ניתן לפתוח צאט קבוצתי.", Toast.LENGTH_SHORT).show();
                        new GeneralTask(mActivity, new UseTask() {
                            @Override
                            public void doAfterTask(String result) {
                                ((MainActivity) MapFragment.getInstance().getActivity()).initFragment(GroupChatFragment.getInstance(mActivity, result));
                            }
                        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"id\":" + mMeetingPoint.getiGroupId() + "}", ConnectionUtil.GetGroup);
                    }
                });

                new GeneralTask(mActivity, new UseTask() {
                    @Override
                    public void doAfterTask(String result) {
                        Type programsListType = new com.qb.gson.reflect.TypeToken<Group>() {
                        }.getType();
                        Group group = new Gson().fromJson(result, programsListType);
                        if (group != null && group.getNvGroupName() != null)
                            meetingGroupName.setText(mActivity.getString(R.string.meetingDetailsGroup) + " " + group.getNvGroupName());
                    }
                }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST.toString(), "{\"id\":" + mMeetingPoint.getiGroupId() + "}", ConnectionUtil.GetGroup);


                break;
        }

        mUpLine = (LinearLayout) findViewById(R.id.map_footer_up_line);
        if (mOpenChatBtn != null)
            mOpenChatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPosition == MapFooterPosition.MAP_FOOTER_ME)
                        ((MainActivity) MapFragment.getInstance().getActivity()).initFragment(new MessagesFragment());
                    else if (mPosition == MapFooterPosition.MAP_FOOTER_USER) {
                        ((MainActivity) MapFragment.getInstance().getActivity()).initFragment(ChatFragment.getInstance(mActivity, mUser.getJson()));
                    }
                }
            });

    }

    private void removeMeetingPointMarker() {
        if (mMeetingPointMarker != null)
            mMeetingPointMarker.remove();
        mMeetingPointMarker = null;
    }

    public void createMeetingPoint() {
        changeFooter(MapFooterPosition.MAP_FOOTER_CREATE_MEETING_POINT, Common.user);
    }

    public void changeFooter(MapFooterPosition position) {
        this.mPosition = position;
        mFooterFlipper.setDisplayedChild(position.getValue());//מחליף את החלק התחתון לפי המשתמש שנבחר על המפה
        setViews();
        int color = 0;
        switch (position) {
            case MAP_FOOTER_USER://כשלוחצים על משתמש על המפה
                color = mActivity.getResources().getColor(R.color.green_home);
                mGroupFooter.setVisibility(VISIBLE);
                removeMeetingPointMarker();
                setUserDetails();//הכנסת פרטי משתמש
                break;
            case MAP_FOOTER_CREATE_MEETING_POINT://קביעת נקודת מפגש
                color = mActivity.getResources().getColor(R.color.purple_home);
                mGroupFooter.setVisibility(GONE);
                break;
            case MAP_FOOTER_ME://נתונים עלי
                removeMeetingPointMarker();
                //////???????
                //sometimes mActivity is null, what I do???
                color = mActivity.getResources().getColor(R.color.purple_home);
                mGroupFooter.setVisibility(VISIBLE);
                setUserDetails();
                break;
            case MAP_FOOTER_MEETING_POINT_DETAILS://בלחיצה על מרקר של מפגש על המפה
                removeMeetingPointMarker();
                color = mActivity.getResources().getColor(R.color.green_home);
                mGroupFooter.setVisibility(VISIBLE);
                break;
        }
        mUpLine.setBackgroundColor(color);//שיוני צבע גופן

    }

    public void changeFooter(MapFooterPosition position, User user) {
        setmUser(user);
        changeFooter(position);
    }

    public void changeFooter(MapFooterPosition position, MeetingPoint point) {
        setmMeetingPoint(point);
        changeFooter(position);
    }

    public void changeFooter(MapFooterPosition position, int groupId, User user) {
        setmUser(user);
        this.mGroupIdToCreateMeetingPoint = groupId;
        changeFooter(position);
    }


    private void setUserDetails() {//הכנסת פרטי משתמש
        //SPManager.getInstance(mActivity).saveInteger(SPManager.NOTIFICATION_COUNTER, 5);
        //mUser.setbIsMainUser(true);

        mUserName.setText(mUser.getiUserId() == Common.user.getiUserId() ? mUser.getFullName() : mUser.getNvShantiName());
        //אם המשתמש הוא לא אני
        if (mUser.getiUserId() != Common.user.getiUserId())
            mUserInfo.setText(mUser.getUserInfo(mActivity));

        switch (this.mPosition) {
            case MAP_FOOTER_USER:
                mUser.getoLocation().getAddressByLatLng(mActivity, new CallBackFunction() {
                    @Override
                    public void doCallBack(Objects objects) {
                        mUserAddress.setText((mUser.getoLocation().getAddress() == null || mUser.getoLocation().getAddress().equals("")) ? mActivity.getString(R.string.chatNoAddress) : mUser.getoLocation().getAddress());
                    }
                });
                break;
            case MAP_FOOTER_ME:
                updateNotificationCounter();
                break;
            case MAP_FOOTER_CREATE_MEETING_POINT:
                break;
            case MAP_FOOTER_MEETING_POINT_DETAILS:
                break;
        }
    }

    public void updateNotificationCounter() {//מציג מספר הודעות שנשלחו לי - אם נשלחו
        if (SPManager.getInstance(mActivity).getInt(SPManager.NOTIFICATION_COUNTER) > 0) {
            mNotificationCounter.setText(String.valueOf(SPManager.getInstance(mActivity).getInt(SPManager.NOTIFICATION_COUNTER)));
            mNotificationCounter.setVisibility(View.VISIBLE);
        } else mNotificationCounter.setVisibility(View.GONE);
    }

    public void setMarkerAfterDrag(Marker marker) {
        this.mMeetingPointMarker.setPosition(marker.getPosition());
    }

    private void navigateToLatLng(final LatLng latLng) {//יצירת מסלול
        final GMapV2Direction mapV2Direction = new GMapV2Direction();
        removePolyline();
        new AsyncTask<Void, Void, Document>() {
            @Override
            protected Document doInBackground(Void... params) {
                Document doc = mapV2Direction.getDocument(Common.user.getoLocation().getAsLatLng(), latLng, GMapV2Direction.MODE_WALKING);
                return doc;
            }

            @Override
            protected void onPostExecute(Document document) {
                ArrayList<LatLng> directionPoint = mapV2Direction.getDirection(document);
                PolylineOptions rectLine = new PolylineOptions().width(5).color(mActivity.getResources().getColor(R.color.green));

                for (int i = 0; i < directionPoint.size(); i++) {
                    rectLine.add(directionPoint.get(i));
                }
                mPolyline = MapFragment.getInstance().getmMap().addPolyline(rectLine);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void removePolyline() {//מחיקת מסלול קיים
        if (mPolyline != null)
            mPolyline.remove();
    }
}
