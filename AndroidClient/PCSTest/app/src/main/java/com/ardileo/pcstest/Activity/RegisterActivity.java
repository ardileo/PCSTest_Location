package com.ardileo.pcstest.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout til_name, til_email, til_pass;
    private Button btnRegister, btnClose;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = this;

        til_name = findViewById(R.id.til_name);
        til_email = findViewById(R.id.til_email);
        til_pass = findViewById(R.id.til_password);

        btnClose = findViewById(R.id.btnClose);
        btnRegister = findViewById(R.id.btnRegister);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signToServer();
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
        String name = til_name.getEditText().getText().toString().trim();
        String email = til_email.getEditText().getText().toString().trim();
        String password = til_pass.getEditText().getText().toString().trim();

        boolean isValidate = name != null && isValidEmail(til_email) && isValidPassword(til_pass);

        if (isValidate) {
            ProgressDialog pd = ProgressDialog.show(mContext, null, "Please Wait", true, false);
            new ApiClient(mContext).getInstance().signUp(name, email, password).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    pd.dismiss();
                    Respo respo = new Respo(response);
                    if (respo.getError() == null) {
                        UserData user = respo.getResult().user;
                        if (user != null) {
                            Toast.makeText(mContext, "Berhasil Mendaftar! Silahkan Login", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(mContext, SplashScreen.class));
                            finish();
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