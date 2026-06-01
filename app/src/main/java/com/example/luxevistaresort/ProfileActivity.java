package com.example.luxevistaresort;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText;
    private Spinner roomTypeSpinner, serviceSpinner;
    private UserRepository userRepository;
    private String originalUserEmail;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userRepository = new UserRepository(this);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        roomTypeSpinner = findViewById(R.id.roomTypeSpinner);
        serviceSpinner = findViewById(R.id.serviceSpinner);
        Button saveChangesButton = findViewById(R.id.saveChangesButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        View rootLayout = findViewById(R.id.root_layout);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(0, statusBarHeight, 0, 0);
            return insets;
        });

        setupSpinners();
        populateUserData();
        setupBottomNavigation();

        saveChangesButton.setOnClickListener(v -> saveChanges());
        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void populateUserData() {
        originalUserEmail = SharedPrefManager.getInstance(this).getUserEmail();
        if (originalUserEmail == null) return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            currentUser = userRepository.getUserByEmail(originalUserEmail);

            runOnUiThread(() -> {
                if (currentUser != null) {
                    nameEditText.setText(currentUser.getName());
                    emailEditText.setText(currentUser.getEmail());

                    ArrayAdapter<CharSequence> roomAdapter = (ArrayAdapter<CharSequence>) roomTypeSpinner.getAdapter();
                    if (currentUser.getPreferredRoomType() != null) {
                        int roomPosition = roomAdapter.getPosition(currentUser.getPreferredRoomType());
                        roomTypeSpinner.setSelection(roomPosition);
                    }

                    ArrayAdapter<CharSequence> serviceAdapter = (ArrayAdapter<CharSequence>) serviceSpinner.getAdapter();
                    if (currentUser.getPreferredService() != null) {
                        int servicePosition = serviceAdapter.getPosition(currentUser.getPreferredService());
                        serviceSpinner.setSelection(servicePosition);
                    }
                }
            });
        });
    }

    private void saveChanges() {
        String newName = nameEditText.getText().toString().trim();
        String newEmail = emailEditText.getText().toString().trim();
        String prefRoom = roomTypeSpinner.getSelectedItem().toString();
        String prefService = serviceSpinner.getSelectedItem().toString();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Name and email cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            boolean isSuccess = userRepository.updateUserProfile(originalUserEmail, newName, newEmail, prefRoom, prefService);

            runOnUiThread(() -> {
                if (isSuccess) {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();


                    // update the current user object and re-save the entire session.
                    currentUser.setName(newName);
                    currentUser.setEmail(newEmail);
                    currentUser.setPreferredRoomType(prefRoom);
                    currentUser.setPreferredService(prefService);
                    SharedPrefManager.getInstance(this).userLogin(currentUser);

                    // Update the original email tracker for subsequent updates
                    originalUserEmail = newEmail;
                } else {
                    Toast.makeText(this, "Failed to update profile. Email might already be in use.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> roomAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item_style, getResources().getTextArray(R.array.room_type_preferences));
        roomAdapter.setDropDownViewResource(R.layout.spinner_item_style);

        ArrayAdapter<CharSequence> serviceAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item_style, getResources().getTextArray(R.array.service_preferences));
        serviceAdapter.setDropDownViewResource(R.layout.spinner_item_style);

        roomTypeSpinner.setAdapter(roomAdapter);
        serviceSpinner.setAdapter(serviceAdapter);
    }

    private void logoutUser() {
        SharedPrefManager.getInstance(this).logoutUser();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
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
                startActivity(new Intent(getApplicationContext(), BookingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }
}
