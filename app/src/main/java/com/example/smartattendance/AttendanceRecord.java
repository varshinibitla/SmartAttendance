package com.example.smartattendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class AttendanceRecord extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_record);

        MaterialButton open = findViewById(R.id.open);

        open.setOnClickListener(v -> {
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView d = findViewById(R.id.date);
            String date = d.getText().toString();
            if (date.isEmpty()) {
                Toast.makeText(AttendanceRecord.this, "Please Enter Date", Toast.LENGTH_LONG).show();
            } else {
                DateInfo.getInstance().setDate(date);

                Intent i = new Intent(getApplicationContext(), AttendanceList.class);
                startActivity(i);
            }
        });
    }
}