package com.example.luxevistaresort;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class LoggedInServicesActivity extends AppCompatActivity {

    private RecyclerView servicesRecyclerView;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_services);

        userRepository = new UserRepository(this);
        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadServices();
    }

    private void loadServices() {
        List<Service> serviceList = userRepository.getAllServices();
        LoggedInServicesAdapter adapter = new LoggedInServicesAdapter(this, serviceList);
        servicesRecyclerView.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_services);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                overridePendingTransition(0, 0);
                finish(); // Finish to prevent deep back-stack
                return true;
            } else if (itemId == R.id.nav_rooms) {
                startActivity(new Intent(getApplicationContext(), LoggedInRoomsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_services) {
                return true; // Already here
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