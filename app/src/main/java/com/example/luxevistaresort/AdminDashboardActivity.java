package com.example.luxevistaresort;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        ImageView logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            // Call the centralized logout method from SharedPrefManager
            SharedPrefManager.getInstance(this).logoutUser();
            finish();
        });

        BottomNavigationView bottomNav = findViewById(R.id.admin_bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        // Load the default fragment when the activity is first created
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragment_container,
                    new AdminHomeFragment()).commit();
        }
    }

    private final BottomNavigationView.OnItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_admin_home) {
                    selectedFragment = new AdminHomeFragment();
                } else if (itemId == R.id.nav_admin_rooms) {
                    selectedFragment = new AdminManageRoomsFragment();
                } else if (itemId == R.id.nav_admin_services) {
                    selectedFragment = new AdminManageServicesFragment();
                } else if (itemId == R.id.nav_admin_offers) {
                    selectedFragment = new AdminManageOffersFragment();
                }

                // Perform the fragment transaction
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragment_container,
                            selectedFragment).commit();
                }
                return true;
            };
}

