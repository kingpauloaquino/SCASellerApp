package com.cdgpacific.sellercatpal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import static android.R.attr.button;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog pDialog;

    public String URL = ""; //http://138.128.118.2:8090/api/signin/795edd365fd0e371ceaaf1ddd559a85d/";
    public EditText txtEmail, txtPassword;
    public TextView forgot_password;
    public Button btnLogin;

    public String email, password;

    public int ScreenCheckSizeIfUsing10Inches;

    public int width = 0, height = 370;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //<< this

        URL = Helpers.Host + "/api/signin/795edd365fd0e371ceaaf1ddd559a85d/";

        setContentView(R.layout.activity_login);

        ScreenCheckSizeIfUsing10Inches = Helpers.getScreenOfTablet(getApplicationContext());

        txtEmail = (EditText) findViewById(R.id.email);
        txtPassword = (EditText) findViewById(R.id.password);
        forgot_password = (TextView) findViewById(R.id.forgot_password);

        txtEmail.setText("");
        txtPassword.setText("");
        forgot_password.setPaintFlags(forgot_password.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = txtEmail.getText().toString();
                password = txtPassword.getText().toString();

                if(email == "") {
                    return;
                }
                if(password == "") {
                    return;
                }

                URL =  Helpers.Host + "/api/signin/795edd365fd0e371ceaaf1ddd559a85d/" + email + "/" + password;

                Log.d("Response: ", "> " + URL);

                new getLogin().execute();
            }
        });


        Context context = getApplicationContext();

        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
            Log.d("DEVICE", "TABLET");
            if(ScreenCheckSizeIfUsing10Inches == 1) {
                height = 570;
            }
            else if(ScreenCheckSizeIfUsing10Inches == 2) {
                height = 745;
            }
            width = 500;
        }else{
            Log.d("DEVICE", "MOBILE");
            width = 1050;
            height = 1210;
        }
    }

    public class getLogin extends AsyncTask<Void, Void, String> {
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
            loaderHide();
            validate_user_account(json);
        }
    }

    private void validate_user_account(String json) {
        String message_ = "Please connect to the internet in order to log on to your account.\n";
        if (json != null) {
            try
            {
                JSONObject job = new JSONObject(json);
                String Status = job.getString("LoginStatus");
                Log.d("Status: ", Status);

                int LoginStatus = Integer.parseInt(Status);
                if(LoginStatus == 200) {
                    String UID = job.getString("Id");
                    String ROLE = job.getString("role");

                    int user_role = Integer.parseInt(ROLE);
                    if(user_role == 5) {
                        JSONHelper.Seller_UID = UID;
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                        return;
                    }

                    message_ = "Please use your valid seller account.\n";
                }
                else {
                    message_ = "This Username and/or Password is invalid. Please try again.\n";
                }

            }catch(Exception e)
            {
                e.printStackTrace();
            }

            showDialogForDynamicError(
                    LoginActivity.this,
                    "USER LOGIN",
                    message_, width, height, 15);

        }
    }


    public void showDialogForDynamicError(Activity activity, String caption, String message, int width, int height, final int number){

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_custom_dialog_error);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.height = height;

        if(width > 0) {
            lp.width = width;
        }

        TextView title_dialog = (TextView) dialog.findViewById(R.id.title_dialog);
        TextView title_message = (TextView) dialog.findViewById(R.id.title_message);
        TextView screen_code = (TextView) dialog.findViewById(R.id.screen_code);
        Button btn_dialog_OK = (Button) dialog.findViewById(R.id.btn_dialog_OK);
        title_dialog.setText(caption);
        title_message.setText(message);
        screen_code.setText("MW-" + number);

        btn_dialog_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setAttributes(lp);
        dialog.show();
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

    private void loaderShow(String Message)
    {
        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage(Message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void loaderHide(){
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
