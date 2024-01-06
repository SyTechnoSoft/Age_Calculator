package com.sytechnosoft.agecalculator;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    TextView textView_birth, textView_today, textView_year, textView_month, textView_day, textView_calculate, textView_clear, textView_extra_text, textView_extra_result;
    View layout_birth, layout_today;
    DatePickerDialog.OnDateSetListener dateSetListener1, dateSetListener2;
    int years, months, days, total_months;
    long total_week, total_days, total_hours, total_minutes, total_seconds;
    private com.google.android.gms.ads.AdView adView_home;

    private InterstitialAd mInterstitialAd;

    //menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Rate) {
            Toast.makeText(this, "Saurabh Yadav says Thanks. Please rate us 5 stars in Google Play Store.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.agecalculators"));
            intent.setPackage("com.android.vending");
            startActivity(intent);

        } else if (item.getItemId() == R.id.Share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Awesome...\n Please install this App.");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey!\n Download *Age Calculator* App for calculate your age \nhttps://play.google.com/store/apps/details?id=com.agecalculators");
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
            Toast.makeText(this, "Saurabh Yadav says Thanks.", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Please share now.", Toast.LENGTH_LONG).show();

        } else if (item.getItemId() == R.id.Feedback) {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String[] recipients = {"yadav.saurabh9517+feedbackAC@gmail.com"};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback For Age Calculator App v" + BuildConfig.VERSION_NAME);
                intent.putExtra(Intent.EXTRA_TEXT, "Feedback is....\n");
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                startActivity(Intent.createChooser(intent, "Send mail"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (item.getItemId() == R.id.more_app) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/collection/cluster?clp=igM4ChkKEzU0NTE5NTM4NDY2NTc2NTM3OTYQCBgDEhkKEzU0NTE5NTM4NDY2NTc2NTM3OTYQCBgDGAA%3D:S:ANO1ljLCYeQ&gsr=CjuKAzgKGQoTNTQ1MTk1Mzg0NjY1NzY1Mzc5NhAIGAMSGQoTNTQ1MTk1Mzg0NjY1NzY1Mzc5NhAIGAMYAA%3D%3D:S:ANO1ljK_aU8"));//https://play.google.com/store/search?q=pub%3ASyTechnoSoft&c=apps
            intent.setPackage("com.android.vending");
            startActivity(intent);

        } else if (item.getItemId() == R.id.language) {
            //show alert dailog for chuse language
            showChangeLanguageDailog();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }// for menu in Actionbar

    private void showChangeLanguageDailog() {
        final String[] Languages = {"English", "हिन्दी"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.choose_language);
        builder.setSingleChoiceItems(Languages, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    setLocale("en");
                    recreate();
                }
                if (i == 1) {
                    setLocale("hi");
                    recreate();
                }
                dialogInterface.dismiss();
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        //parse data
        SharedPreferences.Editor editor = getSharedPreferences("Setting", MODE_PRIVATE).edit();
        editor.putString("My_Language", language);
        editor.apply();


    }

    //load language
    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_Language", "");
        setLocale(lang);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        AppUpdate();
        setContentView(R.layout.activity_main);
        //

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        layout_birth = findViewById(R.id.layout_birth);
        layout_today = findViewById(R.id.layout_today);
        textView_today = findViewById(R.id.tv_today);
        textView_birth = findViewById(R.id.tv_birth);
        textView_year = findViewById(R.id.tv_year);
        textView_month = findViewById(R.id.tv_month);
        textView_day = findViewById(R.id.tv_day);
        textView_calculate = findViewById(R.id.tv_calculate);
        textView_clear = findViewById(R.id.tv_clear);
        textView_extra_text = findViewById(R.id.tv_extra_txt);
        textView_extra_result = findViewById(R.id.tv_extra_result);
        adView_home = findViewById(R.id.adView_home);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView_home.loadAd(adRequest);

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH);

        int day = calendar.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String date = simpleDateFormat.format(Calendar.getInstance().getTime());
        textView_today.setText(date);
        clear_text();

        layout_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener1, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                textView_birth.setText(date);

            }
        };
        layout_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener2, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });
        dateSetListener2 = (datePicker, year1, month1, day1) -> {
            month1 = month1 + 1;
            String date12 = day1 + "/" + month1 + "/" + year1;
            textView_today.setText(date12);

        };
        textView_calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dob_Date = textView_birth.getText().toString();
                String today_Date = textView_today.getText().toString();
                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");

                try {
                    Date date1 = simpleDateFormat1.parse(dob_Date);
                    Date date2 = simpleDateFormat1.parse(today_Date);


                    long d_date = date1.getTime();
                    long t_date = date2.getTime();

                    if (d_date <= t_date) {
                        org.joda.time.Period period = new Period(d_date, t_date, PeriodType.yearMonthDay());
                        org.joda.time.Period period_months = new Period(d_date, t_date, PeriodType.months());//total months
                        org.joda.time.Period period_week = new Period(d_date, t_date, PeriodType.weeks());//total weeks
                        org.joda.time.Period period_day = new Period(d_date, t_date, PeriodType.days());//total days
                        years = period.getYears();
                        months = period.getMonths();
                        days = period.getDays();
                        total_months = period_months.getMonths();
                        total_week = period_week.getWeeks();
                        total_days = period_day.getDays();
                        total_hours = total_days * 24;
                        total_minutes = total_hours * 60;
                        total_seconds = total_minutes * 60;

                        // show the final output
                        textView_year.setText(years + "");
                        textView_month.setText(months + "");
                        textView_day.setText(days + "");
                        textView_extra_result.setText(MessageFormat.format("{0}\n{1}\n{2}\n{3}\n{4}\n{5}\n{6}", years, total_months, total_week, total_days, total_hours, total_minutes, total_seconds));

                    } else {
                        // show message
                        Toast.makeText(MainActivity.this, "BirthDate should not be larger then today's date!", Toast.LENGTH_SHORT).show();
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });
        textView_clear.setOnClickListener(view -> clear_text());

    }

    private void AppUpdate() {
        final AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(result -> {
            if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, this, 110);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });//in app update

    }


    private void clear_text() {
        textView_year.setText("0");
        textView_month.setText("0");
        textView_day.setText("0");
        textView_extra_result.setText("0\n0\n0\n0\n0\n0\n0");
        textView_extra_text.setText(R.string.total_summary_name);

    }

    public void shareAge(View view) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Awesome...\n Please install this App. https://play.google.com/store/apps/details?id=com.agecalculators");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "My Age is " + years + " Years " + months + " Months " + days + " Days \n\n Total Summery is \n" + "\nTotal Years: " + years + "\nTotal Months: " + total_months + "\nTotal Week: " + total_week + "\nTotal Days: " + total_days + "\nTotal Hours: " + total_hours + "\nTotal Minutes: " + total_minutes + "\nTotal Seconds: " + total_seconds + "\n\nYou can check your age from:- https://play.google.com/store/apps/details?id=com.agecalculators");
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);

    }

    @Override
    protected void onStart() {


        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, String.valueOf(R.string.back_interstitialAd), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                               // Log.d(TAG, "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;
                                onBackPressed();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                               // Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                               // Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                //Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                      //  Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        } else {
            super.onBackPressed();
        }
    }
}