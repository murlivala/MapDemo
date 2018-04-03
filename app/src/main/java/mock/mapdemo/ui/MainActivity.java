package mock.mapdemo.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import mock.mapdemo.Constants;
import mock.mapdemo.utils.JsonData;
import mock.mapdemo.R;
import mock.mapdemo.listeners.ResponseCallback;
import mock.mapdemo.utils.JsonUtils;
import mock.mapdemo.utils.ServiceDataClass;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,ResponseCallback {

    private final String TAG = MainActivity.class.getSimpleName();
    private JsonData mData[];
    @BindView(R.id.tv_car) TextView tv_car;
    @BindView(R.id.tv_train) TextView tv_train;
    @BindView(R.id.spinner) Spinner spinner;
    private int curItem;
    Vector<String> items = null;
    ArrayAdapter<String> spinnerArrayAdapter;
    private ProgressDialog mProgressDialog;
    private ServiceDataClass serviceDataClass;
    private JsonUtils jsonUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
        if(null == savedInstanceState) {
            makeRequest();
        }
    }

    private void setupViews(){
        ButterKnife.bind(this);
        // Initializing a String Array
        if(null == items){
            items = new Vector();
            //This is header/title, it will be shown until items are fetched
            items.add("Fetching destinations...");
        }
        // Initializing an ArrayAdapter
        spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, items
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(this);

    }

    /**
     * It will get called on pressing Navigate button
     *
     */
    public void onNavigate(View v){
            Intent callMap = new Intent(this, MapsActivity.class);
            callMap.putExtra(Constants.LONGITUDE_KEY, mData[curItem].getLng());
            callMap.putExtra(Constants.LATITUDE_KEY, mData[curItem].getLat());
            callMap.putExtra(Constants.DESTINATION_KEY,mData[curItem].getStringValue(Constants.NAME_KEY));
            startActivity(callMap);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_in_right);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        curItem = pos;
        spinner.setSelection(curItem);
        update(curItem);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            /**
             * It will request from server
             */
            makeRequest();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void makeRequest(){
        showProgressDialog();
        if(null == serviceDataClass){
            serviceDataClass = new ServiceDataClass(MainActivity.this,
                    Constants.SERVICE_URL);
            serviceDataClass.setResponseCallback(this);
            serviceDataClass.execute();
            curItem = spinner.getSelectedItemPosition();
        }
    }

    /**
     * update the values as per selected item
     */
    public void update(final int index){
        if(null == mData){
            return;
        }
        switch(index){
            case 0:
            case 1:
            case 2:
                tv_car.setText("Car   : "+mData[index].getStringValue(Constants.CAR_KEY));
                tv_train.setText("Train : "+mData[index].getStringValue(Constants.TRAIN_KEY));

        }
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        dismiss();
        if(null != serviceDataClass){
            serviceDataClass.cancel(true);
        }
        if(null != jsonUtils){
            jsonUtils.cancel(true);
        }
    }

    /**
     *
     * save the data when orientation changes
     *
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(Constants.JSON_DATA,mData.length);
        for(int i=Constants.BEGIN;i<mData.length;i++){
            savedInstanceState.putParcelable(Constants.PARCELABLE_KEY+i, mData[i]);
        }
        savedInstanceState.putInt(Constants.CURRENT_SELECTION,curItem);
    }

    /**
     * restores the data/state after orientation gets changed
     */

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        int length = savedInstanceState.getInt(Constants.JSON_DATA,Constants.INVALID_DATA);
        if(length > Constants.INVALID_DATA){
            mData = new JsonData[length];
            items.removeAllElements();
            for(int i = Constants.BEGIN; i<length; i++){
                mData[i] = savedInstanceState.getParcelable(Constants.PARCELABLE_KEY+i);
                items.add(i,mData[i].getStringValue(Constants.NAME_KEY));
            }
        }
        curItem = savedInstanceState.getInt(Constants.CURRENT_SELECTION,Constants.INVALID_DATA);
        spinnerArrayAdapter.notifyDataSetChanged();
        if(curItem > Constants.INVALID_DATA) {
            update(curItem);
            spinner.setSelection(curItem);
        }

    }

    private void showProgressDialog(){
        if(null == mProgressDialog){
            mProgressDialog = ProgressDialog.show(this, "", null , false, true);
            mProgressDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            mProgressDialog.setContentView( R.layout.progress_bar );
            mProgressDialog.setCancelable(true);
        }
    }

    private void dismiss(){
        try {
            if (null != mProgressDialog && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }catch(IllegalArgumentException e){
            Log.e(TAG, "dismiss() - IllegalArgumentException: " + e.getMessage());
        }catch (Exception e){
            Log.e(TAG, "dismiss() - Exception: " + e.getMessage());
        }
    }

    @Override
    public void onSuccess(String result) {
        serviceDataClass = null;
        items.removeAllElements();
        jsonUtils = new JsonUtils(MainActivity.this,
                result);
        jsonUtils.setResponseCallback(this);
        jsonUtils.execute();
    }

    @Override
    public void onFailure(String errorMessage) {
        serviceDataClass = null;
        dismiss();
    }
    public void onUpdate(int state, int index){
        switch (state){
            case Constants.DIALOG_DISMISS:
                dismiss();
                break;
            case Constants.JSON_PARSE_COMPLETED:
                jsonUtils = null;
                break;
            default:

        }
    }

    /**
     * This api updates the dataset and UI
     * while json parsing is in progress
     * @param jsonData transport data
     * @param index    index of data
     * @param totalCount total items
     */
    public void updateData(final JsonData jsonData, final int index,final int totalCount){
        if(null == mData){
            mData = new JsonData[totalCount];
        }
        mData[index] = jsonData;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                items.add(index,jsonData.getStringValue(Constants.NAME_KEY));
                spinnerArrayAdapter.notifyDataSetChanged();
                if(Constants.BEGIN == index){
                    spinner.setSelection(index);
                    update(index);
                }
            }
        });
    }

}
