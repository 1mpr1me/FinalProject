package ru.myitschool.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardFragment extends Fragment {

    private RecyclerView leaderboardList;
    private List<UserScore> userScores;
    private DatabaseReference databaseRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        // Initialize Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference();

        // Initialize RecyclerView
        leaderboardList = view.findViewById(R.id.leaderboard_list);
        leaderboardList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize user scores list
        userScores = new ArrayList<>();

        // Load leaderboard data
        loadLeaderboardData();

        return view;
    }

    private void loadLeaderboardData() {
        databaseRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userScores.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String username = userSnapshot.child("username").getValue(String.class);
                    Integer score = userSnapshot.child("score").getValue(Integer.class);
                    if (username != null && score != null) {
                        userScores.add(new UserScore(username, score));
                    }
                }

                // Sort by score in descending order
                Collections.sort(userScores, (a, b) -> b.getScore() - a.getScore());

                // Set up adapter
                LeaderboardAdapter adapter = new LeaderboardAdapter(userScores);
                leaderboardList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error loading leaderboard", Toast.LENGTH_SHORT).show();
            }
        });
    }
}