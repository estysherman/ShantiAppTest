package webit.android.shanti.main.map;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

import webit.android.shanti.BuildConfig;
import webit.android.shanti.entities.UsersListDetails;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.connection.ConnectionUtil;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;
import webit.android.shanti.main.MainActivity;
import webit.android.shanti.main.messages.ChatFragment;

/**
 * Created by AndroIT on 29/01/2015.
 */
public class SendCurrentLocationTimer implements LocationListener {

    private static final int INTERVAL_MILLS = 30 * 1000;
    private static final int INTERVAL_KM = 2000;

    public static UsersListDetails usersListDetails = new UsersListDetails();

    private boolean mIsTimerWork = false, mIsFirstChange = false;
    public static SendCurrentLocationTimer mSendCurrentLocationTimer;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Activity mContext;
    //private MapFragment mMapFragment;

    private SendCurrentLocationTimer(Activity context) {
        this.mContext = context;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL_MILLS, INTERVAL_KM, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, INTERVAL_MILLS, INTERVAL_KM, this);
//        currentLocation = new webit.android.shanti.entities.Location(Common.user.getiUserId());
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                sendLocation();
            }
        };
    }


    public void sendLocation() {
        //Common.user.setoLocation(new webit.android.shanti.entities.Location(32.085300, 34.781768, Common.user.getiUserId()));
        //אם mIsTimerWork=true וקיים משתמש ויש לו מיקום
        if (mIsTimerWork && Common.user != null && Common.user.getoLocation().getdLongitude() != 0)
            new GeneralTask(mContext, new UseTask() {
                @Override
                public void doAfterTask(String result) {
                    try {
                       if(BuildConfig.DEBUG)//בגירסת ה DEBUG בלבד
                            Toast.makeText(mContext, "sendLocation", Toast.LENGTH_SHORT).show();
                        usersListDetails = new Gson().fromJson(result, UsersListDetails.class);
                        if (MainActivity.getInstance().getCurrentFragment().equals(MapFragment.class.toString()))//אם הפונקציה נקראה ממפות
                            MapFragment.getInstance().setUsersOnMap(usersListDetails);//שולח רשימת פרטי משתמשים
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.POST + "", Common.user.getoLocation().getLocationAsJson(), ConnectionUtil.SetLocationGetUsersList);
    }

    public void startSendLocation() {
        if (!mIsTimerWork) {
            mTimer.schedule(mTimerTask, 0, INTERVAL_MILLS);
            mIsTimerWork = true;
        }
    }

    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimerTask.cancel();
            mTimer = null;
            mTimerTask = null;
            mIsTimerWork = false;
            mContext = null;
            mSendCurrentLocationTimer = null;
        }

    }

    public static SendCurrentLocationTimer getInstance(Activity context) {
        if (mSendCurrentLocationTimer == null) {
            mSendCurrentLocationTimer = new SendCurrentLocationTimer(context);
        }
        return mSendCurrentLocationTimer;
    }

    @Override//בעת שינוי מיקום
    public void onLocationChanged(Location location) {
        webit.android.shanti.entities.Location myLocation = new webit.android.shanti.entities.Location(location.getLatitude(), location.getLongitude(), Common.user.getiUserId());//שומר את המיקום החדש
        Common.user.setoLocation(myLocation);//מציב את המיקום החדש אצל המשתמש
//        currentLocation.setdLatitude(location.getLatitude());
//        currentLocation.setdLongitude(location.getLongitude());
//        Log.d("Location", currentLocation.toString());
        if (!mIsFirstChange) {
            sendLocation();
            mIsFirstChange = true;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
