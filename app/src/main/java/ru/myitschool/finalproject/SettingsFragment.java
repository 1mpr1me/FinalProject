package ru.myitschool.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference userRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        }

        // Initialize views
        MaterialButton changeUsernameButton = view.findViewById(R.id.change_username_button);
        MaterialButton changePasswordButton = view.findViewById(R.id.change_password_button);
        MaterialButton changeEmailButton = view.findViewById(R.id.change_email_button);
        MaterialButton deleteAccountButton = view.findViewById(R.id.delete_account_button);
        MaterialButton aboutButton = view.findViewById(R.id.about_button);
        MaterialButton privacyButton = view.findViewById(R.id.privacy_button);
        MaterialButton logoutButton = view.findViewById(R.id.logout_button);
        SwitchMaterial darkModeSwitch = view.findViewById(R.id.dark_mode_switch);
        SwitchMaterial notificationsSwitch = view.findViewById(R.id.notifications_switch);

        // Set click listeners
        changeUsernameButton.setOnClickListener(v -> showChangeUsernameDialog());
        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());
        changeEmailButton.setOnClickListener(v -> showChangeEmailDialog());
        deleteAccountButton.setOnClickListener(v -> showDeleteAccountDialog());
        aboutButton.setOnClickListener(v -> showAboutDialog());
        privacyButton.setOnClickListener(v -> showPrivacyPolicy());
        logoutButton.setOnClickListener(v -> logoutUser());

        // Set switch listeners
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Implement dark mode toggle
            Toast.makeText(getContext(), "Dark mode: " + (isChecked ? "On" : "Off"), Toast.LENGTH_SHORT).show();
        });

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // TODO: Implement notifications toggle
            Toast.makeText(getContext(), "Notifications: " + (isChecked ? "On" : "Off"), Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void showChangeUsernameDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_username, null);
        TextInputEditText newUsernameInput = dialogView.findViewById(R.id.new_username_input);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change Username")
                .setView(dialogView)
                .setPositiveButton("Change", (dialog, which) -> {
                    String newUsername = newUsernameInput.getText().toString().trim();
                    if (!newUsername.isEmpty()) {
                        updateUsername(newUsername);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateUsername(String newUsername) {
        if (userRef != null) {
            userRef.child("username").setValue(newUsername)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to update username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        TextInputEditText currentPasswordInput = dialogView.findViewById(R.id.current_password_input);
        TextInputEditText newPasswordInput = dialogView.findViewById(R.id.new_password_input);
        TextInputEditText confirmPasswordInput = dialogView.findViewById(R.id.confirm_password_input);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Change", (dialog, which) -> {
                    String currentPassword = currentPasswordInput.getText().toString().trim();
                    String newPassword = newPasswordInput.getText().toString().trim();
                    String confirmPassword = confirmPasswordInput.getText().toString().trim();

                    if (!currentPassword.isEmpty() && !newPassword.isEmpty() && !confirmPassword.isEmpty()) {
                        if (newPassword.equals(confirmPassword)) {
                            updatePassword(currentPassword, newPassword);
                        } else {
                            Toast.makeText(getContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updatePassword(String currentPassword, String newPassword) {
        if (currentUser != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);
            currentUser.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        currentUser.updatePassword(newPassword)
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to update password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void showChangeEmailDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_email, null);
        TextInputEditText currentPasswordInput = dialogView.findViewById(R.id.current_password_input);
        TextInputEditText newEmailInput = dialogView.findViewById(R.id.new_email_input);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Change Email")
                .setView(dialogView)
                .setPositiveButton("Change", (dialog, which) -> {
                    String currentPassword = currentPasswordInput.getText().toString().trim();
                    String newEmail = newEmailInput.getText().toString().trim();

                    if (!currentPassword.isEmpty() && !newEmail.isEmpty()) {
                        updateEmail(currentPassword, newEmail);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateEmail(String currentPassword, String newEmail) {
        if (currentUser != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);
            currentUser.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        currentUser.updateEmail(newEmail)
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(getContext(), "Email updated successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to update email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void showDeleteAccountDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteAccount())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        if (currentUser != null) {
            // Delete ru.myitschool.finalproject.User data from Realtime Database
            userRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Delete Firebase Auth account
                        currentUser.delete()
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(getContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getActivity(), LoginActivity.class));
                                    requireActivity().finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to delete account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to delete ru.myitschool.finalproject.User data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void showAboutDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("About App")
                .setMessage("Python Learning App v1.0\n\n" +
                        "A comprehensive app for learning Python programming with interactive lessons, " +
                        "exercises, and a supportive community.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showPrivacyPolicy() {
        // TODO: Implement privacy policy view
        Toast.makeText(getContext(), "Privacy policy feature coming soon!", Toast.LENGTH_SHORT).show();
    }

    private void logoutUser() {
        mAuth.signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        requireActivity().finish();
    }
}




