package webit.android.shanti.main.map;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import webit.android.shanti.R;
import webit.android.shanti.entities.BaseMarker;
import webit.android.shanti.entities.MeetingPoint;
import webit.android.shanti.entities.User;
import webit.android.shanti.entities.UsersListDetails;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.Dimension;
import webit.android.shanti.general.photo.ImageHelper;
import webit.android.shanti.general.photo.PutUserMarkerOnMapTask;
import webit.android.shanti.main.MainActivity;
import webit.android.shanti.main.MainBaseFragment;
import webit.android.shanti.main.info.InfoMainFragment;


public class MapFragment extends MainBaseFragment {

    static MapFragment instance = null;
    public static String ARG_MEETING_POINT = "meetingPoint";
    public static String IS_CREATE_MEETING_POINT = "meetingPoint";
    private int mGroupIdToCreateMeetingPoint = -1;
    private boolean mIsCreateMeetingPoint = false;


    public void setmGroupIdToCreateMeetingPoint(int mGroupIdToCreateMeetingPoint) {
        this.mGroupIdToCreateMeetingPoint = mGroupIdToCreateMeetingPoint;
    }

    public static void setInstance(MapFragment instance) {
        MapFragment.instance = instance;
    }

    public static MapFragment getInstance() {

        return MapFragment.getInstance(-1);
    }

    public static MapFragment getInstance(int groupId) {
        boolean isActive = instance != null;
        Bundle args = new Bundle();
        args.putInt(ARG_MEETING_POINT, groupId);
        if (!isActive) {
            instance = new MapFragment();
            instance.setArguments(args);
        } else
            instance.getArguments().putInt(ARG_MEETING_POINT, groupId);
        return instance;
    }

    public static MapFragment getInstance(int groupId, boolean isCreateMeetingPoint) {
        boolean isActive = instance != null;
        Bundle args = new Bundle();
        args.putInt(ARG_MEETING_POINT, groupId);
        args.putBoolean(IS_CREATE_MEETING_POINT, isCreateMeetingPoint);
        if (!isActive) {
            instance = new MapFragment();
            instance.setArguments(args);
        } else
            instance.getArguments().putInt(ARG_MEETING_POINT, groupId);
        return instance;
    }


    public enum MarkerType {
        UNIQUE_USER("USER"),//משתמש ייחודי
        UNIQUE_MEETING_POINT("MEETING_POINT");//נקודת מפגש ייחודית

        private String value;

        MarkerType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private HashMap<String, BaseMarker> mAllMarkers = new HashMap<>();//רשימה של מרקרים

    private View mRoot = null;
    private GoogleMap mMap;

    private String mMyKeyMarker = "";//ה KEY של המרקר שלי
    private Marker mMyMarker;//המרקר שלי
    private BaseMarker mMyBaseMarker;
    private Bitmap mMyBitmap;
    private MapView mMapView;
    private boolean mIsMapInit = false;
    private Bundle mSavedInstanceState;
    private MapFooterManager mMapFooterManager;
    private User mLlastMarker = null;
    private Button locationSettings;
    boolean flag = true;
    boolean flagGPS = true;

    private Configuration config;
    ImageView mArrow;

    CountDownTimer timer;
    // private LocationManager locationManager;
   /* private LocationListener locationListener = new LocationListener() {//GPS
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(getActivity(), "LocationChanged", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(getActivity(), "StatusChanged", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };*/

    public GoogleMap getmMap() {
        return mMap;
    }

    public HashMap<String, BaseMarker> getmAllMarkers() {
        return mAllMarkers;
    }


    public MapFooterManager getmMapFooterManager() {
        return mMapFooterManager;
    }


    @Override
    public void setActionBarEvents(View view) {
        super.setActionBarEvents(view);
        //בעת לחיצה על כפתור תפריט
        view.findViewById(R.id.actionMapToggleBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).drawerClick();
            }
        });
        //לחיצה על כפתור הודעה - פתתיחת הודעה לקבוצה
        view.findViewById(R.id.actionMapPublicMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment.getInstance().getmMapFooterManager().sendPublicMessageClick();
            }
        });
        //בעת לחיצה על מידע
        view.findViewById(R.id.actionMapLeftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).initFragment(InfoMainFragment.getInstance());
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mSavedInstanceState = savedInstanceState;

        SendCurrentLocationTimer.getInstance(getActivity()).startSendLocation();//שליחת מיקום נוכחי
        if (getArguments() != null) {
            mGroupIdToCreateMeetingPoint = getArguments().getInt(ARG_MEETING_POINT, -1);
            mIsCreateMeetingPoint = getArguments().getBoolean(IS_CREATE_MEETING_POINT, false);
            if (mGroupIdToCreateMeetingPoint != -1 && mMapFooterManager != null) {
                mMapFooterManager.changeFooter(MapFooterManager.MapFooterPosition.MAP_FOOTER_CREATE_MEETING_POINT, mGroupIdToCreateMeetingPoint, Common.user);
                //SendCurrentLocationTimer.getInstance(getActivity()).stopTimer();
            }
        }
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.fragment_map, null);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            try {
                MapsInitializer.initialize(getActivity());
            } catch (Exception e) {
                Log.e("Shanti", "Could not initialize google play", e);
            }
            mArrow = (ImageView) mRoot.findViewById(R.id.arrow);
            if (getResources().getConfiguration().orientation == 2) {//אם המסך שוכב
                config = getResources().getConfiguration();//לנראות משמאל לימין
                ViewFlipper viewFlipper = (ViewFlipper) mRoot.findViewById(R.id.map_footer_view_flipper);
                if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {//ימין לשמאל
                    mArrow.setImageResource(R.drawable.arrow_purple_left);//כיוון החץ
                } else {//שמאל לימין
                    mArrow.setImageResource(R.drawable.arrow_purple_right);
                }
            }
            setUpMapIfNeeded();


            this.setActionBarEvents(mRoot);
            //this.changeActionBar();
            //mMapView = (MapView) mRoot.findViewById(R.id.map);
            mMapFooterManager = (MapFooterManager) mRoot.findViewById(R.id.mapFooter);
//            if (mGroupIdToCreateMeetingPoint != -1) {
//                mMapFooterManager.changeFooter(MapFooterManager.MapFooterPosition.MAP_FOOTER_CREATE_MEETING_POINT, mGroupIdToCreateMeetingPoint, Common.user);
//                SendCurrentLocationTimer.getInstance(getActivity()).stopTimer();
//            } else
//                mMapFooterManager.changeFooter(MapFooterManager.MapFooterPosition.MAP_FOOTER_ME, Common.user);
            mMapFooterManager.changeFooter(MapFooterManager.MapFooterPosition.MAP_FOOTER_ME, Common.user);

            //  locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            //כפתור 'כיבוי שירותי מיקום'
            locationSettings = (Button) mRoot.findViewById(R.id.mapGpsSettings);

            //הדלקה וכיבוי שירותי מיקום
            setGpsSettingsText(locationSettings);

           /* locationSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setGpsSettingsText(locationSettings); //הדלקה וכיבוי שירותי מיקום
                }
            });*/


            locationSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {//פותח חלון של הגדרות מיקום
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                }
            });

        }


        if (getResources().getConfiguration().orientation == 2)//אם המסך שוכב
        {


            final RelativeLayout mRelative = (RelativeLayout) mRoot.findViewById(R.id.relative);
            final RelativeLayout mRelativeMap = (RelativeLayout) mRoot.findViewById(R.id.relative_map);
            final int size = (mRelative.getLayoutParams().width) - (mRelativeMap.getLayoutParams().width);

            mArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//לחיצה על החץ
                    if (flag) {
                        mRelative.getLayoutParams().width = (mRelative.getLayoutParams().width) - size;
                        mRelativeMap.getLayoutParams().width = (mRelativeMap.getLayoutParams().width) / 2;
                        mMapFooterManager.getLayoutParams().width = mRelativeMap.getLayoutParams().width;
                         /*   Animation animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_for_arrow);
                    mArrow.startAnimation(animation1);
                    mMapFooterManager.startAnimation(animation1);*/
                    } else {
                        mRelative.getLayoutParams().width = (mRelative.getLayoutParams().width) + size;
                        mRelativeMap.getLayoutParams().width = (mRelativeMap.getLayoutParams().width) * 2;
                        mMapFooterManager.getLayoutParams().width = mRelativeMap.getLayoutParams().width;
                        /*    Animation animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_for_arrow);
                    mArrow.startAnimation(animation1);
                    mMapFooterManager.startAnimation(animation1);*/
                    }
                    flag = !flag;


                }
            });
        }
        return mRoot;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mIsCreateMeetingPoint)
            mMapFooterManager.createMeetingPoint();
    }

    @Override//כשחוזר מחלון הגדרות מיקום - משנה את טקסט הכפתור כיבוי/הדלקה
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setGpsSettingsText(locationSettings);
    }

    //שינוי טקסט כפתור הגדרות מיקום לכיבוי/הדלקה

    private void setGpsSettingsText(TextView gpsSettingsBtn) {

        final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//if=true אם לא מאופשר
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locationListener);//איפשור GPS
            gpsSettingsBtn.setText(getString(R.string.mapGpsSettingsOff));//שינוי הטקסט לכיבוי

        } else//if=false אם מאופשר
        {
            gpsSettingsBtn.setText(getString(R.string.mapGpsSettingsOn));//שינוי טקסט להדלקה
            // locationManager.removeUpdates(locationListener);//ביטול איפשור GPS
        }
    }


    //מעדכן מספר הודעות שנשלחו למשתמש ויוצגו על המרקר

    public void updateNotificationsCounter() {//יצירת מרקר למשתמש אם אין לו כזה על המפה
        if (mAllMarkers.get(Common.user.getMarkerId()) == null)//המרקר של המשתמש לא קיים
            return;
        //if MapFragment not yet initialized
        if (Common.user != null) {
            new PutUserMarkerOnMapTask(getActivity(), Common.user, Common.user.getMarkerId().equals(Common.user.getMarkerId()), new PutUserMarkerOnMapTask.CallBack() {
                @Override
                public void doAfterTask(Bitmap croppedImg) {
                    MarkerFactory markerFactory = new MarkerFactory();
                    Marker marker = mAllMarkers.get(Common.user.getMarkerId()).getMarker();//המרקר שלי
                    if (mMap != null && marker != null && croppedImg != null)
//                        try {
                        marker.setIcon(markerFactory.getCustomMarker(getActivity(), Common.user, croppedImg).getIcon());
//                        } catch (Exception e) {
//                            Log.d("updateNotificationsCounter", e.toString());
//                        }
                }
            }
            ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Common.user).getNvImage());

        }
        mMapFooterManager.updateNotificationCounter();//
    }

    //public void addMeetingPointOnMap
//שמה את כל המרקרים על המפה
    public void setUsersOnMap(UsersListDetails allData) {//מקבל אובקייט המכיל רשימת פרטי משתמשים  ופרטי נקודות מפגש וכו'

        mMapFooterManager.setUserInfoText(allData.getUsersDetails());
        List<String> noRemove = new ArrayList<>();
        ArrayList<BaseMarker> baseMarkers = new ArrayList<>();
        if (allData.getUsers() != null)//אם יש משתמשים ברשימה
            baseMarkers.addAll(allData.getUsers());//public class User extends BaseMarker
        if (allData.getMeetingPoints() != null)//אם יש נקודות מפגש ברשימה
            baseMarkers.addAll(allData.getMeetingPoints());//public class MeetingPoint extends BaseMarker

        for (final BaseMarker entry : baseMarkers) {
            Log.d("entry location", entry.getoLocation().getAsLatLng().toString());
            noRemove.add(entry.getMarkerId());//מעתיק את הקוד של המרקרים של משתמשים כעת
            // update marker
            if (mAllMarkers.get(entry.getMarkerId()) != null) {//אם המרקר קיים ברשימת המרקרים
                BaseMarker markerEntry = mAllMarkers.get(entry.getMarkerId());//במקום לשלוף כל פעם שומרים במשתנה
                Marker marker = markerEntry.getMarker();//המרקר מתוך ה markerentry
                //if image still proceeding

                if (marker != null) {//אם יש מרקר בעצמו
                    markerEntry.getMarker().setPosition(entry.getoLocation().getAsLatLng()); //מציב מיקום מעודכן ה position הוא חלק מה location
                    animateMarker(marker, entry.getoLocation().getAsLatLng(), false);//אנימציה
                    mAllMarkers.get(entry.getMarkerId()).setoLocation(entry.getoLocation());//מציב מיקום מעודכן
                }
            }
            // add marker
            else {//אם המרקר לא קיים ברשימת המרקרים - התווסף משתמש למפה תוך כדי שימוש באפליקציה
                //ייתכן שזה אני - בפעם הראשונה שנכנסתי לאפליקציה  - גם אותי צריך להכניס לרשימה של המרקרים
                final MarkerFactory markerFactory = new MarkerFactory();
                mAllMarkers.put(entry.getMarkerId(), entry);//מכניס המרקר לרשימה
                if (entry instanceof User)//אם המרקר הוא של משתמש
                    //ה bitmup של המשתמש יחד עם מסגרת - icon user
                    new PutUserMarkerOnMapTask(getActivity(), (User) entry, entry.getMarkerId().equals(Common.user.getMarkerId()), new PutUserMarkerOnMapTask.CallBack() {
                        @Override
                        public void doAfterTask(Bitmap croppedImg) {
                            Log.d("test", getActivity() == null ? "getActivity() null" : "getActivity() not null");
                            Log.d("test", entry == null ? "entry null" : "entry not null");
                            Log.d("test", croppedImg == null ? "croppedImg null" : "croppedImg not null");
                            Log.d("test", mMap == null ? "mMap null" : "mMap not null");

                            //אם אחד מהאוביקטים חוזר ריק שולח הודעה למייל של המתכנת - זה לבדיקה
                            if (getActivity() == null || entry == null || croppedImg == null || mMap == null) {
                                String activity = getActivity() == null ? "getActivity() null_____" : "getActivity() not null____";
                                String entryS = entry == null ? "entry null____" : "entry not null____";
                                String crop = croppedImg == null ? "croppedImg null_____" : "croppedImg not null____";
                                String map = mMap == null ? "mMap null" : "mMap not null";
                                String body = activity + entryS + crop + map;
                                Log.d("test", body);

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("plain/text");
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"katz@webit-sys.com", "mimiyaari@gmail.com"});
                                intent.putExtra(Intent.EXTRA_SUBJECT, "נא לשלוח את המייל, לצורך פתירת בעיות באפליקציה");
                                intent.putExtra(Intent.EXTRA_TEXT, body);
                                startActivity(intent);
                            }//אם חוזר מלא
                            if (getActivity() != null && entry != null && croppedImg != null && mMap != null) {
                                if (((User) entry).getiUserId() == Common.user.getiUserId()) {//אם המשתמש - הוא אני - יוצר לי מרקר חדש
                                    mMyKeyMarker = entry.getMarkerId();
                                    mMyBitmap = croppedImg;
                                    mMyMarker = mMap.addMarker(markerFactory.getCustomMarker(getActivity(), (User) entry, croppedImg));
                                    entry.setMarker(mMyMarker);
                                    mMyBaseMarker = entry;
                                } else {//אם המשתמש - הוא לא אני - יוצר לו מרקר
                                    Marker marker = mMap.addMarker(markerFactory.getCustomMarker(getActivity(), (User) entry, croppedImg));
                                    entry.setMarker(marker);
                                }
                                entry.setSmallIcon(markerFactory.getCustomMarker(getActivity(), (User) entry, croppedImg).getIcon());
                                //update marker in users list
                                mAllMarkers.put(entry.getMarkerId(), entry);
                            }
                        }
                    }
                    ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ((User) entry).getNvImage());
                else if (entry instanceof MeetingPoint) {//אם המרקר הוא של נקודת מפגש
                    Marker marker = mMap.addMarker(markerFactory.getMeetingPointCreateMarker(getActivity(), (MeetingPoint) entry));
                    marker.showInfoWindow();
                    entry.setMarker(marker);
                    mAllMarkers.put(entry.getMarkerId(), entry);
                }
            }
        }
        //removeUser
        //מוחק את כל המרקרים שכבר לא על המפה עכשיו
        for (Map.Entry<String, BaseMarker> entry : mAllMarkers.entrySet()) {
            BaseMarker marker = entry.getValue();
            if (!noRemove.contains(marker.getMarkerId()) && marker.getMarker() != null) {//אם לא קיים ברשימה המעודכנת של המרקרים
                marker.getMarker().remove();
                mAllMarkers.remove(marker.getMarkerId());
                //אם המשתמש שנמחק פתוח ב footer - פותח אותי ב footer
                //footer = המסך התחתון במפה - מידע על המשתמש
                if (mMapFooterManager.getmUser().getMarkerId() == marker.getMarkerId()) {
                    mMapFooterManager.removePolyline();
                    mMapFooterManager.changeFooter(MapFooterManager.MapFooterPosition.MAP_FOOTER_ME);
                    if (getResources().getConfiguration().orientation == 2) {//אם המסך שוכב
                        config = getResources().getConfiguration();//לנראות משמאל לימין
                        ViewFlipper viewFlipper = (ViewFlipper) mRoot.findViewById(R.id.map_footer_view_flipper);
                        if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {//ימין לשמאל
                            mArrow.setImageResource(R.drawable.arrow_purple_left);//החלפת צבע חץ
                        } else {//שמאל לימין
                            mArrow.setImageResource(R.drawable.arrow_purple_right);
                        }
                    }
                }
            }
        }
    }


    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            try {
                mMapView.onDestroy();
            } catch (Exception e) {
                Log.d("onDestroy Map", e.toString());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//if=false אם  מאופשר
            timer = new CountDownTimer(60000, 1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                    // locationManager.removeUpdates(locationListener);//ביטול איפשור GPS
                }
            }.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SendCurrentLocationTimer.getInstance(getActivity()).startSendLocation();
        if (flagGPS)//כשנכנס בפעם הראשונה למסך
            flagGPS = false;
        else {
            timer.cancel();//שחוזר לאפליקציה או למסך עוצר את הטיימר ומאפשר GPS


            //אם נפתח את ה intent אחרי שכיבה וירצה לצאת מה intent  זה יגיע לכאן ויפתח לו שוב  אז אם ירצה שיהיה מופעל ילחץ על הכפתור
           /* final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//if=true אם לא מאופשר
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
            }*/



//        mMapView.onResume();
//        for (Map.Entry<String, BaseMarker> entry : mAllMarkers.entrySet()) {
//            if (entry.getValue().getMarker() != null)
//                entry.getValue().getMarker().remove();
//        }
//        mLlastMarker = null;
//        mAllMarkers.clear();
//
//        SendCurrentLocationTimer.getInstance(getActivity()).sendLocation();
//        setGpsSettingsText(locationSettings);
        }
    }
    private void setUpMapIfNeeded() {//מעדכנת מפה
        mMapView = (MapView) mRoot.findViewById(R.id.map);
        mMapView.onCreate(mSavedInstanceState);
        mMapView.onResume();
//        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
//        mMapView = (MapView) mRoot.findViewById(R.id.map);
        if (mMapView != null) {
            mMap = mMapView.getMap();
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {//כשהמפה נטענת
                    if (mMap != null) {
                        mMap.setMyLocationEnabled(true);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Common.user.getoLocation().getAsLatLng(), 15));
                        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {//מאזין לשינוי מקום שלי
                            @Override
                            public void onMyLocationChange(Location location) {//בעת שינוי המיקום שלי
                                //Common.user.setoLocation(new webit.android.shanti.entities.Location(32.085300, 34.781768, Common.user.getiUserId()));
                                //LatLng latLng = new LatLng(32.085300, 34.781768);
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                if (!mIsMapInit) {//אם המפה לא מאותחלת
                                    mIsMapInit = true;
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                }

                                if (mAllMarkers != null && !mMyKeyMarker.equals("") && mMyMarker != null && mMyBitmap != null)
                                    //אם המיקום שלי השתנה
                                    //למה צריך ליצור אובייקט מיקום חדש ולא להציב את האובייקט שהתקבל מהליסינר?
                                    if (mAllMarkers.get(mMyKeyMarker).getoLocation().getdLatitude() != location.getLatitude() || mAllMarkers.get(mMyKeyMarker).getoLocation().getdLongitude() != location.getLongitude()) {
                                        final MarkerFactory markerFactory = new MarkerFactory();
//                                      mAllMarkers.get(mMyKeyMarker).setoLocation(location1);
                                        try {
                                            ///מה השורה הבאה מבצעת ומה ההבדל בין שורה זו לשורה האחרונה
                                            mAllMarkers.get(mMyKeyMarker).setoLocation(new webit.android.shanti.entities.Location(location.getLatitude(), location.getLongitude(), 0));
                                            mMyMarker.remove();//מחיקת מרקר ישן
                                            mMyMarker = mMap.addMarker(markerFactory.getCustomMarker(getActivity(), (User) mAllMarkers.get(mMyKeyMarker), mMyBitmap));//מרקר המעודכן
                                            mMyBaseMarker.setMarker(mMyMarker);
                                            mAllMarkers.put(mMyKeyMarker, mMyBaseMarker);
                                        } catch (Exception e) {

                                        }
                                    }

                            }
                        });

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {//ליחצה על מרקר
                            @Override
                            public boolean onMarkerClick(final Marker marker) {
                                final MarkerFactory markerFactory = new MarkerFactory();
                                User user = null;
                                if (mAllMarkers.get(marker.getSnippet()) instanceof User)//מכיל גם מרקרים של נקודות מפגש לכן שואל אם הוא מרקר של משתמש
                                    user = (User) mAllMarkers.get(marker.getSnippet());
                                else//אם המרקר הוא נקודת מפגש
                                    changeFooter(marker);//מרכוז מרקר נקודת מפגש
                                final User finalUser = user;
                                if (user != null)
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Bitmap bitmap = ImageHelper.getBitmapFromURL(finalUser.getNvImage(), getActivity());
                                            //bitmap = Bitmap.createScaledBitmap(bitmap, (int) Dimension.convertPixelsToDp(1000, getActivity()), (int) Dimension.convertPixelsToDp(1000, getActivity()), false);
                                            Matrix m = new Matrix();
                                            int size = (int) Dimension.convertDpToPixel(100, getActivity());//המרה מdp לפיקסל
                                            m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, size, size), Matrix.ScaleToFit.CENTER);
                                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                                            bitmap = setFrameForBitmap(bitmap);
                                            final Bitmap finalBitmapBase = bitmap;//כדי שיוכר בסרד הבא


                                            getActivity().runOnUiThread(new Runnable() {
                                                //    final Bitmap finalBitmap = finalBitmapBase;//שמירת התמונה הגדולה

                                                @Override
                                                public void run() {
                                                    if (mAllMarkers.get(marker.getSnippet()).isBigImage()) {//אם התמונה גדולה
                                                        mAllMarkers.get(marker.getSnippet()).getMarker().setIcon(mAllMarkers.get(marker.getSnippet()).getSmallIcon());//מחליף לתמונה קטנה
                                                        mAllMarkers.get(marker.getSnippet()).setIsBigImage(false);
                                                    } else {//אם התמונה קטנה
                                                        try {//
                                                            mAllMarkers.get(marker.getSnippet()).getMarker().setIcon(markerFactory.getCustomMarker(getActivity(), finalUser, finalBitmapBase).getIcon());
                                                            mAllMarkers.get(marker.getSnippet()).setIsBigImage(true);
                                                        } catch (Exception e) {

                                                        }
                                                    }
                                                    changeFooter(marker);//מרכוז מרקר משתמש
                                                }
                                            });
                                        }
                                    }).start();

//                                if (mLlastMarker == null)
//                                    mLlastMarker = (User) mAllMarkers.get(Common.user.getMarkerId());
                                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                // //////////////////////////////////////////////
                                // ////////////////////////////////////////////
                                //לחיצה על מרקר נוסף מקטינה את התמונה של הקודם
                                if (mLlastMarker != null && (user != null && mLlastMarker.getiUserId() != user.getiUserId() || user == null))//לחיצה על מרקר
                                    new PutUserMarkerOnMapTask(getActivity(), mLlastMarker, false, new PutUserMarkerOnMapTask.CallBack() {
                                        @Override
                                        public void doAfterTask(Bitmap croppedImg) {
//                                            try {
                                            //mAllMarkers.get(mLlastMarker.getMarkerId()).getMarker().setIcon(BitmapDescriptorFactory.fromBitmap(croppedImg));
                                            mAllMarkers.get(mLlastMarker.getMarkerId()).getMarker().setIcon(markerFactory.getCustomMarker(getActivity(), mLlastMarker, croppedImg).getIcon());
                                            if (mAllMarkers.get(marker.getSnippet()) instanceof User) {
                                                mLlastMarker = (User) mAllMarkers.get(marker.getSnippet());
                                            }
//                                            } catch (Exception e) {
//
//                                            }
                                        }
                                    }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (mLlastMarker.getNvImage()));
                                if (mLlastMarker == null && (mAllMarkers.get(marker.getSnippet()) instanceof User))//מרקר הוא משתמש ולא נקודת  מפגש
                                    mLlastMarker = (User) mAllMarkers.get(marker.getSnippet());//שמירת המשתמש שלחצו עליו
                                return true;
                            }
                        });
                        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {//פעילה רק שיוצרים נקודת מפגש ורוצים למקם על המפה
                            @Override
                            public void onMarkerDragStart(Marker marker) {
                            }

                            @Override
                            public void onMarkerDrag(Marker marker) {
                            }

                            @Override
                            public void onMarkerDragEnd(Marker marker) {
                                mMapFooterManager.setMarkerAfterDrag(marker);
                            }
                        });
                    }
                }
            });
        }
    }

    private void changeFooter(final Marker marker) {//משנה את התחתית וממרכזת מרקר

        if (marker.getSnippet() == null)//סוג המרקר getSnippet
            return;
        final BaseMarker baseMarker = mAllMarkers.get(marker.getSnippet().toString());

        LatLng latLng = new LatLng(0, 0);
        if (baseMarker instanceof User)//אם המרקר מסוג user
            if (baseMarker.getMarkerId().equals(Common.user.getMarkerId())) {//אם המרקר שלי

                mMapFooterManager.changeFooter(MapFooterManager.MapFooterPosition.MAP_FOOTER_ME, Common.user);//יציג בתחתית את הפרגמנט שלי
                latLng = baseMarker.getoLocation().getAsLatLng();
            } else {
                mMapFooterManager.changeFooter(MapFooterManager.MapFooterPosition.MAP_FOOTER_USER, (User) baseMarker);//יציג בתחתית את הפרגמנט של המשתמש שנלחץ
                latLng = baseMarker.getoLocation().getAsLatLng();
            }
        else if (baseMarker instanceof MeetingPoint) {//אם המרקר מסוג נקודת מפגש
            mMapFooterManager.changeFooter(MapFooterManager.MapFooterPosition.MAP_FOOTER_MEETING_POINT_DETAILS, (MeetingPoint) baseMarker);//יציג בתחתית את הפרגמנט של נקודת מפגש
            latLng = baseMarker.getoLocation().getAsLatLng();
        }
        if (getResources().getConfiguration().orientation == 2) {//אם המסך שוכב
            config = getResources().getConfiguration();//לנראות משמאל לימין
            ViewFlipper viewFlipper = (ViewFlipper) mRoot.findViewById(R.id.map_footer_view_flipper);
            if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {//ימין לשמאל
                if (viewFlipper.getDisplayedChild() == 0 || viewFlipper.getDisplayedChild() == 3)
                    mArrow.setImageResource(R.drawable.arrow_green_left);//כיוון החץ והצבע
                else
                    mArrow.setImageResource(R.drawable.arrow_purple_left);
            } else {//שמאל לימין
                if (viewFlipper.getDisplayedChild() == 0 || viewFlipper.getDisplayedChild() == 3)
                    mArrow.setImageResource(R.drawable.arrow_green_right);
                else
                    mArrow.setImageResource(R.drawable.arrow_purple_right);
            }
        }
        if (latLng.latitude != 0) {
            CameraUpdate center =
                    CameraUpdateFactory.newLatLng(latLng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, mMap.getCameraPosition().zoom));//ממרכז את המרקר הנבחר באמצע המסך
        }
        //mMap.moveCamera(center);
        //mMap.animateCamera(zoom);
    }

    private Bitmap setFrameForBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int radius = Math.min(h / 2, w / 2);
        Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setAntiAlias(true);

        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);
        p.setStyle(Paint.Style.FILL);

        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        c.drawBitmap(bitmap, 4, 4, p);
        p.setXfermode(null);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(getResources().getColor(R.color.frame_big_marker));
        p.setStrokeWidth(3);
        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        return output;
    }
}

