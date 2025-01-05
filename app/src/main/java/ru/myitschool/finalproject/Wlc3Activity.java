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

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Wlc3Activity extends AppCompatActivity {

    Button Cnt;
    EditText M;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wlc3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Cnt = findViewById(R.id.continue3);
        M = findViewById(R.id.weight);
        mAuth = FirebaseAuth.getInstance();

        Cnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String weight = M.getText().toString();

                if(weight.isEmpty()){
                    Toast.makeText(Wlc3Activity.this, "Please enter your height.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userId = mAuth.getCurrentUser().getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userRef = database.getReference("users").child(userId);

                userRef.child("weight").setValue(weight)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(Wlc3Activity.this, "Weight saved successfully!", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(getApplicationContext(), Wlc3Activity.class);
                            startActivity(i);
                            finish(); // Close the current activity after data is saved
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Wlc3Activity.this, "Failed to save weight.", Toast.LENGTH_LONG).show();
                        });
            }
        });
    }
}