package ru.myitschool.finalproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.List;

public class CommunityExercisesFragment extends Fragment {
    private static final String TAG = "CommunityExercisesFrag";
    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private List<Exercise> exercises;
    private DatabaseReference communityExercisesRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_exercises, container, false);
        recyclerView = view.findViewById(R.id.exercise_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        exercises = new ArrayList<>();
        adapter = new ExerciseAdapter(exercises, this::onExerciseClick);
        recyclerView.setAdapter(adapter);
        
        // Initialize Firebase reference
        communityExercisesRef = FirebaseDatabase.getInstance().getReference("community_exercises");
        loadCommunityExercises();
        
        return view;
    }

    private void loadCommunityExercises() {
        communityExercisesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                exercises.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Exercise exercise = snapshot.getValue(Exercise.class);
                    if (exercise != null) {
                        exercises.add(exercise);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load exercises: " + databaseError.getMessage());
            }
        });
    }

    private void onExerciseClick(Exercise exercise) {
        CodeEditorFragment codeEditorFragment = new CodeEditorFragment();
        Bundle args = new Bundle();
        args.putParcelable("exercise", exercise);
        codeEditorFragment.setArguments(args);
        
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, codeEditorFragment)
                .addToBackStack(null)
                .commit();
    }
} 