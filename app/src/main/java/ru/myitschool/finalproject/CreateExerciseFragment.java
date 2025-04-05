package ru.myitschool.finalproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateExerciseFragment extends Fragment {
    private static final String TAG = "CreateExerciseFrag";
    private TextInputEditText titleInput;
    private TextInputEditText descriptionInput;
    private WebView initialCodeEditor;
    private TextInputEditText testInput;
    private TextInputEditText expectedOutput;
    private TextInputEditText hintsInput;
    private MaterialButton createButton;
    private DatabaseReference communityExercisesRef;
    private DatabaseReference userScoreRef;
    private static final int EXERCISE_COST = 10;
    private WebAppInterface initialCodeInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_exercise, container, false);
        
        // Initialize Firebase references
        communityExercisesRef = FirebaseDatabase.getInstance().getReference("community_exercises");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userScoreRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("score");
        
        // Initialize views
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        titleInput = view.findViewById(R.id.title_input);
        descriptionInput = view.findViewById(R.id.description_input);
        initialCodeEditor = view.findViewById(R.id.initial_code_editor);
        testInput = view.findViewById(R.id.test_input);
        expectedOutput = view.findViewById(R.id.expected_output);
        hintsInput = view.findViewById(R.id.hints_input);
        createButton = view.findViewById(R.id.create_button);
        
        // Initialize WebAppInterface
        initialCodeInterface = new WebAppInterface(requireContext());
        
        // Set up code editor
        setupCodeEditor(initialCodeEditor, initialCodeInterface);
        
        // Set up toolbar navigation
        toolbar.setNavigationOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
        
        // Set up create button
        createButton.setOnClickListener(v -> checkScoreAndCreate());
        
        return view;
    }
    
    private void setupCodeEditor(WebView webView, WebAppInterface webAppInterface) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(webAppInterface, "Android");
        webView.loadUrl("file:///android_asset/codemirror.html");
    }
    
    private void checkScoreAndCreate() {
        userScoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer currentScore = dataSnapshot.getValue(Integer.class);
                if (currentScore == null) {
                    currentScore = 0;
                }
                
                if (currentScore >= EXERCISE_COST) {
                    getCodeAndCreate(currentScore);
                } else {
                    Toast.makeText(getContext(), 
                        "Not enough score! You need " + EXERCISE_COST + " score, but you have " + currentScore, 
                        Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to check score: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Failed to check score", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void getCodeAndCreate(int currentScore) {
        initialCodeEditor.evaluateJavascript("javascript:sendCode()", initialValue -> {
            String initialCode = stripQuotes(initialValue);
            createExercise(currentScore, initialCode);
        });
    }
    
    private String stripQuotes(String value) {
        if (value == null || value.equals("null")) return "";
        return value.substring(1, value.length() - 1).replace("\\n", "\n").replace("\\\"", "\"");
    }
    
    private void createExercise(int currentScore, String initialCode) {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String difficulty = "Medium"; // Default difficulty
        String hintsText = hintsInput.getText().toString().trim();
        
        // Validate inputs
        if (title.isEmpty() || description.isEmpty() || initialCode.isEmpty() || hintsText.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Split hints into a list
        List<String> hints = new ArrayList<>(Arrays.asList(hintsText.split("\n")));
        
        // Get test inputs and outputs
        List<String> testInputs = getTestInputs();
        List<String> expectedOutputs = getExpectedOutputs();
        
        // Create test cases from inputs and outputs
        List<Exercise.TestCase> testCases = new ArrayList<>();
        for (int i = 0; i < testInputs.size(); i++) {
            List<String> inputs = Arrays.asList(testInputs.get(i));
            String expectedOutput = expectedOutputs.get(i);
            testCases.add(new Exercise.TestCase(inputs, expectedOutput));
        }
        
        // Create exercise object
        Exercise exercise = new Exercise(
            title,
            description,
            difficulty,
            "Community", // Category
            initialCode,
            testCases,  // test cases instead of solution
            hints,
            "solution",  // default method name for community exercises
            EXERCISE_COST // Score for community exercises
        );
        
        // Add to Firebase
        String exerciseId = communityExercisesRef.push().getKey();
        if (exerciseId != null) {
            // Update score first
            userScoreRef.setValue(currentScore - EXERCISE_COST)
                .addOnSuccessListener(aVoid -> {
                    // Then create the exercise
                    communityExercisesRef.child(exerciseId).setValue(exercise)
                        .addOnSuccessListener(aVoid2 -> {
                            Toast.makeText(getContext(), "Exercise created successfully!", Toast.LENGTH_SHORT).show();
                            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                                getParentFragmentManager().popBackStack();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to create exercise: " + e.getMessage());
                            Toast.makeText(getContext(), "Failed to create exercise: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update score: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to update score", Toast.LENGTH_SHORT).show();
                });
        }
    }
    
    private List<String> getTestInputs() {
        List<String> inputs = new ArrayList<>();
        String testInputText = testInput.getText().toString().trim();
        if (!testInputText.isEmpty()) {
            // Split by newlines and add each line as a test input
            String[] lines = testInputText.split("\n");
            inputs.addAll(Arrays.asList(lines));
        }
        return inputs;
    }
    
    private List<String> getExpectedOutputs() {
        List<String> outputs = new ArrayList<>();
        String expectedOutputText = expectedOutput.getText().toString().trim();
        if (!expectedOutputText.isEmpty()) {
            // Split by newlines and add each line as an expected output
            String[] lines = expectedOutputText.split("\n");
            outputs.addAll(Arrays.asList(lines));
        }
        return outputs;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (initialCodeEditor != null) {
            initialCodeEditor.loadUrl("about:blank");
            initialCodeEditor.clearHistory();
            initialCodeEditor.clearCache(true);
            initialCodeEditor.destroy();
        }
    }
} 