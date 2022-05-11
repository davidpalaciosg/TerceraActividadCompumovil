package com.palacios.terceraactividadjaveriana;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class UserMap extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);

        Intent intent = getIntent();
        String uuId = intent.getStringExtra("user");
        System.out.println("Usuario: "+uuId);
        String uuidTouched = intent.getStringExtra("userTouched");
        System.out.println("uuidTouched: "+uuidTouched);
    }
}