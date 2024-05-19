package com.example.smartattendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class ProfessorLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.professor_login);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialButton qrgButton = findViewById(R.id.qrgbutton);
        qrgButton.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), QRActivity.class);
            startActivity(i);
        });

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialButton attendanceList = findViewById(R.id.attendancerecord);
        attendanceList.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), AttendanceRecord.class);
            startActivity(i);
        });
    }
}