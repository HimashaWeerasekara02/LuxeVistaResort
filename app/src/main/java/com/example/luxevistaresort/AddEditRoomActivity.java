package com.example.luxevistaresort;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class AddEditRoomActivity extends AppCompatActivity {

    private EditText editTextRoomName, editTextRoomDescription, editTextRoomPrice, editTextRoomAmenities, editTextRoomCapacity;
    private Button buttonSaveRoom, buttonSelectImage;
    private TextView headerTitle;
    private ImageView imageViewRoomPreview, backButton;
    private UserRepository userRepository;
    private boolean isEditMode = false;
    private int roomIdToEdit = -1;
    private String permanentImageUriString;

    private String notificationTitle;
    private String notificationMessage;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                Log.d("NotificationDebug", "Permission callback fired. isGranted: " + isGranted);
                if (isGranted) {
                    NotificationHelper.sendNotification(this, notificationTitle, notificationMessage);
                } else {
                    Toast.makeText(this, "Permission denied. Users will not be notified.", Toast.LENGTH_LONG).show();
                }
                Log.d("NotificationDebug", "FINISHING activity from permission callback.");
                finish();
            });

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri tempUri = result.getData().getData();
                    permanentImageUriString = saveImageToInternalStorage(tempUri);
                    if (permanentImageUriString != null) {
                        imageViewRoomPreview.setImageURI(Uri.parse(permanentImageUriString));
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_room);
        userRepository = new UserRepository(this);
        initializeViews();

        backButton.setOnClickListener(v -> finish());
        buttonSelectImage.setOnClickListener(v -> openGallery());
        buttonSaveRoom.setOnClickListener(v -> saveRoom());

        if (getIntent().hasExtra("ROOM_ID")) {
            isEditMode = true;
            roomIdToEdit = getIntent().getIntExtra("ROOM_ID", -1);
            headerTitle.setText("Edit Room");
            buttonSaveRoom.setText("Save Changes");
            loadRoomData();
        } else {
            headerTitle.setText("Add New Room");
            buttonSaveRoom.setText("Add Room");
        }
    }

    private void initializeViews() {
        headerTitle = findViewById(R.id.headerTitle);
        imageViewRoomPreview = findViewById(R.id.imageViewRoomPreview);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        editTextRoomName = findViewById(R.id.editTextRoomName);
        editTextRoomDescription = findViewById(R.id.editTextRoomDescription);
        editTextRoomPrice = findViewById(R.id.editTextRoomPrice);
        editTextRoomAmenities = findViewById(R.id.editTextRoomAmenities);
        editTextRoomCapacity = findViewById(R.id.editTextRoomCapacity);
        buttonSaveRoom = findViewById(R.id.buttonSaveRoom);
        backButton = findViewById(R.id.backButton);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void loadRoomData() {
        if (roomIdToEdit != -1) {
            Room room = userRepository.getRoomById(roomIdToEdit);
            if (room != null) {
                editTextRoomName.setText(room.getName());
                editTextRoomDescription.setText(room.getDescription());
                editTextRoomPrice.setText(String.valueOf(room.getPrice()));
                editTextRoomAmenities.setText(room.getAmenities());
                editTextRoomCapacity.setText(String.valueOf(room.getCapacity()));
                permanentImageUriString = room.getImageUri();
                if (permanentImageUriString != null && !permanentImageUriString.isEmpty()) {
                    imageViewRoomPreview.setImageURI(Uri.parse(permanentImageUriString));
                }
            }
        }
    }

    private void saveRoom() {
        String name = editTextRoomName.getText().toString().trim();
        String description = editTextRoomDescription.getText().toString().trim();
        String priceStr = editTextRoomPrice.getText().toString().trim();
        String amenities = editTextRoomAmenities.getText().toString().trim();
        String capacityStr = editTextRoomCapacity.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || amenities.isEmpty() || capacityStr.isEmpty() || (permanentImageUriString == null && !isEditMode)) {
            Toast.makeText(this, "Please fill all fields and select an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int price = Integer.parseInt(priceStr);
            int capacity = Integer.parseInt(capacityStr);
            boolean success;

            if (isEditMode) {
                Room updatedRoom = new Room(roomIdToEdit, name, description, price, amenities, capacity, permanentImageUriString);
                success = userRepository.updateRoom(updatedRoom);
                if (success) {
                    Toast.makeText(this, "Room updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update room.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Room newRoom = new Room(0, name, description, price, amenities, capacity, permanentImageUriString);
                success = userRepository.addRoom(newRoom);
                if (success) {
                    Toast.makeText(this, "New room added!", Toast.LENGTH_SHORT).show();
                    sendNewItemNotificationAndFinish(name);
                } else {
                    Toast.makeText(this, "Failed to add room.", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number for Price and Capacity.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNewItemNotificationAndFinish(String itemName) {
        notificationTitle = "New Room Available!";
        notificationMessage = "Check out our new " + itemName + " room.";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Log.d("NotificationDebug", "Permission already granted. Sending notification.");
                NotificationHelper.sendNotification(this, notificationTitle, notificationMessage);
                Log.d("NotificationDebug", "FINISHING activity immediately.");
                finish();
            } else {
                Log.d("NotificationDebug", "Permission not granted. Requesting now.");
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            NotificationHelper.sendNotification(this, notificationTitle, notificationMessage);
            Log.d("NotificationDebug", "Older Android. FINISHING activity immediately.");
            finish();
        }
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File directory = getApplicationContext().getDir("images", Context.MODE_PRIVATE);
            String fileName = "room_" + System.currentTimeMillis() + ".jpg";
            File file = new File(directory, fileName);

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
            return Uri.fromFile(file).toString();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}