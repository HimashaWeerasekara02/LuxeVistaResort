package com.example.luxevistaresort;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ServiceManagementAdapter extends RecyclerView.Adapter<ServiceManagementAdapter.ServiceViewHolder> {

    private final List<Service> serviceList;
    private final Context context;
    private final UserRepository userRepository;

    public ServiceManagementAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
        this.userRepository = new UserRepository(context);
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service_management, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.serviceNameTextView.setText(service.getName());

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditServiceActivity.class);
            intent.putExtra("SERVICE_ID", service.getId());
            context.startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Service")
                    .setMessage("Are you sure you want to delete '" + service.getName() + "'? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        boolean success = userRepository.deleteService(service.getId());
                        if (success) {
                            int currentPosition = holder.getAdapterPosition();
                            serviceList.remove(currentPosition);
                            notifyItemRemoved(currentPosition);
                            notifyItemRangeChanged(currentPosition, serviceList.size());
                            Toast.makeText(context, "Service deleted.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to delete service.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameTextView;
        Button editButton, deleteButton;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}