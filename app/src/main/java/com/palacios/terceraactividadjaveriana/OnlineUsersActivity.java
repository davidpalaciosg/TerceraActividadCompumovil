package com.palacios.terceraactividadjaveriana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.palacios.terceraactividadjaveriana.Classes.OnlineUsersAdapter;
import com.palacios.terceraactividadjaveriana.Classes.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class OnlineUsersActivity extends AppCompatActivity {

    //Firebase
    private FirebaseAuth mAuth;
    //Database
    //Root path of every user in FB
    public static final String PATH_USERS = "users/";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    private String uuId;

    //List of users

    public static ArrayList<User> users = new ArrayList<>();
    private OnlineUsersAdapter adapter;
    private ListView listOnlineUsers;
    private Button locationOnlineUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_users);

        Intent intent = getIntent();
        uuId = intent.getStringExtra("user");

        //Inflate
        listOnlineUsers = findViewById(R.id.listOnlineUsers);
        locationOnlineUser = findViewById(R.id.locationOnlineUser);
        //startButtonLocation();

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        //Database
        database = FirebaseDatabase.getInstance();
        //Storage
        mStorageRef = FirebaseStorage.getInstance().getReference();
        getOnlineUsers();
    }


    private void getOnlineUsers() {

        myRef = database.getReference(PATH_USERS);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> onlineUsers;
                onlineUsers = new ArrayList<>();
                Bitmap image = null;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    User user = singleSnapshot.getValue(User.class);
                    //if the user is online and is not the user that is logged in
                    if (user.getIsAvailable() && user.getUuid() != uuId) {
                        onlineUsers.add(user);
                        users.add(user);
                    }
                }
                users = onlineUsers;
                updateList(onlineUsers);
                startListenerListOnlineUsers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DATABASE", "error en la consulta", databaseError.toException());
            }
        });

        //return onlineUsers;
    }

    private void updateList(ArrayList<User> onlineUsers) {
        listOnlineUsers.getEmptyView();
        adapter = new OnlineUsersAdapter(OnlineUsersActivity.this, R.layout.onlineuseritem, onlineUsers);
        listOnlineUsers.setAdapter(adapter);
        startListenerListOnlineUsers();
    }

    private void startListenerListOnlineUsers() {
        listOnlineUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("LIST", "Clicked on item " + i);
                Intent intent = new Intent(getBaseContext(), UserMap.class);
                intent.putExtra("user", uuId);
                intent.putExtra("userTouched", users.get(i).getUuid());
                startActivity(intent);

            }
        });

    }


    private Bitmap getOnlineUserImage(User user) throws IOException {
        Bitmap bitmap = null;
        String imagePath = "images/profile/" + user.getUuid() + "/image.jpg";
        Log.i("DATABASE", "imagePath = " + imagePath);
        try {
            File localFile = downloadFile(imagePath);
            bitmap = fileToBitmap(localFile);
            Log.i("DATABASE", "Encontró imagen: " + localFile.getAbsolutePath());
        } catch (IOException e) {
            Log.i("DATABASE", "No se encontró imagen para el usuario: " + user.getName() + " " + user.getLastName());
        }
        return bitmap;
    }

    private Bitmap fileToBitmap(File file) {
        //Convert file to bitmap
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        return bitmap;
    }

    private File downloadFile(String locationFile) throws IOException {
        File localFile = File.createTempFile("images", "jpg");
        //StorageReference imageRef = mStorageRef.child( "images/profile/" + uuId + "/image.jpg");
        StorageReference imageRef = mStorageRef.child(locationFile);
        imageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                        Log.i("DOWNLOAD", "succesfully downloaded");
                        //UpdateUI using the localFile
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });
        return localFile;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}