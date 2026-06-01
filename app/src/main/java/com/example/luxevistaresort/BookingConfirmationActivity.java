package com.example.luxevistaresort;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BookingConfirmationActivity extends AppCompatActivity {

    private TextView checkInDateText, checkOutDateText, roomNameText;
    private TextView priceDetailsText, subtotalPriceText, taxesPriceText, totalPriceText;
    private LinearLayout bookingSummaryCard, checkInLayout, checkOutLayout;
    private Button confirmBookingButton;
    private ImageView roomImage, backButton;
    private Spinner adultsSpinner, childrenSpinner;

    private Calendar checkInCalendar, checkOutCalendar;
    private int roomPricePerNight;
    private String roomName;
    private String roomImageUri;
    private UserRepository userRepository;
    private boolean isModifyMode = false;
    private int bookingIdToModify = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        userRepository = new UserRepository(this);
        initializeViews();

        Intent intent = getIntent();
        isModifyMode = intent.getBooleanExtra(Booking.EXTRA_IS_MODIFY_MODE, false);
        bookingIdToModify = intent.getIntExtra(Booking.EXTRA_BOOKING_ID, -1);
        roomPricePerNight = intent.getIntExtra(Booking.EXTRA_ROOM_PRICE, 0); // This one is unique to rooms
        roomName = intent.getStringExtra(Booking.EXTRA_ITEM_NAME);
        roomImageUri = intent.getStringExtra(Booking.EXTRA_ITEM_IMAGE_URI);

        roomNameText.setText(roomName);
        if (roomImageUri != null && !roomImageUri.isEmpty()) {
            roomImage.setImageURI(Uri.parse(roomImageUri));
        }

        backButton.setOnClickListener(v -> finish());
        checkInLayout.setOnClickListener(v -> showDatePickerDialog(true));
        checkOutLayout.setOnClickListener(v -> showDatePickerDialog(false));
        setupSpinners();
        confirmBookingButton.setOnClickListener(v -> confirmBooking());

        if (isModifyMode) {
            confirmBookingButton.setText("Update Booking");
            long existingStartDateMillis = intent.getLongExtra(Booking.EXTRA_START_DATE, -1);
            long existingEndDateMillis = intent.getLongExtra(Booking.EXTRA_END_DATE, -1);

            if (existingStartDateMillis != -1 && existingEndDateMillis != -1) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                checkInCalendar = Calendar.getInstance();
                checkInCalendar.setTimeInMillis(existingStartDateMillis);
                checkOutCalendar = Calendar.getInstance();
                checkOutCalendar.setTimeInMillis(existingEndDateMillis);

                checkInDateText.setText(sdf.format(checkInCalendar.getTime()));
                checkOutDateText.setText(sdf.format(checkOutCalendar.getTime()));
                updateBookingSummary();
            }
        }
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        roomImage = findViewById(R.id.roomImage);
        roomNameText = findViewById(R.id.roomName);
        checkInLayout = findViewById(R.id.checkInLayout);
        checkOutLayout = findViewById(R.id.checkOutLayout);
        checkInDateText = findViewById(R.id.checkInDateText);
        checkOutDateText = findViewById(R.id.checkOutDateText);
        adultsSpinner = findViewById(R.id.adultsSpinner);
        childrenSpinner = findViewById(R.id.childrenSpinner);
        bookingSummaryCard = findViewById(R.id.bookingSummaryCard);
        priceDetailsText = findViewById(R.id.priceDetails);
        subtotalPriceText = findViewById(R.id.subtotalPrice);
        taxesPriceText = findViewById(R.id.taxesPrice);
        totalPriceText = findViewById(R.id.totalPrice);
        confirmBookingButton = findViewById(R.id.confirmBookingButton);

        View rootLayout = findViewById(R.id.root_layout);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(v.getPaddingLeft(), statusBarHeight, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
    }

    private void confirmBooking() {
        if (checkInCalendar == null || checkOutCalendar == null) {
            Toast.makeText(this, "Please select check-in and check-out dates.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = SharedPrefManager.getInstance(this).getUserEmail();
        if (userEmail == null) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        long checkInMillis = checkInCalendar.getTimeInMillis();
        long checkOutMillis = checkOutCalendar.getTimeInMillis();
        boolean isSuccess;

        if (isModifyMode) {
            if (!userRepository.isRoomAvailableForModification(roomName, checkInMillis, checkOutMillis, bookingIdToModify)) {
                Toast.makeText(this, "Sorry, the selected dates are no longer available.", Toast.LENGTH_LONG).show();
                return;
            }
            isSuccess = userRepository.updateBooking(bookingIdToModify, checkInMillis, checkOutMillis);
            if (isSuccess) {
                Toast.makeText(this, "Booking updated successfully!", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to update booking.", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!userRepository.isRoomAvailable(roomName, checkInMillis, checkOutMillis)) {
                Toast.makeText(this, "Sorry, the selected dates are no longer available.", Toast.LENGTH_LONG).show();
                return;
            }
            Booking newBooking = new Booking(0, userEmail, roomName, checkInMillis, checkOutMillis, "Room", "Upcoming", roomImageUri);
            isSuccess = userRepository.addBooking(newBooking);
            if (isSuccess) {
                Toast.makeText(this, "Booking confirmed!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(BookingConfirmationActivity.this, BookingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to confirm booking.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> adultsAdapter = ArrayAdapter.createFromResource(this, R.array.adult_options, R.layout.spinner_item_style);
        adultsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adultsSpinner.setAdapter(adultsAdapter);

        ArrayAdapter<CharSequence> childrenAdapter = ArrayAdapter.createFromResource(this, R.array.children_options, R.layout.spinner_item_style);
        childrenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        childrenSpinner.setAdapter(childrenAdapter);
    }

    private void showDatePickerDialog(boolean isCheckIn) {
        Calendar initialCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            String formattedDate = sdf.format(selectedDate.getTime());

            if (isCheckIn) {
                checkInCalendar = selectedDate;
                checkInDateText.setText(formattedDate);
                if (checkOutCalendar != null && !checkOutCalendar.after(checkInCalendar)) {
                    checkOutCalendar = null;
                    checkOutDateText.setText("");
                }
            } else {
                if (checkInCalendar == null) {
                    Toast.makeText(this, "Please select a check-in date first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!selectedDate.after(checkInCalendar)) {
                    Toast.makeText(this, "Check-out date must be after check-in date.", Toast.LENGTH_SHORT).show();
                    return;
                }
                checkOutCalendar = selectedDate;
                checkOutDateText.setText(formattedDate);
            }
            updateBookingSummary();
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener,
                initialCalendar.get(Calendar.YEAR),
                initialCalendar.get(Calendar.MONTH),
                initialCalendar.get(Calendar.DAY_OF_MONTH));

        if (isCheckIn) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        } else if (checkInCalendar != null) {
            datePickerDialog.getDatePicker().setMinDate(checkInCalendar.getTimeInMillis() + TimeUnit.DAYS.toMillis(1));
        } else {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        }
        datePickerDialog.show();
    }

    private void updateBookingSummary() {
        if (checkInCalendar != null && checkOutCalendar != null && checkOutCalendar.after(checkInCalendar)) {
            Calendar startDate = (Calendar) checkInCalendar.clone();
            Calendar endDate = (Calendar) checkOutCalendar.clone();

            startDate.set(Calendar.HOUR_OF_DAY, 0); startDate.set(Calendar.MINUTE, 0); startDate.set(Calendar.SECOND, 0); startDate.set(Calendar.MILLISECOND, 0);
            endDate.set(Calendar.HOUR_OF_DAY, 0); endDate.set(Calendar.MINUTE, 0); endDate.set(Calendar.SECOND, 0); endDate.set(Calendar.MILLISECOND, 0);

            long diffInMillis = endDate.getTimeInMillis() - startDate.getTimeInMillis();
            long numberOfNights = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            if (numberOfNights > 0 && roomPricePerNight > 0) {
                double subtotal = numberOfNights * roomPricePerNight;
                double taxes = subtotal * 0.12;
                double total = subtotal + taxes;

                priceDetailsText.setText(numberOfNights + (numberOfNights == 1 ? " night" : " nights") + " x $" + roomPricePerNight);
                subtotalPriceText.setText(String.format(Locale.US, "$%.2f", subtotal));
                taxesPriceText.setText(String.format(Locale.US, "$%.2f", taxes));
                totalPriceText.setText(String.format(Locale.US, "$%.2f", total));
                bookingSummaryCard.setVisibility(View.VISIBLE);
            } else {
                bookingSummaryCard.setVisibility(View.GONE);
                if (numberOfNights <= 0) {
                    Toast.makeText(this, "Check-out must be at least one day after check-in.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            bookingSummaryCard.setVisibility(View.GONE);
        }
    }
}