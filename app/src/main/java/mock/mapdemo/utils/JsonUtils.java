package mock.mapdemo.utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import mock.mapdemo.Constants;
import mock.mapdemo.R;
import mock.mapdemo.listeners.ResponseCallback;
import mock.mapdemo.dialogs.ShowErrorDialogAndCloseApp;


public class JsonUtils extends AsyncTask<Void, Integer, String> {
    private static final String TAG = JsonUtils.class.getSimpleName();

    private Activity activity;
    private String jsonString;
    private ResponseCallback responseCallback;
    private boolean isNetworkFailure;

    public JsonUtils(Activity activity, String jsonData) {
        this.activity = activity;
        jsonString = jsonData;
    }

    public JsonUtils(String jsonData) {
        jsonString = jsonData;
    }

    public JsonUtils(){

    }

    JsonUtils getJsonUtils(){
        return new JsonUtils(jsonString);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            if (InternetUtil.isInternetOn(activity)) {
                parseJson(jsonString);
            }else{
                isNetworkFailure = true;
            }

        } catch (Exception e) {
            return e.getMessage();
        }
        return jsonString;
    }

    protected void onProgressUpdate(Integer... index) {
        if(null != activity &&
                !activity.isFinishing()){
            if(index[0] == -1){
                responseCallback.onUpdate(Constants.UPDATE_TITLE,index[0]);
            }else{
                responseCallback.onUpdate(Constants.JSON_PARSE_PARTIAL,index[0]);
            }
        }else{
            cancel(true);
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(null != activity &&
                !activity.isFinishing()){
            if(isNetworkFailure){
                new ShowErrorDialogAndCloseApp(activity).getAlert(activity.getString(R.string.network_error)).show();
            }
            responseCallback.onUpdate(Constants.JSON_PARSE_COMPLETED,0);
        }
    }

    public void parseJson(final String jsonData){

        /****************** Start Parse Response JSON Data *************/

        Log.d(TAG,"JsonUtils - parseJson ---- IN");
        try {
            /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
            /***** Returns the value mapped by name if it exists and is a JSONArray. ***/

            JSONArray jsonMainNode = new JSONArray(jsonData);

            /*********** Process each JSON Node ************/

            int lengthJsonArr = jsonMainNode.length();

            JsonData jsondata;
            for (int i = 0; i < lengthJsonArr; i++) {
                /****** Get Object for each JSON node.***********/
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                jsondata = new JsonData();

                /******* Fetch node values **********/
                /**
                 * below parsing is as per given JSON
                 * could be improved to be more generic
                 */

                String id = jsonChildNode.optString("id").toString();
                String name = jsonChildNode.optString("name").toString();

                JSONObject FromCentral = jsonChildNode.getJSONObject("fromcentral");

                String car = FromCentral.getString("car");
                String train = "";
                if (FromCentral.has("train")) {
                    train = FromCentral.getString("train");
                }else{
                    train = "NA";
                }
                JSONObject location = jsonChildNode.getJSONObject("location");
                String longitude = location.getString("longitude");
                String latitude = location.getString("latitude");

                //Jso = new JsonData(Integer.parseInt(id),name,car,train,Double.parseDouble(longitude),Double.parseDouble(latitude));

                jsondata.id = Integer.parseInt(id);
                jsondata.name = name;
                jsondata.car = car;
                jsondata.train = train;
                jsondata.lng = Double.parseDouble(longitude);
                jsondata.lat = Double.parseDouble(latitude);
                responseCallback.updateData(jsondata,i,lengthJsonArr);

            }
            responseCallback.onUpdate(Constants.DIALOG_DISMISS,0);

            /****************** End Parse Response JSON Data *************/

        } catch (JSONException e) {
            Log.d(TAG,"NewsFeedActivity - parseJson -------- Error parsing jSon:"+e.getMessage());
            e.printStackTrace();
			responseCallback.onUpdate(Constants.DIALOG_DISMISS,0);
        }
    }

    public void setResponseCallback(ResponseCallback responseCallback) {
        this.responseCallback = responseCallback;
    }

}
