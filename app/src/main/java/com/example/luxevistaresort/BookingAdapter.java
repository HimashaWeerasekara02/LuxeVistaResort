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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private final List<Booking> bookingList;
    private final Context context;
    private final boolean isUpcoming;
    private final UserRepository userRepository;

    public BookingAdapter(Context context, List<Booking> bookingList, boolean isUpcoming) {
        this.context = context;
        this.bookingList = bookingList;
        this.isUpcoming = isUpcoming;
        this.userRepository = new UserRepository(context);
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = isUpcoming ? R.layout.item_booking_upcoming : R.layout.item_booking_past;
        View view = LayoutInflater.from(context).inflate(layout, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.bind(booking);

        if (isUpcoming && holder.modifyButton != null) {
            holder.modifyButton.setOnClickListener(v -> {
                Intent intent;
                if ("Room".equalsIgnoreCase(booking.getItemType())) {
                    Room roomDetails = userRepository.getRoomByName(booking.getItemName());
                    if (roomDetails == null) {
                        Toast.makeText(context, "Error: Could not find room details.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    intent = new Intent(context, BookingConfirmationActivity.class);
                    // Add room-specific extras
                    intent.putExtra(Booking.EXTRA_ROOM_PRICE, roomDetails.getPrice());
                    intent.putExtra(Booking.EXTRA_END_DATE, booking.getEndDate());
                } else { // Service
                    intent = new Intent(context, ServiceReservationActivity.class);
                }

                intent.putExtra(Booking.EXTRA_IS_MODIFY_MODE, true);
                intent.putExtra(Booking.EXTRA_BOOKING_ID, booking.getId());
                intent.putExtra(Booking.EXTRA_ITEM_NAME, booking.getItemName());
                intent.putExtra(Booking.EXTRA_ITEM_IMAGE_URI, booking.getImageUri());
                intent.putExtra(Booking.EXTRA_START_DATE, booking.getStartDate());


                if (context instanceof BookingsActivity) {
                    ((BookingsActivity) context).launchModification(intent);
                }
            });

            holder.cancelButton.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Cancel Booking")
                        .setMessage("Are you sure you want to cancel this booking?")
                        .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                            boolean isCancelled = userRepository.cancelBooking(booking.getId());
                            if (isCancelled) {
                                int currentPosition = holder.getAdapterPosition();
                                bookingList.remove(currentPosition);
                                notifyItemRemoved(currentPosition);
                                notifyItemRangeChanged(currentPosition, bookingList.size());
                                Toast.makeText(context, "Booking cancelled.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to cancel booking.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemDate, itemStatus;
        Button modifyButton, cancelButton;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemDate = itemView.findViewById(R.id.itemDate);
            itemStatus = itemView.findViewById(R.id.itemStatus);
            modifyButton = itemView.findViewById(R.id.modifyButton);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }

        void bind(Booking booking) {
            itemName.setText(booking.getItemName());
            if (booking.getImageUri() != null && !booking.getImageUri().isEmpty()) {
                itemImage.setImageURI(Uri.parse(booking.getImageUri()));
            }

            String formattedDate;

            if ("Room".equalsIgnoreCase(booking.getItemType())) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                String startDate = sdf.format(new Date(booking.getStartDate()));
                String endDate = sdf.format(new Date(booking.getEndDate()));
                formattedDate = "<b>Check-in:</b> " + startDate + "<br><b>Check-out:</b> " + endDate;
                itemDate.setText(android.text.Html.fromHtml(formattedDate));
            } else { // For Services
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.US);
                formattedDate = "<b>Date:</b> " + sdf.format(new Date(booking.getStartDate()));
                itemDate.setText(android.text.Html.fromHtml(formattedDate));
            }

            if (itemStatus != null) { // This view only exists in the past bookings layout
                itemStatus.setText(booking.getStatus());
            }
        }
    }
}