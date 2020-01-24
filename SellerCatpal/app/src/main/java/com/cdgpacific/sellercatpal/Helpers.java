package com.cdgpacific.sellercatpal;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Created by kingp on 10/5/2016.
 */
public class Helpers {

    public static String YardId;

    public static String Host = "https://sca-live-api.2kpa.me";

    public static String QR_BOX_ID = null;
    public static String QR_BOX_SHOW_ID = null;

    public static ArrayList<ImageGalleryItem> ImageGalleryContainer;
    public static String[] ImageUrlList;

    public static boolean isTabletDevice(Context mContext) {
        TelephonyManager telephony = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        int type = telephony.getPhoneType();

        if (type == TelephonyManager.PHONE_TYPE_NONE) {
            return true;
        }
        return false;
    }

    public static int getScreenOfTablet(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        int screen_id = 0;

        if(width >= 1900 && width < 2000) {
            screen_id = 1;
        }

        else if(width >= 2000 && width < 2500) {
            screen_id = 2;
        }

        return screen_id;
    }
}
