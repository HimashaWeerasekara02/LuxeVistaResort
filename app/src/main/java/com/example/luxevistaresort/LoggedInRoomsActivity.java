package com.example.luxevistaresort;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.slider.RangeSlider;
import java.util.ArrayList;
import java.util.List;

public class LoggedInRoomsActivity extends AppCompatActivity {

    private RecyclerView roomsRecyclerView;
    private UserRepository userRepository;
    private LoggedInRoomsAdapter adapter;
    private EditText searchEditText;
    private ImageView filterIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_rooms);

        userRepository = new UserRepository(this);
        roomsRecyclerView = findViewById(R.id.roomsRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        filterIcon = findViewById(R.id.filterIcon);

        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadRooms();
        setupBottomNavigation();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });

        filterIcon.setOnClickListener(v -> showFilterDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload rooms every time the user returns to this screen to see updates
        loadRooms();
    }

    private void loadRooms() {
        List<Room> roomList = userRepository.getAllRooms();
        adapter = new LoggedInRoomsAdapter(this, roomList);
        roomsRecyclerView.setAdapter(adapter);
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_filter_sort, null);
        builder.setView(dialogView);

        final RangeSlider priceRangeSlider = dialogView.findViewById(R.id.priceRangeSlider);
        final CheckBox cbOceanView = dialogView.findViewById(R.id.cbOceanView);
        final CheckBox cbBalcony = dialogView.findViewById(R.id.cbBalcony);

        builder.setTitle("Filter Options")
                .setPositiveButton("Apply", (dialog, id) -> {
                    List<Float> values = priceRangeSlider.getValues();
                    int minPrice = values.get(0).intValue();
                    int maxPrice = values.get(1).intValue();

                    List<String> selectedAmenities = new ArrayList<>();
                    if (cbOceanView.isChecked()) {
                        selectedAmenities.add("Ocean View");
                    }
                    if (cbBalcony.isChecked()) {
                        selectedAmenities.add("Balcony");
                    }

                    adapter.filterByCriteria(minPrice, maxPrice, selectedAmenities);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss())
                .setNeutralButton("Reset", (dialog, id) -> {
                    adapter.resetFilter();
                    searchEditText.setText("");
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_rooms);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_rooms) {
                return true; // Already here
            } else if (itemId == R.id.nav_services) {
                startActivity(new Intent(getApplicationContext(), LoggedInServicesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_bookings) {
                startActivity(new Intent(getApplicationContext(), BookingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
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