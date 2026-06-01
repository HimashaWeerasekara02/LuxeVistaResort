package com.example.luxevistaresort;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editTextName;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private Button buttonRegister;
    private TextView textViewLoginLink;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize the UserRepository
        userRepository = new UserRepository(this);

        // Link UI elements
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmailReg);
        editTextPassword = findViewById(R.id.editTextPasswordReg);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLoginLink = findViewById(R.id.textViewLoginLink);

        // Set click listeners
        buttonRegister.setOnClickListener(v -> registerUser());

        textViewLoginLink.setOnClickListener(v -> {
            // Finish this activity to go back to the previous one (Login)
            finish();
        });
    }

    private void registerUser() {
        // Get user input and remove leading/trailing whitespace
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // --- Input Validation ---
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Use UserRepository for the registration logic ---
        UserRepository.RegistrationResult result = userRepository.register(name, email, password);

        // Handle the result from the repository
        switch (result) {
            case SUCCESS:
                Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_LONG).show();
                finish(); // Close RegisterActivity and return to LoginActivity
                break;
            case EMAIL_EXISTS:
                Toast.makeText(this, "User with this email already exists.", Toast.LENGTH_SHORT).show();
                break;
            case FAILURE:
                Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
