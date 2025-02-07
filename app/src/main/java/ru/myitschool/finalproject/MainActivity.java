package ru.myitschool.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    FirebaseAuth Auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Auth = FirebaseAuth.getInstance();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        user = Auth.getCurrentUser();
        if (user == null){
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        }

        //---------------------------------------------------------------------------

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id==R.id.nav_home){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new HomeFragment()).commit();
                    return true;
                } else if (id==R.id.nav_calendar) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new CalendarFragment()).commit();
                    return true;
                } else if (id==R.id.nav_progress) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new ProgressFragment()).commit();
                    return true;
                } else if (id==R.id.nav_settings) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new SettingsFragment()).commit();
                    return true;
                } else if (id==R.id.nav_start) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new StartFragment()).commit();
                    return true;
                } return false;
            }
        });


    }
}