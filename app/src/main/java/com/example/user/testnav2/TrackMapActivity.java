package com.example.user.testnav2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.maps.MapboxMap;
import com.mapquest.mapping.maps.OnMapReadyCallback;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class TrackMapActivity extends AppCompatActivity{
    private MapboxMap mMapboxmap;
    private MapView mMapview;
//    private SlidingUpPanelLayout mLayout;

    private SharedPreferences mPreferences;


    private double lat;
    private double lon;
    private String childName = "DEFAULT";
    private String childDetails = "DEFAULT";
    private LatLng childLatLng = (new LatLng(0,0));
    private MarkerOptions childMarker;

    private JSONArray stationMarkers = null;


    private void updateChildData() {
        final String deviceID = DeviceIDGenerator.getID(TrackMapActivity.this);
        final String url = "http://13.59.24.178/getChildLocation.php?parentid=" + deviceID;
        final String example = "[]";
        String temp = example;
        try {
            temp = new AsyncTaskRestClient().doInBackground(url);
            JSONArray ja = new JSONArray(temp);
            childName = ja.getJSONObject(0).getString("name");
            lat = ja.getJSONObject(0).getDouble("childLat");
            lon = ja.getJSONObject(0).getDouble("childLon");
            childDetails = ja.getJSONObject(0).getString("details");
            childLatLng = new LatLng(lat,lon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(getApplicationContext());
        setContentView(R.layout.activity_slidingpaneltest);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mMapview = (MapView) findViewById(R.id.ParentMapView);
        mMapview.onCreate(savedInstanceState);
        mMapview.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mMapboxmap = mapboxMap;
                mMapboxmap.setStyleUrl("mapbox://styles/mikeds/cjlzs6p6c6qk62sqrz30jvhvq");
//                addStations(mMapboxmap);
                updateChildData();
                addChild(mMapboxmap);
                updateChildTimer(mMapboxmap);
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
            private void addChild(MapboxMap mapboxMap) {
                MarkerOptions markerOptions = new MarkerOptions();
                IconFactory iconFactory = IconFactory.getInstance(TrackMapActivity.this);
                Drawable iconDrawable = ContextCompat.getDrawable(TrackMapActivity.this, R.drawable.star);
                Icon icon = iconFactory.fromDrawable(iconDrawable);

                markerOptions.setIcon(icon);
                markerOptions.setTitle(childName);
                markerOptions.setSnippet(childDetails);
                markerOptions.position(childLatLng);
                mapboxMap.addMarker(markerOptions);
                Log.e("help", childLatLng.toString());
                mMapboxmap.moveCamera(CameraUpdateFactory.newLatLngZoom(childLatLng, 15));
            }

            private void updateChildAction(MapboxMap mapboxMap) {

                mapboxMap.clear();

                updateChildData();

                MarkerOptions markerOptions = new MarkerOptions();
                IconFactory iconFactory = IconFactory.getInstance(TrackMapActivity.this);
                Drawable iconDrawable = ContextCompat.getDrawable(TrackMapActivity.this, R.drawable.star);
                Icon icon = iconFactory.fromDrawable(iconDrawable);

                markerOptions.setIcon(icon);
                markerOptions.setTitle(childName);
                markerOptions.setSnippet(childDetails);
                markerOptions.position(childLatLng);
                mapboxMap.addMarker(markerOptions);
                Log.e("help", childLatLng.toString());
                mMapboxmap.moveCamera(CameraUpdateFactory.newLatLngZoom(childLatLng, 15));

            }


            public void updateChildTimer(final MapboxMap mapboxMap) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateChildAction(mapboxMap);
                        Log.e("help", "success");
                        handler.postDelayed(this,15000);
                    }
                },15000);
            }

        });

            }
            ;
}
