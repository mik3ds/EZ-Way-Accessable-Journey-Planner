package com.example.user.testnav2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewManager;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.maps.MapboxMap;
import com.mapquest.mapping.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

import static com.example.user.testnav2.R.id.sliderpanelTitleTextView;

public class Main3Activity extends AppCompatActivity    implements NavigationView.OnNavigationItemSelectedListener {
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
    String tempString = null;
    private JSONArray JSONResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
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
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.toiletshidden);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.stationshidden);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        //change the title of navigation drawer to username
        updateDrawerTitle();

        ArrayList<Double> list = getLocation();
//        list = LU.getLocation();
        Double lulat = list.get(0);
        Double lulon = list.get(1);


        asyncAllMarkers(list.get(0),list.get(1));


        //USER LOCATION
        final LatLng latLng = new LatLng(lulat, lulon);

        final IconFactory iconFactory = IconFactory.getInstance(Main3Activity.this);
        Drawable iconDrawable = ContextCompat.getDrawable(Main3Activity.this, R.drawable.train);
        final Icon trainIcon = iconFactory.fromDrawable(iconDrawable);
        iconDrawable = ContextCompat.getDrawable(Main3Activity.this, R.drawable.toilet);
        final Icon toiletIcon = iconFactory.fromDrawable(iconDrawable);


        mMapView.getMapAsync(new OnMapReadyCallback() {

            public void removetoilets() {

                String toast = "";



                if (toimarkershown && stamarkershown) {
                    mMapboxMap.clear();
                    addUserLocation();
                    asyncStationMarkers(-37.877848,145.034677);
                    toimarkershown = false;
                    toast = "Toilets Disabled";
                } else if (toimarkershown && !stamarkershown) {
                    mMapboxMap.clear();
                    addUserLocation();
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
                    addUserLocation();
                    asyncToiletMarkers(-37.877848,145.034677);
                    stamarkershown = false;
                    toast = "Stations Disabled";
                } else if (stamarkershown && !toimarkershown) {
                    mMapboxMap.clear();
                    addUserLocation();
                    stamarkershown = false;
                    toast = "Stations Disabled";
                } else {
                    asyncStationMarkers(-37.877848,145.034677);
                    stamarkershown = true;
                    toast = "Stations Enabled";
                }
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();

            }

            public void addUserLocation(){
                ArrayList<Double> list = new ArrayList();
                list = getLocation();
                MarkerOptions markerOptions = new MarkerOptions();
                IconFactory iconFactory = IconFactory.getInstance(Main3Activity.this);
                Drawable iconDrawable = ContextCompat.getDrawable(Main3Activity.this, R.drawable.star);
                Icon icon = iconFactory.fromDrawable(iconDrawable);
                latitude = list.get(0);
                longitude = list.get(1);
                LatLng userloc = new LatLng(latitude, longitude);
                markerOptions.title("User location");
                markerOptions.icon(icon);
                markerOptions.position(userloc);
                mMapboxMap.addMarker(markerOptions);

                if (mPreferences.getString("childlat", "").length() > 1) {

                    Drawable starIcon = ContextCompat.getDrawable(Main3Activity.this,R.drawable.childicon);
                    Icon ic = iconFactory.fromDrawable(starIcon);
                    Double chlat = Double.parseDouble(mPreferences.getString("childlat", "0.0"));
                    Double chlon = Double.parseDouble(mPreferences.getString("childlon", "0.0"));
                    LatLng childLatLng = new LatLng(chlat,chlon);
                    MarkerOptions mo = new MarkerOptions();
                    mo.title("Child Location");
                    mo.icon(ic);
                    mo.position(childLatLng);
                    mMapboxMap.addMarker(mo);


                }


            }

            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                mMapboxMap = mapboxMap;

                mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                mMapboxMap.setStyleUrl("mapbox://styles/mikeds/cjlzs6p6c6qk62sqrz30jvhvq");

//                asyncToiletMarkers(-37.877848,145.034677);
//                asyncStationMarkers(-37.877848,145.034677);
                addUserLocation();
                toimarkershown = true;
                stamarkershown = true;




                Boolean isParent = mPreferences.getBoolean("isParent", true);
                Log.e("help", "line 149 triggers");
                if (!isParent) {
                    Log.e("help", "line 150 triggers");
                    //  updateChildLocationToServer();
                }


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
                NavigationView nv = (NavigationView) findViewById(R.id.nav_view1);

                nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int itemID = item.getItemId();
                        Log.e("help", Integer.toString(itemID));

                        if (itemID == R.id.nav_profile) {
                            startActivity(new Intent(Main3Activity.this, Main1Activity.class));
                        } else if (itemID == R.id.nav_trackchild) {
                            startActivity(new Intent(Main3Activity.this, Main1Activity.class));
                        } else if (itemID == R.id.nav_trackparent) {
                            startActivity(new Intent(Main3Activity.this, Main2Activity.class));

                        } else if (itemID == R.id.nav_station) {
                            removestation();

                        } else if (itemID == R.id.nav_toilet) {
                            removetoilets();
                        } else {}


                        return false;
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDrawerTitle();
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

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_camera) {
                // Handle the camera action
            } else if (id == R.id.nav_gallery) {

            } else if (id == R.id.nav_slideshow) {

            } else if (id == R.id.nav_manage) {

            } else if (id == R.id.nav_share) {

            } else if (id == R.id.nav_send) {

            }

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
        Main3Activity.ToiletMarkersAsyncTask t = new Main3Activity.ToiletMarkersAsyncTask(this);
        t.getClosestToilets(lat,lon);
    }


    public boolean onMarkerClick(@NonNull Marker marker, @NonNull View view, @NonNull com.mapbox.mapboxsdk.maps.MapboxMap.MarkerViewAdapter markerViewAdapter) {
        TextView title = (TextView) findViewById(R.id.sliderpanelTitleTextView);
        title.setText(marker.getTitle());
        return false;

    }

    public class ToiletMarkersAsyncTask extends AsyncTask<Void,Void,Void> {

        private WeakReference<Main3Activity> activityWeakReference;
        private String urls = "http://13.59.24.178/";
        ToiletMarkersAsyncTask(Main3Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        protected void getClosestToilets(Double lat, Double lon) {
            urls += "nearbyToilets.php?lat=" + lat + "&lon=" + lon;
            Log.e("toilet", urls.toString());
            execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("help", "doInBackground triggered");
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
            IconFactory iconFactory = IconFactory.getInstance(Main3Activity.this);
            Drawable iconDrawable = ContextCompat.getDrawable(Main3Activity.this, R.drawable.toilet);
            Icon toileticon = iconFactory.fromDrawable(iconDrawable);
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
        Main3Activity.StationMarkersAsyncTask t = new Main3Activity.StationMarkersAsyncTask(this);
        t.getClosestStations(lat,lon);
    }


    public class StationMarkersAsyncTask extends AsyncTask<Void,Void,Void> {

        private WeakReference<Main3Activity> activityWeakReference;
        private String urls = "http://13.59.24.178/";
        StationMarkersAsyncTask(Main3Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        protected void getClosestStations(Double lat, Double lon) {
            urls += "nearbyStations.php?lat=" + lat + "&lon=" + lon;
            Log.e("toilet", urls.toString());
            execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("help", "doInBackground triggered");
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
            IconFactory iconFactory = IconFactory.getInstance(Main3Activity.this);
            Drawable iconDrawable = ContextCompat.getDrawable(Main3Activity.this, R.drawable.train);
            Icon stationicon = iconFactory.fromDrawable(iconDrawable);
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
        Main3Activity.AllMarkersAsyncTask t = new Main3Activity.AllMarkersAsyncTask(this);
        t.getClosestMarkers(lat,lon);
    }

    public class AllMarkersAsyncTask extends AsyncTask<Void,Void,Void>{
        private WeakReference<Main3Activity> activityWeakReference;
        private String urls = "http://13.59.24.178/";
        private String url1;
        private String url2;

        private String stationdata;
        private String toiletdata;

        private JSONArray JSONResult1;
        private JSONArray JSONResult2;


        AllMarkersAsyncTask(Main3Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        public void getClosestMarkers(Double lat, Double lon) {
            url1 = urls + "nearbyStations.php?lat=" + lat + "&lon=" + lon;
            url2 = urls + "nearbyToilets.php?lat=" + lat + "&lon=" + lon;
            execute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.e("help", "all Markers doInBackground triggered");
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
            IconFactory iconFactory = IconFactory.getInstance(Main3Activity.this);
            Drawable iconDrawable = ContextCompat.getDrawable(Main3Activity.this, R.drawable.train);
            Icon stationicon = iconFactory.fromDrawable(iconDrawable);
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
            IconFactory iconFactory2 = IconFactory.getInstance(Main3Activity.this);
            Drawable iconDrawable2 = ContextCompat.getDrawable(Main3Activity.this, R.drawable.toilet);
            Icon toileticon = iconFactory2.fromDrawable(iconDrawable2);
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
                List loc = getLocation();
                String childid = DeviceIDGenerator.getID(Main3Activity.this);
                String url = "http://13.59.24.178/updateLocation.php?childid=" + childid + "&lat=" + loc.get(0).toString() + "&lon=" + loc.get(1).toString();
                String result = new AsyncTaskRestClient().doInBackground(url);
                Log.e("help", "success");
                handler.postDelayed(this,15000);
            }
        },15000);
    }

    };


