package com.example.user.testnav2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.maps.MapboxMap;
import com.mapquest.mapping.maps.OnMapReadyCallback;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class TrackMapActivity extends AppCompatActivity{
    private MapboxMap mMapboxmap;
    private MapView mMapview;
//    private SlidingUpPanelLayout mLayout;

    private SharedPreferences mPreferences;


    final double lat = -37.877848;
    final double lon = 145.044696;
    LatLng latLng = new LatLng(lat,lon);

    private JSONArray stationMarkers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initialise
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(getApplicationContext());
        setContentView(R.layout.activity_slidingpaneltest);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        mLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingPanel);
//        mLayout.setAnchorPoint(0.5f);
        mMapview = (MapView) findViewById(R.id.ParentMapView);
        mMapview.onCreate(savedInstanceState);
        String name = mPreferences.getString("name", null);
        String code = mPreferences.getString("code", null);
        String deviceID = mPreferences.getString("deviceID", null);


        //get child's location from server
        if(mPreferences.getString("name", "Guest").equals("Guest")){
            latLng = new LatLng(lat, lon);
        }else{
            final String url = "http://13.59.24.178/linkParent2.php?name=" + name + "&code=" + code + "&parentID=" + deviceID;
            final String example = "[]";
            String temp = example;
            try {
                temp = new AsyncTaskRestClient().execute(url).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            try {
                JSONArray ja = new JSONArray(temp);
                double lat = ja.getJSONObject(0).getDouble("childLat");
                double lon = ja.getJSONObject(0).getDouble("childLon");
                String details = ja.getJSONObject(0).getString("details");
                latLng =new LatLng(lat,lon);
                    //  String response = name + " was located at " + lat + "," + lon + " on " + details;
                    //    tv.setText(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        mMapview.getMapAsync(new OnMapReadyCallback() {
            // initialise map
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mMapboxmap = mapboxMap;
                mMapboxmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                mMapboxmap.setStyleUrl("mapbox://styles/mikeds/cjlzs6p6c6qk62sqrz30jvhvq");
                addStations(mMapboxmap);
            }


            //Stations icon making
            private void addStations(MapboxMap mapboxMap) {
                MarkerOptions markerOptions = new MarkerOptions();
                IconFactory iconFactory = IconFactory.getInstance(TrackMapActivity.this);
                Drawable iconDrawable = ContextCompat.getDrawable(TrackMapActivity.this, R.drawable.train);
                Icon icon = iconFactory.fromDrawable(iconDrawable);
                String stationname = "";
                JSONArray stations = null;
                if (stationMarkers == null) {
                    stations = RestClient.getStaLoc(-37.877848, 145.044696);
                    stationMarkers = stations;
                } else {
                    stations = stationMarkers;
                };
                double stalat = 0.0;
                double stalon = 0.0;
                for(int i = 0; i < stations.length(); i++){
                    try{
                        //Get toilets details from server;
                        stalat = stations.getJSONObject(i).optDouble("lat");
                        stalon = stations.getJSONObject(i).optDouble("lon");
                        stationname = stations.getJSONObject(i).optString("name");
                        LatLng station_position = new LatLng(stalat, stalon);
                        markerOptions.icon(icon);
                        markerOptions.title(stationname);
                        markerOptions.position(station_position);
                        mapboxMap.addMarker(markerOptions);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }


        });

    }
}
