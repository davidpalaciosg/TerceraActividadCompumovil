package com.palacios.terceraactividadjaveriana;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    //Firebase
    private FirebaseAuth mAuth;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
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


    }
}