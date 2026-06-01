package com.example.luxevistaresort;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

public class AddEditServiceActivity extends AppCompatActivity {

    private EditText editTextServiceName, editTextServiceDescription, editTextServicePrice, editTextServiceCapacity;
    private Button buttonSaveService, buttonSelectImage;
    private TextView headerTitle;
    private ImageView imageViewServicePreview, backButton;
    private UserRepository userRepository;
    private boolean isEditMode = false;
    private int serviceIdToEdit = -1;
    private String permanentImageUriString;

    private String notificationTitle;
    private String notificationMessage;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    NotificationHelper.sendNotification(this, notificationTitle, notificationMessage);
                } else {
                    Toast.makeText(this, "Permission denied. Users will not be notified.", Toast.LENGTH_LONG).show();
                }
                finish();
            });

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri tempUri = result.getData().getData();
                    permanentImageUriString = saveImageToInternalStorage(tempUri);
                    if (permanentImageUriString != null) {
                        imageViewServicePreview.setImageURI(Uri.parse(permanentImageUriString));
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_service);
        userRepository = new UserRepository(this);
        initializeViews();

        backButton.setOnClickListener(v -> finish());
        buttonSelectImage.setOnClickListener(v -> openGallery());
        buttonSaveService.setOnClickListener(v -> saveService());

        if (getIntent().hasExtra("SERVICE_ID")) {
            isEditMode = true;
            serviceIdToEdit = getIntent().getIntExtra("SERVICE_ID", -1);
            headerTitle.setText("Edit Service");
            buttonSaveService.setText("Save Changes");
            loadServiceData();
        } else {
            headerTitle.setText("Add New Service");
            buttonSaveService.setText("Add Service");
        }
    }

    private void initializeViews() {
        headerTitle = findViewById(R.id.headerTitle);
        imageViewServicePreview = findViewById(R.id.imageViewServicePreview);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        editTextServiceName = findViewById(R.id.editTextServiceName);
        editTextServiceDescription = findViewById(R.id.editTextServiceDescription);
        editTextServicePrice = findViewById(R.id.editTextServicePrice);
        editTextServiceCapacity = findViewById(R.id.editTextServiceCapacity);
        buttonSaveService = findViewById(R.id.buttonSaveService);
        backButton = findViewById(R.id.backButton);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void loadServiceData() {
        if (serviceIdToEdit != -1) {
            Service service = userRepository.getServiceById(serviceIdToEdit);
            if (service != null) {
                editTextServiceName.setText(service.getName());
                editTextServiceDescription.setText(service.getDescription());
                editTextServicePrice.setText(String.valueOf(service.getPrice()));
                editTextServiceCapacity.setText(String.valueOf(service.getCapacity()));
                permanentImageUriString = service.getImageUri();
                if (permanentImageUriString != null && !permanentImageUriString.isEmpty()) {
                    imageViewServicePreview.setImageURI(Uri.parse(permanentImageUriString));
                }
            }
        }
    }

    private void saveService() {
        String name = editTextServiceName.getText().toString().trim();
        String description = editTextServiceDescription.getText().toString().trim();
        String priceStr = editTextServicePrice.getText().toString().trim();
        String capacityStr = editTextServiceCapacity.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty() || capacityStr.isEmpty() || (permanentImageUriString == null && !isEditMode)) {
            Toast.makeText(this, "Please fill all fields and select an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int price = Integer.parseInt(priceStr);
            int capacity = Integer.parseInt(capacityStr);
            boolean success;

            if (isEditMode) {
                Service updatedService = new Service(serviceIdToEdit, name, description, price, capacity, permanentImageUriString);
                success = userRepository.updateService(updatedService);
                if (success) {
                    Toast.makeText(this, "Service updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update service.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Service newService = new Service(0, name, description, price, capacity, permanentImageUriString);
                success = userRepository.addService(newService);
                if (success) {
                    Toast.makeText(this, "New service added!", Toast.LENGTH_SHORT).show();
                    sendNewItemNotificationAndFinish(name);
                } else {
                    Toast.makeText(this, "Failed to add service.", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for Price and Capacity.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNewItemNotificationAndFinish(String itemName) {
        notificationTitle = "New Service Available";
        notificationMessage = "Check out our new " + itemName + " service.";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                NotificationHelper.sendNotification(this, notificationTitle, notificationMessage);
                finish();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            NotificationHelper.sendNotification(this, notificationTitle, notificationMessage);
            finish();
        }
    }

    private String saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            File directory = getApplicationContext().getDir("images", Context.MODE_PRIVATE);
            String fileName = "service_" + System.currentTimeMillis() + ".jpg";
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