package com.example.luxevistaresort;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoggedInRoomsAdapter extends RecyclerView.Adapter<LoggedInRoomsAdapter.RoomViewHolder> implements Filterable {

    private final List<Room> roomList;
    private final List<Room> roomListFull; // A copy of the original list for filtering
    private final Context context;

    public LoggedInRoomsAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
        this.roomListFull = new ArrayList<>(roomList);
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_loggedin, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.bind(room);
        holder.bookNowButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingConfirmationActivity.class);
            intent.putExtra(Booking.EXTRA_ITEM_NAME, room.getName());
            intent.putExtra("ROOM_PRICE", room.getPrice());
            intent.putExtra(Booking.EXTRA_ITEM_IMAGE_URI, room.getImageUri());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    @Override
    public Filter getFilter() {
        return roomFilter;
    }

    private final Filter roomFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Room> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(roomListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Room item : roomListFull) {
                    // Search in both room name and amenities
                    if (item.getName().toLowerCase().contains(filterPattern) || item.getAmenities().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            roomList.clear();
            roomList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    /**
     * Filters the list based on price range and selected amenities.
     */
    public void filterByCriteria(int minPrice, int maxPrice, List<String> selectedAmenities) {
        List<Room> filteredList = new ArrayList<>();
        for (Room item : roomListFull) {
            boolean priceMatch = item.getPrice() >= minPrice && item.getPrice() <= maxPrice;
            boolean amenitiesMatch = true;
            for (String amenity : selectedAmenities) {
                if (!item.getAmenities().toLowerCase().contains(amenity.toLowerCase())) {
                    amenitiesMatch = false;
                    break;
                }
            }

            if (priceMatch && amenitiesMatch) {
                filteredList.add(item);
            }
        }
        roomList.clear();
        roomList.addAll(filteredList);
        notifyDataSetChanged();
    }

    /**
     * Resets all filters and shows the original, complete list of rooms.
     */
    public void resetFilter() {
        roomList.clear();
        roomList.addAll(roomListFull);
        notifyDataSetChanged();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        ImageView roomImage;
        TextView roomName, roomDescription, roomAmenities, roomPrice;
        Button bookNowButton;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.roomImage);
            roomName = itemView.findViewById(R.id.roomName);
            roomDescription = itemView.findViewById(R.id.roomDescription);
            roomAmenities = itemView.findViewById(R.id.roomAmenities);
            roomPrice = itemView.findViewById(R.id.roomPrice);
            bookNowButton = itemView.findViewById(R.id.bookNowButton);
        }

        void bind(Room room) {
            roomName.setText(room.getName());
            roomDescription.setText(room.getDescription());
            roomAmenities.setText(room.getAmenities());
            roomPrice.setText(String.format(Locale.US, "$%d / night", room.getPrice()));
            if (room.getImageUri() != null && !room.getImageUri().isEmpty()) {
                roomImage.setImageURI(Uri.parse(room.getImageUri()));
            } else {
                roomImage.setImageResource(R.drawable.room_ocean_suite);
            }
        }
    }
}