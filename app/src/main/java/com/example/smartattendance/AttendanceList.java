package com.example.smartattendance;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AttendanceList extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_list);

        String date = DateInfo.getInstance().getDate();

        TableLayout tableLayout = findViewById(R.id.table);

        TableRow tableRowHeader = new TableRow(getApplicationContext());
        tableRowHeader.setBackgroundColor(Color.parseColor("#417BBB"));
        TextView textViewIdHeader = new TextView(getApplicationContext());
        textViewIdHeader.setTextColor(Color.WHITE);
        textViewIdHeader.setText("ID");
        tableRowHeader.addView(textViewIdHeader);
        TextView textViewNameHeader = new TextView(getApplicationContext());
        textViewNameHeader.setTextColor(Color.WHITE);
        textViewNameHeader.setText("Name");
        tableRowHeader.addView(textViewNameHeader);
        TextView textViewEmailHeader = new TextView(getApplicationContext());
        textViewEmailHeader.setTextColor(Color.WHITE);
        textViewEmailHeader.setText("Email");
        tableRowHeader.addView(textViewEmailHeader);
        TextView textViewDateHeader = new TextView(getApplicationContext());
        textViewDateHeader.setTextColor(Color.WHITE);
        textViewDateHeader.setText("Date : " + date);
        tableRowHeader.addView(textViewDateHeader);
        TextView textViewTimeHeader = new TextView(getApplicationContext());
        textViewTimeHeader.setTextColor(Color.WHITE);
        textViewTimeHeader.setText("Time");
        tableRowHeader.addView(textViewTimeHeader);
        tableLayout.addView(tableRowHeader, 0);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("StudentInfo");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 1;
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    TableRow tableRowData = new TableRow(getApplicationContext());
                    StudentInfo studentInfo = studentSnapshot.getValue(StudentInfo.class);
                    if (null != studentInfo) {
                        TextView textViewIdData = new TextView(getApplicationContext());
                        textViewIdData.setTextColor(Color.BLACK);
                        textViewIdData.setText(String.valueOf(studentInfo.getId()));
                        tableRowData.addView(textViewIdData);
                        TextView textViewNameData = new TextView(getApplicationContext());
                        textViewNameData.setTextColor(Color.BLACK);
                        textViewNameData.setText(String.valueOf(studentInfo.getName()));
                        tableRowData.addView(textViewNameData);
                        TextView textViewEmailData = new TextView(getApplicationContext());
                        textViewEmailData.setTextColor(Color.BLACK);
                        textViewEmailData.setText(String.valueOf(studentInfo.getEmail()));
                        tableRowData.addView(textViewEmailData);
                    }

                    FirebaseDatabase aFirebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference aDatabaseReference = aFirebaseDatabase.getReference(date);
                    aDatabaseReference.addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot attendanceSnapshot : dataSnapshot.getChildren()) {
                                if (Objects.equals(studentSnapshot.getKey(), attendanceSnapshot.getKey())) {
                                    AttendanceInfo attendanceInfo = attendanceSnapshot.getValue(AttendanceInfo.class);
                                    if (null != attendanceInfo) {
                                        TextView textViewDateData = new TextView(getApplicationContext());
                                        textViewDateData.setTextColor(Color.BLACK);
                                        textViewDateData.setText("Present");
                                        tableRowData.addView(textViewDateData);
                                        TextView textViewTimeData = new TextView(getApplicationContext());
                                        textViewTimeData.setTextColor(Color.BLACK);
                                        textViewTimeData.setText(String.valueOf(attendanceInfo.getTime()));
                                        tableRowData.addView(textViewTimeData);
                                    }
                                } else {
                                    TextView textViewDateData = new TextView(getApplicationContext());
                                    textViewDateData.setTextColor(Color.BLACK);
                                    textViewDateData.setText("Absent");
                                    tableRowData.addView(textViewDateData);
                                    TextView textViewTimeData = new TextView(getApplicationContext());
                                    textViewTimeData.setTextColor(Color.BLACK);
                                    textViewTimeData.setText("-");
                                    tableRowData.addView(textViewTimeData);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(AttendanceList.this, "Try Again", Toast.LENGTH_LONG).show();
                        }
                    });
                    tableLayout.addView(tableRowData, i);
                    i++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AttendanceList.this, "Try Again", Toast.LENGTH_LONG).show();
            }
        });
    }
}