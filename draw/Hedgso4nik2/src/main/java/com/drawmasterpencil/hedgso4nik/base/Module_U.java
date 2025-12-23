package com.drawmasterpencil.hedgso4nik.base;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.drawmasterpencil.hedgso4nik.R;
import com.drawmasterpencil.hedgso4nik.utlis.DLog;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Module_U {

    private static final String PKG_NAME_VENDING = "com.android.vending";


    //Show me the magik...

    private static boolean isFromGooglePlay(Context context, String pName) {
        PackageManager pm = context.getPackageManager();
        String name = pm.getInstallerPackageName(pName);
        // Installed from the Google Play
        if (name == null) {
            // Definitely not from Google Play
            return false;
        } else return PKG_NAME_VENDING.equals(name)
                || "com.com.google.android.feedback".equals(name);
    }

    private static boolean isFromGooglePlay(Context context) {
        return isFromGooglePlay(context, context.getPackageName());
    }

    public static void aboutDialog(Context context) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        //&#169; - html
        String title = "\u00a9 " + year + " " + context.getString(R.string.play_google_pub);

        View mView = LayoutInflater.from(context).inflate(R.layout.about, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(null)
                .setCancelable(true)
                .setIcon(null)
                //.setPositiveButton(button, null)
                .setView(mView)
                .create();
        mView.setOnClickListener(v -> dialog.dismiss());
        TextView textView = mView.findViewById(R.id.about_version);
        textView.setText(DLog.getAppVersion(context));
        TextView _c = mView.findViewById(R.id.about_copyright);
        _c.setText(title);
        ImageView logo = mView.findViewById(R.id.aboutLogo);
        logo.setOnLongClickListener(v -> {
            String _o = "[+]gp->" + isFromGooglePlay(mView.getContext());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                _o = _o + ", category->" + mView.getContext().getApplicationInfo().category;
            }
            _c.setText(_o);
            return false;
        });
        dialog.show();
    }


    public static Map<String, String> anomaly(Context context) {

        String current_package_name = "com.walhall.123";

        Map<String, String> map = new HashMap<>();
        ApplicationInfo info = context.getApplicationInfo();
        String processName = info.processName;
        String pn = context.getPackageName();
        if (!pn.equals(current_package_name)) {
            map.put("packageName", current_package_name + "::" + pn);
        }
        if (!pn.equals(processName)) {
            map.put("processName", processName + "::" + pn);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int category = info.category;
            if (category == -1) {
                map.put("category", "" + category);
            }
        }
        PackageManager pm = context.getPackageManager();
        String installer = pm.getInstallerPackageName(pn);
        map.put("installer", "" + installer);

        Signature[] sigs = new Signature[0];
        try {
            sigs = pm.getPackageInfo(pn, PackageManager.GET_SIGNATURES).signatures;
            if (sigs == null || sigs.length <= 0) {
            } else {
                for (Signature signature : sigs) {

                    X509Certificate x509Certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(signature.toByteArray()));
                    //DLog.d("@@123" + x509Certificate);
                    DLog.d("@@123" + x509Certificate.getSerialNumber().toString());//1231018131612

                    String[] mm = x509Certificate.getIssuerX500Principal().toString().split(", ");
                    for (int i = 0; i < mm.length; i++) {
                        String[] aaa = mm[i].trim().split("=");
                        if (aaa.length == 2) {
                            DLog.d(aaa[0] + "=>" + aaa[1]);
                        }
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * more_apps_link = "https://play.google.com/store/apps/dev?id=5700313618786177705"
     */
    public static void moreApp(Context context) {
        final String pub = context.getString(R.string.play_google_pub);
        try {
            context.startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:" + pub)));

        } catch (android.content.ActivityNotFoundException anfe) {
            openBrowser(context, "https://play.google.com/store/search?q=pub:" + pub);
        }
    }

    public static void openMarket(Context context, String packageName) {
        try {
            Uri uri = Uri.parse(UConst.MARKET_CONSTANT + packageName);
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (android.content.ActivityNotFoundException anfe) {
            openBrowser(context, UConst.GOOGLE_PLAY_CONSTANT + packageName);
        }
    }

    public static void rateUs(Context context) {
        String packageName = context.getPackageName();
        openMarket(context, packageName);
    }

    public static void openBrowser(Context context, String data) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Browser not found", Toast.LENGTH_SHORT).show();
        }
    }


    public static void feedback(Context context) {

        try {
            String subject = Uri.encode(context.getPackageName()) + "_" + DLog.getAppVersion(context);
            DLog.d(subject + "\t" + context.getString(R.string.publisher_feedback_email));


//            intent.setData(Uri.parse("mailto:" + PublisherConfig.FEEDBACK_EMAIL +
//                    "?share_subject=" + Uri.encode(context.getPackageName())));

            composeEmail(context, new String[]{context.getString(R.string.publisher_feedback_email)}, subject);
        } catch (Exception e) {
            DLog.handleException(e);
            Toast.makeText(context, "e-mail client not found", Toast.LENGTH_LONG).show();
        }
    }

    private static void composeEmail(Context context, String[] addresses, String subject) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, addresses);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, "");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "e-mail client not found", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            DLog.handleException(e);
            Toast.makeText(context, "e-mail client not found", Toast.LENGTH_LONG).show();
        }
    }

    public static void shareText(Context context, String shareBody) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
        intent.putExtra("com.pinterest.EXTRA_DESCRIPTION", shareBody);
        context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.app_name)));
    }

    public static void shareThisApp(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT,
                "Hey my friend check out this app"
                        + (char) 10 + UConst.GOOGLE_PLAY_CONSTANT
                        + context.getPackageName() + (char) 10
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
