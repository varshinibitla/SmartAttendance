package com.example.smartattendance;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AttendanceActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_LOCATION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_activity);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AttendanceActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        } else {
            initLocation();
        }
    }

    private void initLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView aa = findViewById(R.id.attendanceactivity);

                @SuppressLint("SimpleDateFormat") String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(System.currentTimeMillis()));

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                DatabaseReference databaseReference = firebaseDatabase.getReference(date.substring(0, 10));

                databaseReference.addValueEventListener(new ValueEventListener() {
                    boolean dataChange = false;

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.child(deviceId).exists()) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            if (latitude > 40 && latitude < 41 &&
                                    longitude > -75 && longitude < -74) {
                                dataChange = true;
                                AttendanceInfo attendanceInfo = new AttendanceInfo();
                                attendanceInfo.setTime(date.substring(11, 19));
                                attendanceInfo.setLatitude(String.valueOf(location.getLatitude()));
                                attendanceInfo.setLongitude(String.valueOf(location.getLongitude()));
                                databaseReference.child(deviceId).setValue(attendanceInfo);
                                aa.setText("Attendance Recorded");
                            } else {
                                aa.setText("Your Attendance Is Not Recorded - Location Not Matched");
                            }
                        } else {
                            if (!dataChange) {
                                aa.setText("Your Attendance Is Already Recorded With This Device");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AttendanceActivity.this, "Try Again" + error, Toast.LENGTH_LONG).show();
                    }
                });

                Toast.makeText(AttendanceActivity.this, "Scan Successful", Toast.LENGTH_LONG).show();
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocation();
            } else {
                Toast.makeText(AttendanceActivity.this, "Location permission is required", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}