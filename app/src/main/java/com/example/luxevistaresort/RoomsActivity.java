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

public class RoomsActivity extends AppCompatActivity {

    private RecyclerView roomsRecyclerView;
    private UserRepository userRepository;
    private TextView noRoomsText; // Optional: Add a TextView with this ID to your layout for empty state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        userRepository = new UserRepository(this);
        roomsRecyclerView = findViewById(R.id.roomsRecyclerView);
        // noRoomsText = findViewById(R.id.noRoomsText); // Uncomment if you add this view
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRoomsFromDatabase();
    }

    private void loadRoomsFromDatabase() {
        List<Room> allRooms = userRepository.getAllRooms();

        // This check is optional but good practice
        if (allRooms.isEmpty()) {
            roomsRecyclerView.setVisibility(View.GONE);
            // if (noRoomsText != null) noRoomsText.setVisibility(View.VISIBLE);
        } else {
            roomsRecyclerView.setVisibility(View.VISIBLE);
            // if (noRoomsText != null) noRoomsText.setVisibility(View.GONE);
            RoomAdapter adapter = new RoomAdapter(this, allRooms);
            roomsRecyclerView.setAdapter(adapter);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_rooms);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_rooms) {
                return true; // Already here
            } else if (itemId == R.id.nav_services) {
                startActivity(new Intent(getApplicationContext(), ServicesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}