package webit.android.shanti.entities;

import android.content.Context;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import webit.android.shanti.general.BaseShantiObject;
import webit.android.shanti.general.CallBackFunction;
import webit.android.shanti.general.connection.GeneralTask;
import webit.android.shanti.general.connection.UseTask;

/**
 * Created by AndroIT on 21/01/2015.
 */
public class Location implements Serializable, BaseShantiObject {
    private double dLatitude;
    private int iDriverId;
    private double dLongitude;
    private transient String address;
    private double Distance;

    public Location(double dLatitude, double dLongitude, int driverId) {
        this.dLatitude = dLatitude;
        this.dLongitude = dLongitude;
        this.iDriverId = driverId;
    }

    public Location(double dLatitude, double dLongitude, int driverId, double distance) {
        this.dLatitude = dLatitude;
        this.dLongitude = dLongitude;
        this.iDriverId = driverId;
        this.Distance = distance;
    }

    public Location(int driverId) {
        this.iDriverId = driverId;
    }


    public double getdLatitude() {
        return dLatitude;
    }

    public void setdLatitude(double dLatitude) {
        this.dLatitude = dLatitude;
    }

    public double getdLongitude() {
        return dLongitude;
    }

    public void setdLongitude(double dLongitude) {
        this.dLongitude = dLongitude;
    }

    public double getDistance() {
        return Distance;
    }

    public void setDistance(double distance) {
        this.Distance = distance;
    }

    public int getiDriverId() {
        return iDriverId;
    }

    public void setiDriverId(int iDriverId) {
        this.iDriverId = iDriverId;
    }

    public LatLng getAsLatLng() {
        return new LatLng(dLatitude, dLongitude);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void getAddressByLatLng(Context context, CallBackFunction callBackFunction) {

        Geocoder geocoder;
        List<android.location.Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(getdLatitude(), getdLongitude(), 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses == null || addresses.size() == 0)
            getAddressFromBing(context, callBackFunction);
        else if (addresses != null && addresses.size() > 0) {
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getAddressLine(1);
            String country = addresses.get(0).getAddressLine(2);
            //editText.setText(address);
            setAddress(address);
            callBackFunction.doCallBack(null);
            ///return new String[]{address, city, country};
        }
        //return new String[]{"", "", ""};
    }

//    public boolean isLocationChange(Location location) {
//        if (location.getdLongitude() == this.getdLongitude() || location.getdLatitude() == this.getdLatitude())
//            return false;
//        else return true;
//    }

    private String getAddressFromBing(final Context context, final CallBackFunction callBackFunction) {
        String url = "http://dev.virtualearth.net/REST/v1/Locations/" + getdLatitude() + "" + "," + getdLongitude() + "?c="+Locale.getDefault()+"&o=json&key=AgYcBsn3D2AgPB2hqXO6h855PV8siNOhApxKJ3eUxiR0s4BM7iRrWmryB2tqvO4y";
        String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
        String urlEncoded = Uri.encode(url, ALLOWED_URI_CHARS);
        new GeneralTask(context, urlEncoded, new UseTask() {
            @Override
            public void doAfterTask(String result) {
                //
                String address = getAddressFromBingMaps(result);
                setAddress(address);
                callBackFunction.doCallBack(null);
            }
        }, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, GeneralTask.SendType.GET.toString());
        return "";
    }

    @Override
    public String getJson() {
        return null;
    }


    private class LocationTosend {


    }


    public String getLocationAsJson() {
        LocationHelp locationHelp = new LocationHelp(this);
        return new Gson().toJson(locationHelp);
    }

    private class LocationHelp {
        Location location;

        public LocationHelp(Location location) {
            this.location = location;
        }
    }

    private String getAddressFromBingMaps(String result) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            JSONObject jsonObject1 = new JSONObject(jsonObject.getJSONArray("resourceSets").get(0).toString());
            JSONObject jsonObject2 = new JSONObject(jsonObject1.getJSONArray("resources").get(0).toString());
            return jsonObject2.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

}
