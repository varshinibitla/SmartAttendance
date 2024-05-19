package com.example.smartattendance;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.util.UUID;

public class QRActivity extends AppCompatActivity {
    private final int WHITE = 0xFFFFFFFF;
    private final int BLACK = 0xFF000000;

    Handler handler = new Handler();
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.black));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_activity);

        Toast.makeText(QRActivity.this, "Please Wait", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        handler.postDelayed(runnable = () -> {
            handler.postDelayed(runnable, 5000);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView qr = findViewById(R.id.qractivity);
            UUID uuid = UUID.randomUUID();
            QRInfo qrInfo = new QRInfo();
            qrInfo.setSecret(uuid.toString());
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("QR");
            databaseReference.setValue(qrInfo);
            try {
                MultiFormatWriter writer = new MultiFormatWriter();
                BitMatrix result = writer.encode(uuid.toString(), BarcodeFormat.QR_CODE, 250, 250, null);
                int width = result.getWidth();
                int height = result.getHeight();
                int[] pixels = new int[width * height];
                for (int y = 0; y < height; y++) {
                    int offset = y * width;
                    for (int x = 0; x < width; x++) {
                        pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                    }
                }
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
                qr.setImageBitmap(bitmap);
            } catch (Exception ex) {
                Toast.makeText(QRActivity.this, "Try Again", Toast.LENGTH_LONG).show();
            }
        }, 5000);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}