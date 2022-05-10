package com.palacios.terceraactividadjaveriana.Classes;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.palacios.terceraactividadjaveriana.R;

import java.util.ArrayList;

public class OnlineUsersAdapter extends CursorAdapter {

    private static final int CONTACT_ID_INDEX = 0;
    private static final int DISPLAY_NAME_INDEX = 1;

    private ArrayList<User> onlineUsers;
    private ArrayList<Bitmap> onlineUsersImages;

    public OnlineUsersAdapter(Context context, Cursor c, int flags, ArrayList<User> onlineUsers, ArrayList<Bitmap> onlineUsersImages) {
        super(context, c, flags);
        this.onlineUsers = onlineUsers;
        this.onlineUsersImages = onlineUsersImages;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context)
                .inflate(R.layout.onlineuseritem, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
