package com.dilshan.clickfixandroidapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class homeServiceAdapter extends RecyclerView.Adapter<homeServiceAdapter.ServiceViewHolder> {

    private Context context;
    private List<Service> serviceList;

    public homeServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_item_home, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.serviceDiscription.setText(service.getDescription());
        holder.catagory.setText(service.getCategory());
        Glide.with(context).load(service.getImageUrl()).into(holder.serviceImage);

        // Set onClickListener on ImageView to navigate to ServiceDetailsActivity
        holder.serviceImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, ServiceDetailActivity.class);
            intent.putExtra("name", service.getName());
            intent.putExtra("name", service.getPrice());
            intent.putExtra("description", service.getDescription());
            intent.putExtra("imageUrl", service.getImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceDiscription;
        TextView catagory;
        ImageView serviceImage;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceDiscription = itemView.findViewById(R.id.service_discription);
            catagory = itemView.findViewById(R.id.catagory);
            serviceImage = itemView.findViewById(R.id.service_image);
        }
    }
}
