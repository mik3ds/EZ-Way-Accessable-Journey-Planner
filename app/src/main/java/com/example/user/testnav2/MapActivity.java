package com.example.user.testnav2;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.user.testnav2.R.id.sliderpanelTitleTextView;

public class MapActivity extends AppCompatActivity    implements NavigationView.OnNavigationItemSelectedListener {
    //Initialise map
    private SharedPreferences mPreferences;
    private MapboxMap mMapboxMap;
    private MapView mMapView;
//    ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();

    //initialize latitude and longitude, location manager and the address list here
    static final int REQUEST_LOCATION = 1;
    public static double latitude;
    public static double longitude;
    LocationManager locationManager;
    List<Address> addressList = null;
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
    JSONArray stations = null;
    JSONObject currentChildLocation = null;
    String tempString = null;
    private JSONArray JSONResult;
    private Boolean linkedChild;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,"pk.eyJ1IjoibWlrZWRzIiwiYSI6ImNqbHpyZmdndjBoMWkzcXBhMmY5amFzYjcifQ.ri5sWryC1uWwkqEM0IwPpg");
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        mMapView = (MapView) findViewById(R.id.mapquestMapView);
        mMapView.onCreate(savedInstanceState);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String location = "";
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        configureProfileImage();

        //change the title of navigation drawer to username
        updateDrawerTitle();

        ArrayList<Double> list = getLocation();
        Double lulat = list.get(0);
        Double lulon = list.get(1);


        //USER LOCATION
        final LatLng latLng = new LatLng(lulat, lulon);

        final IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
        final Icon trainIcon = iconFactory.fromResource(R.drawable.train);
        final Icon toiletIcon = iconFactory.fromResource(R.drawable.toilet);

        mMapView.getMapAsync(new OnMapReadyCallback() {

            public void removetoilets() {

                String toast = "";
                if (toimarkershown && stamarkershown) {
                    mMapboxMap.clear();
                    addUserLocation(mMapboxMap);
                    asyncStationMarkers(-37.877848,145.034677);
                    toimarkershown = false;
                    toast = "Toilets Disabled";
                } else if (toimarkershown && !stamarkershown) {
                    mMapboxMap.clear();
                    addUserLocation(mMapboxMap);
                    toimarkershown = false;
                    toast = "Toilets Disabled";
                } else {
                    asyncToiletMarkers(-37.877848,145.034677);
                    toimarkershown = true;
                    toast = "Toilets Enabled";
                }
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
            }

            //Hide and show stations method
            public void removestation() {
                String toast = "";
                if (stamarkershown && toimarkershown) {
                    mMapboxMap.clear();
                    addUserLocation(mMapboxMap);
                    asyncToiletMarkers(-37.877848,145.034677);
                    stamarkershown = false;
                    toast = "Stations Disabled";
                } else if (stamarkershown && !toimarkershown) {
                    mMapboxMap.clear();
                    addUserLocation(mMapboxMap);
                    stamarkershown = false;
                    toast = "Stations Disabled";
                } else {
                    asyncStationMarkers(-37.877848,145.034677);
                    stamarkershown = true;
                    toast = "Stations Enabled";
                }
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
            }


            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                mMapboxMap = mapboxMap;
                mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                mMapboxMap.setStyleUrl("mapbox://styles/mikeds/cjlzs6p6c6qk62sqrz30jvhvq");

                Boolean isParent = mPreferences.getBoolean("isParent", false);
                if (!isParent) {
                    asyncAllMarkers(list.get(0),list.get(1));
                    updateChildLocationToServer();
                }
                addUserLocation(mMapboxMap);
                toimarkershown = true;
                stamarkershown = true;

                //Set up marker button
                mMapboxMap.setOnMarkerClickListener(new com.mapbox.mapboxsdk.maps.MapboxMap.OnMarkerClickListener() {
                                                        @Override
                                                        public boolean onMarkerClick(@NonNull Marker marker) {
                                                            slidepanelTitle = (TextView) findViewById(sliderpanelTitleTextView);
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
                                                            stationName = stationName.replace(footer, "");
                                                            if (marker.getIcon().getBitmap().sameAs(trainIcon.getBitmap())) {
                                                                String url = "http://13.59.24.178/getStationByID.php/?stationname=" + stationName;
                                                                Log.e("help", marker.toString());
                                                                example = new AsyncTaskRestClient().doInBackground(url);

                                                            } else if (marker.getIcon().getBitmap().sameAs(toiletIcon.getBitmap())) {

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

                NavigationView nv = (NavigationView) findViewById(R.id.nav_view1);
                nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int itemID = item.getItemId();
                        Log.e("help", Integer.toString(itemID));

                        if (itemID == R.id.nav_profile) {
                            startActivity(new Intent(MapActivity.this, ProfileActivity.class));
                        } else if (itemID == R.id.nav_trackchild) {
                            startActivity(new Intent(MapActivity.this, TrackingChildActivity.class));
                        } else if (itemID == R.id.nav_trackparent) {
                            startActivity(new Intent(MapActivity.this, TrackingParentActivity.class));
                        } else if (itemID == R.id.nav_station) {
                            removestation();
                        } else if (itemID == R.id.nav_toilet) {
                            removetoilets();
                        } else {

                        }

                        return false;
                    }
                });
            }
        });
    }

    private void configureProfileImage() {
        NavigationView nv = (NavigationView) findViewById(R.id.nav_view1);
        View v = nv.getHeaderView(0);
        ImageView profileDisplay = (ImageView) v.findViewById(R.id.drawerHeaderProfilePic);
        String gender = mPreferences.getString("gender","f");
        if (gender.equals("m")) {
            profileDisplay.setBackgroundResource(R.drawable.boy);
        } else {
            profileDisplay.setBackgroundResource(R.drawable.girl);
        }
    }

    private void addChildLocationFromPref() {
        IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
        Icon ic = iconFactory.fromResource(R.drawable.childicon);
        Double chlat = Double.parseDouble(mPreferences.getString("childlat", "0.0"));
        Double chlon = Double.parseDouble(mPreferences.getString("childlon", "0.0"));
        LatLng childLatLng = new LatLng(chlat,chlon);
        MarkerOptions mo = new MarkerOptions();
        mo.title("Child Location");
        mo.icon(ic);
        mo.position(childLatLng);
        LatLng empty = new LatLng(0.0,0.0);

        if (empty != childLatLng) {

            mMapboxMap.addMarker(mo);
            Log.e("Child added to map", mo.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDrawerTitle();
        configureProfileImage();

        if (mPreferences.getBoolean("isParent",false) && !mPreferences.getBoolean("refreshStatus",false)) {
            mEditor = mPreferences.edit();
            mEditor.putBoolean("refreshStatus",true);
            mEditor.apply();
            MapActivity.this.recreate();
        }

        Log.e("help","resume happened");

    }

    private void updateDrawerTitle() {

        String tempTitle = mPreferences.getString(getString(R.string.username), "Guest");

        NavigationView nv = findViewById(R.id.nav_view1);
        View hv = nv.getHeaderView(0);
        TextView tv = (TextView) hv.findViewById(R.id.drawer_title);
        tv.setText(tempTitle);

    }

    @Override
    public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            return false;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
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
            list.add(-37.877848);
            list.add(145.034677);
        }
        return list;
    }

    public void asyncToiletMarkers(Double lat, Double lon) {
        MapActivity.ToiletMarkersAsyncTask t = new MapActivity.ToiletMarkersAsyncTask(this);
        t.getClosestToilets(lat,lon);
    }

    public boolean onMarkerClick(@NonNull Marker marker, @NonNull View view, @NonNull com.mapbox.mapboxsdk.maps.MapboxMap.MarkerViewAdapter markerViewAdapter) {
        TextView title = (TextView) findViewById(R.id.sliderpanelTitleTextView);
        title.setText(marker.getTitle());
        return false;

    }

    public class ToiletMarkersAsyncTask extends AsyncTask<Void,Void,Void> {

        private WeakReference<MapActivity> activityWeakReference;
        private String urls = "http://13.59.24.178/";
        ToiletMarkersAsyncTask(MapActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        protected void getClosestToilets(Double lat, Double lon) {
            urls += "nearbyToilets.php?lat=" + lat + "&lon=" + lon;
            Log.e("toilet", urls.toString());
            execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("ToiletMarkersAsyncTask", "doInBackground triggered");
            JSONResult = new JSONArray();
            try{
                URL url = new URL(urls);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }
                urlConnection.disconnect();
                tempString = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally
            {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if (tempString == null) {
                tempString = "EMPTY";
            }
            Log.e("Number One", tempString);
            Log.e("Number Two", JSONResult.toString());

            try {
                JSONResult = new JSONArray(tempString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            MarkerOptions markerOptions = new MarkerOptions();
            IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
            Drawable iconDrawable = ContextCompat.getDrawable(MapActivity.this, R.drawable.toilet);
            Icon toileticon = iconFactory.fromResource(R.drawable.toilet);
            String toiletname = "";
            String address = "";
            JSONArray toilets = JSONResult;
            Log.e("help", JSONResult.toString());
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
                    markerOptions.icon(toileticon);
                    markerOptions.title(toiletname);
                    markerOptions.snippet(address);
                    markerOptions.position(toilet_position);
                    mMapboxMap.addMarker(markerOptions);
                    toimarkershown = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void asyncStationMarkers(Double lat, Double lon) {
        MapActivity.StationMarkersAsyncTask t = new MapActivity.StationMarkersAsyncTask(this);
        t.getClosestStations(lat,lon);
    }

    public class StationMarkersAsyncTask extends AsyncTask<Void,Void,Void> {

        private WeakReference<MapActivity> activityWeakReference;
        private String urls = "http://13.59.24.178/";
        StationMarkersAsyncTask(MapActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
    }

        protected void getClosestStations(Double lat, Double lon) {
            urls += "nearbyStations.php?lat=" + lat + "&lon=" + lon;
            Log.e("toilet", urls.toString());
            execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("StationMarkersAsyncTask", "doInBackground triggered");
            JSONResult = new JSONArray();
            try{
                URL url = new URL(urls);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }
                urlConnection.disconnect();
                tempString = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally
            {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if (tempString == null) {
                tempString = "EMPTY";
            }
            Log.e("Number One", tempString);
            Log.e("Number Two", JSONResult.toString());

            try {
                JSONResult = new JSONArray(tempString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            MarkerOptions markerOptions = new MarkerOptions();
            IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
            Drawable iconDrawable = ContextCompat.getDrawable(MapActivity.this, R.drawable.train);
            Icon stationicon = iconFactory.fromResource(R.drawable.train);
            String stationname = "";
            String route = "";
            JSONArray stations = JSONResult;
            Log.e("help", JSONResult.toString());
            double stalat = 0.0;
            double stalon = 0.0;
            for (int i = 0; i < stations.length(); i++) {
                try {
                    //Get toilets details from server;
                    stalat = stations.getJSONObject(i).optDouble("lat");
                    stalon = stations.getJSONObject(i).optDouble("lon");
                    stationname = stations.getJSONObject(i).optString("name");
                    route = stations.getJSONObject(i).optString("routes");
                    LatLng sta_position = new LatLng(stalat, stalon);
                    markerOptions.icon(stationicon);
                    markerOptions.title(stationname);
                    markerOptions.snippet(route);
                    markerOptions.position(sta_position);
                    mMapboxMap.addMarker(markerOptions);
                    stamarkershown = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }



        }
    }

    public void asyncAllMarkers(Double lat, Double lon) {
        MapActivity.AllMarkersAsyncTask t = new MapActivity.AllMarkersAsyncTask(this);
        t.getClosestMarkers(lat,lon);
    }

    public class AllMarkersAsyncTask extends AsyncTask<Void,Void,Void>{
        private WeakReference<MapActivity> activityWeakReference;
        private String urls = "http://13.59.24.178/";
        private String url1;
        private String url2;

        private String stationdata;
        private String toiletdata;

        private JSONArray JSONResult1;
        private JSONArray JSONResult2;


        AllMarkersAsyncTask(MapActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        public void getClosestMarkers(Double lat, Double lon) {
            url1 = urls + "nearbyStations.php?lat=" + lat + "&lon=" + lon;
            url2 = urls + "nearbyToilets.php?lat=" + lat + "&lon=" + lon;
            execute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.e("All Markers", "doInBackground triggered");
            JSONResult1 = new JSONArray();
            try{
                Log.e("url1", url1);
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }
                urlConnection.disconnect();
                stationdata = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("stationdata", stationdata.toString());

            JSONResult2 = new JSONArray();
            try{
                URL url = new URL(url2);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }
                urlConnection.disconnect();
                toiletdata = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("toiletdata", toiletdata.toString());

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

            try {
                JSONResult1 = new JSONArray(stationdata);
                JSONResult2 = new JSONArray(toiletdata);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            MarkerOptions markerOptions = new MarkerOptions();
            IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
            Icon stationicon = iconFactory.fromResource(R.drawable.train);
            String stationname = "";
            String route = "";
            JSONArray stations = JSONResult1;
            Log.e("help", JSONResult1.toString());
            double stalat = 0.0;
            double stalon = 0.0;
            for (int i = 0; i < stations.length(); i++) {
                try {
                    //Get toilets details from server;
                    stalat = stations.getJSONObject(i).optDouble("lat");
                    stalon = stations.getJSONObject(i).optDouble("lon");
                    stationname = stations.getJSONObject(i).optString("name");
                    route = stations.getJSONObject(i).optString("routes");
                    LatLng sta_position = new LatLng(stalat, stalon);
                    markerOptions.icon(stationicon);
                    markerOptions.title(stationname);
                    markerOptions.snippet(route);
                    markerOptions.position(sta_position);
                    mMapboxMap.addMarker(markerOptions);
                    stamarkershown = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            MarkerOptions markerOptions2 = new MarkerOptions();
            IconFactory iconFactory2 = IconFactory.getInstance(MapActivity.this);
            Icon toileticon = iconFactory2.fromResource(R.drawable.toilet);
            String toiletname = "";
            String address = "";
            JSONArray toilets = JSONResult2;
            Log.e("help", JSONResult2.toString());
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
                    markerOptions2.icon(toileticon);
                    markerOptions2.title(toiletname);
                    markerOptions2.snippet(address);
                    markerOptions2.position(toilet_position);

                    mMapboxMap.addMarker(markerOptions2);
                    toimarkershown = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    //Update child location automatically
    public void updateChildLocationToServer() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<Double> temp = getLocation();
                asyncUpdateChildLocationToServer(temp.get(0), temp.get(1));
                handler.postDelayed(this,15000);
            }
        },15000);
    }

    public void asyncUpdateChildLocationToServer(Double lat, Double lon) {
        MapActivity.UpdateLocationAsyncTask t = new MapActivity.UpdateLocationAsyncTask(this);
        t.updateLocation(lat,lon);

    }

    public class UpdateLocationAsyncTask extends AsyncTask<Void,Void,Void> {

        private WeakReference<MapActivity> activityWeakReference;
        private String urls;
        UpdateLocationAsyncTask(MapActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        protected void updateLocation(Double lat, Double lon) {
            String childid = DeviceIDGenerator.getID(MapActivity.this);
            urls = "http://13.59.24.178/updateLocation.php?childid=" + childid + "&lat=" + lat + "&lon=" + lon;
            execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("UpdateLocationAsyncTask", "doInBackground triggered");
            JSONResult = new JSONArray();
            try{
                URL url = new URL(urls);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }
                urlConnection.disconnect();
                tempString = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally
            {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }
    }

    public void updateChildLocationFromServer(final MapboxMap mapboxMap){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<Double> temp = getLocation();
                asyncUpdateChildLocationFromServer(mapboxMap);
                handler.postDelayed(this,15000);
            }
        },15000);
    }

    private void asyncUpdateChildLocationFromServer(MapboxMap mapboxMap) {
        MapActivity.GetChildLocationAsyncTask t = new MapActivity.GetChildLocationAsyncTask(this);
        t.updateChildLocation(mapboxMap);
    }


    public class GetChildLocationAsyncTask extends AsyncTask<Void,Void,Void>{

        MapboxMap currentMap = null;
        private WeakReference<MapActivity> activityWeakReference;
        GetChildLocationAsyncTask(MapActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        final String deviceID = DeviceIDGenerator.getID(MapActivity.this);
        final String urls = "http://13.59.24.178/getChildLocation.php?parentid=" + deviceID;
        final String example = "[]";
        JSONArray ja = null;
        JSONObject jo = null;
        String tempString = "";

        protected void updateChildLocation(MapboxMap mapboxMap) {
            currentMap = mapboxMap;
            execute();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            Log.e("GetChildLocationAsyncTa","get child location in background");
            try{
                URL url = new URL(urls);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }
                urlConnection.disconnect();
                tempString = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void v) {

            if (tempString.equals(example) && mPreferences.getBoolean("isParent",false)) {
                mEditor = mPreferences.edit();
                mEditor.putBoolean("isParent", false);
                mEditor.apply();
                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                builder.setTitle("Alert");
                builder.setMessage("You have been unpaired with your child. Tracking will no longer function.");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MapActivity.this.recreate();
                    }
                });
                AlertDialog ad = builder.create();
                ad.show();
            }

            try {
                ja = new JSONArray(tempString);
                jo = ja.getJSONObject(0);
                currentChildLocation = jo;
                updateChildMarker(currentMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateChildMarker(MapboxMap mapboxMap) throws JSONException {
        mapboxMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
        Drawable iconDrawable = ContextCompat.getDrawable(MapActivity.this, R.drawable.childicon);
        Icon icon = iconFactory.fromResource(R.drawable.childicon);
        markerOptions.setIcon(icon);
        markerOptions.setTitle(currentChildLocation.getString("name"));
        markerOptions.setSnippet(currentChildLocation.getString("details"));
        LatLng childLatLng = new LatLng(currentChildLocation.getDouble("childLat"),currentChildLocation.getDouble("childLon"));
        markerOptions.position(childLatLng);
        mapboxMap.addMarker(markerOptions);
        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(childLatLng, 15));

    }

    public void addUserLocation(MapboxMap m){
        Log.e("addUserLocation","happened");
        ArrayList<Double> list = new ArrayList();
        list = getLocation();
        MarkerOptions markerOptions = new MarkerOptions();
        IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
        Icon icon = iconFactory.fromResource(R.drawable.star);
        latitude = list.get(0);
        longitude = list.get(1);
        LatLng userloc = new LatLng(latitude, longitude);
        markerOptions.title("User location");
        markerOptions.icon(icon);
        markerOptions.position(userloc);
        m.addMarker(markerOptions);

        if (mPreferences.getBoolean("isParent",false)) {
            asyncUpdateChildLocationFromServer(m);
            updateChildLocationFromServer(m);
        }


    }
};


