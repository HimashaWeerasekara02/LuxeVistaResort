package com.example.luxevistaresort;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AdminManageOffersFragment extends Fragment {

    private EditText offerMessageEditText;
    private Button sendOfferButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_offers, container, false);

        // Find the views from the layout
        offerMessageEditText = view.findViewById(R.id.offerMessageEditText);
        sendOfferButton = view.findViewById(R.id.sendOfferButton);

        // Set the click listener for the send button
        sendOfferButton.setOnClickListener(v -> sendOffer());

        return view;
    }

    private void sendOffer() {
        String message = offerMessageEditText.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an offer message.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the NotificationHelper to send the alert
        NotificationHelper.sendNotification(getContext(), "Special Offer!", message);

        // Give feedback to the admin and clear the text box
        Toast.makeText(getContext(), "Offer sent to all users!", Toast.LENGTH_LONG).show();
        offerMessageEditText.setText("");
    }
}