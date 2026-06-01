package com.example.luxevistaresort;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class BookingsActivity extends AppCompatActivity {

    private TextView upcomingTab, pastTab, tvNoBookings;
    private View upcomingIndicator, pastIndicator;
    private RecyclerView upcomingRecyclerView, pastRecyclerView;
    private BookingAdapter upcomingAdapter, pastAdapter;
    private List<Booking> upcomingBookings, pastBookings;
    private UserRepository userRepository;

    private final ActivityResultLauncher<Intent> modificationLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // If the result is OK, it means a booking was changed.
                    // We reload the list to show the update.
                    loadUserBookings();
                }
            }
    );



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        initializeViews();

        upcomingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pastRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        upcomingBookings = new ArrayList<>();
        pastBookings = new ArrayList<>();

        upcomingAdapter = new BookingAdapter(this, upcomingBookings, true);
        pastAdapter = new BookingAdapter(this, pastBookings, false);

        upcomingRecyclerView.setAdapter(upcomingAdapter);
        pastRecyclerView.setAdapter(pastAdapter);

        upcomingTab.setOnClickListener(v -> selectTab(true));
        pastTab.setOnClickListener(v -> selectTab(false));

        selectTab(true);
        setupBottomNavigation();
    }

    public void launchModification(Intent intent) {
        modificationLauncher.launch(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadUserBookings();
    }

    private void initializeViews() {
        userRepository = new UserRepository(this);
        upcomingTab = findViewById(R.id.upcomingTab);
        pastTab = findViewById(R.id.pastTab);
        tvNoBookings = findViewById(R.id.tvNoBookings);
        upcomingIndicator = findViewById(R.id.upcomingIndicator);
        pastIndicator = findViewById(R.id.pastIndicator);
        upcomingRecyclerView = findViewById(R.id.upcomingRecyclerView);
        pastRecyclerView = findViewById(R.id.pastRecyclerView);

        View rootLayout = findViewById(R.id.root_layout);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(v.getPaddingLeft(), statusBarHeight, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
    }

    private void loadUserBookings() {
        String userEmail = SharedPrefManager.getInstance(this).getUserEmail();
        if (userEmail == null) return;

        upcomingBookings.clear();
        pastBookings.clear();

        List<Booking> allBookings = userRepository.getBookingsForUser(userEmail);
        long currentTime = System.currentTimeMillis();

        for (Booking booking : allBookings) {
            if (booking.getStatus().equalsIgnoreCase("Upcoming") && booking.getEndDate() > currentTime) {
                upcomingBookings.add(booking);
            } else {
                pastBookings.add(booking);
            }
        }

        upcomingAdapter.notifyDataSetChanged();
        pastAdapter.notifyDataSetChanged();

        updateNoBookingsMessage();
    }

    private void selectTab(boolean isUpcoming) {
        if (isUpcoming) {
            upcomingTab.setTextColor(getResources().getColor(R.color.text_light));
            pastTab.setTextColor(getResources().getColor(R.color.text_muted));
            upcomingIndicator.setVisibility(View.VISIBLE);
            pastIndicator.setVisibility(View.INVISIBLE);
            upcomingRecyclerView.setVisibility(View.VISIBLE);
            pastRecyclerView.setVisibility(View.GONE);
        } else {
            upcomingTab.setTextColor(getResources().getColor(R.color.text_muted));
            pastTab.setTextColor(getResources().getColor(R.color.text_light));
            pastIndicator.setVisibility(View.VISIBLE);
            upcomingIndicator.setVisibility(View.INVISIBLE);
            upcomingRecyclerView.setVisibility(View.GONE);
            pastRecyclerView.setVisibility(View.VISIBLE);
        }
        updateNoBookingsMessage();
    }

    private void updateNoBookingsMessage() {
        boolean isUpcomingTabSelected = upcomingRecyclerView.getVisibility() == View.VISIBLE;
        if (isUpcomingTabSelected && upcomingBookings.isEmpty()) {
            tvNoBookings.setText("You have no upcoming bookings.");
            tvNoBookings.setVisibility(View.VISIBLE);
        } else if (!isUpcomingTabSelected && pastBookings.isEmpty()) {
            tvNoBookings.setText("You have no past bookings.");
            tvNoBookings.setVisibility(View.VISIBLE);
        } else {
            tvNoBookings.setVisibility(View.GONE);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_bookings);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_rooms) {
                startActivity(new Intent(getApplicationContext(), LoggedInRoomsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_services) {
                startActivity(new Intent(getApplicationContext(), LoggedInServicesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_bookings) {
                return true; // Already here
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}