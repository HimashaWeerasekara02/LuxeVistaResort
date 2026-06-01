package com.example.luxevistaresort;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ServiceReservationActivity extends AppCompatActivity {

    private TextView dateText, timeText;
    private Calendar selectedDateTime;
    private String serviceName;
    private String serviceImageUri;
    private UserRepository userRepository;

    private boolean isModifyMode = false;
    private int bookingIdToModify = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_reservation);

        userRepository = new UserRepository(this);
        selectedDateTime = Calendar.getInstance();

        ImageView backButton = findViewById(R.id.backButton);
        ImageView serviceImage = findViewById(R.id.serviceImage);
        TextView serviceNameText = findViewById(R.id.serviceName);
        LinearLayout dateLayout = findViewById(R.id.dateLayout);
        LinearLayout timeLayout = findViewById(R.id.timeLayout);
        dateText = findViewById(R.id.dateText);
        timeText = findViewById(R.id.timeText);
        Button confirmButton = findViewById(R.id.confirmReservationButton);

        View rootLayout = findViewById(R.id.root_layout);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(v.getPaddingLeft(), statusBarHeight, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        Intent intent = getIntent();
        isModifyMode = intent.getBooleanExtra(Booking.EXTRA_IS_MODIFY_MODE, false);
        bookingIdToModify = intent.getIntExtra(Booking.EXTRA_BOOKING_ID, -1);

        serviceName = intent.getStringExtra(Booking.EXTRA_ITEM_NAME);
        serviceImageUri = intent.getStringExtra(Booking.EXTRA_ITEM_IMAGE_URI);

        serviceNameText.setText(serviceName);
        if (serviceImageUri != null && !serviceImageUri.isEmpty()) {
            serviceImage.setImageURI(Uri.parse(serviceImageUri));
        }

        backButton.setOnClickListener(v -> finish());
        dateLayout.setOnClickListener(v -> showDatePickerDialog());
        timeLayout.setOnClickListener(v -> showTimePickerDialog());
        confirmButton.setOnClickListener(v -> confirmReservation());

        if (isModifyMode) {
            confirmButton.setText("Update Reservation");
            long existingStartMillis = intent.getLongExtra(Booking.EXTRA_START_DATE, -1);
            if (existingStartMillis != -1) {
                selectedDateTime.setTimeInMillis(existingStartMillis);
                SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.US);
                dateText.setText(sdfDate.format(selectedDateTime.getTime()));
                timeText.setText(sdfTime.format(selectedDateTime.getTime()));
            }
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            selectedDateTime.set(Calendar.YEAR, year);
            selectedDateTime.set(Calendar.MONTH, month);
            selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            dateText.setText(sdf.format(selectedDateTime.getTime()));
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener,
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDateTime.set(Calendar.MINUTE, minute);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
            timeText.setText(sdf.format(selectedDateTime.getTime()));
        };

        new TimePickerDialog(this, timeSetListener,
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                false).show();
    }


    private void confirmReservation() {
        if (dateText.getText().toString().isEmpty() || timeText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select a date and time.", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedDateTime.set(Calendar.SECOND, 0);
        selectedDateTime.set(Calendar.MILLISECOND, 0);

        if (selectedDateTime.before(Calendar.getInstance())) {
            Toast.makeText(this, "You cannot book a time that has already passed.", Toast.LENGTH_LONG).show();
            return;
        }

        long bookingStartMillis = selectedDateTime.getTimeInMillis();
        String userEmail = SharedPrefManager.getInstance(this).getUserEmail();

        if (userEmail == null) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isSuccess;

        if (isModifyMode) {
            if (!userRepository.isServiceSlotAvailableForModification(serviceName, bookingStartMillis, bookingIdToModify)) {
                Toast.makeText(this, "This time slot is no longer available.", Toast.LENGTH_LONG).show();
                return;
            }
            isSuccess = userRepository.updateBooking(bookingIdToModify, bookingStartMillis, bookingStartMillis);
            if (isSuccess) {
                Toast.makeText(this, "Reservation updated!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update reservation.", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!userRepository.isServiceSlotAvailable(serviceName, bookingStartMillis)) {
                Toast.makeText(this, "This time slot is no longer available.", Toast.LENGTH_LONG).show();
                return;
            }
            isSuccess = userRepository.addBooking(new Booking(
                    0, userEmail, serviceName, bookingStartMillis,
                    bookingStartMillis, "Service", "Upcoming", serviceImageUri
            ));
            if (isSuccess) {
                Toast.makeText(this, "Reservation confirmed!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ServiceReservationActivity.this, BookingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to confirm reservation.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}