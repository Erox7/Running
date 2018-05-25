package com.example.erox.running;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.erox.running.POJO.Example;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String GEOFENCE_ID = "MyGeofenceId";
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    public static String sPref = null;
    public static boolean isInsideOfGeofence = false;
    private static boolean wifiConnected = false;
    private static boolean mobileConnected = false;
    public Geofence geofence;
    NetworkReceiver receiver;
    LocationRequest mLocationRequest;
    Intent intent;
    PendingIntent pendingIntent;
    Button start, stop;
    Polyline line;
    private String proposedDistance, avgTime, finalDistance;
    private GoogleMap mMap;
    private boolean mapClicked = false, stopButtonClicked = false;
    private Marker destination, origin, person;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private TextView chrono;
    private FusedLocationProviderClient mFusedLocationClient;
    private GeofencingClient mGeofencingClient;
    private long startTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (!(savedInstanceState == null)) {
            String UID = savedInstanceState.getString("user");
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        createLocationCallback();
        createLocationRequest();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        start = findViewById(R.id.startRunningButton);
        stop = findViewById(R.id.stopRunningButton);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        intent = new Intent(this, GeofenceService.class);
        pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mGeofencingClient = LocationServices.getGeofencingClient(this);
        chrono = findViewById(R.id.chrono);


    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                updatePosition();
                if (isInsideOfGeofence) {
                    endOfTheRun();
                    stopLocationUpdates();
                }

                /*
                if(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()) == destination.getPosition()){
                    endOfTheRun();
                    stopLocationUpdates();
                }*/
            }
        };
    }

    //geofence
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    private void updatePosition() {
        if (mCurrentLocation != null && person != null) {
            person.remove();
            person = mMap.addMarker(new MarkerOptions().
                    position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).
                    title(getString(R.string.itsMe)).
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.main_arrow)));
        } else if (mCurrentLocation != null) {
            person = mMap.addMarker(new MarkerOptions().
                    position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).
                    title(getString(R.string.itsMe)).
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.main_arrow)));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefs.registerOnSharedPreferenceChangeListener(new SettingsActivity());
        // Retrieves a string value for the preferences. The second parameter
        // is the default value to use if a preference value is not found.
        sPref = sharedPrefs.getString("listPref", "Wi-Fi");
        updateConnectedFlags();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.isConnected()) {
            wifiConnected = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        mFusedLocationClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                float zoomlvl = 15.0f;
                mCurrentLocation = task.getResult();
                if (mCurrentLocation != null) {
                    LatLng first = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    origin = mMap.addMarker(new MarkerOptions().
                            position(first).
                            title(getString(R.string.origin)).
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.red_dot)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(first, zoomlvl));
                }
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mapClicked = true;
                mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
                if (destination != null) {
                    destination.remove();
                }
                destination = mMap.addMarker(new MarkerOptions().
                        position(point).
                        title(getString(R.string.destiny)).
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.green_dot)));
                //Destination changes: we change the position point for a geofence with 10m radius
                geofence = new Geofence.Builder()
                        .setRequestId(GEOFENCE_ID)
                        .setCircularRegion(destination.getPosition().latitude, destination.getPosition().longitude, 10)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setNotificationResponsiveness(1000)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                        .build();



            }
        });

    }
    public AppCompatActivity getActivity(){
        return this;
    }

    @SuppressLint("MissingPermission")
    public void startRunningClicked(View view) {
        if (!mapClicked) {
            Toast.makeText(this, getString(R.string.noPositionClicked), Toast.LENGTH_LONG).show();
        } else {
            mGeofencingClient.addGeofences(getGeofencingRequest(), pendingIntent).addOnCompleteListener(getActivity(), new MyOnCompleteListener());
            start.setVisibility(View.INVISIBLE);
            stop.setVisibility(View.VISIBLE);
            startLocationUpdates();
            build_retrofit_and_get_response("walking");

            startTime = System.currentTimeMillis();
            CountDownTimer newtimer = new CountDownTimer(1000000000, 1000) {

                public void onTick(long millisUntilFinished) {
                    timeCalculator();
                }

                public void onFinish() {

                }
            };
            newtimer.start();
        }
    }

    public void timeCalculator() {
        long actual = System.currentTimeMillis();
        float currentTime = (actual - startTime) / 1000;
        String str = Float.toString(currentTime);
        System.out.println(str);
        chrono.setText(str);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        updatePosition();
    }

    private void build_retrofit_and_get_response(String type) {

        String url = "https://maps.googleapis.com/maps/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMaps service = retrofit.create(RetrofitMaps.class);

        Call<Example> call = service.getDistanceDuration("metric", mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude(), destination.getPosition().latitude + "," + destination.getPosition().longitude, type);
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response, Retrofit retrofit) {

                try {
                    //Remove previous line from map
                    if (line != null) {
                        line.remove();
                    }
                    // This loop will go through all the results and add marker on each location.
                    for (int i = 0; i < response.body().getRoutes().size(); i++) {
                        proposedDistance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                        avgTime = response.body().getRoutes().get(i).getLegs().get(i).getDuration().getText();
                        //ShowDistanceDuration.setText("Distance:" + distance + ", Duration:" + time);
                        String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                        List<LatLng> list = decodePoly(encodedString);
                        line = mMap.addPolyline(new PolylineOptions()
                                .addAll(list)
                                .width(20)
                                .color(Color.RED)
                                .geodesic(true)
                        );
                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });

    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    public void stopRunningClicked(View view) {
        stopLocationUpdates();
        stopButtonClicked = true;
        endOfTheRun();
    }

    private void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }

    private void endOfTheRun() {
        RunningLogs actualLog = new RunningLogs();
        actualLog.setAvgWalkingTime(avgTime);
        actualLog.setDistanceProposed(proposedDistance);
        long finishTime = System.currentTimeMillis();
        float totalTime = (finishTime - startTime) / 1000;
        actualLog.setTimeInSeconds(totalTime);

        if (!stopButtonClicked) {
            Toast.makeText(this, getString(R.string.endOfTheroad), Toast.LENGTH_LONG).show();
            calculateFinalDistanceDone();
            actualLog.setDistanceDone(finalDistance);
        } else {
            Toast.makeText(this, getString(R.string.forcedStop), Toast.LENGTH_LONG).show();
            actualLog.setDistanceDone(proposedDistance);
        }
        mMap.clear();
        Intent toResults = new Intent(this, ResultActivity.class);
        Bundle b = new Bundle();
        b.putSerializable("log", actualLog);
        toResults.putExtras(b);
        startActivity(toResults);
        //HERE THE LOG GOES TO THE BACKEND AND THE ACTIVITY GOES TO ANOTHER ONE THAT WILL READ THAT LOG AND SHOW IT

    }

    private void calculateFinalDistanceDone() {
        String url = "https://maps.googleapis.com/maps/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMaps service = retrofit.create(RetrofitMaps.class);

        Call<Example> call = service.getDistanceDuration("metric", origin.getPosition().latitude + "," + origin.getPosition().longitude, destination.getPosition().latitude + "," + destination.getPosition().longitude, "walking");
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Response<Example> response, Retrofit retrofit) {
                for (int i = 0; i < response.body().getRoutes().size(); i++) {
                    finalDistance = response.body().getRoutes().get(i).getLegs().get(i).getDistance().getText();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });
    }

    // Populates the activity's options menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Handles the user's menu selection.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settingsActivity = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(settingsActivity);
                return true;
            case R.id.Profile:
                Intent profileActivity = new Intent(getBaseContext(), ProfileActivity.class);
                startActivity(profileActivity);
                return true;
            case R.id.Music:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com")));
                return true;
            case R.id.Groups:
                //Intent groupsActivity = new Intent(getBaseContext(), GroupsActivity.class);
                //startActivity(groupsActivity);
                Toast.makeText(this, "Can't implement Group funcionality at the moment", Toast.LENGTH_LONG).show();
                return true;
            case R.id.About:
                startActivity(new Intent(getBaseContext(), AboutActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connMgr != null) {
                networkInfo = connMgr.getActiveNetworkInfo();
            }

            // Checks the user prefs and the network connection. Based on the result, decides
            // whether
            // to refresh the display or keep the current display.
            // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
            if (WIFI.equals(sPref) && networkInfo != null
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // If device has its Wi-Fi connection, sets refreshDisplay
                // to true. This causes the display to be refreshed when the user
                // returns to the app.
                Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();

                // If the setting is ANY network and there is a network connection
                // (which by process of elimination would be mobile), sets refreshDisplay to true.
            } else if (ANY.equals(sPref) && networkInfo != null) {
                Toast.makeText(context, R.string.network_connected, Toast.LENGTH_SHORT).show();
                // Otherwise, the app can't download content--either because there is no network
                // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
                // is no Wi-Fi connection.
                // Sets refreshDisplay to false.
            } else {
                Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
