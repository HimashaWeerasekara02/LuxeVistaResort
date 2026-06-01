package com.example.luxevistaresort;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminHomeFragment extends Fragment {

    private UserRepository userRepository;
    private RecyclerView bookingsRecyclerView;
    private TextView tvNoBookings;
    private TextView headerTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        userRepository = new UserRepository(getContext());
        bookingsRecyclerView = view.findViewById(R.id.bookingsRecyclerView);
        tvNoBookings = view.findViewById(R.id.tvNoBookings);
        headerTitle = view.findViewById(R.id.headerTitle);

        // Set up the RecyclerView
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set the header to today's date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        headerTitle.setText("Today's Bookings (" + sdf.format(new Date()) + ")");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTodaysBookings();
    }

    private void loadTodaysBookings() {
        List<Booking> todaysBookings = userRepository.getTodaysBookings();

        if (todaysBookings.isEmpty()) {
            tvNoBookings.setVisibility(View.VISIBLE);
            bookingsRecyclerView.setVisibility(View.GONE);
        } else {
            tvNoBookings.setVisibility(View.GONE);
            bookingsRecyclerView.setVisibility(View.VISIBLE);
            AdminBookingAdapter adapter = new AdminBookingAdapter(getContext(), todaysBookings);
            bookingsRecyclerView.setAdapter(adapter);
        }
    }
}