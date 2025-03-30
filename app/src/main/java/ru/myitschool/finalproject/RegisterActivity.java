package ru.myitschool.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText usernameInput, emailInput, passwordInput;
    private MaterialButton registerButton;
    private TextView loginLink;
    private FirebaseAuth auth;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        registerButton = findViewById(R.id.register_button);
        loginLink = findViewById(R.id.login_link);

        // Set click listeners
        registerButton.setOnClickListener(v -> registerUser());
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate inputs
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user with email and password
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Get the user ID
                        String userId = auth.getCurrentUser().getUid();

                        // Set display name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();
                        auth.getCurrentUser().updateProfile(profileUpdates)
                                .addOnCompleteListener(profileTask -> {
                                    if (profileTask.isSuccessful()) {
                                        // Create user data
                                        Map<String, Object> userData = new HashMap<>();
                                        userData.put("username", username);
                                        userData.put("email", email);
                                        userData.put("score", 0);

                                        // Save user data to Firebase Database
                                        databaseRef.child("users").child(userId).setValue(userData)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(RegisterActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Failed to set display name", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}