package webit.android.shanti.main.map;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import webit.android.shanti.entities.Distance;

/**
 * Created by 1 on 07/05/15.
 */
public class DistanceCalculator {//פניה ל GoogleMap

    public static String GetDuration(LatLng src, LatLng dest) throws IOException, JSONException {//חישוב משך זמן בין 2 נקודות

        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json?");
        urlString.append("origin=");//from
        urlString.append( Double.toString(src.latitude));
        urlString.append(",");
        urlString.append( Double.toString(src.longitude));
        urlString.append("&destination=");//to
        urlString.append( Double.toString(dest.latitude));
        urlString.append(",");
        urlString.append( Double.toString(dest.longitude));
        urlString.append("&mode=walking&sensor=true");
        urlString.append(",");
        urlString.append("&language=");//to
        urlString.append(Locale.getDefault());
        Log.d("xxx", "URL=" + urlString.toString());

        // get the JSON And parse it to get the directions data.
        HttpURLConnection urlConnection= null;
        URL url = null;

        url = new URL(urlString.toString());
        urlConnection=(HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.connect();

        InputStream inStream = urlConnection.getInputStream();
        BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));

        String temp, response = "";
        while((temp = bReader.readLine()) != null){
            //Parse data
            response += temp;
        }
        //Close the reader, stream & connection
        bReader.close();
        inStream.close();
        urlConnection.disconnect();

        //Sortout JSONresponse
        JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
        JSONArray array = object.getJSONArray("routes");
        //Log.d("JSON","array: "+array.toString());

        //Routes is a combination of objects and arrays
        JSONObject routes = array.getJSONObject(0);
        //Log.d("JSON","routes: "+routes.toString());

        String summary = routes.getString("summary");
        //Log.d("JSON","summary: "+summary);

        JSONArray legs = routes.getJSONArray("legs");
        //Log.d("JSON","legs: "+legs.toString());

        JSONObject steps = legs.getJSONObject(0);
        //Log.d("JSON","steps: "+steps.toString());

        JSONObject duration = steps.getJSONObject("duration");
        //Log.d("JSON","distance: " +distance.toString());

        String sDistance = duration.getString("text");
        return sDistance;
        //int iDistance = distance.getInt("value");
        //return  String.valueOf(iDistance);
    }public static Distance GetDistance(LatLng src, LatLng dest) throws IOException, JSONException {//חישוב מרחק

        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json?");
        urlString.append("origin=");//from
        urlString.append( Double.toString(src.latitude));
        urlString.append(",");
        urlString.append( Double.toString(src.longitude));
        urlString.append("&destination=");//to
        urlString.append( Double.toString(dest.latitude));
        urlString.append(",");
        urlString.append( Double.toString(dest.longitude));
        urlString.append("&mode=walking&sensor=true");
        urlString.append(",");
        urlString.append("&language=");//to
        urlString.append(Locale.getDefault());
        Log.d("xxx", "URL=" + urlString.toString());

        // get the JSON And parse it to get the directions data.
        HttpURLConnection urlConnection= null;
        URL url = null;

        url = new URL(urlString.toString());
        urlConnection=(HttpURLConnection)url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.connect();

        InputStream inStream = urlConnection.getInputStream();
        BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));

        String temp, response = "";
        while((temp = bReader.readLine()) != null){
            //Parse data
            response += temp;
        }
        //Close the reader, stream & connection
        bReader.close();
        inStream.close();
        urlConnection.disconnect();

        //Sortout JSONresponse
        JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
        JSONArray array = object.getJSONArray("routes");
        //Log.d("JSON","array: "+array.toString());

        //Routes is a combination of objects and arrays
        JSONObject routes = array.getJSONObject(0);
        //Log.d("JSON","routes: "+routes.toString());

        String summary = routes.getString("summary");
        //Log.d("JSON","summary: "+summary);

        JSONArray legs = routes.getJSONArray("legs");
        //Log.d("JSON","legs: "+legs.toString());

        JSONObject steps = legs.getJSONObject(0);
        //Log.d("JSON","steps: "+steps.toString());

//        JSONObject duration = steps.getJSONObject("duration");
//        //Log.d("JSON","distance: " +distance.toString());
        JSONObject distans = steps.getJSONObject("distance");
        //Log.d("JSON","distance: " +distance.toString());

        return new Distance(distans.getString("text"),distans.getInt("value"));
        //int iDistance = distance.getInt("value");
        //return  String.valueOf(iDistance);
    }
}
