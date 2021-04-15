package com.ardileo.pcstest.Activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ardileo.pcstest.Model.UserData;
import com.ardileo.pcstest.R;
import com.ardileo.pcstest.Rest.ApiClient;
import com.ardileo.pcstest.Rest.Respo;
import com.ardileo.pcstest.Utils.AppReceiver;
import com.ardileo.pcstest.Utils.LocUtils;
import com.ardileo.pcstest.Utils.SessionManager;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnLogs, btnLogout, btnStart, btnStop, btnSaveNow;
    private Context mContext;
    private PendingIntent pendingIntent;

    private static final int ALARM_REQUEST_CODE = 134;
    private int NOTIFICATION_ID = 1;
    AlarmManager alarmManager;
    LocUtils locUtils;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        sessionManager = new SessionManager(mContext);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        TextView tvGreet = findViewById(R.id.tvGreet);
        String s = "Hi, " + sessionManager.getUser().getName();
        tvGreet.setText(s);

        btnLogs = findViewById(R.id.btnLogs);
        btnLogout = findViewById(R.id.btnLogout);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnSaveNow = findViewById(R.id.btnSaveLocation);

        TextView tvEditName = findViewById(R.id.tvEditName);
        tvEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogChangeName();
            }
        });

        locUtils = new LocUtils(mContext);
        if (!locUtils.isPermissionGrant()) {
            btnStart.setEnabled(false);
            btnStop.setEnabled(false);
            locUtils.requestPermissionGrant();
            locUtils.turnOnGPS();
        }

        Intent alarmIntent = new Intent(mContext, AppReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_REQUEST_CODE, alarmIntent, 0);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAlarmManager();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarmManager();
            }
        });

        btnSaveNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog pd = ProgressDialog.show(mContext, null, "Please Wait", true, false);
                double lat = locUtils.getLocation().getLatitude();
                double lng = locUtils.getLocation().getLongitude();
                new ApiClient(mContext).getInstance().saveLocation(lat, lng).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        pd.dismiss();
                        if (response.isSuccessful()) {
                            Toast.makeText(mContext, "Lokasi Tersimpan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        pd.dismiss();
                    }
                });
            }
        });

        btnLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, LocationLogsActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                startActivity(new Intent(mContext, SplashScreen.class));
                finish();
            }
        });
    }

    private void showDialogChangeName() {
        EditText et = new EditText(mContext);
        et.setHint("Name");
        et.setText(sessionManager.getUser().getName());
        et.setSelection(et.getText().length());
        et.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((ViewGroup.MarginLayoutParams) et.getLayoutParams()).leftMargin = 20;
        ((ViewGroup.MarginLayoutParams) et.getLayoutParams()).rightMargin = 20;

        AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
        ab.setView(et);
        ab.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new ApiClient(mContext).getInstance().changeName(et.getText().toString()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        Respo respo = new Respo(response);
                        if (respo.getError() == null) {
                            if (respo.getResult().user != null) {
                                UserData u = sessionManager.getUser();
                                u.setName(respo.getResult().user.getName());
                                sessionManager.setUser(u);
                            }
                            Toast.makeText(mContext, "Success", Toast.LENGTH_SHORT).show();
                            recreate();
                        } else {
                            Toast.makeText(mContext, "Fail", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });
        ab.create().show();
    }

    public void startAlarmManager() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(mContext, "Tracker Started.", Toast.LENGTH_SHORT).show();
    }

    public void stopAlarmManager() {
        alarmManager.cancel(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        Toast.makeText(mContext, "Tracker Stopped.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}