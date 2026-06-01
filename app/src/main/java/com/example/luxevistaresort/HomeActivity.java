package com.example.luxevistaresort;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private HorizontalScrollView discoveryScrollView;
    private static final long SCROLL_DELAY = 3000; // 3 seconds
    private static final int CALL_PHONE_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // --- FIX FOR STATUS BAR OVERLAP ---
        View rootLayout = findViewById(R.id.root_layout);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(0, statusBarHeight, 0, 0);
            return insets;
        });

        setupDiscoveryCards();

        discoveryScrollView = findViewById(R.id.discovery_scroll_view);
        setupAutoScrolling();

        Button loginRegisterButton = findViewById(R.id.loginRegisterButton);
        loginRegisterButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        Button callNowButton = findViewById(R.id.callNowButton);
        callNowButton.setOnClickListener(v -> makePhoneCall());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Already on the home screen
                return true;
            } else if (itemId == R.id.nav_rooms) {
                startActivity(new Intent(getApplicationContext(), RoomsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_services) {
                startActivity(new Intent(getApplicationContext(), ServicesActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });
    }

    private void makePhoneCall() {
        String phoneNumber = "tel:+94112233445"; // Example phone number
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_PERMISSION_REQUEST_CODE);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PHONE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupDiscoveryCards() {
        // Card 1: Our Rooms
        View cardOurRooms = findViewById(R.id.cardOurRooms);
        ImageView roomsImage = cardOurRooms.findViewById(R.id.discoveryCardImage);
        TextView roomsTitle = cardOurRooms.findViewById(R.id.discoveryCardTitle);
        roomsImage.setImageResource(R.drawable.home_discovery_rooms);
        roomsTitle.setText("Our Rooms");

        // Card 2: Our Services
        View cardOurServices = findViewById(R.id.cardOurServices);
        ImageView servicesImage = cardOurServices.findViewById(R.id.discoveryCardImage);
        TextView servicesTitle = cardOurServices.findViewById(R.id.discoveryCardTitle);
        servicesImage.setImageResource(R.drawable.home_discovery_services);
        servicesTitle.setText("Our Services");

        // Card 3: Fine Dining
        View cardFineDining = findViewById(R.id.cardFineDining);
        ImageView diningImage = cardFineDining.findViewById(R.id.discoveryCardImage);
        TextView diningTitle = cardFineDining.findViewById(R.id.discoveryCardTitle);
        diningImage.setImageResource(R.drawable.home_discovery_dining);
        diningTitle.setText("Fine Dining");

        // Card 4: Spa
        View cardSpa = findViewById(R.id.cardSpa);
        ImageView spaImage = cardSpa.findViewById(R.id.discoveryCardImage);
        TextView spaTitle = cardSpa.findViewById(R.id.discoveryCardTitle);
        spaImage.setImageResource(R.drawable.home_discovery_spa);
        spaTitle.setText("Spa Treatments");

        // Card 5: Cabanas
        View cardCabanas = findViewById(R.id.cardCabanas);
        ImageView cabanasImage = cardCabanas.findViewById(R.id.discoveryCardImage);
        TextView cabanasTitle = cardCabanas.findViewById(R.id.discoveryCardTitle);
        cabanasImage.setImageResource(R.drawable.home_discovery_cabanas);
        cabanasTitle.setText("Poolside Cabanas");
    }

    private void setupAutoScrolling() {
        autoScrollHandler = new Handler();
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (discoveryScrollView.getWidth() == 0) {
                    autoScrollHandler.postDelayed(this, 100);
                    return;
                }

                int maxScrollX = discoveryScrollView.getChildAt(0).getWidth() - discoveryScrollView.getWidth();
                int currentScrollX = discoveryScrollView.getScrollX();

                if (currentScrollX >= maxScrollX - 5) {
                    discoveryScrollView.smoothScrollTo(0, 0);
                } else {
                    View firstCard = findViewById(R.id.cardOurRooms);
                    if (firstCard != null && firstCard.getWidth() > 0) {
                        android.view.ViewGroup.MarginLayoutParams layoutParams = (android.view.ViewGroup.MarginLayoutParams) firstCard.getLayoutParams();
                        int cardWidth = firstCard.getWidth() + layoutParams.rightMargin;
                        discoveryScrollView.smoothScrollBy(cardWidth, 0);
                    }
                }

                autoScrollHandler.postDelayed(this, SCROLL_DELAY);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> {
            if (autoScrollHandler != null && autoScrollRunnable != null) {
                autoScrollHandler.postDelayed(autoScrollRunnable, SCROLL_DELAY);
            }
        }, 500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }
}

