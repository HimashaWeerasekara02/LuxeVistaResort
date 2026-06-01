package com.example.luxevistaresort;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class RoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Constants to identify the type of view
    private static final int VIEW_TYPE_ROOM = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    private final List<Room> roomList;
    private final Context context;

    public RoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    /**
     * this method decides which layout to show based on the item's position.
     */
    @Override
    public int getItemViewType(int position) {
        // If the position is the last item in the list, it's our footer.
        // Otherwise, it's a normal room item.
        if (position == roomList.size()) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_ROOM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Based on the view type, we inflate the correct layout.
        if (viewType == VIEW_TYPE_ROOM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_room_public, parent, false);
            return new RoomViewHolder(view);
        } else { // It must be the footer
            View view = LayoutInflater.from(context).inflate(R.layout.item_login_prompt_footer, parent, false);
            return new FooterViewHolder(view);
        }
    }

    /**
     * This method binds the data to the views.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // We check which type of holder we have before binding data.
        if (holder.getItemViewType() == VIEW_TYPE_ROOM) {
            RoomViewHolder roomHolder = (RoomViewHolder) holder;
            Room room = roomList.get(position);
            roomHolder.bind(room);
        } else {
            // It's the footer, so we set up its clickable text.
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            setupLoginPrompt(footerHolder.loginPromptText);
        }
    }

    /**
     * This method tells the RecyclerView how many items are in the list.
     * It's the size of our room list plus one extra for the footer.
     */
    @Override
    public int getItemCount() {
        return roomList.size() + 1;
    }

    // ViewHolder for the regular room item
    // This is the inner class inside RoomAdapter.java

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        private final ImageView roomImage;
        private final TextView roomName;
        private final TextView roomDescription;
        private final TextView roomAmenities;
        private final TextView roomPrice;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.roomImage);
            roomName = itemView.findViewById(R.id.roomName);
            roomDescription = itemView.findViewById(R.id.roomDescription);
            roomAmenities = itemView.findViewById(R.id.roomAmenities);
            roomPrice = itemView.findViewById(R.id.roomPrice);
        }

        public void bind(Room room) {
            Log.d("AdapterDebug", "Binding PUBLIC Room: " + room.getName() + " | Image URI: " + room.getImageUri());

            roomName.setText(room.getName());
            roomDescription.setText(room.getDescription());
            roomAmenities.setText(room.getAmenities());
            roomPrice.setText(String.format(Locale.US, "$%d / night", room.getPrice()));

            if (room.getImageUri() != null && !room.getImageUri().isEmpty()) {
                roomImage.setImageURI(Uri.parse(room.getImageUri()));
            } else {
                // Set a default placeholder image if the URI is missing
                roomImage.setImageResource(R.drawable.room_ocean_suite);
            }
        }
    }

    // ViewHolder for the login prompt footer
    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView loginPromptText;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            loginPromptText = itemView.findViewById(R.id.loginPromptText);
        }
    }

    // Helper method to make the "Login" and "Register" text clickable
    private void setupLoginPrompt(TextView loginPromptText) {
        String fullText = "To make a reservation, please Login or Register";
        SpannableString spannableString = new SpannableString(fullText);
        ClickableSpan loginSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        };
        ClickableSpan registerSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                context.startActivity(new Intent(context, RegisterActivity.class));
            }
        };
        int loginStart = fullText.indexOf("Login");
        int registerStart = fullText.indexOf("Register");
        spannableString.setSpan(loginSpan, loginStart, loginStart + "Login".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(registerSpan, registerStart, registerStart + "Register".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginPromptText.setText(spannableString);
        loginPromptText.setMovementMethod(LinkMovementMethod.getInstance());
        loginPromptText.setHighlightColor(ContextCompat.getColor(context, android.R.color.transparent));
    }
}