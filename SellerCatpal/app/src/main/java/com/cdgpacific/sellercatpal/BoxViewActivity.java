package com.cdgpacific.sellercatpal;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class BoxViewActivity extends AppCompatActivity {

    public TextView txtA, txtB, txtC, txtD, txtE, txtF, txtG, txtH, txtI, txtJ;

    ScrollView scrollId;

    private boolean IsMobile;

    Button btnBack, btnViewUnits, btnCountDetails;
    Button btnBack2, btnViewUnits2, btnCountDetails2;

    public static String BOX_UID;

    String URL = ""; //http://cheappartsguy.com:8090/api/box_information_mobile_v2/795edd365fd0e371ceaaf1ddd559a85d/" + BOX_UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_box_view);

        URL = Helpers.Host + "/api/box_information_mobile_v2/795edd365fd0e371ceaaf1ddd559a85d/" + BOX_UID;

        IsMobile = Helpers.isTabletDevice(getApplicationContext());

        reset_text(false);

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack2 = (Button) findViewById(R.id.btnBack2);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_activity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        btnBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_activity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        btnViewUnits = (Button) findViewById(R.id.btnViewUnits);
        btnViewUnits2 = (Button) findViewById(R.id.btnViewUnits2);
        btnViewUnits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_activity(new Intent(getApplicationContext(), ViewUnitImagesActivity.class));
            }
        });
        btnViewUnits2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_activity(new Intent(getApplicationContext(), ViewUnitImagesActivity.class));
            }
        });

        btnCountDetails = (Button) findViewById(R.id.btnCountDetails);
        btnCountDetails2 = (Button) findViewById(R.id.btnCountDetails2);
        btnCountDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_activity(new Intent(getApplicationContext(), ViewUnitsActivity.class));
            }
        });
        btnCountDetails2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_activity(new Intent(getApplicationContext(), ViewUnitsActivity.class));
            }
        });

        scrollId = (ScrollView) findViewById(R.id.scrollViewId);
        boolean IsTablet = Helpers.isTabletDevice(getApplicationContext());
        if(IsTablet) {
            scrollId.getLayoutParams().height = 600;
            btnBack2.setVisibility(View.INVISIBLE);
            btnViewUnits2.setVisibility(View.INVISIBLE);
            btnCountDetails2.setVisibility(View.INVISIBLE);
        }
        else {
            btnBack.setVisibility(View.INVISIBLE);
            btnViewUnits.setVisibility(View.INVISIBLE);
            btnCountDetails.setVisibility(View.INVISIBLE);
        }

        new get_box_information().execute();
    }

    public void open_activity(Intent intent) {
        ViewUnitsActivity.BOX_UID = BOX_UID;
        startActivity(intent);
        finish();
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
        return super.onKeyDown(keycode, event);
    }

    public void reset_text(boolean IsTrue) {

        if(!IsTrue) {
            txtA = (TextView) findViewById(R.id.boxA);
            txtB = (TextView) findViewById(R.id.boxB);
            txtC = (TextView) findViewById(R.id.boxC);
            txtD = (TextView) findViewById(R.id.boxD);
            txtE = (TextView) findViewById(R.id.boxE);
            txtF = (TextView) findViewById(R.id.boxF);
            txtG = (TextView) findViewById(R.id.boxG);
            txtH = (TextView) findViewById(R.id.boxH);
            txtI = (TextView) findViewById(R.id.boxI);
            txtJ = (TextView) findViewById(R.id.boxJ);
        }

        txtA.setText("***");
        txtB.setText("***");
        txtC.setText("***");
        txtD.setText("***");
        txtE.setText("***");
        txtF.setText("***");
        txtG.setText("***");
        txtH.setText("***");
        txtI.setText("***");
        txtJ.setText("***");
    }

    public void set_value(String a_, String b_, String c_, String d_, String e_, String f_, String g_, String h_, String i_, String j_) {
        txtA.setText(a_);
        txtB.setText(b_);
        txtC.setText(c_);
        txtD.setText(d_);
        txtE.setText(e_);
        txtF.setText(f_);
        txtG.setText(g_);
        txtH.setText(h_);
        txtI.setText(i_);
        txtJ.setText(j_);

        DecimalFormat formatter = new DecimalFormat("#,###,###.##");
        Double x_a =  Double.parseDouble(i_);
        txtI.setText("$" + formatter.format(x_a));
    }

    public class get_box_information extends AsyncTask<Void, Void, String> {
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
                Log.d("Response: ", "> " + URL);
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
                JSONObject job = new JSONObject(json);
                String box_uid = job.getString("box_uid");
                Log.d("box_uid: ", box_uid);

                int IsPassed = Integer.parseInt(box_uid);
                if(IsPassed > 0) {
                    String a = job.getString("account_name");
                    String b = job.getString("yard_name");
                    String c = job.getString("box_code");
                    String d = job.getString("units_in_the_box");
                    String e = job.getString("box_status");
                    String f = job.getString("last_synch_by");
                    String g = job.getString("last_synch_time");
                    String h = job.getString("appraisal_complete");
                    String i = job.getString("appraisal_value");
                    String j = job.getString("included_in_lot_number");

                    set_value(
                            a,
                            b,
                            c,
                            d,
                            e,
                            f,
                            g,
                            h,
                            i,
                            j
                    );
                }

                return;
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
