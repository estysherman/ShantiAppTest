package webit.android.shanti.customViews;

import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import webit.android.shanti.R;
import webit.android.shanti.general.Common;

/**
 * Created by Android2 on 28/05/2015.
 */
public class CreateMeetingPointCustomView extends RelativeLayout {


    private Context context;
    private GoogleMap mMap;
    private MapView mMapView;
    private boolean mIsMapInit = false;

    public CreateMeetingPointCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //
        init(context, attrs);

    }

    public CreateMeetingPointCustomView(Context context) {
        super(context);
        init(context, null);
    }


    public void init(Context context, AttributeSet attrs) {

        View.inflate(context, R.layout.create_meeting_point_custom_view, this);
        TextView textView = (TextView) findViewById(R.id.meeting_point_MeetingTime);
        try {
            MapsInitializer.initialize(context);
        } catch (Exception e) {
            Log.e("Shanti", "Could not initialize google play", e);
        }

    }

    private void setUpMapIfNeeded() {
        mMapView = (MapView) findViewById(R.id.map);
        //mMapView.onCreate(mSavedInstanceState);
//        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
//        mMapView = (MapView) mRoot.findViewById(R.id.map);
        if (mMapView != null) {
            mMap = mMapView.getMap();
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    if (mMap != null) {
                        mMap.setMyLocationEnabled(true);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Common.user.getoLocation().getAsLatLng(), 15));
                        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                            @Override
                            public void onMyLocationChange(Location location) {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                if (!mIsMapInit) {
                                    mIsMapInit = true;
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                }
                            }
                        });

                        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                            @Override
                            public void onMarkerDragStart(Marker marker) {

                            }

                            @Override
                            public void onMarkerDrag(Marker marker) {
                            }

                            @Override
                            public void onMarkerDragEnd(Marker marker) {
                                //mMapFooterManager.setMarkerAfterDrag(marker);
                            }
                        });
                    }
                }
            });
        }
    }
}
