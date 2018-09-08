package com.example.user.testnav2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
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

import org.json.JSONArray;

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

    //initialize latitude and longitude, location manager and the address list here
    static final int REQUEST_LOCATION = 1;
    public static double latitude;
    public static double longitude;
    LocationManager locationManager;
    List<android.location.Address> addressList = null;



    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(getApplicationContext());
        setContentView(R.layout.activity_map);
        mMapView = (MapView) findViewById(R.id.mapquestMapView);
        mMapView.onCreate(savedInstanceState);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String location = "";

        Geocoder gc = new Geocoder(this);
        try{
            addressList = gc.getFromLocationName(location,1000);
            // add adress to list here
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


        //USER LOCATION
        //final LatLng latLng = new LatLng(latitude, longitude);
            final LatLng latLng = new LatLng(-37.877848, 145.044696);

        //CAULFIELD STATION LOCATION
        final LatLng calsat = new LatLng(-37.8769,145.0424);

        //ACCESSABLE TOILETS LOCATION
        final LatLng toilet1 = new LatLng( -37.86601353,  145.0496936);
        final LatLng toilet2 = new LatLng( -37.86915066,  145.0540776);


        mMapView.getMapAsync(new OnMapReadyCallback() {



            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                mMapboxMap = mapboxMap;
                mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                addmarker(mMapboxMap);
                addToilets(mMapboxMap);
            }



            private void addToilets(MapboxMap mapboxMap) {
                final MarkerOptions markerOptions = new MarkerOptions();
                IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
                Drawable iconDrawable = ContextCompat.getDrawable(MapActivity.this, R.drawable.toilet);
                Icon icon = iconFactory.fromDrawable(iconDrawable);
                String toiletname = "";
                String address = "";
                JSONArray toilets = RestClient.getToiLoc();
                for(int i = 0; i < 1000; i++){
                    try{
                        //Get toilets details from server;
                        latitude = toilets.getJSONObject(i).optDouble("Latitude");
                        longitude = toilets.getJSONObject(i).optDouble("Longitude");
                        toiletname = "Accessible Toilet";
                        address = toilets.getJSONObject(i).optString("Address1");
                        LatLng toilet_position = new LatLng(latitude, longitude);
                        markerOptions.icon(icon);
                        markerOptions.title(toiletname);
                        markerOptions.snippet(address);
                        markerOptions.position(toilet_position);
                        mapboxMap.addMarker(markerOptions);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }

            private void addmarker(MapboxMap mapboxMap) {
                final MarkerOptions markerOptions = new MarkerOptions();


                IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);

                // Create an Icon object for the marker to use

                Drawable iconDrawable = ContextCompat.getDrawable(MapActivity.this, R.drawable.person);
                Icon icon = iconFactory.fromDrawable(iconDrawable);
                markerOptions.position(latLng);
                markerOptions.icon(icon);
                markerOptions.title("Your Location");
                mapboxMap.addMarker(markerOptions);

                markerOptions.position(calsat);
                iconDrawable = ContextCompat.getDrawable(MapActivity.this, R.drawable.train);
                icon = iconFactory.fromDrawable(iconDrawable);
                markerOptions.title("Caulfield Station");
                markerOptions.icon(icon);
                mapboxMap.addMarker(markerOptions);

                markerOptions.position(toilet1);
                iconDrawable = ContextCompat.getDrawable(MapActivity.this, R.drawable.toilet);
                icon = iconFactory.fromDrawable(iconDrawable);
                markerOptions.title("Accessible Public Toilet");
                markerOptions.icon(icon);
                mapboxMap.addMarker(markerOptions);

                // After creating first toilet marker, second one reuses data besides GPS coordinates
                markerOptions.position(toilet2);
                mapboxMap.addMarker(markerOptions);






//                new AsyncTask<Void, Void, String>(){
//                    @Override
//                    protected String doInBackground(Void... params) {
//                        return RestClient.getToiLoc();
//                    }
//
//                    @Override
//                    protected void onPostExcute(String result){
//
//                        markerOptions.position(result.)
//
//                    }
//                }.execute();


//                for(int i =0; i < 100000; i++){
//                    Address address = addressList.get(i);
//                    double lat = address.getLatitude();
//                    double lon = address.getLongitude();
//                    LatLng latlon = new LatLng(lat,lon);
//                    markerOptions.position(latlon);
//                    markerOptions.title("Disabled Toilets");
//                    mapboxMap.addMarker(markerOptions);
//                }

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

    //Get user's current location from GPS
    public void getLocation() {

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

    }
}
