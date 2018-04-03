package mock.mapdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import mock.mapdemo.Constants;
import mock.mapdemo.R;
import mock.mapdemo.listeners.ResponseCallback;
import mock.mapdemo.dialogs.ShowErrorDialogAndCloseApp;


public class ServiceDataClass extends AsyncTask<Void, Integer, String> {

    private Activity activity;
    private Context mContext;
    private String url;
    private String jsonString;
    private ResponseCallback responseCallback;
    private boolean isNetworkFailure = false;

    public ServiceDataClass(Activity activity, String url) {
        this.activity = activity;
        this.url = url;
    }

    public ServiceDataClass(Context context, String url) {
        mContext = context;
        this.url = url;
    }
    public ServiceDataClass(){

    }

    ServiceDataClass getServiceDataClass(){
        return new ServiceDataClass(mContext,url);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            if (InternetUtil.isInternetOn(activity)) {
                publishProgress(-1);
                jsonString = InternetUtil.sendHttpRequest(url,"");
            }else{
                isNetworkFailure = true;
            }

        } catch (Exception e) {
            return e.getMessage();
        }
        return jsonString;
    }

    @Override
    protected void onProgressUpdate(Integer... index) {
        super.onProgressUpdate(index);

        if(null != activity &&
                !activity.isFinishing()){
           if(index[0] == -1){
               responseCallback.onUpdate(Constants.SHOW_DIALOG,0);
           }
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
            if (responseCallback != null) {
                if (result == null) {
                    responseCallback.onFailure(null);
                } else {
                    responseCallback.onSuccess(result);
                }
            }
        }
    }

    public void setResponseCallback(ResponseCallback responseCallback) {
        this.responseCallback = responseCallback;
    }
}
