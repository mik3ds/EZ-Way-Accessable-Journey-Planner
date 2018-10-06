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
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuInflater;
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

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
import static com.example.user.testnav2.R.id.sliderpanelJourneyTextView;
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

    private boolean isJourneyCurrentlyShowing;

    private TextView slidepanelTitle;
    private TextView slidepanelSubtitle;
    private TextView slidepanelJourney;
    private TextView slidepanelJourneyText;
    private TextView slidepanelJourneyToText;
    private TextView slidepanelArriveAtText;
    private TextView slidepanelArriveTimeText;
    private TextView slidepanelDepartAtText;
    private TextView slidepanelDepartureTimeText;
    private ImageView slidepanelImage;
    private Button slidePanelJourneyButton;
    private Button slidepanelbeginNavButton;
    private Button slidepanelHideRouteButton;
    private Button slidepanelStepByStep;
    private Button slidepanelExitRoute;

    JSONObject currentChildLocation = null;
    String tempString = null;
    private JSONArray JSONResult;
    private SharedPreferences.Editor mEditor;
    private JSONObject directionResults;
    private NavigationMapRoute currentNavMap;
    private List<DirectionsRoute> currentRoute = new ArrayList<DirectionsRoute>();


    private Double lulat;
    private Double lulon;

    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationLayerPlugin;
    private LocationEngine locationEngine;
    private Location originLocation;

    List<android.location.Address> destination = null;
    private SlidingUpPanelLayout panel;
    String childEmergencyStatus = "0";
    private LatLng childLatLng;


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
        isJourneyCurrentlyShowing = false;
        panel = findViewById(R.id.slidingPanelMapActivity);
        panel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        slidepanelbeginNavButton = findViewById(R.id.sliderpanelNavButton);
        slidepanelExitRoute = findViewById(R.id.sliderpanelExitRoute);
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
                    Icon icon = iconFactory.fromResource(R.drawable.placeholder);
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

        slidepanelJourneyText = findViewById(R.id.sliderpanelJourneyTextView);



        mMapView = (MapView) findViewById(R.id.mapquestMapView);
        mMapView.onCreate(savedInstanceState);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String location = "";
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();
        configureProfileImage();

        //change the title of navigation drawer to username
        updateDrawerTitle();


        ArrayList<Double> list = getLocation();
        lulat = list.get(0);
        lulon = list.get(1);

        //USER LOCATION
        final LatLng latLng = new LatLng(lulat, lulon);

        final IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
        final Icon trainIcon = iconFactory.fromResource(R.drawable.train);
        final Icon toiletIcon = iconFactory.fromResource(R.drawable.toilet);
        final Icon childIcon = iconFactory.fromResource(R.drawable.star);
        final Icon searchIcon = iconFactory.fromResource(R.drawable.placeholder);

        mMapView.getMapAsync(new OnMapReadyCallback() {




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

                slidepanelTitle = findViewById(sliderpanelTitleTextView);
                slidepanelSubtitle = findViewById(R.id.sliderpanelSubtitleTextView);
                slidepanelJourney = findViewById(R.id.sliderpanelJourneyTextView);
                slidepanelImage = findViewById(R.id.sliderpanelImageView1);
                slidePanelJourneyButton = findViewById(R.id.sliderpanelJourneyButton);
                slidepanelArriveAtText = findViewById(R.id.sliderPanelArriveAtText);
                slidepanelArriveTimeText = findViewById(R.id.sliderpanelArrivalTime);
                slidepanelDepartAtText = findViewById(R.id.sliderPanelDepartAtText);
                slidepanelDepartureTimeText = findViewById(R.id.sliderpanelDepartureTime);
                slidepanelJourneyToText = findViewById(R.id.sliderpanelJourneyToText);

                slidepanelStepByStep = findViewById(R.id.sliderpanelStepByStep);

                slidepanelJourneyToText.setVisibility(View.INVISIBLE);
                slidepanelArriveAtText.setVisibility(View.INVISIBLE);
                slidepanelArriveTimeText.setVisibility(View.INVISIBLE);
                slidepanelDepartAtText.setVisibility(View.INVISIBLE);
                slidepanelDepartureTimeText.setVisibility(View.INVISIBLE);
                slidepanelJourneyText.setVisibility(View.INVISIBLE);

                LatLng tmpUser = new LatLng(lulat,lulon);
                Address currentAddress = null;
                try {
                    currentAddress = gc.getFromLocation(tmpUser.getLatitude(),tmpUser.getLongitude(),1).get(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                slidepanelTitle.setText(currentAddress.getAddressLine(0));
                slidepanelSubtitle.setText("");


                //Set up marker button
                mMapboxMap.setOnMarkerClickListener(new com.mapbox.mapboxsdk.maps.MapboxMap.OnMarkerClickListener() {
                                                        @Override
                                                        public boolean onMarkerClick(@NonNull Marker marker) {
                                                            if (!isJourneyCurrentlyShowing) {
                                                                slidepanelTitle.setText(marker.getTitle());
                                                                slidepanelSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                                                                slidepanelSubtitle.setText(marker.getSnippet());
                                                            }
                                                            Log.e("helpp","triggers");

                                                            slidepanelImage.setImageBitmap(marker.getIcon().getBitmap());
                                                            enableJourneyRouteButton(marker);

                                                            //Create temporary train icon and compare
                                                            String example = "";

                                                            String footer = " Train Station";
                                                            String stationName = marker.getTitle();
                                                            stationName = stationName.replace(footer, "");
                                                            if (marker.getIcon().getBitmap().sameAs(trainIcon.getBitmap())) {
                                                                slidePanelJourneyButton.setText("Show Route");
                                                                slidePanelJourneyButton.setVisibility(View.VISIBLE);

                                                            } else if (marker.getIcon().getBitmap().sameAs(toiletIcon.getBitmap())) {
                                                                slidePanelJourneyButton.setVisibility(View.VISIBLE);
                                                                slidepanelJourneyText.setVisibility(View.INVISIBLE);
                                                                slidepanelSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);

                                                            } else if (marker.getIcon().getBitmap().sameAs(childIcon.getBitmap())) {
                                                                slidePanelJourneyButton.setVisibility(View.VISIBLE);

                                                            } else if (marker.getIcon().getBitmap().sameAs(searchIcon.getBitmap())) {
                                                                slidePanelJourneyButton.setVisibility(View.VISIBLE);
                                                            } else {
                                                                    slidepanelSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
                                                                    slidePanelJourneyButton.setVisibility(View.GONE);
                                                                    slidepanelJourneyText.setVisibility(View.INVISIBLE);

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
                        } else if (itemID == R.id.tutorial){
                            startActivity(new Intent(MapActivity.this, Tutorial1.class));
                        } else if (itemID == R.id.emergency){
                            if(!isParent) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                                builder.setMessage("Send notification to your parent?");
                                builder.setTitle("Alert");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        asyncUpdateEmergencyOn();
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                                builder.setMessage("You are not in child mode!");
                                builder.setTitle("Alert");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();

                            }
                        } else {

                        }
                        return false;
                    }
                });
            }
        });
    }

    private void enableJourneyRouteButton(Marker m) {
        slidePanelJourneyButton.setVisibility(View.VISIBLE);
        slidePanelJourneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Double> loc = getLocation();
                LatLng destinationCoord = new LatLng(m.getPosition());
                if (currentNavMap != null) {
                    Log.e("currentNavMap","routes have been removed");
                    currentNavMap.removeRoute();
                } else {
                    currentNavMap = new NavigationMapRoute(null,mMapView,mMapboxMap,R.style.NavigationMapRoute);
                }
                getDirections(loc.get(0),loc.get(1),destinationCoord.getLatitude(),destinationCoord.getLongitude());
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
            }
        });
    }

    private void disableJourneyRouteButton() {
        slidePanelJourneyButton.setVisibility(View.INVISIBLE);
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
        nrb.profile(DirectionsCriteria.PROFILE_WALKING);


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
                Log.e("important","added to currentRoute");
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Log.e("getSingleRoute",t.toString());
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

    @Override
    protected void onResume() {
        super.onResume();

        updateDrawerTitle();
        configureProfileImage();
        mMapView.onResume();
        if (mPreferences.getBoolean("isParent",false) && mPreferences.getBoolean("firstTimeRun",false)) {
            mEditor = mPreferences.edit();
            mEditor.putBoolean("firstTimeRun",false);
            mEditor.apply();
            MapActivity.this.recreate();
        }
        Log.e("help","onResume triggered");
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
            } else if (panel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                panel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            } else if (isJourneyCurrentlyShowing) {
                MapActivity.this.recreate();
            }else {
                super.onBackPressed();
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = new MenuInflater(this);
        mi.inflate(R.menu.main3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_stations) {
            removestations(mMapboxMap);
            return true;
        } else if (id == R.id.action_toilets) {
            removetoilets(mMapboxMap);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Hide and show stations method
    public void removestations(MapboxMap m) {
        String toast = "";

        if (stamarkershown && toimarkershown) {
            m.clear();
            //addUserLocation(mMapboxMap);
            asyncToiletMarkers(lulat,lulon);
            stamarkershown = false;
            toast = "Stations Disabled";
        } else if (stamarkershown && !toimarkershown) {
            m.clear();
            //addUserLocation(mMapboxMap);
            stamarkershown = false;
            toast = "Stations Disabled";
        } else {
            asyncStationMarkers(lulat,lulon);
            stamarkershown = true;
            toast = "Stations Enabled";
        }
        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
    }

    public void removetoilets(MapboxMap m) {

        String toast = "";
        if (toimarkershown && stamarkershown) {
            m.clear();
            //addUserLocation(mMapboxMap);
            asyncStationMarkers(lulat,lulon);
            toimarkershown = false;
            toast = "Toilets Disabled";
        } else if (toimarkershown && !stamarkershown) {
            m.clear();
            //addUserLocation(mMapboxMap);
            toimarkershown = false;
            toast = "Toilets Disabled";
        } else {
            asyncToiletMarkers(lulat,lulon);
            toimarkershown = true;
            toast = "Toilets Enabled";
        }
        Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_LONG).show();
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
            list.add(-37.87700);
            list.add(145.04426);
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
        childLatLng = new LatLng(currentChildLocation.getDouble("childLat"),currentChildLocation.getDouble("childLon"));
        markerOptions.position(childLatLng);
        mapboxMap.addMarker(markerOptions);
        if (mPreferences.getBoolean("firstTimeRun",false)) {
            mapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(childLatLng,13));
            mEditor.putBoolean("firstTimeRun",false);
            mEditor.apply();
        }
        mapboxMap.moveCamera(CameraUpdateFactory.newLatLng(childLatLng));

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
            Log.e("important",al.toString());
            getManyRoutes(al);

            //hide all markers
            //only show relevant train markers
            //New Code 1/10/2018
            mMapboxMap.clear();

            drawJourneyMarkers(al);
            if (currentNavMap != null) {
                currentNavMap.removeRoute();
            } else {
                currentNavMap = new NavigationMapRoute(null,mMapView,mMapboxMap,R.style.NavigationMapRoute);
            }
            if (currentRoute.size()> 0) {
                currentNavMap.addRoutes(currentRoute);
                Log.e("important","routes have been added to the map from currentRoute");
            }
        }
    }

    private void writeJourneyInformation(JSONObject legs) {
        String arrivalTime = "error";
        String departureTime = "error";
        String distance = "error";
        String duration = "error";
        String endAddress = "error,";
        String startAddress = "error,";
        isJourneyCurrentlyShowing = true;
        Log.e("writeJourneyInfo",legs.toString());
        try {
            arrivalTime = legs.getJSONObject("arrival_time").getString("text");
            departureTime = legs.getJSONObject("departure_time").getString("text");
        } catch (JSONException e) {
            Calendar cal = Calendar.getInstance(Locale.getDefault());
            try {
                arrivalTime = legs.getJSONArray("steps").getJSONObject(0).getJSONObject("duration").getString("text");
            } catch (JSONException ef) {
                ef.printStackTrace();
                arrivalTime = "error";
            }
            departureTime = "Now";
            e.printStackTrace();
        }
        try {
            distance = legs.getJSONObject("distance").getString("text");
            duration = legs.getJSONObject("duration").getString("text");
            endAddress = legs.getString("end_address");
            startAddress = legs.getString("start_address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("endAddress",endAddress);
        Log.e("startAddress",startAddress);


        String example = "Arrival Time: " + arrivalTime + ". Departure Time: " + departureTime + ". Distance: " + distance + ". Duration" + duration;
        example += ". Start Location: " + startAddress + ". End Location: " + endAddress ;


        startAddress = startAddress.substring(0,startAddress.lastIndexOf(","));
        endAddress = endAddress.substring(0,endAddress.lastIndexOf(","));

        slidepanelExitRoute.setVisibility(View.VISIBLE);
        slidepanelArriveTimeText.setText(arrivalTime);
        slidepanelDepartureTimeText.setText(departureTime);
        slidepanelTitle.setText(startAddress);
        slidepanelSubtitle.setText(endAddress);
        slidepanelSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);

        slidepanelExitRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapActivity.this.recreate();
            }
        });



        slidepanelStepByStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray steps = null;
                try {
                    steps = legs.getJSONArray("steps");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mEditor = mPreferences.edit();
                mEditor.putString("instructions", steps.toString());
                mEditor.apply();
                startActivity(new Intent(MapActivity.this, StepByStep2Activity.class));
            }
        });
        slidepanelStepByStep.setVisibility(View.VISIBLE);

        slidepanelJourneyToText.setVisibility(View.VISIBLE);
//        slidepanelJourneyText.setVisibility(View.VISIBLE);
        slidepanelArriveAtText.setVisibility(View.VISIBLE);
        slidepanelArriveTimeText.setVisibility(View.VISIBLE);
        slidepanelDepartAtText.setVisibility(View.VISIBLE);
        slidepanelDepartureTimeText.setVisibility(View.VISIBLE);

    }

    private void drawJourneyMarkers(ArrayList<JSONObject> al) {
        int i = 0;
        final int size = al.size();
        MarkerOptions markerOptions = new MarkerOptions();
        IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);

        while (i<al.size()) {
            try {
                if (al.get(i).getString("travel_mode").equals("WALKING")) {
                    Log.e("drawJourneyMarkers", "walking");
                } else if (al.get(i).getString("travel_mode").equals("TRANSIT")) {
                    Log.e("drawJourneyMarkers", "transit");
                    if (al.get(i).getString("html_instructions").substring(0,5).contains("Train")) {
                        Icon icon = iconFactory.fromResource(R.drawable.train);
                        LatLng markerloc = new LatLng(al.get(i).getJSONObject("transit_details").getJSONObject("arrival_stop").getJSONObject("location").getDouble("lat"),
                                al.get(i).getJSONObject("transit_details").getJSONObject("arrival_stop").getJSONObject("location").getDouble("lng"));
                        markerOptions.title(al.get(i).getJSONObject("transit_details").getJSONObject("arrival_stop").getString("name"));
                        markerOptions.icon(icon);
                        markerOptions.position(markerloc);
                        mMapboxMap.addMarker(markerOptions);

                        LatLng markerloc2 = new LatLng(al.get(i).getJSONObject("transit_details").getJSONObject("departure_stop").getJSONObject("location").getDouble("lat"),
                                al.get(i).getJSONObject("transit_details").getJSONObject("departure_stop").getJSONObject("location").getDouble("lng"));
                        markerOptions.title(al.get(i).getJSONObject("transit_details").getJSONObject("departure_stop").getString("name"));
                        markerOptions.icon(icon);
                        markerOptions.position(markerloc2);
                        mMapboxMap.addMarker(markerOptions);

                    } else if (al.get(i).getString("html_instructions").substring(0,3).contains("Bus")) {
                        Icon icon = iconFactory.fromResource(R.drawable.bus);
                        LatLng markerloc = new LatLng(al.get(i).getJSONObject("transit_details").getJSONObject("arrival_stop").getJSONObject("location").getDouble("lat"),
                                al.get(i).getJSONObject("transit_details").getJSONObject("arrival_stop").getJSONObject("location").getDouble("lng"));
                        markerOptions.title(al.get(i).getJSONObject("transit_details").getJSONObject("arrival_stop").getString("name"));
                        markerOptions.icon(icon);
                        markerOptions.position(markerloc);
                        mMapboxMap.addMarker(markerOptions);

                        LatLng markerloc2 = new LatLng(al.get(i).getJSONObject("transit_details").getJSONObject("departure_stop").getJSONObject("location").getDouble("lat"),
                                al.get(i).getJSONObject("transit_details").getJSONObject("departure_stop").getJSONObject("location").getDouble("lng"));
                        markerOptions.title(al.get(i).getJSONObject("transit_details").getJSONObject("departure_stop").getString("name"));
                        markerOptions.icon(icon);
                        markerOptions.position(markerloc2);
                        mMapboxMap.addMarker(markerOptions);
                    } else if (al.get(i).getString("html_instructions").substring(0,4).contains("Tram")) {
                        Icon icon = iconFactory.fromResource(R.drawable.tram);
                        LatLng markerloc = new LatLng(al.get(i).getJSONObject("transit_details").getJSONObject("arrival_stop").getJSONObject("location").getDouble("lat"),
                                al.get(i).getJSONObject("transit_details").getJSONObject("arrival_stop").getJSONObject("location").getDouble("lng"));

                        markerOptions.title(al.get(i).getJSONObject("transit_details").getJSONObject("arrival_stop").getString("name"));
                        markerOptions.icon(icon);
                        markerOptions.position(markerloc);
                        mMapboxMap.addMarker(markerOptions);

                        LatLng markerloc2 = new LatLng(al.get(i).getJSONObject("transit_details").getJSONObject("departure_stop").getJSONObject("location").getDouble("lat"),
                                al.get(i).getJSONObject("transit_details").getJSONObject("departure_stop").getJSONObject("location").getDouble("lng"));
                        markerOptions.title(al.get(i).getJSONObject("transit_details").getJSONObject("departure_stop").getString("name"));
                        markerOptions.icon(icon);
                        markerOptions.position(markerloc2);
                        mMapboxMap.addMarker(markerOptions);
                    } else {

                    }

                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i++;
        }
    }

    private ArrayList<JSONObject> configureDirections(JSONObject results) {

        try {
            JSONArray routes = results.getJSONArray("routes");
            JSONObject theRoute = routes.getJSONObject(0);
            JSONObject legs = theRoute.getJSONArray("legs").getJSONObject(0);
            writeJourneyInformation(legs);
            JSONArray steps = legs.getJSONArray("steps");
            ArrayList<JSONObject> aList = new ArrayList<>();
            int i = 0;
            while (i<steps.length()) {
                aList.add(steps.getJSONObject(i));
                i++;
            }
            i = 0;
            //aList current holds each step of the journey as an entry of JSONObject
            return aList;
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
            // Create an instance of LOST location engine
            initializeLocationEngine();
            // Create an instance of the plugin. Adding in LocationLayerOptions is also an optional
            // parameter
            locationLayerPlugin = new LocationLayerPlugin(mMapView, mMapboxMap);

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

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
    }
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

    //update child emergency status from server
    public class updateEmergenceFromServer extends AsyncTask<Void,Void,Void> {

        private WeakReference<MapActivity> activityWeakReference;
        private String urls;
        updateEmergenceFromServer(MapActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        protected void updateEmergency() {
            String parentid = DeviceIDGenerator.getID(MapActivity.this);
            urls = "http://13.59.24.178/emergencyStatus.php?parentID=" + parentid;
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
                JSONArray ts = new JSONArray(builder.toString());
                JSONObject jsono = ts.getJSONObject(0);
                tempString = jsono.getString("emergency");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if(tempString.equals("1")){
                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                builder.setTitle("Alert");
                builder.setMessage("You child sends you an emergency notification.");
                builder.setCancelable(false);
                Vibrator vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrate.vibrate(new long[]{1000,3000,1000,3000},1);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        asyncUpdateEmergencyOff();
                        vibrate.cancel();
                        MapActivity.this.recreate();
                    }
                });
                AlertDialog ad = builder.create();
                ad.show();
            }
        }
    }

    private void asyncUpdateChildEmergencyFromServer() {
        MapActivity.updateEmergenceFromServer t = new MapActivity.updateEmergenceFromServer(this);
        t.updateEmergency();
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


    //turn off emergency
    public void asyncUpdateEmergencyOff() {
        MapActivity.emergencyOff t = new MapActivity.emergencyOff(this);
        t.emergencyUpdateOff();
    }

    public class emergencyOff extends AsyncTask<Void,Void,Void> {

        private WeakReference<MapActivity> activityWeakReference;
        private String urls;
        emergencyOff(MapActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        protected void emergencyUpdateOff() {
            String parentid = DeviceIDGenerator.getID(MapActivity.this);
            urls = "http://13.59.24.178/emergencyOff.php?parentID=" + parentid;
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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }
    }

    //Update child emergency status on to server
    public void asyncUpdateEmergencyOn() {
        MapActivity.emergencyOn t = new MapActivity.emergencyOn(this);
        t.emergencyUpdateOn();
    }

    public class emergencyOn extends AsyncTask<Void,Void,Void> {

        private WeakReference<MapActivity> activityWeakReference;
        private String urls;
        emergencyOn(MapActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        protected void emergencyUpdateOn() {
            String childid = DeviceIDGenerator.getID(MapActivity.this);
            urls = "http://13.59.24.178/emergencyOn.php?childID=" + childid;
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
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }
    }

};


