package com.example.user.testnav2;

import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.maps.MapboxMap;
import com.mapbox.mapboxsdk.*;
import com.mapquest.mapping.maps.OnMapReadyCallback;


/**
 * Created by mark on 8/20/2018.
 */

public class MapActivity extends AppCompatActivity{
    private MapboxMap mMapboxMap;
    private MapView mMapView;



    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(getApplicationContext());
        setContentView(R.layout.activity_map);
        mMapView = (MapView) findViewById(R.id.mapquestMapView);
        mMapView.onCreate(savedInstanceState);

        Geocoder gc = new Geocoder(this);
        //       double lat = LocationUtils.latitude;
        //     double lng = LocationUtils.longitude;

        double lat = -37.8775468;
        double lng = 145.0443;
        final LatLng latLng = new LatLng(lat, lng);

        mMapView.getMapAsync(new OnMapReadyCallback() {



            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                mMapboxMap = mapboxMap;
                mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                addmarker(mMapboxMap);
            }

            private void addmarker(MapboxMap mapboxMap) {
                MarkerOptions markerOptions = new MarkerOptions();

                // Create an Icon object for the marker to use
                IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
                Drawable iconDrawable = ContextCompat.getDrawable(MapActivity.this, R.drawable.default_marker);
                Icon icon = iconFactory.fromDrawable(iconDrawable);
                markerOptions.position(latLng);
                markerOptions.icon(icon);

                markerOptions.title("Caulfield campus");

                mapboxMap.addMarker(markerOptions);
            }


        });
    }



    @Override
    public void onResume(){
        super.onResume();
        mMapView.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
        mMapView.onPause();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mMapView.onDestroy();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

}
