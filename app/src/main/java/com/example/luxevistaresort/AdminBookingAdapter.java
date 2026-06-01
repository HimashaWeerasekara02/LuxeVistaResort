package com.example.luxevistaresort;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.BookingViewHolder> {

    private final List<Booking> bookingList;
    private final Context context;
    private final UserRepository userRepository;

    public AdminBookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
        this.userRepository = new UserRepository(context);
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.tvItemName.setText(booking.getItemName());
        holder.tvUserEmail.setText("Email: " + booking.getUserEmail());

        // Fetch user by email to get their name
        User user = userRepository.getUserByEmail(booking.getUserEmail());
        if (user != null) {
            holder.tvUserName.setText("Name: " + user.getName());
            holder.tvUserName.setVisibility(View.VISIBLE);
        } else {
            // Hide the name field if the user is not found
            holder.tvUserName.setVisibility(View.GONE);
        }

        // Format the date and time based on the booking type
        SimpleDateFormat sdf;
        if (booking.getItemType().equalsIgnoreCase("Room")) {
            sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.tvBookingTime.setText("Check-in: " + sdf.format(new Date(booking.getStartDate())));
        } else {
            sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            holder.tvBookingTime.setText("Time: " + sdf.format(new Date(booking.getStartDate())));
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvUserEmail, tvBookingTime, tvUserName;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvBookingTime = itemView.findViewById(R.id.tvBookingTime);
        }
    }
}