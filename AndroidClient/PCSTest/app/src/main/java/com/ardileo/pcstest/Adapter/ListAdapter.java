package com.ardileo.pcstest.Adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ardileo.pcstest.Model.LocModel;
import com.ardileo.pcstest.R;
import com.ardileo.pcstest.Rest.ApiClient;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListAdapter extends RecyclerView.Adapter {
    List<LocModel> listData = new ArrayList<>();
    Context mContext;

    public ListAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH_Item(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((VH_Item) holder).setData(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addAll(List<LocModel> locations) {
        for (LocModel m : locations) {
            listData.add(m);
            notifyItemInserted(listData.size() - 1);
        }
    }

    private static class VH_Item extends RecyclerView.ViewHolder {
        Context mContext;
        TextView tvLat, tvLon, tvId;

        public VH_Item(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false));
            mContext = itemView.getContext();
            tvId = itemView.findViewById(R.id.tvId);
            tvLat = itemView.findViewById(R.id.tvLat);
            tvLon = itemView.findViewById(R.id.tvLon);
        }

        public void setData(LocModel locModel) {
            tvId.setText("" + locModel.id);
            tvLat.setText("" + locModel.latitude);
            tvLon.setText("" + locModel.longitude);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
                    ab.setMessage("Delete?");
                    ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new ApiClient(mContext).getInstance().deleteLocation(locModel.id).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if(response.isSuccessful()){
                                        itemView.getLayoutParams().height = 0;
                                        itemView.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {

                                }
                            });
                        }
                    });
                    ab.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    ab.create();
                    ab.show();

                    return false;
                }
            });
        }
    }
}
