package com.palacios.terceraactividadjaveriana.Classes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.palacios.terceraactividadjaveriana.OnlineUsersActivity;
import com.palacios.terceraactividadjaveriana.R;
import com.palacios.terceraactividadjaveriana.UserMap;

import java.util.ArrayList;

public class OnlineUsersAdapter extends ArrayAdapter<User> {

    private  Context mContext;
    int mResource;

    public OnlineUsersAdapter(Context context, int resource, ArrayList<User> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String name = capitalizeString(getItem(position).getName());
        String lastName = capitalizeString(getItem(position).getLastName());
        Bitmap image = getItem(position).getUserImage();

        String uuId = getItem(position).getUuid();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView nameOnlineUser = (TextView) convertView.findViewById(R.id.nameOnlineUser);
        ImageView imgOnlineUser = (ImageView) convertView.findViewById(R.id.imgOnlineUser);
        Button locationOnlineUser = (Button) convertView.findViewById(R.id.locationOnlineUser);

        nameOnlineUser.setText(name+" "+lastName);
        if(image != null) {
            System.out.println("Entro a la imagen");
            imgOnlineUser.setImageBitmap(image);
        }

        locationOnlineUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UserMap.class);
                intent.putExtra("user", uuId);
            }
        });

        return convertView;
    }
    private String capitalizeString(String str)
    {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


}
