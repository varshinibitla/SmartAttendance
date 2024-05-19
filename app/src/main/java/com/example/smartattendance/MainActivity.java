package com.example.smartattendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        Network network = connectivityManager.getActiveNetwork();
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
        if (null != networkCapabilities) {
            boolean vpn = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
            if (vpn) {
                finish();
            } else {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(this.getResources().getColor(R.color.black));

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                super.onCreate(savedInstanceState);
                setContentView(R.layout.main_activity);

                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialButton sButton = findViewById(R.id.loginbutton);
                sButton.setOnClickListener(v -> {
                    Intent i = new Intent(getApplicationContext(), Student.class);
                    startActivity(i);
                });

                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialButton pButton = findViewById(R.id.loginpbutton);
                pButton.setOnClickListener(v -> {
                    Intent i = new Intent(getApplicationContext(), Professor.class);
                    startActivity(i);
                });
            }
        }
    }
}