package ru.myitschool.finalproject;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CodeEditorFragment extends Fragment {

    private SlidingPaneLayout slidingPane;
    private ImageButton toggleButton;
    private WebView codeEditor;
    private Button runCodeButton;
    private WebAppInterface webAppInterface;
    private TextView output;
    private TextView exerciseDescription;
    private String initialCode;
    private boolean isDescriptionOpen = false;
    private int exercisePoints;
    private String exerciseTitle;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private String lastOutput = "";  // Add this field to store the last execution output

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_editor, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Initialize UI elements
        slidingPane = view.findViewById(R.id.sliding_pane);
        toggleButton = view.findViewById(R.id.toggle_description);
        codeEditor = view.findViewById(R.id.codeEditor);
        runCodeButton = view.findViewById(R.id.run_code_button);
        output = view.findViewById(R.id.output);
        exerciseDescription = view.findViewById(R.id.exercise_description);

        // Get exercise data from arguments
        Bundle args = getArguments();
        if (args != null) {
            exerciseTitle = args.getString("title");
            String description = args.getString("description");
            initialCode = args.getString("initial_code");
            exercisePoints = args.getInt("points");
            
            // Set exercise description
            exerciseDescription.setText(exerciseTitle + "\n\n" + description);
        }

        // Initialize WebAppInterface
        webAppInterface = new WebAppInterface(requireContext());

        // Configure WebView
        setupWebView();

        // Initialize Python
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(requireContext()));
        }

        // Set up click listeners
        toggleButton.setOnClickListener(v -> toggleDescriptionPanel());
        runCodeButton.setOnClickListener(v -> runCode());

        return view;
    }

    private void setupWebView() {
        WebSettings webSettings = codeEditor.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        codeEditor.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Set initial code when page is loaded
                if (initialCode != null) {
                    String js = String.format("javascript:editor.setValue(`%s`);", initialCode);
                    view.evaluateJavascript(js, null);
                }
            }
        });

        codeEditor.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                Log.d("WebViewConsole", message);
            }
        });

        codeEditor.addJavascriptInterface(webAppInterface, "Android");
        codeEditor.loadUrl("file:///android_asset/codemirror.html");
    }

    private void runCode() {
        Log.d("CodeEditor", "Starting code execution...");
        codeEditor.evaluateJavascript("javascript:sendCode()", value -> {
            Log.d("CodeEditor", "JavaScript returned value: " + value);
            try {
                if (value != null && !value.equals("null")) {
                    // Remove quotes and handle JSON string
                    String code = value.substring(1, value.length() - 1);
                    code = code.replace("\\n", "\n").replace("\\\"", "\"");
                    Log.d("CodeEditor", "Extracted code: " + code);
                    
                    // Execute the code
                    executePython(code);
                    
                    // Check if the solution is correct
                    boolean isCorrect = checkSolution(code);
                    if (isCorrect) {
                        updateUserScore();
                    } else {
                        // Show message for wrong answer
                        new Handler(requireActivity().getMainLooper()).post(() -> {
                            Toast.makeText(getContext(), "Try again! Your solution isn't quite right.", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    Log.e("CodeEditor", "Error: No code received from JavaScript");
                    output.setText("Error: No code received from editor");
                    output.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                Log.e("CodeEditor", "Error processing code", e);
                output.setText("Error processing code: " + e.getMessage());
                output.setVisibility(View.VISIBLE);
            }
        });
    }

    private void executePython(String code) {
        try {
            Log.d("PythonExecution", "Starting Python execution with code: " + code);
            Python py = Python.getInstance();
            PyObject pyobj = py.getModule("myhelper");
            String executedOutput = pyobj.callAttr("main", code).toString();
            Log.d("PythonExecution", "Python execution completed. Output: " + executedOutput);
            
            // Store the output for solution checking
            lastOutput = executedOutput;
            
            // Update UI on the main thread
            new Handler(requireActivity().getMainLooper()).post(() -> {
                output.setText(executedOutput);
                output.setVisibility(View.VISIBLE);
            });
        } catch (Exception e) {
            Log.e("PythonExecution", "Error executing Python code", e);
            // Update UI on the main thread
            new Handler(requireActivity().getMainLooper()).post(() -> {
                output.setText("Error executing code: " + e.getMessage());
                output.setVisibility(View.VISIBLE);
            });
        }
    }

    private boolean checkSolution(String code) {
        // Check solution based on expected output
        switch (exerciseTitle) {
            // Easy Problems (5 points)
            case "Hello World":
                return lastOutput.trim().equals("Hello, World!");
            case "Basic Calculator":
                return lastOutput.trim().equals("8");  // 5 + 3 = 8
            case "String Length":
                return lastOutput.trim().equals("13");  // Length of "Hello, Python!"
            case "Count Vowels":
                return lastOutput.trim().equals("3");  // Count of vowels in "Hello, World!"
            
            // Medium Problems (15 points)
            case "Even or Odd":
                return lastOutput.toLowerCase().contains("odd") || lastOutput.toLowerCase().contains("even");
            case "List Operations":
                return lastOutput.contains("[") && lastOutput.contains("]");  // Any list operation that shows result
            case "Palindrome Check":
                return lastOutput.toLowerCase().contains("true") || lastOutput.toLowerCase().contains("palindrome");
            case "Factorial Calculator":
                return lastOutput.trim().equals("120");  // 5! = 120
            case "Fibonacci Sequence":
                return lastOutput.contains("0") && lastOutput.contains("1") && lastOutput.contains("1") && 
                       lastOutput.contains("2") && lastOutput.contains("3") && lastOutput.contains("5");
            case "Prime Number Check":
                return lastOutput.toLowerCase().contains("true") || lastOutput.toLowerCase().contains("prime");
            
            // Hard Problems (30 points)
            case "Valid Parentheses":
                return lastOutput.toLowerCase().contains("true") || lastOutput.toLowerCase().contains("valid");
            case "Reverse String":
                return lastOutput.equals("olleh");  // "hello" reversed
            case "Add Two Numbers":
                return lastOutput.contains("[7,0,8]");  // [2,4,3] + [5,6,4] = [7,0,8]
            case "Longest Palindromic Substring":
                return lastOutput.contains("bab") || lastOutput.contains("aba");  // For "babad" input
            case "Median of Two Sorted Arrays":
                return lastOutput.contains("2.0");  // For [1,3] and [2] input
            case "Regular Expression Matching":
                return lastOutput.toLowerCase().contains("true") || lastOutput.toLowerCase().contains("match");
            case "Binary Tree Level Order Traversal":
                return lastOutput.contains("[[3]") && lastOutput.contains("[9,20]") && 
                       lastOutput.contains("[15,7]");
            case "Merge K Sorted Lists":
                return lastOutput.contains("[1,1,2,3,4,4,5,6]");
            default:
                return false;
        }
    }

    private void updateUserScore() {
        // First check if this exercise is already completed
        userRef.child("completed_exercises").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Boolean> completedExercises = new HashMap<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot exercise : dataSnapshot.getChildren()) {
                        completedExercises.put(exercise.getKey(), true);
                    }
                }

                // If exercise is not completed, update score
                if (!completedExercises.containsKey(exerciseTitle)) {
                    userRef.child("score").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int currentScore = 0;
                            if (dataSnapshot.exists()) {
                                currentScore = dataSnapshot.getValue(Integer.class);
                            }

                            // Update score and mark exercise as completed
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("score", currentScore + exercisePoints);
                            updates.put("completed_exercises/" + exerciseTitle, true);
                            userRef.updateChildren(updates);

                            // Show success message
                            new Handler(requireActivity().getMainLooper()).post(() -> {
                                String message = String.format("Correct! You earned %d points!", exercisePoints);
                                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            new Handler(requireActivity().getMainLooper()).post(() -> {
                                Toast.makeText(getContext(), "Error updating score", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                } else {
                    new Handler(requireActivity().getMainLooper()).post(() -> {
                        Toast.makeText(getContext(), "You've already completed this exercise!", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new Handler(requireActivity().getMainLooper()).post(() -> {
                    Toast.makeText(getContext(), "Error checking completed exercises", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void toggleDescriptionPanel() {
        if (isDescriptionOpen) {
            slidingPane.closePane();
            toggleButton.setImageResource(R.drawable.arrow_right);
        } else {
            slidingPane.openPane();
            toggleButton.setImageResource(R.drawable.arrow_left);
        }
        isDescriptionOpen = !isDescriptionOpen;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (codeEditor != null) {
            codeEditor.loadUrl("about:blank");
            codeEditor.clearHistory();
            codeEditor.clearCache(true);
            codeEditor.destroy();
        }
    }
}
