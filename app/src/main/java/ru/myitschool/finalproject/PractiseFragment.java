package ru.myitschool.finalproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class PractiseFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Exercise> exerciseList;
    private ExerciseAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practise, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewExercises);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load exercises
        exerciseList = new ArrayList<>();  // Ensure it's initialized
        exerciseList = getExercises();
        adapter = new ExerciseAdapter(exerciseList);
        recyclerView.setAdapter(adapter);

        return view;
    }


    private List<Exercise> getExercises() {
        List<Exercise> exercises = new ArrayList<>();

        exercises.add(new Exercise("Two Sum", "Find two numbers that add up to a target.", "https://leetcode.com/problems/two-sum/", "Easy"));
        exercises.add(new Exercise("Binary Search", "Efficiently find an element in a sorted array.", "https://leetcode.com/problems/binary-search/", "Easy"));
        exercises.add(new Exercise("Merge Intervals", "Merge overlapping intervals.", "https://leetcode.com/problems/merge-intervals/", "Medium"));
        exercises.add(new Exercise("Dijkstra's Algorithm", "Find shortest paths in a graph.", "https://leetcode.com/problems/network-delay-time/", "Hard"));

        return exercises;
    }
}
