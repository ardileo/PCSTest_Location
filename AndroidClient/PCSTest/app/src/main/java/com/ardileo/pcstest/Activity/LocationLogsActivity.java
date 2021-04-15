package com.ardileo.pcstest.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.ardileo.pcstest.Adapter.ListAdapter;
import com.ardileo.pcstest.R;
import com.ardileo.pcstest.Rest.ApiClient;
import com.ardileo.pcstest.Rest.Respo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationLogsActivity extends AppCompatActivity {

    private Context mContext;
    private RecyclerView rv;

    ListAdapter adapter;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mContext = this;

        adapter = new ListAdapter(mContext);
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.setAdapter(adapter);
        pd = ProgressDialog.show(mContext, null, "Please Wait", true, false);
        new ApiClient(mContext).getInstance().getLocations().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Respo respo = new Respo(response);
                pd.dismiss();
                if (respo.getError() == null) {
                    adapter.addAll(respo.getResult().locations);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
            }
        });

    }
}