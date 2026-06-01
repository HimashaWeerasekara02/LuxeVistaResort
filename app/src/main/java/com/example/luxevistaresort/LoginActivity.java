package com.example.luxevistaresort;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRepository = new UserRepository(this);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.loginButton);
        TextView registerLink = findViewById(R.id.registerPrompt);

        loginButton.setOnClickListener(v -> loginUser());

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = userRepository.loginUser(email, password);

        if (user != null) {
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

            // Save the entire user object to SharedPreferences for session management.
            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

            // Check the user's role and redirect to the appropriate dashboard.
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                // Pass the user's name to the dashboard for a personalized welcome.
                intent.putExtra("USER_NAME", user.getName());
                startActivity(intent);
            }
            finishAffinity(); // Finish all previous activities so the user can't go back.
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}

