package com.cdgpacific.sellercatpal;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by kingp on 9/23/2016.
 */
public class BackgroundService extends IntentService {

    public static SQLiteDatabase sqlDB;
    public MySqlLite mysql;
    public static boolean RESET_DB;
    private static final String TAG = "HelloService";
    public static boolean isRunning  = false;
    public static int counter  = 0;
    public static int laps  = 1;
    public String API_URL;

    public static Integer Worker_uid;
    public static String Token = "795edd365fd0e371ceaaf1ddd559a85d";
    public static String Host = "http://api.scrapcatapp.com";
    public static String Url_get_all_boxes = "/api/list_of_boxes_mobile/"; // parameter token + user_id

    public BackgroundService() {
        super(BackgroundService.class.getName());
        isRunning = false;
        Log.i(TAG, "Service onCreate");
        API_URL = Host + Url_get_all_boxes + Token + "/" + JSONHelper.Seller_UID;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Service Started!");
        try {
//            customHandler.postDelayed(updateTimerThread, 0);
            do_async();
            Log.d(TAG, "json -> " + API_URL);
        } catch (Exception e) {
            Log.d(TAG, "status -> " + e.toString());
        }
        this.stopSelf();
    }

    public static boolean hostAvailableisOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    private Handler customHandler = new Handler();
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            isRunning = true;
            Log.d("TIMER_Handler", "Counter " + counter);
            if(counter == laps) {
                boolean hasInternet = hostAvailableisOnline();
                Log.d("Response: ", "> " + hasInternet);
                if(hasInternet) {
                    customHandler.removeCallbacks(updateTimerThread);
                    Log.d("TIMER_Handler", "SHOULD BE STOPPED");
                    do_async();
                    return;
                }
                laps = 60 * 10;
                counter = 0;
                customHandler.postDelayed(updateTimerThread, 0);
                return;
            }
            counter++;
            customHandler.postDelayed(this, 1000);
        }
    };

    public void do_async() {
        new do_fetching().execute();
    }

    public class do_fetching extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String json = null;
            try {
                Thread.sleep(100);
                JSONHelper json_help = new JSONHelper();
                Log.d("Response: ", "> " + API_URL);
                json = json_help.makeServiceCall(API_URL, JSONHelper.GET);
                Log.d("Response: ", "> " + json);
                return json;
            } catch (InterruptedException e) {
            }
            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(final String json) {
            do_update_db(json);
        }

    }

    public void do_update_db(String json) {
        if (json != null) {
            try
            {
                JSONObject job = new JSONObject(json);
                String box_counts = job.getString("box_count");
                Log.d("BOX Count: ", box_counts);

                int box_length = Integer.parseInt(box_counts);
                if(box_length > 0) {
                    sqlDB = openOrCreateDatabase(MySqlLite.DB_NAME, Context.MODE_PRIVATE, null);
                    mysql = new MySqlLite(sqlDB);
                    for(int i = 0; i < box_length; i++) {
                        String box_uid = job.getString("Id_" + i);
                        String worker_id = job.getString("worker_of_" + i);
                        String yard_id = job.getString("yard_id_" + i);
                        String box_id = job.getString("box_id_" + i);
                        String box_status = job.getString("box_status_" + i);

                        Log.d("Results: box_uid", "> " + box_uid );
                        Log.d("Results: worker_id", "> " + worker_id );
                        Log.d("Results: yard_id", "> " + yard_id );
                        Log.d("Results: box_id", "> " + box_id );
                        Log.d("Results: box_status", "> " + box_status );
                        mysql.insert("list_of_boxes", "'" + box_uid + "', '" + worker_id + "', '" + yard_id + "', '" + box_id + "', '" + box_status + "'");
                    }
                }

            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        isRunning = false;
        laps = 60 * 10;
        counter = 0;
    }

}