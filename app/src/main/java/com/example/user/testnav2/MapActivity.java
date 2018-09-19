package com.example.user.testnav2;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewManager;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.maps.MapboxMap;
import com.mapbox.mapboxsdk.*;
import com.mapquest.mapping.maps.OnMapReadyCallback;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Created by mark on 8/20/2018.
 */


public class MapActivity extends AppCompatActivity {
    //Initialise map
    private SharedPreferences mPreferences;
    private MapboxMap mMapboxMap;
    private MapView mMapView;
    private MarkerViewManager mMarkerViewManager;
//    ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

    //initialize latitude and longitude, location manager and the address list here
    static final int REQUEST_LOCATION = 1;
    public static double latitude;
    public static double longitude;
    LocationManager locationManager;
    List<android.location.Address> addressList = null;
    private static boolean toimarkershown = false;
    private static boolean stamarkershown = false;
    private FloatingActionButton floatingActionButton1;
    private FloatingActionButton floatingActionButton2;
    private JSONArray toiletMarkers = null;
    private JSONArray stationMarkers = null;
    private TextView slidepanelTitle;
    private TextView slidepanelSubtitle;
    private TextView slidepanelJourney;
    private ImageView slidepanelImage;


//    private SlidingUpPanelLayout mLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(getApplicationContext());
        setContentView(R.layout.activity_map);
//        mLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingPanelMapActivity);
//        mLayout.setAnchorPoint(0.5f);

        mMapView = (MapView) findViewById(R.id.mapquestMapView);
        mMapView.onCreate(savedInstanceState);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String location = "";
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.toiletshidden);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.stationshidden);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        Geocoder gc = new Geocoder(this);
//        try{
//            addressList = gc.getFromLocationName(location,1000);
//            // add adress to list here
//        }catch (IOException e)
//        {
//            e.printStackTrace();
//        }

//        LocationUtils LU = new LocationUtils();
        ArrayList<Double> list = getLocation();
//        list = LU.getLocation();
        Double lulat = list.get(0);
        Double lulon = list.get(1);


        //USER LOCATION
         final LatLng latLng = new LatLng(lulat, lulon);
      //  final LatLng latLng = new LatLng(-37.877848, 145.044696);

        mMapView.getMapAsync(new OnMapReadyCallback() {


            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                mMapboxMap = mapboxMap;

                mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                mMapboxMap.setStyleUrl("mapbox://styles/mikeds/cjlzs6p6c6qk62sqrz30jvhvq");
//                addUserLocation(mMapboxMap);
//                addToilets(mMapboxMap);
//                addStations(mMapboxMap);

                final IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
                Drawable iconDrawable = ContextCompat.getDrawable(MapActivity.this, R.drawable.train);
                final Icon trainIcon = iconFactory.fromDrawable(iconDrawable);
                iconDrawable = ContextCompat.getDrawable(MapActivity.this, R.drawable.toilet);
                final Icon toiletIcon = iconFactory.fromDrawable(iconDrawable);

                Boolean isParent = mPreferences.getBoolean("isParent", true);
                Log.e("help", "line 149 triggers");
                if (!isParent) {
                    Log.e("help","line 150 triggers");
                    updateChildLocationToServer();
                }






                //Set up marker button
                mMapboxMap.setOnMarkerClickListener(new com.mapbox.mapboxsdk.maps.MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        slidepanelTitle = (TextView) findViewById(R.id.sliderpanelTitleTextView);
                        slidepanelTitle.setText(marker.getTitle());
                        slidepanelSubtitle = (TextView) findViewById(R.id.sliderpanelSubtitleTextView);
                        slidepanelSubtitle.setText(marker.getSnippet());
                        slidepanelJourney = (TextView) findViewById(R.id.sliderpanelJourneyTextView);
                        slidepanelImage = (ImageView) findViewById(R.id.sliderpanelImageView1);

                        slidepanelImage.setImageBitmap(marker.getIcon().getBitmap());


                        //Create temporary train icon and compare
                        String example = "";
                        String footer = " Train Station";
                        String stationName = marker.getTitle();
                        stationName = stationName.replace(footer,"");
                        if (marker.getIcon().getBitmap().sameAs(trainIcon.getBitmap())){
                            String url = "http://13.59.24.178/getStationByID.php/?stationname=" + stationName;
                            Log.e("help", marker.toString());
                            example = new AsyncTaskRestClient().doInBackground(url);

                        } else if (marker.getIcon().getBitmap().sameAs(toiletIcon.getBitmap())){

//                            String url = "http://13.59.24.178/getToiletByID.php/?toiletID=" + marker.getTitle();
//                            Log.e("help", marker.toString());
//                            example = new AsyncTaskRestClient().doInBackground(url);
                        } else {
                            Log.e("help", "If Statement not doing anything");
                        }
                        slidepanelJourney.setText(example);



                        return false;
                    }
                }
                );

                //Hide and show toilets button
                floatingActionButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removetoilets();
                    }
                });

                //Hide and show stations button
                floatingActionButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removestation();
                    }
                });



            }

            //Hide and show toilets methods
            public void removetoilets() {

                String toast = "";

                if (toimarkershown && stamarkershown) {
                    mMapboxMap.clear();
                    addUserLocation(mMapboxMap);
                    addStations(mMapboxMap);
                    toimarkershown = false;
                    toast = "Toilets Disabled";
                } else if (toimarkershown && !stamarkershown) {
                    mMapboxMap.clear();
                    addUserLocation(mMapboxMap);
                    toimarkershown = false;
                    toast = "Toilets Disabled";
                } else { addToilets(mMapboxMap);
                toast = "Toilets Enabled";}

                Toast.makeText(getApplicationContext(), toast,Toast.LENGTH_LONG).show();

            }

            //Hide and show stations method
            public void removestation() {
                String toast = "";
                if (stamarkershown && toimarkershown) {
                    mMapboxMap.clear();
                    addUserLocation(mMapboxMap);
                    addToilets(mMapboxMap);
                    stamarkershown = false;
                    toast = "Stations Disabled";
                } else if (stamarkershown && !toimarkershown) {
                    mMapboxMap.clear();
                    addUserLocation(mMapboxMap);
                    stamarkershown = false;
                    toast = "Stations Disabled";
                } else {
                    addStations(mMapboxMap);
                    toast = "Stations Enabled";
                }
                Toast.makeText(getApplicationContext(), toast,Toast.LENGTH_LONG).show();

            }


            //Stations icon making
            private void addStations(MapboxMap mapboxMap) {
                MarkerOptions markerOptions = new MarkerOptions();
                IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
                Drawable iconDrawable = ContextCompat.getDrawable(MapActivity.this, R.drawable.train);
                Icon icon = iconFactory.fromDrawable(iconDrawable);
                String stationname = "";
                JSONArray stations = null;
                if (stationMarkers == null) {
                    stations = RestClient.getStaLoc(-37.877848, 145.044696);
                    stationMarkers = stations;
                } else {
                    stations = stationMarkers;
                }
                ;
                double stalat = 0.0;
                double stalon = 0.0;
                for (int i = 0; i < stations.length(); i++) {
                    try {
                        //Get toilets details from server;
                        stalat = stations.getJSONObject(i).optDouble("lat");
                        stalon = stations.getJSONObject(i).optDouble("lon");
                        stationname = stations.getJSONObject(i).optString("name");
                        LatLng station_position = new LatLng(stalat, stalon);
                        markerOptions.icon(icon);
                        //Fix up marker name formatting to make it more clear these markers are Stations
                        String lastChar = stationname.substring(stationname.length() - 1);
                        if (lastChar.equals(" ")) {
                            stationname += "Train Station";
                        } else {
                            stationname += " Train Station";
                        }

                        markerOptions.title(stationname);
                        markerOptions.position(station_position);
                        mapboxMap.addMarker(markerOptions);
                        stamarkershown = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            //Toilets icon making
            private void addToilets(MapboxMap mapboxMap) {
                MarkerOptions markerOptions = new MarkerOptions();
                IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
                Drawable iconDrawable = ContextCompat.getDrawable(MapActivity.this, R.drawable.toilet);
                Icon icon = iconFactory.fromDrawable(iconDrawable);
                String toiletname = "";
                String address = "";
                JSONArray toilets = null;
                toilets = RestClient.getToiLoc(-37.877848, 145.044696);
//                if (toiletMarkers == null) {
//                    toilets = RestClient.getStaLoc(-37.877848, 145.044696);
//                    toiletMarkers = toilets;
//                } else {
//                    toilets = toiletMarkers;
//                };
                double toilat = 0.0;
                double toilon = 0.0;
                for (int i = 0; i < toilets.length(); i++) {
                    try {
                        //Get toilets details from server;
                        toilat = toilets.getJSONObject(i).optDouble("Latitude");
                        toilon = toilets.getJSONObject(i).optDouble("Longitude");
                        toiletname = "Accessible Toilet";
                        address = toilets.getJSONObject(i).optString("Address1");
                        LatLng toilet_position = new LatLng(toilat, toilon);
                        markerOptions.icon(icon);
                        markerOptions.title(toiletname);
                        markerOptions.snippet(address);
                        markerOptions.position(toilet_position);
                        mapboxMap.addMarker(markerOptions);
                        toimarkershown = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


            private void addUserLocation(MapboxMap mapboxMap) {
                MarkerOptions markerOptions = new MarkerOptions();
                IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);

                // Create an Icon object for the marker to use
                Drawable iconDrawable = ContextCompat.getDrawable(MapActivity.this, R.drawable.star);
                Icon icon = iconFactory.fromDrawable(iconDrawable);
                markerOptions.position(latLng);
                markerOptions.icon(icon);
                markerOptions.title("Your Location");
                markerOptions.snippet("111");
                mapboxMap.addMarker(markerOptions);
            }
        });



    }




    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    //Get user's current location from GPS
    public ArrayList getLocation() {
        ArrayList<Double> list = new ArrayList();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                list.add(latitude);
                list.add(longitude);
            }
        }
        if (list.size() == 0) {
            list.add(-37.8770);
            list.add(145.0442);
        }
        return list;

    }

    //Update child location automatically
    public void updateChildLocationToServer() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List loc = getLocation();
                String childid = DeviceIDGenerator.getID(MapActivity.this);
                String url = "http://13.59.24.178/updateLocation.php?childid=" + childid + "&lat=" + loc.get(0).toString() + "&lon=" + loc.get(1).toString();
                String result = new AsyncTaskRestClient().doInBackground(url);
                Log.e("help", "success");
                handler.postDelayed(this,15000);
            }
        },15000);
    }
}