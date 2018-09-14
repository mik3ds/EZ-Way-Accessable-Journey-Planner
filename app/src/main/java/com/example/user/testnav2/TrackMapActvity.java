package com.example.user.testnav2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import org.json.JSONArray;

/**
 * Created by mark on 9/14/2018.
 */

public class TrackMapActvity extends AppCompatActivity{

        private MapboxMap mMapboxmap;
        private MapView mMapview;
        Intent intent = getIntent();

        double lat = intent.getDoubleExtra("lat", 0.0);
        double lon = intent.getDoubleExtra("lon", 0.0);
        LatLng latLng = new LatLng(lat,lon);
        private JSONArray stationMarkers = null;


        @Override
        public void onCreate(Bundle savedInstanceState){
                super.onCreate(savedInstanceState);
                MapboxAccountManager.start(getApplicationContext());
                setContentView(R.layout.activity_track);
                mMapview = (MapView) findViewById(R.id.MapView);
                mMapview.onCreate(savedInstanceState);




                mMapview.getMapAsync(new OnMapReadyCallback() {

                        @Override
                        public void onMapReady(MapboxMap mapboxMap) {

                                mMapboxmap = mapboxMap;
                                mMapboxmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                                addStations(mMapboxmap);
                        }


                        //Stations icon making
                        private void addStations(MapboxMap mapboxMap) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                IconFactory iconFactory = IconFactory.getInstance(TrackMapActvity.this);
                                Drawable iconDrawable = ContextCompat.getDrawable(TrackMapActvity.this, R.drawable.train);
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
                                for(int i = 0; i < 1000; i++){
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
