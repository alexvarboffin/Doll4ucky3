package com.drawfanatteberad.mortalfived.utlis;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.drawfanatteberad.mortalfived.R;

public class Utils {

    static String MARKET = "https://play.google.com/store/apps/details?id=";

    public static void openAppOnPlayMarket(Context context) {
        try {
            String url = MARKET + context.getPackageName();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }
}
