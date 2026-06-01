package com.example.luxevistaresort;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private HorizontalScrollView discoveryScrollView;
    private static final long SCROLL_DELAY = 3000;
    private static final int CALL_PHONE_PERMISSION_REQUEST_CODE = 101;

    private UserRepository userRepository;
    private User currentUser;
    private LinearLayout recommendationContainer, spaRecommendationCard, offerRecommendationCard, gardenRecommendationCard, diningRecommendationCard, cabanaRecommendationCard;
    private List<View> recommendationDividers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        userRepository = new UserRepository(this);

        discoveryScrollView = findViewById(R.id.discovery_scroll_view);
        recommendationContainer = findViewById(R.id.recommendationContainer);
        spaRecommendationCard = findViewById(R.id.spaRecommendationCard);
        offerRecommendationCard = findViewById(R.id.offerRecommendationCard);
        gardenRecommendationCard = findViewById(R.id.gardenRecommendationCard);
        diningRecommendationCard = findViewById(R.id.diningRecommendationCard);
        cabanaRecommendationCard = findViewById(R.id.cabanaRecommendationCard);
        recommendationDividers = new ArrayList<>();
        recommendationDividers.add(findViewById(R.id.recommendationDivider1));
        recommendationDividers.add(findViewById(R.id.recommendationDivider2));
        recommendationDividers.add(findViewById(R.id.recommendationDivider3));
        recommendationDividers.add(findViewById(R.id.recommendationDivider4));

        View rootLayout = findViewById(R.id.root_layout);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            ScrollView scrollView = findViewById(R.id.scroll_view);
            if (scrollView.getChildCount() > 0) {
                scrollView.getChildAt(0).setPadding(0, statusBarHeight, 0, 0);
            }
            return insets;
        });

        setupDiscoveryCards();
        setupAutoScrolling();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAndPersonalizeData();

        new Handler().postDelayed(() -> {
            if (autoScrollHandler != null && autoScrollRunnable != null) {
                autoScrollHandler.postDelayed(autoScrollRunnable, SCROLL_DELAY);
            }
        }, 500);
    }

    private void loadAndPersonalizeData() {
        TextView welcomeMessage = findViewById(R.id.welcomeMessage);
        String userEmail = SharedPrefManager.getInstance(this).getUserEmail();
        if (userEmail != null) {
            currentUser = userRepository.getUserByEmail(userEmail);
            if (currentUser != null) {
                welcomeMessage.setText("Welcome Back, " + currentUser.getName() + "!");
                setupPersonalizedRecommendations();
            }
        }

        View callNowButton = findViewById(R.id.callNowButton);
        if (callNowButton != null) {
            callNowButton.setOnClickListener(v -> makePhoneCall());
        }
    }

    private void setupPersonalizedRecommendations() {
        if (currentUser == null || recommendationContainer == null) return;

        boolean showSpa = "Spa Treatments".equals(currentUser.getPreferredService());
        boolean showOffer = "Ocean View".equals(currentUser.getPreferredRoomType());
        boolean showGarden = "Garden View".equals(currentUser.getPreferredRoomType());
        boolean showDining = "Fine Dining".equals(currentUser.getPreferredService());
        boolean showCabana = "Poolside Cabanas".equals(currentUser.getPreferredService());

        spaRecommendationCard.setVisibility(showSpa ? View.VISIBLE : View.GONE);
        offerRecommendationCard.setVisibility(showOffer ? View.VISIBLE : View.GONE);
        gardenRecommendationCard.setVisibility(showGarden ? View.VISIBLE : View.GONE);
        diningRecommendationCard.setVisibility(showDining ? View.VISIBLE : View.GONE);
        cabanaRecommendationCard.setVisibility(showCabana ? View.VISIBLE : View.GONE);

        List<View> visibleCards = new ArrayList<>();
        if (showSpa) visibleCards.add(spaRecommendationCard);
        if (showOffer) visibleCards.add(offerRecommendationCard);
        if (showGarden) visibleCards.add(gardenRecommendationCard);
        if (showDining) visibleCards.add(diningRecommendationCard);
        if (showCabana) visibleCards.add(cabanaRecommendationCard);

        for (View divider : recommendationDividers) {
            divider.setVisibility(View.GONE);
        }

        int visibleCount = 0;
        if (showSpa) visibleCount++;
        if (showOffer) {
            if(visibleCount > 0) recommendationDividers.get(0).setVisibility(View.VISIBLE);
            visibleCount++;
        }
        if (showGarden) {
            if(visibleCount > 0) recommendationDividers.get(1).setVisibility(View.VISIBLE);
            visibleCount++;
        }
        if (showDining) {
            if(visibleCount > 0) recommendationDividers.get(2).setVisibility(View.VISIBLE);
            visibleCount++;
        }
        if (showCabana) {
            if(visibleCount > 0) recommendationDividers.get(3).setVisibility(View.VISIBLE);
            visibleCount++;
        }

        if (visibleCards.isEmpty()) {
            recommendationContainer.setVisibility(View.GONE);
        } else {
            recommendationContainer.setVisibility(View.VISIBLE);
        }
    }


    private void makePhoneCall() {
        String phoneNumber = "tel:+94112233445";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_PERMISSION_REQUEST_CODE);
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
                Toast.makeText(this, "Permission to call was denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
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
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void setupDiscoveryCards() {
        View cardOurRooms = findViewById(R.id.cardOurRooms);
        ImageView roomsImage = cardOurRooms.findViewById(R.id.discoveryCardImage);
        TextView roomsTitle = cardOurRooms.findViewById(R.id.discoveryCardTitle);
        roomsImage.setImageResource(R.drawable.home_discovery_rooms);
        roomsTitle.setText("Our Rooms");

        View cardOurServices = findViewById(R.id.cardOurServices);
        ImageView servicesImage = cardOurServices.findViewById(R.id.discoveryCardImage);
        TextView servicesTitle = cardOurServices.findViewById(R.id.discoveryCardTitle);
        servicesImage.setImageResource(R.drawable.home_discovery_services);
        servicesTitle.setText("Our Services");

        View cardFineDining = findViewById(R.id.cardFineDining);
        ImageView diningImage = cardFineDining.findViewById(R.id.discoveryCardImage);
        TextView diningTitle = cardFineDining.findViewById(R.id.discoveryCardTitle);
        diningImage.setImageResource(R.drawable.home_discovery_dining);
        diningTitle.setText("Fine Dining");

        View cardSpa = findViewById(R.id.cardSpa);
        ImageView spaImage = cardSpa.findViewById(R.id.discoveryCardImage);
        TextView spaTitle = cardSpa.findViewById(R.id.discoveryCardTitle);
        spaImage.setImageResource(R.drawable.home_discovery_spa);
        spaTitle.setText("Spa Treatments");

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
    protected void onPause() {
        super.onPause();
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }
}