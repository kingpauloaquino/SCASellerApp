package com.cdgpacific.sellercatpal;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONObject;

public class ViewYardNameActivity extends AppCompatActivity {

    String URL = ""; // "http://cheappartsguy.com:8090/api/listofyard_per_user_mobile/795edd365fd0e371ceaaf1ddd559a85d/";
    public ListView listview;

    public String[] list_yard_names ;

    public SwipeRefreshLayout mySwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_yard_name);
        getSupportActionBar().hide();

        URL = Helpers.Host + "/api/listofyard_per_user_mobile/795edd365fd0e371ceaaf1ddd559a85d/";

        URL = URL + JSONHelper.Seller_UID;

        Button btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        listview = (ListView) findViewById(R.id.loadTrans);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                long data = adapter.getItemIdAtPosition(position);
                int itemPosition = (int)data;
                Log.d("SELECTED", list_yard_names[itemPosition]);
                JSONHelper.Yard_Id = list_yard_names[itemPosition];

                Intent i = new Intent(getApplicationContext(), YardDetailsActivity.class);
                startActivity(i);
                finish();
            }
        });
        loader();

        new get_list_of_yard_name().execute();
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
        return super.onKeyDown(keycode, event);
    }

    public void loader() {

        list_yard_names = new String[] {
                "Please wait...;***;***,***",
                "Please wait...;***;***,***",
                "Please wait...;***;***,***",
                "Please wait...;***;***,***"
        };

        customAdapter c_adapter = new customAdapter(this, list_yard_names);
        listview.setAdapter(c_adapter);
    }

    public class get_list_of_yard_name extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            loader();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String json = null;
            try {
                Thread.sleep(100);
                JSONHelper json_help = new JSONHelper();
                json = json_help.makeServiceCall(URL, JSONHelper.GET);
                Log.d("Response: ", "> " + json);
                return json;
            } catch (InterruptedException e) {
            }
            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(final String json) {
            populate_seller_dashboard(json);
        }
    }

    private void populate_seller_dashboard(String json) {
        if (json != null) {
            try
            {
                Log.d("json: ", json);

                JSONObject job = new JSONObject(json);
                String yard_count = job.getString("yard_count");
                Log.d("yard_count: ", yard_count);

                int yard_total = Integer.parseInt(yard_count);
                list_yard_names = new String[yard_total];
                if(yard_total > 0) {

                    for(int i = 0; i < yard_total; i++) {
                        String yard_id = job.getString("yard_id_" + i);
                        String country = job.getString("country_" + i);
                        String state = job.getString("state_" + i);
                        String city = job.getString("city_" + i);
                        list_yard_names[i] = city + ";" + state + ";" + country + ";" + yard_id ;
                    }

                    customAdapter c_adapter = new customAdapter(this, list_yard_names);
                    listview.setAdapter(c_adapter);
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
