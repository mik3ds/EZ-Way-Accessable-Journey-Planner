package com.example.user.testnav2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by mark on 8/20/2018.
 */


public class MapActivity extends AppCompatActivity{
    private MapboxMap mMapboxMap;
    private MapView mMapView;
//    ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();


    static final int REQUEST_LOCATION = 1;
    public static double latitude;
    public static double longitude;
    LocationManager locationManager;



    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(getApplicationContext());
        setContentView(R.layout.activity_map);
        mMapView = (MapView) findViewById(R.id.mapquestMapView);
        mMapView.onCreate(savedInstanceState);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        String location = "";

        List<android.location.Address> addressList = null;
        Geocoder gc = new Geocoder(this);
        try{
            addressList = gc.getFromLocationName(location,1);
        }catch (IOException e)
        {
            e.printStackTrace();
        }

//        List<Address> addressList = null;
        getLocation();
        //       double lat = LocationUtils.latitude;
//        double lng = LocationUtils.longitude;
//        double lat = -37.8775468;
//        double lng = 145.0443;
        final LatLng latLng = new LatLng(latitude, longitude);



        mMapView.getMapAsync(new OnMapReadyCallback() {



            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                mMapboxMap = mapboxMap;
                mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                addmarker(mMapboxMap);
            }

            private void addmarker(MapboxMap mapboxMap) {
                MarkerOptions markerOptions = new MarkerOptions();

//                List<android.location.Address> addressList = null;
//
//                int i = 0;
//                try {
//                    File csv = new File(Environment.getExternalStorageDirectory() + "/storage/emulated/0/DCIM/toiletmapexport_180801_090000.csv");
//                    BufferedReader br = new BufferedReader(new FileReader(csv));
//                    br.readLine();
//                    String line = "";
//                    String[] onerow;
//                    while ((line = br.readLine()) != null){
//                        onerow = line.split(",");
//                        List<String> infolist = Arrays.asList(onerow);
//                        ArrayList<String> infoarraylist = new ArrayList<String>(infolist);
//                        if( infoarraylist.get(5) == "Victoria") {
//                            data.add(infoarraylist);
//                            addressList = gc.getFromLocationName(infoarraylist.get(4),1000000);
//                            Address address = addressList.get(i);
//                            double lat = address.getLatitude();
//                            double lon = address.getLongitude();
//                            LatLng latlon = new LatLng(lat,lon);
//                            markerOptions.position(latlon);
//                            markerOptions.title(infoarraylist.get(4));
//                            mapboxMap.addMarker(markerOptions);
//
//                        }
//                        i++;
//                    }
//
//                    br.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                // Create an Icon object for the marker to use
                IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
                Drawable iconDrawable = ContextCompat.getDrawable(MapActivity.this, R.drawable.default_marker);
                Icon icon = iconFactory.fromDrawable(iconDrawable);
                markerOptions.position(latLng);
                markerOptions.icon(icon);

                markerOptions.title("real-time location");

                mapboxMap.addMarker(markerOptions);


            }


        });
    }

//
//    private void readcsv() {
//        int i = 0;
//        try {
//            File csv = new File(Environment.getExternalStorageDirectory() + "/storage/emulated/0/DCIM/toiletmapexport_180801_090000.csv");
//            BufferedReader br = new BufferedReader(new FileReader(csv));
//            br.readLine();
//            String line = "";
//            String[] onerow;
//            while ((line = br.readLine()) != null){
//                onerow = line.split(",");
//                List<String> infolist = Arrays.asList(onerow);
//                ArrayList<String> infoarraylist = new ArrayList<String>(infolist);
//                if( infoarraylist.get(5) == "Victoria")
//                    data.add(infoarraylist);
//            }
//
//            br.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


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


    public double getLocation() {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
return latitude;

    }
}
