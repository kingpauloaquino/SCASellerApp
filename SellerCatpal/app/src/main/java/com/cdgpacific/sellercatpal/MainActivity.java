package com.cdgpacific.sellercatpal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";

    private ProgressDialog pDialog;

    public SQLiteDatabase sqlDB;
    public MySqlLite mysql;
    public String MESSAGE = null;
    public View _VIEW;

    TextView a, b, c, d, e, f;

    String URL = ""; //http://cheappartsguy.com:8090/api/summary/seller_dashboard_mobile/795edd365fd0e371ceaaf1ddd559a85d/";

    ScrollView scrollId;

    Button btnBoxInformation, btnLogout, btnViewYard;
    Button btnBoxInformation2, btnLogout2, btnViewYard2;

    public int ScreenCheckSizeIfUsing10Inches;

    public boolean isRefresh = false;

    public SwipeRefreshLayout mySwipeRefreshLayout;

    public SQLiteDatabase CreatedDB() {
        sqlDB = openOrCreateDatabase(MySqlLite.DB_NAME, Context.MODE_PRIVATE, null);
        mysql = new MySqlLite(sqlDB);
        mysql.execute("DROP TABLE IF EXISTS list_of_boxes;");
        return sqlDB;
    }

    public void InitService() {
        CreatedDB();
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, BackgroundService.class);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        URL = Helpers.Host + "/api/summary/seller_dashboard_mobile/795edd365fd0e371ceaaf1ddd559a85d/";

        if(JSONHelper.Seller_UID == null) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
            return;
        }

        ScreenCheckSizeIfUsing10Inches = Helpers.getScreenOfTablet(getApplicationContext());

        URL = URL + JSONHelper.Seller_UID;

        reset_text(false);

        btnBoxInformation = (Button) findViewById(R.id.btnBoxInformation);
        btnBoxInformation2 = (Button) findViewById(R.id.btnBoxInformation2);
        btnBoxInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                _VIEW = view;
//                ShowBoxViewInformation("Position QR Code completely inside the scanner window. When QR code is recognized you will receive a confirmation screen.", "Scan QR Box Code");

                _VIEW = view;

                String message_ = "Position QR Code completely inside the scanner window. ";
                message_ += "When QR code is recognized you will receive a confirmation screen.\n";

                int resize_screen = 400;
                if(ScreenCheckSizeIfUsing10Inches == 1) {
                    resize_screen = 870;
                }
                else if(ScreenCheckSizeIfUsing10Inches == 2) {
                    resize_screen = 1160;
                }

                Log.d("SCREEN_XX", resize_screen + "");

                showDialogForDynamic(
                        MainActivity.this,
                        "Scan QR Box Code",
                        message_, resize_screen,
                        "YES, SCAN BOX QR CODE", "NO, BACK TO DASHBOARD", 2);


            }
        });
        btnBoxInformation2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _VIEW = view;
                ShowBoxViewInformation("Position QR Code completely inside the scanner window. When QR code is recognized you will receive a confirmation screen.", "Scan QR Box Code");
            }
        });

        btnViewYard = (Button) findViewById(R.id.btnViewYard);
        btnViewYard2 = (Button) findViewById(R.id.btnViewYard2);
        btnViewYard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_activity(new Intent(getApplicationContext(), ViewYardNameActivity.class));
            }
        });
        btnViewYard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_activity(new Intent(getApplicationContext(), ViewYardNameActivity.class));
            }
        });

        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnLogout2 = (Button) findViewById(R.id.btnLogout2);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONHelper.Seller_UID = null;
                open_activity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
        btnLogout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONHelper.Seller_UID = null;
                open_activity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        scrollId = (ScrollView) findViewById(R.id.scrollId);
        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("REFRESH", "onRefresh called from SwipeRefreshLayout");
                        new get_seller_dashboard().execute();
                    }
                }
        );

        boolean IsTablet = Helpers.isTabletDevice(getApplicationContext());
        if(IsTablet) {
            scrollId.getLayoutParams().height = 450;
            btnBoxInformation2.setVisibility(View.INVISIBLE);
            btnViewYard2.setVisibility(View.INVISIBLE);
            btnLogout2.setVisibility(View.INVISIBLE);
        }
        else {
            btnBoxInformation.setVisibility(View.INVISIBLE);
            btnViewYard.setVisibility(View.INVISIBLE);
            btnLogout.setVisibility(View.INVISIBLE);
        }
        new get_seller_dashboard().execute();
    }

    public void open_activity(Intent intent) {
        startActivity(intent);
        finish();
    }

    private void ShowBoxViewInformation(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message).setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                        SCAN_QR_CODE(_VIEW,
                                "Confirm Box #"
                        );
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

        Button bq = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        bq.setTextColor(Color.BLACK);
        bq.setBackgroundColor(Color.LTGRAY);
    }

    public void showDialogForQRScanned(Activity activity, String title, String message){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_confirmation_dialog_catpal);

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        title_dialog.setText(title);

        TextView text_dialog = (TextView) dialog.findViewById(R.id.text_dialog);
        text_dialog.setText(message);

        Button btn_dialog_YES = (Button) dialog.findViewById(R.id.btn_dialog_YES);
        btn_dialog_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btn_dialog_NO = (Button) dialog.findViewById(R.id.btn_dialog_NO);
        btn_dialog_NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void SCAN_QR_CODE(View v, String message) {
        try {
            _VIEW = v;
            MESSAGE = message;
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");

            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            showDialog(MainActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    public String SCAN_RESULT = null;
    public String SCAN_RESULT_FORMAT = null;
    public String SCAN_URL = ""; //http://cheappartsguy.com:8090/api/box_information_mobile_v2/795edd365fd0e371ceaaf1ddd559a85d/";

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        SCAN_URL = Helpers.Host + "/api/box_information_mobile_v2/795edd365fd0e371ceaaf1ddd559a85d/";

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                SCAN_RESULT = intent.getStringExtra("SCAN_RESULT");
                SCAN_RESULT_FORMAT = intent.getStringExtra("SCAN_RESULT_FORMAT");

                int box_uid = 0;
                String[] results = null;
                try {
                    results = SCAN_RESULT.split("-");
                    Helpers.QR_BOX_ID = results[0];
                    Helpers.QR_BOX_SHOW_ID = results[1];

//                    box_uid = mysql.findListOfBoxes(Helpers.QR_BOX_ID);
                    SCAN_URL = SCAN_URL + Helpers.QR_BOX_ID;

                    Log.d("TYPE: ", SCAN_URL);

                    new get_box_info().execute();
                }
                catch (Exception ex) {
                    Log.d("TYPE: ", ">on " + ex);
                }
            }
        }
    }

    public class get_box_info extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            loaderShow("Please wait...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String json = null;
            try {
                Thread.sleep(100);
                JSONHelper json_help = new JSONHelper();
                json = json_help.makeServiceCall(SCAN_URL, JSONHelper.GET);
                Log.d("Response: ", "> " + json);
                return json;
            } catch (InterruptedException e) {
            }
            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(final String json) {
            box_validate(json);
        }
    }

    private void box_validate(String json) {
        loaderHide();
        if (json != null) {
            try
            {
                JSONObject job = new JSONObject(json);
                String member_id = job.getString("seller_name");
                Log.d("seller_name: ", member_id);

                int uid = Integer.parseInt(member_id);
                if(uid > 0) {
                    if(uid != Integer.parseInt(JSONHelper.Seller_UID)) {
                        messageAlertMessage("You are not able to view this box#: "+ Helpers.QR_BOX_SHOW_ID + " information.", "INFORMATION");
                        return;
                    }
                    view_box_info(Helpers.QR_BOX_ID);
                }
                else {
                    messageAlertMessage("Oops, Invalid QR Code information.", "INFORMATION");
                }

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void view_box_info(String box_uid) {
        Log.d("BOX_UID: ", "> " + box_uid);
        BoxViewActivity.BOX_UID = box_uid;
        Intent intent = new Intent(getApplicationContext(), BoxViewActivity.class);
        startActivity(intent);
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    public void reset_text(boolean IsTrue) {

        if(!IsTrue) {
            a = (TextView) findViewById(R.id.numberA);
            b = (TextView) findViewById(R.id.numberB);
            c = (TextView) findViewById(R.id.numberC);
            d = (TextView) findViewById(R.id.numberD);
            e = (TextView) findViewById(R.id.numberE);
            f = (TextView) findViewById(R.id.numberF);
        }

        a.setText("***");
        b.setText("***");
        c.setText("***");
        d.setText("***");
        e.setText("***");
        f.setText("***");
    }

    public void set_value(String a_, String b_, String c_, String d_, String e_, String f_) {

        a.setText(a_);
        b.setText(b_);
        c.setText(c_);

        DecimalFormat formatter = new DecimalFormat("#,###,###.##");

        Double x_a =  Double.parseDouble(d_);
        d.setText("$" + formatter.format(x_a));

        Double x_b = Double.parseDouble(e_);
        e.setText("$" +  formatter.format(x_b));

        Double x_c =  Double.parseDouble(f_);
        f.setText("$" +  formatter.format(x_c));
    }

    public class get_seller_dashboard extends AsyncTask<Void, Void, String> {
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
                Log.d("Response: ", "> " + URL);
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
                String member_id = job.getString("member_id");
                Log.d("member_id: ", member_id);

                String total_number_of_units = "N/A";
                String total_number_of_units_appraised = "N/A";
                String total_number_of_units_need_to_appraised = "N/A";
                String avg_price_per_unit_of_appraised_units = "0";
                String total_appraisal_value = "0";

                int IsPassed = Integer.parseInt(member_id);
                if(IsPassed > 0) {
                    total_number_of_units = job.getString("total_number_of_units");
                    total_number_of_units_appraised = job.getString("total_number_of_units_appraised");
                    total_number_of_units_need_to_appraised = job.getString("total_number_of_units_need_to_appraised");
                    avg_price_per_unit_of_appraised_units = job.getString("avg_price_per_unit_of_appraised_units");
                    total_appraisal_value = job.getString("total_appraisal_value");
                }

                set_value(
                        total_number_of_units,
                        total_number_of_units_appraised,
                        total_number_of_units_need_to_appraised,
                        avg_price_per_unit_of_appraised_units,
                        total_appraisal_value,
                        total_appraisal_value
                );

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        mySwipeRefreshLayout.setRefreshing(false);
    }

    private void messageAlert(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void messageAlertMessage(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showDialogForDynamic(Activity activity, String caption, String message, int height, String yes_button, String no_button, final int number){

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_custom_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.height = height;

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        TextView title_message = (TextView) dialog.findViewById(R.id.title_message);
        TextView screen_code = (TextView) dialog.findViewById(R.id.screen_code);
        Button btn_dialog_YES = (Button) dialog.findViewById(R.id.btn_dialog_YES);
        Button btn_dialog_NO = (Button) dialog.findViewById(R.id.btn_dialog_NO);

        title_dialog.setText(caption);
        title_message.setText(message);
        screen_code.setText("MS-" + number);
        btn_dialog_YES.setText(yes_button);
        btn_dialog_NO.setText(no_button);

        btn_dialog_YES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if(number == 2) {
                    SCAN_QR_CODE(_VIEW,
                            "Confirm Box #"
                    );
                }
            }
        });
        btn_dialog_NO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void loaderShow(String Message)
    {
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage(Message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void loaderHide(){
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
