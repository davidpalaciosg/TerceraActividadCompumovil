package com.palacios.terceraactividadjaveriana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //Layout
    EditText txtEmail;
    EditText txtPassword;
    Button btnLogin;
    Button btnSignUp;

    //Firebase
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inflate
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        mAuth = FirebaseAuth.getInstance();

        startBtnLogin();
        startBtnSignUp();

    }

    public boolean verifyEmailAndPassword(String email, String password) {
        //Verify email with regex
        if (email.matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$")) {
            //Verify password lenght
            if (password.length() > 6)
                return true;
        }
        return false;
    }


    private void startBtnSignUp() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                //startActivity(intent);
            }
        });
    }

    private void startBtnLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString().trim().toLowerCase(Locale.ROOT);
                String password = txtPassword.getText().toString().trim().toLowerCase(Locale.ROOT);
                if (verifyEmailAndPassword(email, password)) {
                    //Try to login
                    tryToLogin(email, password);
                }
                else {
                    txtEmail.setError("Required");
                    txtPassword.setError("Required");
                    Toast.makeText(MainActivity.this, "Email or password is not valid", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void tryToLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            Log.d("LOGIN", "signInWithEmail:onComplete:" + task.isSuccessful());
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else {
                            Log.w("LOGIN",
                                    "signInWithEmail:failed"
                                    , task.getException());
                            Toast.makeText(MainActivity.this, "Was not able to login", Toast.LENGTH_SHORT).show();
                            txtEmail.setText("");
                            txtPassword.setText("");
                        }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(getBaseContext(), MapsActivity.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        } else {
            txtEmail.setText("");
            txtPassword.setText("");
        }
    }

}