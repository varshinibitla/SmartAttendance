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

public class Professor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.professor);

        @SuppressLint("HardwareIds") String id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = firebaseDatabase.getReference("ProfessorLogin");

        MaterialButton signupButton = findViewById(R.id.signupbutton);
        signupButton.setOnClickListener(v -> {
            TextView email = findViewById(R.id.email);
            TextView password = findViewById(R.id.password);
            TextView professorId = findViewById(R.id.professorId);
            String e = email.getText().toString();
            String p = password.getText().toString();
            String pId = professorId.getText().toString();
            if (e.isEmpty() || p.isEmpty() || pId.isEmpty()) {
                Toast.makeText(Professor.this, "Enter Valid Email, Password and Professor Id", Toast.LENGTH_LONG).show();
            } else {
                p = BCrypt.hashpw(p, BCrypt.gensalt(13));
                ProfessorLoginInfo professorLoginInfo = new ProfessorLoginInfo();
                professorLoginInfo.setEmail(e);
                professorLoginInfo.setPassword(p);
                professorLoginInfo.setProfessorId(BCrypt.hashpw(pId, BCrypt.gensalt(13)));
                databaseReference.addValueEventListener(new ValueEventListener() {
                    boolean dataChange = false;

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.child(id).exists()) {
                            FirebaseDatabase firebaseDatabase1 = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference1 = firebaseDatabase1.getReference("SecretID");
                            databaseReference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    SecretInfo secretInfo = dataSnapshot.getValue(SecretInfo.class);
                                    if (null != secretInfo) {
                                        String professorId = secretInfo.getProfessorId();
                                        if (BCrypt.checkpw(pId, professorId)) {
                                            FirebaseDatabase firebaseDatabase2 = FirebaseDatabase.getInstance();
                                            DatabaseReference databaseReference2 = firebaseDatabase2.getReference("StudentLogin");
                                            databaseReference2.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (!dataSnapshot.child(id).exists()) {
                                                        dataChange = true;
                                                        databaseReference.child(id).setValue(professorLoginInfo);
                                                        Toast.makeText(Professor.this, "Signup Successful", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(Professor.this, "Your device is already registered as a student", Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    Toast.makeText(Professor.this, "Try Again", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(Professor.this, "Please enter valid account details", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(Professor.this, "Try Again", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            if (!dataChange) {
                                ProfessorLoginInfo existedProfessorLoginInfo = dataSnapshot.child(id).getValue(ProfessorLoginInfo.class);
                                if (null != existedProfessorLoginInfo) {
                                    if (existedProfessorLoginInfo.getEmail().equals(e)) {
                                        Toast.makeText(Professor.this, "Your Email is already registered with this device", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Professor.this, "Other Email is already registered with this device", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    dataChange = true;
                                    databaseReference.child(id).setValue(professorLoginInfo);
                                    Toast.makeText(Professor.this, "Signup Successful", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        email.setText("");
                        password.setText("");
                        professorId.setText("");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Professor.this, "Signup Unsuccessful" + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        MaterialButton loginButton = findViewById(R.id.loginbutton);
        loginButton.setOnClickListener(v -> {
            TextView email = findViewById(R.id.email);
            TextView password = findViewById(R.id.password);
            TextView professorId = findViewById(R.id.professorId);
            String e = email.getText().toString();
            String p = password.getText().toString();
            String pId = professorId.getText().toString();
            if (e.isEmpty() || p.isEmpty() || pId.isEmpty()) {
                Toast.makeText(Professor.this, "Enter Email, Password and Professor ID", Toast.LENGTH_LONG).show();
            } else {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(id).exists()) {
                            ProfessorLoginInfo professorLoginInfo = dataSnapshot.child(id).getValue(ProfessorLoginInfo.class);
                            if (null != professorLoginInfo) {
                                if (professorLoginInfo.getEmail().equals(e) &&
                                        BCrypt.checkpw(p, professorLoginInfo.getPassword()) &&
                                        BCrypt.checkpw(pId, professorLoginInfo.getProfessorId())) {
                                    Toast.makeText(Professor.this, "Log In Successful", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(getApplicationContext(), ProfessorLogin.class);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(Professor.this, "Enter Valid Email, Password and Professor ID", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(Professor.this, "Please Signup", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(Professor.this, "Please Signup. Your device is not registered", Toast.LENGTH_LONG).show();
                        }
                        email.setText("");
                        password.setText("");
                        professorId.setText("");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(Professor.this, "Sign In Unsuccessful", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}