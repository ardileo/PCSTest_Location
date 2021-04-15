package com.ardileo.pcstest.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.ardileo.pcstest.R;
import com.ardileo.pcstest.Utils.SessionManager;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_sign);
        mContext = this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SessionManager session = new SessionManager(mContext);
                Intent intent;
                if (session.isLoggedIn()) {
                    intent = new Intent(mContext, MainActivity.class);
                } else {
                    intent = new Intent(mContext, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1000);
    }


}