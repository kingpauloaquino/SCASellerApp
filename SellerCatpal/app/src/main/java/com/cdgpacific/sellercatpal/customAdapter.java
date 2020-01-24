package com.cdgpacific.sellercatpal;

/**
 * Created by kingp on 10/17/2016.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class customAdapter extends ArrayAdapter<String>  {
    private final Context context;
    private final String[] values;

    public customAdapter(Context context, String[] values) {
        super(context, R.layout.listyardnames, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listyardnames, parent, false);
        TextView textView1 = (TextView) rowView.findViewById(R.id.firstLine);
        TextView textView2 = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        String[] splits = values[position].split(";");
        textView1.setText(splits[0]);
//        textView2.setText(splits[1] + ", " + splits[2]);
        textView2.setText("");

        imageView.setImageResource(R.drawable.yard_icon);

        return rowView;
    }
}
