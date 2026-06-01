package com.example.luxevistaresort;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class LoggedInServicesAdapter extends RecyclerView.Adapter<LoggedInServicesAdapter.ServiceViewHolder> {

    private final List<Service> serviceList;
    private final Context context;

    public LoggedInServicesAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service_loggedin, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.bind(service);

        holder.reserveButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ServiceReservationActivity.class);


            intent.putExtra(Booking.EXTRA_ITEM_NAME, service.getName());
            intent.putExtra(Booking.EXTRA_ITEM_IMAGE_URI, service.getImageUri());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceImage;
        TextView serviceName, serviceDescription, servicePrice;
        Button reserveButton;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.serviceImage);
            serviceName = itemView.findViewById(R.id.serviceName);
            serviceDescription = itemView.findViewById(R.id.serviceDescription);
            servicePrice = itemView.findViewById(R.id.servicePrice);
            reserveButton = itemView.findViewById(R.id.reserveButton);
        }

        void bind(Service service) {
            serviceName.setText(service.getName());
            serviceDescription.setText(service.getDescription());
            servicePrice.setText(String.format(Locale.US, "$%d", service.getPrice()));
            if (service.getImageUri() != null && !service.getImageUri().isEmpty()) {
                serviceImage.setImageURI(Uri.parse(service.getImageUri()));
            } else {
                serviceImage.setImageResource(R.drawable.service_spa); // Default placeholder
            }
        }
    }
}