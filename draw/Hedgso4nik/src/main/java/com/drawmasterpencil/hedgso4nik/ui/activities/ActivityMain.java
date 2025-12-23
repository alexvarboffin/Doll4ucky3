package com.drawmasterpencil.hedgso4nik.ui.activities;

import static com.drawmasterpencil.hedgso4nik.utlis.AppConstants.EXTRA_SHOW_RATE_US_DIALOG;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.drawmasterpencil.hedgso4nik.R;
import com.drawmasterpencil.hedgso4nik.base.BaseActivity;
import com.drawmasterpencil.hedgso4nik.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ActivityMain extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "ActivityMain";
    private boolean doubleBackToExitPressedOnce = false;

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;

    private ConsentInformation consentInformation;
    private ActivityMainBinding binding;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setCurrentActivity(this);

        mContext = getApplicationContext();

        if (getIntent().getBooleanExtra(EXTRA_SHOW_RATE_US_DIALOG, false)) {
            showRateAppDialog();
        }

        initViews();
        initConsents();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        TextView tvToolbarTitle = findViewById(R.id.tv_title_toolbar);
        tvToolbarTitle.setText(getString(R.string.app_title));

        AppCompatTextView tvAllApps = findViewById(R.id.tv_all_apps);
        tvAllApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeveloperPageOnPlayMarket();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_themes, R.id.nav_rate, R.id.nav_feedback)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        setNavigationViewListener();
    }

    private void initConsents() {
//      Set tag for underage of consent. false means users are not underage.
        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
//              .setConsentDebugSettings(debugSettings)
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(this,
            params,
            new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
                @Override
                public void onConsentInfoUpdateSuccess() {
                    // The consent information state was updated.
                    // You are now ready to check if a form is available.
                    Log.i(TAG, "onConsentInfoUpdateSuccess; Status: "+consentInformation.getConsentStatus());
                    if (consentInformation.isConsentFormAvailable()) {
                        loadConsentForm();
                    }
                }
            },
            new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                @Override
                public void onConsentInfoUpdateFailure(@NonNull FormError formError) {
                    // Handle the error.
                    Log.i(TAG, "onConsentInfoUpdateFailure; formError: "+formError.getMessage());
                }
            }
        );
        // END Consent SDK
    }

    public void loadConsentForm() {
        UserMessagingPlatform.loadConsentForm(this,
                new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
                @Override
                public void onConsentFormLoadSuccess(@NonNull ConsentForm consentForm) {
                    if(consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                        consentForm.show(ActivityMain.this,
                                new ConsentForm.OnConsentFormDismissedListener() {
                            @Override
                            public void onConsentFormDismissed(@Nullable FormError formError) {
                                // Handle dismissal by reloading form.
                                loadConsentForm();
                            }
                        });
                    }
                }
            },
            new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
                @Override
                public void onConsentFormLoadFailure(@NonNull FormError formError) {
                    Log.e(TAG, "onConsentFormLoadFailure; Error Code: "+formError.getErrorCode()+
                            "; Message: "+formError.getMessage());
                }
            }
        );
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
        switch (id) {
            case R.id.nav_rate:
                openAppOnPlayMarket();
                break;

            case R.id.nav_feedback:
                openDefaultMailClient();
                break;

            case R.id.nav_privacy_policy:
                openPrivacyUrl();
                break;

            case R.id.nav_all_apps:
                openDeveloperPageOnPlayMarket();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    private void showRateAppDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityMain.this);
        alertDialog.setTitle(getResources().getString(R.string.app_name));
        alertDialog.setMessage(getString(R.string.alert_rate_us));

        alertDialog.setPositiveButton(getString(R.string.rate_us),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        openAppOnPlayMarket();
                    }
                });

        alertDialog.setNegativeButton(getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, getString(R.string.alert_rate_us_later),
                                Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


    @Override
    public void onBackPressed() {

        //Pressed back => return to home screen
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(count > 0);
        }
        if (count > 0) {
            getSupportFragmentManager()
                    .popBackStack(getSupportFragmentManager()
                                    .getBackStackEntryAt(0).getId(),
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {//count == 0


//                Dialog
//                new AlertDialog.Builder(this)
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setTitle("Leaving this App?")
//                        .setMessage("Are you sure you want to close this application?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//
//                        })
//                        .setNegativeButton("No", null)
//                        .show();
            //super.onBackPressed();

            if (doubleBackToExitPressedOnce) {
//                super.onBackPressed();
//                return;

                // Move the task containing the MainActivity to the back of the activity stack, instead of
                // destroying it. Therefore, MainActivity will be shown when the user switches back to the app.
                moveTaskToBack(true);
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            //Toast.makeText(this, getString(R.string.press_again_to_exit), Toast.LENGTH_SHORT).show();
            Toasty.custom(this, getString(R.string.press_again_to_exit),
                    ContextCompat.getDrawable(this, R.drawable.ic_info),
                    ContextCompat.getColor(this, R.color.colorPrimaryDark),
                    ContextCompat.getColor(this, R.color.white), Toasty.LENGTH_SHORT, true, true).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 800);

        }
    }
}