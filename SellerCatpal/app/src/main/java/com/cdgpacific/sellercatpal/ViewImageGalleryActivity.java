package com.cdgpacific.sellercatpal;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewImageGalleryActivity extends ArrayAdapter<ImageGalleryItem> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageGalleryItem> data = new ArrayList<ImageGalleryItem>();

    public ViewImageGalleryActivity(Context context, int layoutResourceId, ArrayList<ImageGalleryItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        ImageGalleryItem item = data.get(position);
        holder.imageTitle.setText(item.getTitle());

        holder.image.setImageBitmap(item.getImage());
        holder.image.setMinimumWidth(item.getSizes()[0]);
        holder.image.setMinimumHeight(item.getSizes()[1]);

        return row;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image, imageGrade;
    }
}
