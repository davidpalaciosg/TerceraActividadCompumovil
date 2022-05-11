package com.palacios.terceraactividadjaveriana;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.palacios.terceraactividadjaveriana.Classes.User;
import com.palacios.terceraactividadjaveriana.databinding.ActivityDavidPalaciosBinding;

import java.util.HashMap;
import java.util.Map;

public class DavidPalaciosActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String DavidPalaciosUUID = "DVduIWbHi0TLxUd83U65ph0L3PE3";
    public static final double RADIUS_OF_EARTH_M = 6371000;

    private GoogleMap mMap;
    private ActivityDavidPalaciosBinding binding;
    private Marker markerLastLocation;
    private Marker markerDavidPalacios;

    //Firebase
    private FirebaseAuth mAuth;
    //Database
    //Root path of every user in FB
    public static final String PATH_USERS = "users/";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference davidRef;
    private StorageReference mStorageRef;
    private String uuId;

    //locationRequest with google
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location lastLocation;
    private boolean firstMovement;
    private double previousLat = 0;
    private double previousLong = 0;

    private boolean settingsOK = false;
    private double currentLat = 0;
    private double currentLong = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDavidPalaciosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        uuId = intent.getStringExtra("user");

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        //Database
        database = FirebaseDatabase.getInstance();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = createLocationRequest();
        locationCallback = createLocationCallBack();

        //Ask Permission
        getSinglePermission.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        //Check if GPS is ON
        checkLocationSettings();

        //MAP
        startMap();
        getDavidLocationUpdates();

    }

    private void getDavidLocationUpdates() {
        davidRef = database.getReference(PATH_USERS);
        davidRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    User david = singleSnapshot.getValue(User.class);

                    if (david.getUuid().equals(DavidPalaciosUUID)) {
                        String name = david.getName();
                        double davidLat = david.getLatitude();
                        double davidLong = david.getLongitude();
                        Log.i("DAVID", "Location: " + davidLat + " " + davidLong);

                        Toast.makeText(getBaseContext(), "Location: " + davidLat + " " + davidLong, Toast.LENGTH_SHORT).show();
                        updateDavidLocation(davidLat, davidLong);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DAVID", "error en la consulta", databaseError.toException());
            }
        });
    }

    //--------------------------------------------------------LOCATION---------------------------------------------------
    //Ask for permission
    ActivityResultLauncher<String> getSinglePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result == true) { //granted
                        startLocationUpdates();
                    } else {//denied
                        Toast.makeText(DavidPalaciosActivity.this, "Location permission is required", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    //Turn Location settings (GPS) ON
    ActivityResultLauncher<IntentSenderRequest> getLocationSettings = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.i("LOCATION", "Result from settings:" + result.getResultCode());
                    if (result.getResultCode() == RESULT_OK) {
                        settingsOK = true;
                        startLocationUpdates();
                    } else {
                        //locationText.setText("GPS is unavailable");
                    }
                }
            }
    );


    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new
                LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.i("LOCATION", "GPS is ON");
                settingsOK = true;
                startLocationUpdates();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (((ApiException) e).getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    IntentSenderRequest isr = new IntentSenderRequest.Builder(resolvable.getResolution()).build();
                    getLocationSettings.launch(isr);
                } else {
                    //locationText.setText("No GPS available");
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (settingsOK) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);//looper: cada cuanto quiere que lo haga
            }
        }
    }


    private LocationRequest createLocationRequest() {
        LocationRequest request = LocationRequest.create().setFastestInterval(5000).setInterval(10000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return request;
    }


    private LocationCallback createLocationCallBack() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //Get previous Location
                if (lastLocation != null) {
                    previousLat = currentLat;
                    previousLong = currentLong;
                    firstMovement = true;
                }
                //Get Location from Google Services
                lastLocation = locationResult.getLastLocation();

                if (lastLocation != null) {

                    currentLong = lastLocation.getLongitude();
                    currentLat = lastLocation.getLatitude();

                    //If theres a movement, update map with the current location
                    if (currentLong != previousLong && currentLat != previousLat) {
                        updateMapWithLocation();
                        //Update database
                        Map<String, Object> newLocation = new HashMap<>();
                        newLocation.put("latitude", currentLat);
                        newLocation.put("longitude", currentLong);

                        myRef = database.getReference(PATH_USERS + "/" + uuId);
                        myRef.updateChildren(newLocation);
                    }


                    String txt = "Latitude: " + currentLat + " ,Longitude: " + currentLong;
                    Log.i("LOCATION", txt);

                    //Detect if there exists a first movement before detect 30 meters movement
                    if (firstMovement) {
                        //If detect 10 meters movement
                        if (is10MetersForward(previousLat, previousLong, currentLat, currentLong)) {
                            Log.i("LOCATION", "MOVEMENT");
                        }
                    }
                }


            }
        };
    }


    public boolean is10MetersForward(double lat1, double long1, double lat2, double long2) {
        double dist = distance(lat1, long1, lat2, long2);
        if (dist > 10)
            return true;
        return false;
    }

    public double distance(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_M * c;
        return Math.round(result * 100.0) / 100.0;
    }


    private void startMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapDavidP);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //If permission is given
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (settingsOK) {
                updateMapWithLocation();
            }
        } else {
            // Add a marker in Sydney and move the camera
            LatLng PUJ = new LatLng(4.62842868383447, -74.06444992329283);
            mMap.addMarker(new MarkerOptions().position(PUJ).title("Marker in PUJ"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(PUJ));

        }
    }

    public void updateMapWithLocation() {
        if (markerLastLocation != null) {
            markerLastLocation.remove();
        }
        LatLng location = new LatLng(currentLat, currentLong);
        markerLastLocation = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Your location")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                ));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    public void updateDavidLocation(double latitude, double longitude) {
        if(markerDavidPalacios != null) {
            markerDavidPalacios.remove();
        }
        LatLng location = new LatLng(latitude, longitude);
        markerDavidPalacios = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title("David Palacios Location")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
                ));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }


    @Override
    protected void onPause() {
        super.onPause();
        //LOCATION
        mFusedLocationClient.removeLocationUpdates(locationCallback);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //LOCATION
        startLocationUpdates();
    }
}