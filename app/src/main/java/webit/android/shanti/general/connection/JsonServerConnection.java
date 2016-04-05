package webit.android.shanti.general.connection;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class JsonServerConnection {

    /**
     * The method send and get json with httpPost the json format:"UTF-8"
     *
     * @param dataToSend String format json that contain the data to send
     * @param serviceUri server to post the json
     * @return String with the result from server if the result is has a problem
     * the method return null
     */
    public String postGetJson(String dataToSend, String serviceUri) {

        StringEntity entity = null;
        HttpPost httpPost = new HttpPost(serviceUri);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Accept-Charset", "UTF-8");
        String result = null;
        HttpParams httpParameters = null;
        try {
            entity = new StringEntity(dataToSend, "UTF-8");
            httpPost.setEntity(entity);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = null;
            try {
                /*
                 * HttpURLConnection conn = (HttpURLConnection)
				 * url.openConnection(); conn.setConnectTimeout(7000); //set the
				 * timeout in milliseconds
				 */
                httpParameters = httpClient.getParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        120000);
                HttpConnectionParams.setSoTimeout(httpParameters, 120000);
                response = httpClient.execute(httpPost);

                if (response.getStatusLine().getStatusCode() == 400) {
                    result = "Bad Request";
                } else if (response.getStatusLine().getStatusCode() == 405) {
                    result = "Method Not Allowed";
                } else if (response.getStatusLine().getStatusCode() == 200) {
                    result = EntityUtils.toString(response.getEntity());
                } else {
                    result = String.valueOf(response.getStatusLine().getStatusCode());
                }

            } finally {
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }

        return result;
    }

    public String postGetJsonVer2(String dataToSend, String serviceUri) {

        Log.d("Call Server", serviceUri);
        Log.d("dataToSend", dataToSend);
        // Post Data
//        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
//        nameValuePair.add(new BasicNameValuePair("username", "test_user"));
//        nameValuePair.add(new BasicNameValuePair("password", "123456789"));

        BufferedReader reader = null;

        String l = null;
        StringBuffer sb = new StringBuffer("");
        HttpClient client = new DefaultHttpClient();

        try {
            HttpPost request = new HttpPost(serviceUri);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            request.setHeader("Accept-Charset", "UTF-8");
            StringEntity entity = new StringEntity(dataToSend, "UTF-8");
            request.setEntity(entity);

            HttpResponse response = client.execute(request);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);

            while ((l = reader.readLine()) != null) {
                Log.d("TAG", l);
                sb.append(l);
            }
            reader.close();
            Log.d("result from server", sb.toString());
            return sb.toString();

        } catch (ClientProtocolException e) {
            Log.e("TAG", "ClientProtocolException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("TAG", "IOException");
            e.printStackTrace();
        }

        return null;
    }

    public String getJsonFromObject(String titel, Object objectToJson) {
        JSONObject driverStatus = null;
        try {
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(gson.toJson(objectToJson));
            driverStatus = new JSONObject();
            driverStatus.put(titel, jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return driverStatus.toString();

    }

    public String get(String url) throws Exception {
        // Prepare return string
        String response;

        // Create the connection object
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);

        // Set some parameters
        HttpParams httpParams = httpClient.getParams();

        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpConnectionParams.setSoTimeout(httpParams, 10000);

        // Execute the call
        HttpResponse httpResponse = httpClient.execute(httpGet);

        // Analyze status response
        StatusLine statusLine = httpResponse.getStatusLine();
        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
            // Get the response
            HttpEntity httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
        } else {
            // Unexpected status response
            throw new IOException(statusLine.getReasonPhrase());
        }

        return response;
    }


    /**
     * the nethod convert object to json
     *
     * @param object the object to convert
     * @return String format json
     */
    public String objectToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    /**
     * insert the bodyJson under the title
     *
     * @param title    the key of jsonElement
     * @param bodyJson the body of json
     * @return json, the key is the title and bodyJson is the body of the json
     */
    public JSONObject getJson(String title, JSONObject bodyJson) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(title, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Log.d("jsonObject", jsonObject.toString());
        return jsonObject;
    }

    public String postGet(String serviceUri) {

        HttpGet httpPost = new HttpGet(serviceUri);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Accept-Charset", "UTF-8");
        String result = null;
        HttpParams httpParameters = null;
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = null;
            try {
                /*
                 * HttpURLConnection conn = (HttpURLConnection)
				 * url.openConnection(); conn.setConnectTimeout(7000); //set the
				 * timeout in milliseconds
				 */
                httpParameters = httpClient.getParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        120000);
                HttpConnectionParams.setSoTimeout(httpParameters, 120000);
                response = httpClient.execute(httpPost);
                if (response.getStatusLine().getStatusCode() == 400) {
                    result = "Bad Request";
                } else if (response.getStatusLine().getStatusCode() == 405) {
                    result = "Method Not Allowed";
                } else if (response.getStatusLine().getStatusCode() == 200) {
                    result = EntityUtils.toString(response.getEntity());
                } else {
                    result = String.valueOf(response.getStatusLine().getStatusCode());
                }
            } finally {
            }
        } catch (ClientProtocolException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();

        } finally {
        }

        return result;
    }


}