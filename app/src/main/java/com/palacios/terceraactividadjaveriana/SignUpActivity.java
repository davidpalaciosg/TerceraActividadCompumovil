package com.palacios.terceraactividadjaveriana;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SignUpActivity extends AppCompatActivity {
    //Firebase
    private FirebaseAuth mAuth;

    //Database
    //Root path of every user in FB
    public static final String PATH_USERS="users/";
    FirebaseDatabase database;
    DatabaseReference myRef;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        //Database
        database= FirebaseDatabase.getInstance();

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
                //TODO
            }
        });
    }













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
                }
            });

    ActivityResultLauncher<Uri> mGetContentCamera =
            registerForActivityResult(new ActivityResultContracts.TakePicture(),
                    new ActivityResultCallback<Boolean>() {
                        @Override
                        public void onActivityResult(Boolean result) {
                            //Load image on a view
                            setImage(uriCamera);
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

}