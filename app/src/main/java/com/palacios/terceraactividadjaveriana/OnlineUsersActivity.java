package com.palacios.terceraactividadjaveriana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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
    private  String uuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_users);

        Intent intent = getIntent();
        uuId = intent.getStringExtra("user");

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        //Database
        database = FirebaseDatabase.getInstance();
        //Storage
        mStorageRef = FirebaseStorage.getInstance().getReference();
       getOnlineUsers();

    }


    private ArrayList<User> getOnlineUsers() {
        ArrayList<User> onlineUsers = new ArrayList<>();
        myRef = database.getReference(PATH_USERS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    User user = singleSnapshot.getValue(User.class);
                    if(user.getIsAvailable()){
                        onlineUsers.add(user);
                    }
                }
                //TODO COMPLETE
                try{
                    ArrayList<Bitmap> onlineUsersImages = getOnlineUsersImages(onlineUsers);
                    for(int i = 0; i < onlineUsersImages.size(); i++){
                        Log.d("OnlineUsersActivity", "onlineUsersImages.get(i) = " + onlineUsersImages.get(i));

                    }
                }
                catch(Exception e) {
                    Log.e("Error", e.getMessage());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DATABASE", "error en la consulta", databaseError.toException());
            }
        });

        return onlineUsers;
    }

    private ArrayList<Bitmap> getOnlineUsersImages(ArrayList<User> onlineUsers) throws IOException {
        ArrayList<Bitmap> onlineUsersImages = new ArrayList<>();
        for (User user : onlineUsers) {
            String imagePath = "images/profile/" + user.getUuid() + "/image.jpg";
            Log.i("DATABASE", "imagePath = " + imagePath);
            try{
                File localFile = downloadFile(imagePath);
                Bitmap bitmap = fileToBitmap(localFile);
                onlineUsersImages.add(bitmap);
                Log.i("DATABASE", "Encontró imagen: " + localFile.getAbsolutePath());
            }
            catch (IOException e) {
                Log.i("DATABASE", "No se encontró imagen para el usuario: " + user.getName() + " " + user.getLastName());
            }
        }
        return onlineUsersImages;
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
        StorageReference imageRef = mStorageRef.child( locationFile);
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

}