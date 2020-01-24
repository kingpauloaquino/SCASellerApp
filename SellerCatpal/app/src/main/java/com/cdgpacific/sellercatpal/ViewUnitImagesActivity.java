package com.cdgpacific.sellercatpal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class ViewUnitImagesActivity extends AppCompatActivity {

    private ProgressDialog pDialog;

    public String URL = ""; //http://www.cheappartsguy.com:8090/api/list_of_cats_per_yard_grader_revised/795edd365fd0e371ceaaf1ddd559a85d/";

    private GridView gridView;

    private boolean IsMobile;

    private ViewImageGalleryActivity ViewImageGalleryAdapter;

    public ArrayList<ImageGalleryItem> ImageGallery;

    Button btnBack, btnBoxInformation, btnCountDetails;
    Button btnBack2, btnBoxInformation2, btnCountDetails2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_unit_images);
        getSupportActionBar().hide();

        IsMobile = Helpers.isTabletDevice(getApplicationContext());

        URL = Helpers.Host + "/api/list_of_cats_per_yard_grader_revised/795edd365fd0e371ceaaf1ddd559a85d/" + Helpers.QR_BOX_ID;

        gridView = (GridView) findViewById(R.id.gridView);

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

        btnBoxInformation = (Button) findViewById(R.id.btnBoxInformation);
        btnBoxInformation2 = (Button) findViewById(R.id.btnBoxInformation2);
        btnBoxInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_activity(new Intent(getApplicationContext(), BoxViewActivity.class));
            }
        });
        btnBoxInformation2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_activity(new Intent(getApplicationContext(), BoxViewActivity.class));
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

        Log.d("DEVICE_TYPE", "Tablet");
        if(!IsMobile) {
            btnBack.setVisibility(View.INVISIBLE);
            btnBoxInformation.setVisibility(View.INVISIBLE);
            btnCountDetails.setVisibility(View.INVISIBLE);
        }
        else {
            btnBack2.setVisibility(View.INVISIBLE);
            btnBoxInformation2.setVisibility(View.INVISIBLE);
            btnCountDetails2.setVisibility(View.INVISIBLE);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageGalleryItem item = (ImageGalleryItem) parent.getItemAtPosition(position);

                //Create intent
                Intent intent = new Intent(ViewUnitImagesActivity.this, ViewImageFullActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("image", item.getImage());
                intent.putExtra("url", item.getUrl());

                //Start details activity
                startActivity(intent);
            }
        });

        new get_images_url().execute();
    }

    public void open_activity(Intent intent) {
        ViewUnitsActivity.BOX_UID = Helpers.QR_BOX_ID;
        startActivity(intent);
        finish();
    }

    public void do_reload() {
        ViewImageGalleryAdapter = new ViewImageGalleryActivity(this, R.layout.activity_view_image_gallery, getData());
        gridView.setAdapter(ViewImageGalleryAdapter);

        btnBack.setEnabled(true);
        btnBoxInformation.setEnabled(true);
        btnCountDetails.setEnabled(true);
    }

    private ArrayList<ImageGalleryItem> getData() {
        return ImageGallery;
    }

    public class get_images_url extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {

            btnBack.setEnabled(false);
            btnBoxInformation.setEnabled(false);
            btnCountDetails.setEnabled(false);

            loaderShow("Please wait... Downloading Images...");
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
            json_list(json);
        }
    }

    private void json_list(String json) {
        if (json != null) {
            try
            {
                JSONObject jsonObj      = new JSONObject(json);

                String img_count        = jsonObj.optString("box_count").toString();

                Log.d("Message: ", img_count);

                JSONArray jsonMainNode = jsonObj.optJSONArray("list_of_all_box");

                int lengthJsonArr = jsonMainNode.length();

                Log.d("Message: ", lengthJsonArr + "");

                Integer i_count = Integer.parseInt(img_count);

                Helpers.ImageUrlList = new String[i_count];

                for(int i = 0; i < lengthJsonArr; i++)
                {
                    /****** Get Object for each JSON node.***********/
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    /******* Fetch node values **********/
                    String cats_unique_number   = jsonChildNode.optString("cats_unique_number").toString();
                    String cats_content         = jsonChildNode.optString("cats_content").toString();
                    String img_url              = jsonChildNode.optString("img_url").toString();

                    Helpers.ImageUrlList[i] = cats_unique_number + ";" + cats_content + ";" + img_url;

                    Log.d("Message: ", img_url);
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }

            new DownloadImageTask().execute();
        }
    }

    private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(Void... urls) {

            Bitmap bitmap = null;
            try {

                ImageGallery = new ArrayList<>();

//                Helpers.ImageGalleryContainer = new ArrayList<>();

                int t_count_img = Helpers.ImageUrlList.length;



                Log.d("DEVICE_TYPE", "Tablet");
                int[] default_imgsize = new int[] {160, 120};
                if(!IsMobile) {
                    default_imgsize = new int[] {512, 384};
                    Log.d("DEVICE_TYPE", "Mobile");
                }

                for (int i = 0; i < t_count_img; i++) {
                    String[] img_data =  Helpers.ImageUrlList[i].split(";");

                    String img_id = img_data[0];
                    int grade_id = Integer.parseInt(img_data[1]);
                    String img_url = img_data[2];

                    Log.d("DONE", img_url);

                    bitmap = do_downloading(img_url);
                    Log.d("DONE", bitmap + "");

                    ImageGallery.add(new ImageGalleryItem(bitmap, "#" +img_id, grade_id, img_url, default_imgsize));

//                    Helpers.ImageGalleryContainer.add(new ImageGalleryItem(bitmap, "#" +img_id, grade_id, img_url, new int[] {210, 170}));

                    Thread.sleep(200);
                }
            }
            catch (Exception ex) { }

            return bitmap;
        }

        public Bitmap do_downloading(String url) {
            String urldisplay = url;
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            Log.d("DONE", "YES");
            do_reload();
            loaderHide();
        }
    }

    private void loaderShow(String Message) {
        pDialog = new ProgressDialog(ViewUnitImagesActivity.this);
        pDialog.setMessage(Message);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void loaderHide(){
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
