package com.example.coco;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.coco.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.os.PowerManager.FULL_WAKE_LOCK;
import static java.sql.DriverManager.println;

public class DialogActivity extends AppCompatActivity {
    private Handler handler;
    final Context context = this;
    String phoneNum = "01072736600";
    String textMsg;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_screen);
        handler = new Handler();
        Dialog dialog = new Dialog(context);

        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WAKELOCK");

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.emergency_screen);

        dialog.show();

        wakeLock.acquire();

        Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
        Button btnHelp = (Button) dialog.findViewById(R.id.btnHelp);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                wakeLock.release();
                dialog.dismiss();
                finish();
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                double latitude = intent.getExtras().getDouble("lastlat");
                double longitude = intent.getExtras().getDouble("lastlon");

                phoneNum = "01072736600";
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED && (ContextCompat.checkSelfPermission(getApplicationContext(), permission.SEND_SMS) != PackageManager.PERMISSION_DENIED)) {
                    textMsg = getString(R.string.accident) + "http://maps.google.com/?q=" + String.valueOf(latitude) + "," + String.valueOf(longitude);
                    try {
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(phoneNum, null, textMsg, null, null);
                        Toast.makeText(getApplicationContext(), getString(R.string.send_message), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d("Message", textMsg + "<" + phoneNum + ">");
                }

                handler.removeCallbacksAndMessages(null);
                wakeLock.release();
                dialog.dismiss();
                finish();
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                double latitude = intent.getExtras().getDouble("lastlat");
                double longitude = intent.getExtras().getDouble("lastlon");

                phoneNum = "01072736600";
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED && (ContextCompat.checkSelfPermission(getApplicationContext(), permission.SEND_SMS) != PackageManager.PERMISSION_DENIED)) {
                    textMsg = getString(R.string.accident) + "http://maps.google.com/?q=" + String.valueOf(latitude) + "," + String.valueOf(longitude);
                    try {
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(phoneNum, null, textMsg, null, null);
                        Toast.makeText(getApplicationContext(), getString(R.string.send_message), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d("Message", textMsg + "<" + phoneNum + ">");
                }
                handler.removeCallbacksAndMessages(null);
                wakeLock.release();
                dialog.dismiss();
                finish();
            }
        }, 7000);
    }
}



