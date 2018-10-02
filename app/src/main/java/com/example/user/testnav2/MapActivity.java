package com.example.user.testnav2;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.geojson.Point;
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


import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;

import android.support.annotation.NonNull;

import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;


import static com.example.user.testnav2.R.id.emergency;
import static com.example.user.testnav2.R.id.sliderpanelTitleTextView;

public class MapActivity extends AppCompatActivity    implements NavigationView.OnNavigationItemSelectedListener, LocationEngineListener, PermissionsListener {
    //Initialise map
    private SharedPreferences mPreferences;
    private MapboxMap mMapboxMap;
    private MapView mMapView;

    //initialize latitude and longitude, location manager and the address list here
    static final int REQUEST_LOCATION = 1;
    public static double latitude;
    public static double longitude;
    LocationManager locationManager;
    private static boolean toimarkershown = false;
    private static boolean stamarkershown = false;
    private TextView slidepanelTitle;
    private TextView slidepanelSubtitle;
    private TextView slidepanelJourney;
    private ImageView slidepanelImage;
    private ImageView slidepanelNext5;
    private Button slidePanelJourneyButton;
    private Button slidepanelbeginNavButton;
    private Button slidepanelHideRouteButton;
    JSONObject currentChildLocation = null;
    String tempString = null;
    private JSONArray JSONResult;
    private SharedPreferences.Editor mEditor;
    private JSONObject directionResults;
    private NavigationMapRoute currentNavMap;
    private List<DirectionsRoute> currentRoute = new ArrayList<DirectionsRoute>();

    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationLayerPlugin;
    private LocationEngine locationEngine;
    private Location originLocation;


    List<android.location.Address> destination = null;
    private SlidingUpPanelLayout panel;
    String childEmergencyStatus = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this,"pk.eyJ1IjoibWlrZWRzIiwiYSI6ImNqbHpyZmdndjBoMWkzcXBhMmY5amFzYjcifQ.ri5sWryC1uWwkqEM0IwPpg");
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.widget.SearchView msearchview = (android.support.v7.widget.SearchView) findViewById(R.id.searchEditText);
        msearchview.bringToFront();
        msearchview.setSubmitButtonEnabled(true);
        Geocoder gc = new Geocoder(this);

        panel = findViewById(R.id.slidingPanelMapActivity);
        panel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        slidepanelbeginNavButton = findViewById(R.id.sliderpanelNavButton);
        slidepanelHideRouteButton = findViewById(R.id.sliderpanelHideRouteButton);


        msearchview.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try{
                    destination = gc.getFromLocationName(query, 1);
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
                if(destination.size() != 0) {
                    Address temp = destination.get(0);
                    Double templat = temp.getLatitude();
                    Double templon = temp.getLongitude();
                    LatLng templatlon = new LatLng(templat, templon);
                    mMapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(templatlon, 13));

                    MarkerOptions markerOptions = new MarkerOptions();
                    IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
                    Icon icon = iconFactory.fromResource(R.drawable.star);
                    markerOptions.icon(icon);
                    markerOptions.title(query);
                    markerOptions.position(templatlon);
                    mMapboxMap.addMarker(markerOptions);
                }   else {
                    String toast = "Wrong Place, Please enter the correct place.";

                    Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        slidepanelNext5 = findViewById(R.id.imageNext5);

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
                    asyncStationMarkers(lulat,lulon);
                    toimarkershown = false;
                    toast = "Toilets Disabled";
                } else if (toimarkershown && !stamarkershown) {
                    mMapboxMap.clear();
                    addUserLocation(mMapboxMap);
                    toimarkershown = false;
                    toast = "Toilets Disabled";
                } else {
                    asyncToiletMarkers(lulat,lulon);
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
                    asyncToiletMarkers(lulat,lulon);
                    stamarkershown = false;
                    toast = "Stations Disabled";
                } else if (stamarkershown && !toimarkershown) {
                    mMapboxMap.clear();
                    addUserLocation(mMapboxMap);
                    stamarkershown = false;
                    toast = "Stations Disabled";
                } else {
                    asyncStationMarkers(lulat,lulon);
                    stamarkershown = true;
                    toast = "Stations Enabled";
                }
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
            }


            @Override
            public void onMapReady(MapboxMap mapboxMap) {

                Boolean isParent = mPreferences.getBoolean("isParent", false);

                mMapboxMap = mapboxMap;
                if(!isParent) {
                    enableLocationPlugin();
                    mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                    asyncAllMarkers(list.get(0),list.get(1));
                    updateChildLocationToServer();
                }   else {

                    asyncUpdateChildLocationFromServer(mMapboxMap);
                    updateChildLocationFromServer(mMapboxMap);
                    updateChildEmergencyFromServer();
                }

                mMapboxMap.setStyleUrl("mapbox://styles/mikeds/cjlzs6p6c6qk62sqrz30jvhvq");

//                addUserLocation(mMapboxMap);
                toimarkershown = true;
                stamarkershown = true;

                slidepanelTitle = (TextView) findViewById(sliderpanelTitleTextView);
                slidepanelSubtitle = (TextView) findViewById(R.id.sliderpanelSubtitleTextView);
                slidepanelJourney = (TextView) findViewById(R.id.sliderpanelJourneyTextView);
                slidepanelImage = (ImageView) findViewById(R.id.sliderpanelImageView1);
                slidePanelJourneyButton = (Button) findViewById(R.id.sliderpanelJourneyButton);
                slidepanelNext5.setVisibility(View.INVISIBLE);

                //Set up marker button
                mMapboxMap.setOnMarkerClickListener(new com.mapbox.mapboxsdk.maps.MapboxMap.OnMarkerClickListener() {
                                                        @Override
                                                        public boolean onMarkerClick(@NonNull Marker marker) {
                                                            slidepanelTitle.setText(marker.getTitle());
                                                            slidepanelSubtitle.setText(marker.getSnippet());
                                                            slidepanelImage.setImageBitmap(marker.getIcon().getBitmap());

                                                            //Create temporary train icon and compare
                                                            String example = "";
                                                            String footer = " Train Station";
                                                            String stationName = marker.getTitle();
                                                            stationName = stationName.replace(footer, "");
                                                            if (marker.getIcon().getBitmap().sameAs(trainIcon.getBitmap())) {
                                                                slidepanelNext5.setVisibility(View.VISIBLE);
                                                                slidePanelJourneyButton.setText("Show Route");
                                                                slidePanelJourneyButton.setVisibility(View.VISIBLE);
                                                                slidePanelJourneyButton.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        ArrayList<Double> loc = getLocation();
                                                                        LatLng destinationCoord = new LatLng(marker.getPosition());
                                                                        if (currentNavMap != null) {
                                                                            Log.e("currentNavMap","routes have been removed");
                                                                            currentNavMap.removeRoute();
                                                                        } else {
                                                                            currentNavMap = new NavigationMapRoute(null,mMapView,mMapboxMap,R.style.NavigationMapRoute);
                                                                        }
                                                                        getDirections(loc.get(0),loc.get(1),destinationCoord.getLatitude(),destinationCoord.getLongitude());
//                                                                        Point originPoint = Point.fromLngLat(originCoord.getLongitude(),originCoord.getLatitude());
//                                                                        Point destinationPoint = Point.fromLngLat(destinationCoord.getLongitude(),destinationCoord.getLatitude());
//                                                                        getRoute(originPoint,destinationPoint);
                                                                    }
                                                                });
                                                                slidepanelbeginNavButton.setVisibility(View.VISIBLE);
                                                                slidepanelbeginNavButton.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View view) {
                                                                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                                                                .directionsRoute(currentRoute.get(0))
                                                                                .shouldSimulateRoute(true)
                                                                                .build();

                                                                        NavigationLauncher.startNavigation(MapActivity.this,options);
                                                                    }
                                                                });
                                                            } else if (marker.getIcon().getBitmap().sameAs(toiletIcon.getBitmap())) {
                                                                slidePanelJourneyButton.setVisibility(View.VISIBLE);
                                                                slidepanelNext5.setVisibility(View.INVISIBLE);


//                            String url = "http://13.59.24.178/getToiletByID.php/?toiletID=" + marker.getTitle();
//                            Log.e("help", marker.toString());
//                            example = new AsyncTaskRestClient().doInBackground(url);
                                                            } else {
                                                                slidePanelJourneyButton.setVisibility(View.GONE);
                                                                slidepanelNext5.setVisibility(View.INVISIBLE);

                                                                Log.e("help", "Marker is not a train station or toilet");
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
                        } else if (itemID == R.id.tutorial){
                            startActivity(new Intent(MapActivity.this, Tutorial1.class));
                        } else if (itemID == R.id.emergency){
                            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                            builder.setMessage("Sure to send notification to parent?");
                            builder.setTitle("Alert");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    childEmergencyStatus = "1";
                                    asyncUpdateEmergencyStatus(childEmergencyStatus);
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    childEmergencyStatus = "0";
                                }
                            });
                            builder.create().show();
                        } else {

                        }
                        return false;
                    }
                });
            }
        });
    }


    // https://www.mapbox.com/help/android-navigation-sdk/
    //Method logic from here
    private void getAndDisplaySingleRoute(Point oPoint, Point dPoint) {
        Log.e("getRoute method","is happening");

        NavigationRoute.Builder nrb = NavigationRoute.builder(MapActivity.this);
        nrb.accessToken(Mapbox.getAccessToken());
        nrb.origin(oPoint);
        nrb.destination(dPoint);
        nrb.build().getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                Log.e("getRoute method","onResponse method is happening");

                if (response.body() == null) {
                    Log.e("getRoute method", "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.e("getRoute method", "No routes found");
                    return;
                }
                Log.e("getRoute method",response.toString());
                currentRoute.clear();

                currentRoute.add(response.body().routes().get(0));

                if (currentNavMap != null) {
                    currentNavMap.removeRoute();
                } else {
                    currentNavMap = new NavigationMapRoute(null,mMapView,mMapboxMap,R.style.NavigationMapRoute);
                }
                mMapboxMap.deselectMarkers();
                panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                currentNavMap.addRoute(currentRoute.get(0));
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Log.e("getAndDisplaySingleRout",t.toString());

            }
        });
    }

    private void getSingleRoute(Double originLat, Double originLon, Double destLat, Double destLon) {
        Log.e("getSingleRoute","trigggered1");
        Point oPoint = Point.fromLngLat(originLon,originLat);
        Point dPoint = Point.fromLngLat(destLon,destLat);
        NavigationRoute.Builder nrb = NavigationRoute.builder(MapActivity.this);
        nrb.accessToken(Mapbox.getAccessToken());
        nrb.origin(oPoint);
        nrb.destination(dPoint);


        Log.e("getSingleRoute","trigggered2");

        nrb.build().getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                if (currentNavMap == null) {
                    currentNavMap = new NavigationMapRoute(null,mMapView,mMapboxMap,R.style.NavigationMapRoute);
                }

                if (response.body() == null) {
                    return;
                } else if (response.body().routes().size() < 1) {
                    return;
                }
                Log.e("getSingleRoute","trigggered3");
                DirectionsRoute dr;
                dr = response.body().routes().get(0);
                currentRoute.add(dr);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Log.e("getSingleRoute",t.toString());
            }
        });
    }

    private void displayRoutes() {
        if (currentNavMap != null) {
            currentNavMap.removeRoute();
        } else {
            currentNavMap = new NavigationMapRoute(null,mMapView,mMapboxMap,R.style.NavigationMapRoute);
        }
        mMapboxMap.deselectMarkers();
        panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        int i = 0;
        Log.e("displayRoutescurrenRout",currentRoute.toString());
        while (currentRoute.size() > i) {
            currentNavMap.addRoute(currentRoute.get(i));
            i++;
        }
    }

    private void displayRoute() {
        Log.e("displayRoutecurrentrout","1");

        if (currentNavMap != null) {
            currentNavMap.removeRoute();
        } else {
            currentNavMap = new NavigationMapRoute(null,mMapView,mMapboxMap,R.style.NavigationMapRoute);
        }
        Log.e("displayRoutecurrentrout","2");

        mMapboxMap.deselectMarkers();
//        panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        Log.e("displayRoutecurrentrout","3");
        Log.e("currentRoute",currentRoute.toString());

        int i = 0;
        currentNavMap.addRoute(currentRoute.get(i));
        Log.e("displayRoutecurrentrout","4");


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

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        updateDrawerTitle();
//        configureProfileImage();
//        mMapView.onResume();
//        if (mPreferences.getBoolean("isParent",false) && mPreferences.getBoolean("firstTimeRun",false)) {
//            mEditor = mPreferences.edit();
//            mEditor.putBoolean("firstTimeRun",false);
//            mEditor.apply();
//            MapActivity.this.recreate();
//        }
//        Log.e("help","onResume triggered");
//    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
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
            list.add(-37.932438);
            list.add(145.082474);
        }
        return list;
    }

    //Begins ToiletMarkersAsyncTask in background
    public void asyncToiletMarkers(Double lat, Double lon) {
        MapActivity.ToiletMarkersAsyncTask t = new MapActivity.ToiletMarkersAsyncTask(this);
        t.getClosestToilets(lat,lon);
    }

    //Populates toilet markers
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
        int emergencyStatus;
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

    public void getDirections (Double originLat, Double originLon, Double destLat, Double destLon) {
        MapActivity.GetDirectionsAsyncTask t = new MapActivity.GetDirectionsAsyncTask();
        t.startTask(originLat,originLon,destLat,destLon);
    }

    public class GetDirectionsAsyncTask extends AsyncTask<Void,Void,String>{
        private Double oLat;
        private Double oLon;
        private Double dLat;
        private Double dLon;
        private String tempString;

        protected void startTask(Double originLat, Double originLon, Double destLat, Double destLon){
            oLat = originLat;
            oLon = originLon;
            dLat = destLat;
            dLon = destLon;
            execute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String urls = "https://maps.googleapis.com/maps/api/directions/json?origin=" + oLat.toString() + "," + oLon.toString() + "&destination=" + dLat.toString() + "," + dLon.toString() + "&key=AIzaSyDYwDoRvvpDt9FP8WAgPv0s2wfURayOyDk&mode=transit";
            Log.e("urls",urls);
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
            return tempString;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                directionResults = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayList<JSONObject> al = configureDirections(directionResults);
            assert al != null;
            getManyRoutes(al);
            if (currentNavMap != null) {
                currentNavMap.removeRoute();
            } else {
                currentNavMap = new NavigationMapRoute(null,mMapView,mMapboxMap,R.style.NavigationMapRoute);
            }
            if (currentRoute.size()> 0) {
                currentNavMap.addRoutes(currentRoute);
            }
        }
    }

    private ArrayList<JSONObject> configureDirections(JSONObject results) {
//        String arrivalTime;
//        String departureTime;
//        String distance;
//        String duration;
//        String endAddress;
//        String endLocation;
//        String startAddress;
//        String startLocation;
        Log.e("configureDirections","triggers");

        try {
            JSONArray routes = results.getJSONArray("routes");
            Log.e("results",results.toString());
            Log.e("routes",routes.toString());
            JSONObject theRoute = routes.getJSONObject(0);
            JSONObject legs = theRoute.getJSONArray("legs").getJSONObject(0);

//            arrivalTime = legs.getJSONObject("arrival_time").getString("text");
//            departureTime = legs.getJSONObject("departure_time").getString("text");
//            distance = legs.getJSONObject("distance").getString("text");
//            duration = legs.getJSONObject("duration").getString("text");
//            endAddress = legs.getString("end_address");
//            endLocation = legs.getJSONObject("end_location").getString("lat") + " , " + legs.getJSONObject("end_location").getString("lng");
//            startAddress = legs.getString("start_address");
//            startLocation = legs.getJSONObject("start_location").getString("lat") + " , " + legs.getJSONObject("end_location").getString("lng");
//
//            String example = "Arrival Time: " + arrivalTime + ". Departure Time: " + departureTime + ". Distance: " + distance + ". Duration" + duration;
//            example += ". Start Location: " + startAddress + startLocation +  ". End Location: " + endAddress + endLocation;

            JSONArray steps = legs.getJSONArray("steps");
            ArrayList<JSONObject> aList = new ArrayList<>();
            int i = 0;

            Log.e("Line 1137","triggered");
            while (i<steps.length()) {
                aList.add(steps.getJSONObject(i));
                i++;
            }
            Log.e("line 1142","triggered");
            i = 0;
            Log.e("travelmode",aList.get(i).getString("travel_mode"));
            Log.e("aList size", String.valueOf(aList.size()));
            Log.e("aList",aList.get(0).getString("travel_mode"));

            //aList current holds each step of the journey as an entry of JSONObject

            return aList;
//            displayRoute();


//            TextView tv = findViewById(R.id.textView1);
//            tv.setText(example);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getManyRoutes(ArrayList<JSONObject> aList) {
        int i = 0;
        final int size = aList.size();

        while (i<aList.size()) {
            try {
                if (aList.get(i).getString("travel_mode").equals("WALKING")) {
                    Log.e("aList", "triggers");

                    getSingleRoute(aList.get(i).getJSONObject("start_location").getDouble("lat"),
                            aList.get(i).getJSONObject("start_location").getDouble("lng"),
                            aList.get(i).getJSONObject("end_location").getDouble("lat"),
                            aList.get(i).getJSONObject("end_location").getDouble("lng"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i++;
        }

    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine();
            // Create an instance of the plugin. Adding in LocationLayerOptions is also an optional
            // parameter
            LocationLayerPlugin locationLayerPlugin = new LocationLayerPlugin(mMapView, mMapboxMap);

            // Set the plugin's camera mode
            locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
            getLifecycle().addObserver(locationLayerPlugin);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void setCameraPosition(Location location) {
        mMapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 13));
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
    }


    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            originLocation = location;
            setCameraPosition(location);
            locationEngine.removeLocationEngineListener(this);
        }
    }


//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (locationEngine != null) {
//            locationEngine.removeLocationUpdates();
//        }
//        if (locationPlugin != null) {
//            locationPlugin.onStop();
//        }
//        mMapView.onStop();
//    }


    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mMapView.onDestroy();
//        if (locationEngine != null) {
//            locationEngine.deactivate();
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    //Update child emergency status to server
    public void asyncUpdateEmergencyStatus(String emergency) {
        MapActivity.emergencyContact t = new MapActivity.emergencyContact(this);

        t.emergencyUpdate(emergency);

    }

    public class emergencyContact extends AsyncTask<Void,Void,Void> {

        private WeakReference<MapActivity> activityWeakReference;
        private String urls;
        emergencyContact(MapActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        protected void emergencyUpdate(String emergence) {
            String childid = DeviceIDGenerator.getID(MapActivity.this);
            urls = "http://13.59.24.178/emergencyOn.php?childID=" + childid + "emergency" + emergence;
            execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("emergencyContact", "doInBackground triggered");
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

    //update child emergency status from server

    public class updateEmergenceFromServer extends AsyncTask<Void,Void,Void> {

        private WeakReference<MapActivity> activityWeakReference;
        private String urls;
        updateEmergenceFromServer(MapActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        protected void updateEmergency() {
            String parentid = DeviceIDGenerator.getID(MapActivity.this);
            urls = "http://13.59.24.178/emergencyOn.php?parentID=" + parentid;
            execute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("doInBackground", "UpdateChildEmergencyStatusFromServer");
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
            if(tempString.equals(1)){
                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                builder.setTitle("Alert");
                builder.setMessage("You child send emergency notification.");
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
        }
    }

    public void updateChildEmergencyFromServer(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                asyncUpdateChildEmergencyFromServer();
                handler.postDelayed(this,15000);
            }
        },15000);
    }

    private void asyncUpdateChildEmergencyFromServer() {
        MapActivity.updateEmergenceFromServer t = new MapActivity.updateEmergenceFromServer(this);
        t.updateEmergency();
    }


};


