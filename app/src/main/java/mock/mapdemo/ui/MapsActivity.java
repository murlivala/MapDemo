package mock.mapdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import mock.mapdemo.Constants;
import mock.mapdemo.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.CancelableCallback {

    private GoogleMap mMap;
    double lng;
    double lat;
    String destination;
    Intent extraData;
    float zoomIn = 26.0f;
    float defaultZoom = 15.0f;
    float defaultCountryZoom = 4.0f;
    final int ANIMATE_DURATION = 2500;
    private Handler mHandler;
    private final int MAP_READY = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        extraData = getIntent();
        lng = extraData.getDoubleExtra(Constants.LONGITUDE_KEY,0);
        lat = extraData.getDoubleExtra(Constants.LATITUDE_KEY,0);
        destination = extraData.getStringExtra(Constants.DESTINATION_KEY);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       SupportMapFragment mFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mFragment.getMapAsync(this);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch(inputMessage.what){
                    case MAP_READY:
                        showDestination();
                        break;
                    default:
                            /*
                             * Pass along other messages from the UI
                             */
                        super.handleMessage(inputMessage);
                }
            }
        };
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near given location.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(getDefaultMapLocation());//move to Australia
        mHandler.sendEmptyMessageDelayed(MAP_READY,1500);
    }

    private void showDestination(){
        LatLng loc = new LatLng(lat,lng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,zoomIn),ANIMATE_DURATION,this);
    }

    private CameraUpdate getDefaultMapLocation() {
        //Alice Spring
        return CameraUpdateFactory.newLatLngZoom(new LatLng(-23.6835, 133.8797), defaultCountryZoom);
    }

    @Override
    public void onCancel(){

    }

    @Override
    public void onFinish(){
        MarkerOptions markerOptions = new MarkerOptions();
        if(null != destination &&
                !"".equals(destination)){
            markerOptions.title(destination);
        }
        markerOptions.position(new LatLng(lat,lng));
        Marker marker = mMap.addMarker(markerOptions);
        marker.showInfoWindow();
        if(mMap.getCameraPosition().zoom > defaultZoom){
            mMap.animateCamera(CameraUpdateFactory.zoomTo(defaultZoom));
        }
    }
}
