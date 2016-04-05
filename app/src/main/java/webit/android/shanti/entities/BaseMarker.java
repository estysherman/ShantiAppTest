package webit.android.shanti.entities;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import webit.android.shanti.general.Common;

/**
 * Created by 1 on 06/05/15.
 */
public class BaseMarker {

    private transient Marker marker;
    private Location oLocation;
    private boolean isBigImage = false;
    private transient BitmapDescriptor smallIcon;
    private transient BitmapDescriptor bigIcon;


    public boolean isBigImage() {
        return isBigImage;
    }

    public void setIsBigImage(boolean isBigImage) {
        this.isBigImage = isBigImage;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Location getoLocation() {
        return oLocation;
    }

    public void setoLocation(Location oLocation) {
        this.oLocation = oLocation;
    }

    public BitmapDescriptor getSmallIcon() {
        return smallIcon;
    }

    public void setSmallIcon(BitmapDescriptor smallIcon) {
        this.smallIcon = smallIcon;
    }

    public BitmapDescriptor getBigIcon() {
        return bigIcon;
    }

    public void setBigIcon(BitmapDescriptor bigIcon) {
        this.bigIcon = bigIcon;
    }

    public String getMarkerId() {
        return "";
    }

    public static List<Integer> getUsersIdWithoutMe(HashMap<String, BaseMarker> markerHashMap) {
        List<Integer> usersId = new ArrayList<>();
        for (Map.Entry<String, BaseMarker> entry : markerHashMap.entrySet())
            if (entry.getValue() instanceof User && !entry.getValue().getMarkerId().equals(Common.user.getMarkerId()))
                usersId.add(((User) entry.getValue()).getiUserId());
        return usersId;
    }


}
