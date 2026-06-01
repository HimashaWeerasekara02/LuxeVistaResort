package com.example.luxevistaresort;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class ServicesActivity extends AppCompatActivity {

    private RecyclerView servicesRecyclerView;
    private UserRepository userRepository;
    private TextView noServicesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        userRepository = new UserRepository(this);
        servicesRecyclerView = findViewById(R.id.servicesRecyclerView);
        noServicesText = findViewById(R.id.noServicesText);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadServicesFromDatabase();
    }

    private void loadServicesFromDatabase() {
        List<Service> allServices = userRepository.getAllServices();
        if (allServices.isEmpty()) {
            servicesRecyclerView.setVisibility(View.GONE);
            noServicesText.setVisibility(View.VISIBLE);
        } else {
            servicesRecyclerView.setVisibility(View.VISIBLE);
            noServicesText.setVisibility(View.GONE);
            ServiceAdapter adapter = new ServiceAdapter(this, allServices);
            servicesRecyclerView.setAdapter(adapter);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_services);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_rooms) {
                startActivity(new Intent(getApplicationContext(), RoomsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_services) {
                return true; // Already here
            }
            return false;
        });
    }
}