package com.cdgpacific.sellercatpal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class YardDetailsActivity extends AppCompatActivity {

    String URL = ""; //http://cheappartsguy.com:8090/api/details_per_yard_mobile_v2/795edd365fd0e371ceaaf1ddd559a85d/";

    TextView a, b, c, d, e, f, txtAppraisedValue;

    TextView txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yard_details);

        getSupportActionBar().hide();

        URL = Helpers.Host + "/api/details_per_yard_mobile_v2/795edd365fd0e371ceaaf1ddd559a85d/";

        reset_text(false);

        txtTitle = (TextView) findViewById(R.id.txtTitle);

        Log.d("YARD", JSONHelper.Yard_Id);

        if(JSONHelper.Yard_Id != null) {
            String[] yard_id = JSONHelper.Yard_Id.split(";");
            if(yard_id.length > 3) {
                txtTitle.setText(yard_id[0]);
                URL = URL + JSONHelper.Seller_UID + "/" + yard_id[3];
            }
        }

        Button btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ViewYardNameActivity.class);
                startActivity(i);
                finish();
            }
        });

        new get_yard_details().execute();
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(getApplicationContext(), ViewYardNameActivity.class);
            startActivity(i);
            finish();
        }
        return super.onKeyDown(keycode, event);
    }

    public void reset_text(boolean IsTrue) {

        if(!IsTrue) {
            a = (TextView) findViewById(R.id.boxA);
            b = (TextView) findViewById(R.id.boxB);
            c = (TextView) findViewById(R.id.boxC);
            d = (TextView) findViewById(R.id.boxD);
            e = (TextView) findViewById(R.id.boxE);
            f = (TextView) findViewById(R.id.boxF);
            txtAppraisedValue = (TextView) findViewById(R.id.txtAppraisedValue);
        }

        a.setText("***");
        b.setText("***");
        c.setText("***");
        d.setText("***");
        e.setText("***");
        f.setText("***");
        txtAppraisedValue.setText("***");
    }

    public void set_value(String a_, String b_, String c_, String d_, String e_,  String f_) {

        a.setText(a_);
        b.setText(b_);
        c.setText(c_);
        e.setText(e_);
        f.setText(f_);

        DecimalFormat formatter = new DecimalFormat("#,###,###.##");
        Double x_a =  Double.parseDouble(d_);
        d.setText("$" + formatter.format(x_a));

        formatter = new DecimalFormat("#,###,###.##");
        Double x_f =  Double.parseDouble(f_);
        f.setText("$" + formatter.format(x_f));
        txtAppraisedValue.setText("$" + formatter.format(x_a));
    }

    public class get_yard_details extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            reset_text(true);
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
                Log.d("Response: ", "> " + json.length());
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

        Log.d("json_value: ", json);

        if(json.length() == 0) {
            messageAlertMessage("No records found.", "Information");
            return;
        }

        if (json != null) {
            try
            {
                JSONObject job = new JSONObject(json);
                String yard_count = job.getString("yard_count");
                Log.d("yard_count: ", yard_count);

                int IsPassed = Integer.parseInt(yard_count);
                if(IsPassed > 0) {

                    String number_of_box = job.getString("number_of_box_0");
                    String number_of_units = job.getString("number_of_units_0");
                    String number_of_units_appraised = job.getString("number_of_units_appraised_0");
                    String appraisal_value = job.getString("appraisal_value_0");
                    String appraisal_status = job.getString("appraisal_status_0");
                    String average_price_of_appraised_units_0 = job.getString("average_price_of_appraised_units_0");

                    set_value(
                            number_of_box,
                            number_of_units,
                            number_of_units_appraised,
                            appraisal_value,
                            appraisal_status,
                            average_price_of_appraised_units_0
                    );
                }

            }catch(Exception e)
            {
                e.printStackTrace();

                Log.d("Exception: ", e.getMessage());
            }
        }
    }

    private void messageAlertMessage(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                        Intent i = new Intent(getApplicationContext(), ViewYardNameActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
