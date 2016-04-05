package webit.android.shanti.main.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import webit.android.shanti.R;
import webit.android.shanti.entities.MeetingPoint;
import webit.android.shanti.entities.User;
import webit.android.shanti.general.Common;
import webit.android.shanti.general.SPManager;

/**
 * Created by AndroIT on 19/01/2015.
 */
public class MarkerFactory {

    public enum MarkerStatus {
        focus, visible
    }

    public enum MarkerType {
        user, friend
    }


    ImageView imageView;
    MarkerOptions markerOptions;

    public MarkerFactory() {

    }
//הופכת view לתמונה.
    private Bitmap createDrawableFromView(Activity context, View view) {//יוצרת תמונה של המשתמש בצורת מרקר של מפה
        DisplayMetrics displayMetrics = new DisplayMetrics();
        (context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    public MarkerOptions getCustomMarker(final Activity context, User user, Bitmap bitmap) {//יוצרת מרקר למשתמש ושמה עליו את התמונה
        //catch nulls
        if (context != null && user != null && bitmap != null) {

            Log.d("context", "context" + context);
            Log.d("user", "user" + user);
            Log.d("bitmap", "bitmap" + bitmap);
            View markerView = ((LayoutInflater) context
                    .getSystemService(
                            Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.people_marker, null);
            imageView = (ImageView) markerView.findViewById(R.id.markerProfileImage);
            imageView.setImageBitmap(bitmap);
            if (user.getiUserId() == Common.user.getiUserId() && SPManager.getInstance(context).getInt(SPManager.NOTIFICATION_COUNTER) > 0)
                ((TextView) markerView.findViewById(R.id.markerNotificationsCounter)).setText(String.valueOf(SPManager.getInstance(context).getInt(SPManager.NOTIFICATION_COUNTER)));

            markerOptions = new MarkerOptions()
                    .position(user.getoLocation().getAsLatLng())
                    .title(user.getNvShantiName())
                    .snippet(user.getMarkerId())
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(createDrawableFromView(context, markerView)));
            return markerOptions;
        } else return null;
    }
//יוצרת View מעוצב עם מסגרת והתמונה של המשתמש
    public MarkerOptions getMeetingPointCreateMarker(final Activity context, MeetingPoint meetingPoint) {//מרקר של נקודת מפגש
        View markerView = ((LayoutInflater) context
                .getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.meeting_point_marker, null);//נקודת מפגש icon
        ((EditText) markerView.findViewById(R.id.meeting_marker_time)).setText(meetingPoint.GetMeetingShortTime());//שעת נקודת מפגש
        if (meetingPoint.getiGroupId() == -1)
            markerView.findViewById(R.id.meeting_marker_small_marker).setVisibility(View.GONE);
        else
            markerView.findViewById(R.id.meeting_marker_big_marker).setVisibility(View.GONE);
        //LatLng latLng = new LatLng(Common.user.getoLocation().getdLatitude(), Common.user.getoLocation().getdLongitude());

        markerOptions = new MarkerOptions()
                .position(meetingPoint.getoLocation().getAsLatLng()).draggable(meetingPoint.getiGroupId() == -1).snippet(meetingPoint.getMarkerId())
                .icon(BitmapDescriptorFactory
                        .fromBitmap(createDrawableFromView(context, markerView)));
        return markerOptions;
    }
}
