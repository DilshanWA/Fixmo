package com.dilshan.clickfixandroidapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ServiceAdapter extends ArrayAdapter<Service> {
    private Context context;
    private List<Service> services;

    public ServiceAdapter(@NonNull Context context, List<Service> services) {
        super(context, 0, services);
        this.context = context;
        this.services = services;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_items, parent, false);
        }

        Service service = services.get(position);

        TextView userName = convertView.findViewById(R.id.user_name);
        TextView price = convertView.findViewById(R.id.charge_perhour);
        TextView description = convertView.findViewById(R.id.discripction);
        ImageView imageView = convertView.findViewById(R.id.listImage);

        description.setText(service.getDescription());
        String servicePrice = service.getPrice();
        String updateusername = service.getName();

        String priceText = "$" + servicePrice + "/perhour";
        price.setText(priceText);
        String newname = "By " + updateusername;
        userName.setText(newname);

        // Use Glide to load the image
        Glide.with(context)
                .load(service.getImageUrl())
                .apply(new RequestOptions().placeholder(R.drawable.imgplaceholder).error(R.drawable.imgplaceholder))
                .into(imageView);

        // Set onClickListener to open ServiceDetailActivity
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ServiceDetailActivity.class);
                intent.putExtra("category", service.getCategory());
                intent.putExtra("service", service.getPrice());
                intent.putExtra("username", service.getName());
                intent.putExtra("phone", service.getPhone());
                intent.putExtra("email", service.getEmail());
                intent.putExtra("name", service.getName());
                intent.putExtra("description", service.getDescription());
                intent.putExtra("location", service.getLocation());
                intent.putExtra("imageUrl", service.getImageUrl());
                context.startActivity(intent);
            }
        });

        return convertView;
    }
}
