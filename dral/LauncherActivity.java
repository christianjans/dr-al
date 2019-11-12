package com.cjkj.dral;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cjkj.dral.bts.CJDataManager;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        final int LAUNCHER_DISPLAY_TIMEOUT = 5000;

        // check for updates if connected to internet
        if (isNetworkAvailable()) {
            System.out.println("Checking for updates...");

            final Context context = this;
            int startTime;
            int currentTime;

            Thread updateFilesThread = new Thread(new Runnable() {
                Context threadContext = context;
                @Override
                public void run() {
                    CJDataManager.updateFiles(threadContext);
                }
            });
            updateFilesThread.start();

            startTime = (int) System.currentTimeMillis();
            do {
                currentTime = (int) System.currentTimeMillis();

                if (!updateFilesThread.isAlive()) {
                    break;
                }
            } while (currentTime - startTime < LAUNCHER_DISPLAY_TIMEOUT);
        }

        Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
        startActivity(intent);

        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }, LAUNCHER_DISPLAY_TIMEOUT);
        */
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
