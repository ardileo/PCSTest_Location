package com.ardileo.pcstest.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.ardileo.pcstest.Model.UserData;
import com.ardileo.pcstest.R;
import com.ardileo.pcstest.Rest.ApiClient;
import com.ardileo.pcstest.Rest.Respo;
import com.ardileo.pcstest.Utils.SessionManager;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout til_email, til_pass;
    private Button btnRegister, btnLogin;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;

        til_email = findViewById(R.id.til_email);
        til_pass = findViewById(R.id.til_password);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signToServer();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, RegisterActivity.class));
                finish();
            }
        });

    }

    private boolean isValidEmail(@NonNull TextInputLayout til_email) {
        String email = til_email.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            til_email.setError("Enter Email");
        } else {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                til_email.setErrorEnabled(false);
                return true;
            } else {
                til_email.setError("Invalid email address");
            }
        }
        return false;
    }

    private boolean isValidPassword(TextInputLayout til_pass) {
        String pass = til_pass.getEditText().getText().toString().trim();
        if (pass.isEmpty()) {
            til_pass.setError("Enter Password");
        } else {
            if (pass.length() >= 6) {
                til_pass.setErrorEnabled(false);
                return true;
            } else {
                til_pass.setError("Password min. 6 character");
            }
        }
        return false;
    }

    private void signToServer() {
        String email = til_email.getEditText().getText().toString().trim();
        String password = til_pass.getEditText().getText().toString().trim();

        boolean isValidate = isValidEmail(til_email) && isValidPassword(til_pass);

        if (isValidate) {
            ProgressDialog pd = ProgressDialog.show(mContext, null, "Please Wait", true, false);
            new ApiClient(mContext).getInstance().signIn(email, password).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    pd.dismiss();
                    Respo respo = new Respo(response);
                    if (respo.getError() == null) {
                        UserData user = respo.getResult().user;
                        if (user != null) {
                            SessionManager sessionManager = new SessionManager(mContext);
                            sessionManager.setIsLoggedIn(true);
                            sessionManager.setUser(user);
                            startActivity(new Intent(mContext, MainActivity.class));
                            finishAffinity();
                        }
                    } else {
                        AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
                        ab.setMessage(respo.getError().message);
                        ab.create().show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    pd.dismiss();
                }
            });
        }
    }
}