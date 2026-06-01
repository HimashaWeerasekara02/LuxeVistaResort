package com.example.luxevistaresort;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

public class RoomManagementAdapter extends RecyclerView.Adapter<RoomManagementAdapter.RoomViewHolder> {

    private final List<Room> roomList;
    private final Context context;
    private final UserRepository userRepository;

    public RoomManagementAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
        this.userRepository = new UserRepository(context);
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_management, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);

        Log.d("AdapterDebug", "Binding ADMIN Room: " + room.getName() + " | Image URI: " + room.getImageUri());

        // Set the room name
        holder.roomNameTextView.setText(room.getName());

        // Set the click listener for the "Edit" button
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditRoomActivity.class);
            intent.putExtra("ROOM_ID", room.getId());
            context.startActivity(intent);
        });

        // Set the click listener for the "Delete" button
        holder.deleteButton.setOnClickListener(v -> {
            // Show a confirmation dialog to prevent accidental deletion
            new AlertDialog.Builder(context)
                    .setTitle("Delete Room")
                    .setMessage("Are you sure you want to delete '" + room.getName() + "'? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        boolean success = userRepository.deleteRoom(room.getId());
                        if (success) {
                            // Get the current position of the item in the adapter
                            int currentPosition = holder.getAdapterPosition();
                            // Remove the item from the local list
                            roomList.remove(currentPosition);
                            // Notify the adapter that an item was removed for a smooth animation
                            notifyItemRemoved(currentPosition);
                            notifyItemRangeChanged(currentPosition, roomList.size());
                            Toast.makeText(context, "Room deleted.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to delete room.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView roomNameTextView;
        Button editButton, deleteButton;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNameTextView = itemView.findViewById(R.id.roomNameTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}