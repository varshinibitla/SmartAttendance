package com.example.smartattendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Student extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student);

        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = firebaseDatabase.getReference("StudentLogin");

        MaterialButton signupButton = findViewById(R.id.signupbutton);
        signupButton.setOnClickListener(v -> {
            TextView email = findViewById(R.id.email);
            TextView password = findViewById(R.id.password);
            String e = email.getText().toString();
            String p = password.getText().toString();
            if (e.isEmpty() || p.isEmpty()) {
                Toast.makeText(Student.this, "Enter both Email and Password", Toast.LENGTH_LONG).show();
            } else {
                p = BCrypt.hashpw(p, BCrypt.gensalt(13));
                StudentLoginInfo studentLoginInfo = new StudentLoginInfo();
                studentLoginInfo.setEmail(e);
                studentLoginInfo.setPassword(p);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    boolean dataChange = false;

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.child(deviceId).exists()) {
                            FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference1 = firebaseDatabase1.getReference("ProfessorLogin");
                            databaseReference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.child(deviceId).exists()) {
                                        dataChange = true;
                                        databaseReference.child(deviceId).setValue(studentLoginInfo);
                                        Toast.makeText(Student.this, "Signup Successful", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Student.this, "Your device is already registered as a Professor", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(Student.this, "Try Again", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            if (!dataChange) {
                                StudentLoginInfo existedStudentLoginInfo = dataSnapshot.child(deviceId).getValue(StudentLoginInfo.class);
                                if (null != existedStudentLoginInfo) {
                                    if (existedStudentLoginInfo.getEmail().equals(e)) {
                                        Toast.makeText(Student.this, "Your Email is already registered with this device", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Student.this, "Other Email is already registered with this device", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    dataChange = true;
                                    databaseReference.child(deviceId).setValue(studentLoginInfo);
                                    Toast.makeText(Student.this, "Signup Successful", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        email.setText("");
                        password.setText("");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Student.this, "Signup Unsuccessful" + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        MaterialButton loginButton = findViewById(R.id.loginbutton);
        loginButton.setOnClickListener(v -> {
            TextView email = findViewById(R.id.email);
            TextView password = findViewById(R.id.password);
            String e = email.getText().toString();
            String p = password.getText().toString();
            if (e.isEmpty() || p.isEmpty()) {
                Toast.makeText(Student.this, "Enter both Email and Password", Toast.LENGTH_LONG).show();
            } else {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(deviceId).exists()) {
                            StudentLoginInfo studentLoginInfo = dataSnapshot.child(deviceId).getValue(StudentLoginInfo.class);
                            if (null != studentLoginInfo) {
                                if (studentLoginInfo.getEmail().equals(e) &&
                                        BCrypt.checkpw(p, studentLoginInfo.getPassword())) {
                                    Toast.makeText(Student.this, "Log In Successful", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(getApplicationContext(), StudentLogin.class);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(Student.this, "Enter Valid Email and Password", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(Student.this, "Please Signup", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(Student.this, "Please Signup. Your device is not registered", Toast.LENGTH_LONG).show();
                        }
                        email.setText("");
                        password.setText("");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Student.this, "Sign In Unsuccessful", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}