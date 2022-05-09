package com.palacios.terceraactividadjaveriana;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.palacios.terceraactividadjaveriana.Classes.EmailPasswordVerifier;
import com.palacios.terceraactividadjaveriana.Classes.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SignUpActivity extends AppCompatActivity {
    //Firebase
    private FirebaseAuth mAuth;

    //Database
    //Root path of every user in FB
    public static final String PATH_USERS="users/";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;

    //Location with google
    //locationRequest with google
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location lastLocation;
    private boolean settingsOK = false;

    //Views
    private EditText txtNameSign;
    private EditText txtLastSign;
    private EditText txtIdSign;
    private EditText txtEmailSign;
    private EditText txtPasswordSign;
    private EditText txtConfirmPasswordSign;
    private ImageView imgSign;
    private Button btnCameraSign;
    private Button btnFilesSign;
    private Button btnSaveSign;



    //Uri to store the camera picture
    Uri uriCamera;
    Uri fileToUpload;
    double latitude=0;
    double longitude=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        //Database
        database= FirebaseDatabase.getInstance();
        //Storage
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //Location with google
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = createLocationRequest();
        locationCallback = createLocationCallBack();

        //Inflate
        txtNameSign = findViewById(R.id.txtNameSign);
        txtLastSign = findViewById(R.id.txtLastSign);
        txtIdSign = findViewById(R.id.txtIdSign);
        txtEmailSign = findViewById(R.id.txtEmailSign);
        txtPasswordSign = findViewById(R.id.txtPasswordSign);
        txtConfirmPasswordSign = findViewById(R.id.txtConfirmPasswordSign);
        imgSign = findViewById(R.id.imgSign);
        btnCameraSign = findViewById(R.id.btnCameraSign);
        btnFilesSign = findViewById(R.id.btnFilesSign);
        btnSaveSign = findViewById(R.id.btnSaveSign);

        startButtons();
        //Ask Permission
        getSinglePermission.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        //Check if GPS is ON
        checkLocationSettings();


    }


    private void startButtons() {
        //Gallery
        btnFilesSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContentGallery.launch("image/*");
            }
        });
        //Camera
        btnCameraSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCamera();
            }
        });
        btnSaveSign.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                String name = txtNameSign.getText().toString().toLowerCase().trim();
                String last = txtLastSign.getText().toString().toLowerCase().trim();
                String id = txtIdSign.getText().toString().trim();
                String email = txtEmailSign.getText().toString().toLowerCase().trim();
                String password = txtPasswordSign.getText().toString().trim();
                String confirmPassword = txtConfirmPasswordSign.getText().toString().trim();

                boolean isAvailable = false;

                if(validateSignUp(name, last, id, email, password, confirmPassword)==true){
                    tryToSignUp(name, last, id, email,password);

                }
            }
        });
    }

    private void tryToSignUp(String name, String last, String id, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("SIGN UP", "createUserWithEmail:onComplete:" + task.isSuccessful());
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Save picture on user Storage
                            uploadFile(user);
                            //Create user
                            String imageUrl = "images/profile/"+user.getUid()+"/image.jpg";

                            //Save user in FB
                            User newUser = new User(name,last,id,email,password,latitude,longitude,imageUrl,false);
                            myRef=database.getReference(PATH_USERS+user.getUid());
                            myRef.setValue(newUser);

                            updateUi(user);



                            if(user!=null){ //Update user Info
                                UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                                upcrb.setDisplayName(txtNameSign.getText().toString()+" "+txtLastSign.getText().toString());
                                upcrb.setPhotoUri(Uri.parse(imageUrl));//fake uri, use Firebase Storage
                                user.updateProfile(upcrb.build());
                                //updateUI(user);
                            }
                        }
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, task.getException().toString(),
                                    Toast.LENGTH_SHORT).show();
                            Log.e("SIGN UP", task.getException().getMessage());
                        }
                    }
                });
    }

    private void updateUi(FirebaseUser user) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("user", user.getEmail());
        startActivity(intent);
    }

    private void uploadFile(FirebaseUser user)
    {
        if(fileToUpload!=null){
            StorageReference imageRef = mStorageRef.child("images/profile/"+user.getUid()+"/image.jpg");
            imageRef.putFile(fileToUpload)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                            Log.i("UPLOAD", "Succesfully upload image");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                            Log.e("UPLOAD", "Failed to upload image");
                        }
                    });
        }
    }



    private boolean validateSignUp(String name, String last, String id, String email, String password, String confirmPassword) {
        if(name.isEmpty()) {
            txtNameSign.setError("Name is required");
            return false;
        }
        if(last.isEmpty()) {
            txtLastSign.setError("Last name is required");
            return false;
        }
        if(id.isEmpty()) {
            txtIdSign.setError("Id is required");
            return false;
        }
        if(email.isEmpty()) {
            txtEmailSign.setError("Email is required");
            return false;
        }
        if(password.isEmpty()) {
            txtPasswordSign.setError("Password is required");
            return false;
        }
        if(confirmPassword.isEmpty()) {
            txtConfirmPasswordSign.setError("Confirm password is required");
            return false;
        }
        if(!password.equals(confirmPassword)) {
            txtConfirmPasswordSign.setError("Passwords must be equal");
            return false;
        }
        if(EmailPasswordVerifier.verifyEmailAndPassword(email, password)==false){
            txtEmailSign.setError("Email or password is not valid");
            txtPasswordSign.setError("Email or password is not valid");
            return false;
        }
        return true;
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
                        btnSaveSign.setEnabled(true);

                    } else {//denied
                        btnSaveSign.setEnabled(false);
                        Toast.makeText(SignUpActivity.this, "Location permission is required", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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

    private LocationRequest createLocationRequest() {
        LocationRequest request = LocationRequest.create().setFastestInterval(5000).setInterval(10000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return request;
    }

    private LocationCallback createLocationCallBack() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                //Get Location from Google Services
                lastLocation = locationResult.getLastLocation();
                if (lastLocation != null) {
                    latitude = lastLocation.getLatitude();
                    longitude = lastLocation.getLongitude();
                    String txt = "Latitude: " + latitude + " ,Longitude: " + longitude;
                    Log.i("LOCATION", txt);
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (settingsOK) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);//looper: cada cuanto quiere que lo haga
            }
        }
    }

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


    //---------------------------------------------------------------CAMERA AND FILES---------------------------------------------------
    //Starts camera and the photo taken is saved on uriCamera
    private void startCamera(){
        File file = new File(getFilesDir(), "picFromCamera");
        uriCamera = FileProvider.getUriForFile(this,
                getApplicationContext().getPackageName() + ".fileprovider", file);
        mGetContentCamera.launch(uriCamera);
    }


    //Open gallery and get its uri
    ActivityResultLauncher<String> mGetContentGallery = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uriLocal) {
                    //Load image on a view…
                    setImage(uriLocal);
                    fileToUpload=uriLocal;
                }
            });

    ActivityResultLauncher<Uri> mGetContentCamera =
            registerForActivityResult(new ActivityResultContracts.TakePicture(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            //Load image on a view
                            setImage(uriCamera);
                            fileToUpload=uriCamera;
                        }
                    });


    private void setImage(Uri uri) {
        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(uri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            imgSign.setImageBitmap(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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