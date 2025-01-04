package ru.myitschool.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Wlc2Activity extends AppCompatActivity {

    EditText H;
    FirebaseAuth mAuth;
    Button Cnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wlc2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Cnt = findViewById(R.id.continue2);
        mAuth = FirebaseAuth.getInstance();
        H = findViewById(R.id.height);

        Cnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the height from the EditText
                String height = H.getText().toString();

                // Validate the height input
                if (height.isEmpty()) {
                    Toast.makeText(Wlc2Activity.this, "Please enter your height.", Toast.LENGTH_SHORT).show();
                    return; // Don't proceed if height is empty
                }

                // Attaching database
                String userId = mAuth.getCurrentUser().getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userRef = database.getReference("users").child(userId);

                // Store the height as a String
                userRef.child("height").setValue(height)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(Wlc2Activity.this, "Height saved successfully!", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(getApplicationContext(), Wlc3Activity.class);
                            startActivity(i);
                            finish(); // Close the current activity after data is saved
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Wlc2Activity.this, "Failed to save height.", Toast.LENGTH_LONG).show();
                        });
            }
        });

    }
}
