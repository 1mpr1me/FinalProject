package ru.myitschool.finalproject;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CodeEditorFragment extends Fragment {

    private WebView codeEditor;
    private ImageButton runCodeButton;
    private WebAppInterface webAppInterface;
    private TextView output;
    private String initialCode;
    private int exerciseScore;
    private String exerciseTitle;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private String lastOutput = "";
    private Exercise exercise; // Corrected variable declaration
    private String exerciseId;
    private String currentCode;
    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_code_editor, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_code_editor, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Initialize UI elements
        codeEditor = rootView.findViewById(R.id.codeEditor);
        runCodeButton = rootView.findViewById(R.id.run_button);
        output = rootView.findViewById(R.id.output);
        ImageButton shareButton = rootView.findViewById(R.id.share_code_button);

        // Set up toolbar
        MaterialToolbar toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(exerciseTitle != null ? exerciseTitle : "Code Editor");
        }

        // Get Exercise data from arguments
        Bundle args = getArguments();
        if (args != null) {
            exercise = args.getParcelable("exercise"); // Corrected parameter name
            if (exercise != null) {
                exerciseTitle = exercise.getTitle();
                initialCode = exercise.getCode();
                exerciseScore = exercise.getScore();
                
                // Set toolbar title
                if (actionBar != null) {
                    actionBar.setTitle(exerciseTitle);
                }
            }
        }

        // Initialize WebAppInterface webAppInterface = new ru.myitschool.finalproject.WebAppInterface(requireContext());

        // Configure WebView
        setupWebView(codeEditor, initialCode);

        // Initialize Python
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(requireContext()));
        }

        // Set up click listeners
        runCodeButton.setOnClickListener(v -> runCode());
        shareButton.setOnClickListener(v -> showShareDialog());

        return rootView;
    }

    private void setupWebView(WebView webView, String initialCode) {
        WebSettings webSettings = webView.getSettings();
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

        webView.setWebViewClient(new WebViewClient() {
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

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                //super.onConsoleMessage(consoleMessage);
                // Log.d("WebViewConsole", consoleMessage.message() + " -- From line " +
                // consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
                output.append("\n" + consoleMessage.message());
                return true;
            }

            // For older Android versions, you might need to override this method as well
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                // Log.d("WebViewConsole", message + " -- From line " + lineNumber + " of " + sourceID);
                output.append("\n" + message);
            }
        });

        webView.addJavascriptInterface(webAppInterface, "Android");
        webView.loadUrl("file:///android_asset/codemirror.html");
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
                    
                    List<Exercise.TestCase> testCases = exercise.getTestCases();
                    String methodName = exercise.getSolutionMethodName();
                    
                    if (testCases != null && !testCases.isEmpty()) {
                        // Run all test cases
                        StringBuilder outputBuilder = new StringBuilder();
                        int passedTests = 0;
                        int totalTests = testCases.size();
                        
                        outputBuilder.append("Running ").append(totalTests).append(" test cases:\n\n");
                        
                        for (int i = 0; i < testCases.size(); i++) {
                            Exercise.TestCase testCase = testCases.get(i);
                            String result = executePythonMethod(code, methodName, testCase.getInputs());
                            boolean passed = result.trim().equals(testCase.getExpectedOutput().trim());
                            
                            outputBuilder.append("Test Case ").append(i + 1).append(":\n");
                            outputBuilder.append("Input: ").append(String.join(", ", testCase.getInputs())).append("\n");
                            outputBuilder.append("Expected Output: ").append(testCase.getExpectedOutput()).append("\n");
                            outputBuilder.append("Your Output: ").append(result).append("\n");
                            outputBuilder.append("Status: ").append(passed ? "PASSED ✓" : "FAILED ✗").append("\n\n");
                            
                            if (passed) passedTests++;
                        }
                        
                        outputBuilder.append("Summary: ").append(passedTests).append("/")
                                   .append(totalTests).append(" tests passed");
                        
                        final String testResults = outputBuilder.toString();
                        final boolean allPassed = passedTests == totalTests;
                        
                        new Handler(requireActivity().getMainLooper()).post(() -> {
                            output.setText(testResults);
                            output.setVisibility(View.VISIBLE);
                            
                            if (allPassed) {
                                updateUserScore();
                                Toast.makeText(getContext(), 
                                    "Congratulations! All test cases passed!", 
                                    Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), 
                                    "Some test cases failed. Keep trying!", 
                                    Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // If no test cases, just run the code normally
                        executePython(code);
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
    
    private String executePythonMethod(String code, String methodName, List<String> inputs) {
        try {
            Python py = Python.getInstance();
            PyObject pyobj = py.getModule("myhelper");
            return pyobj.callAttr("run_method", code, methodName, inputs).toString();
        } catch (Exception e) {
            Log.e("PythonExecution", "Error executing Python method", e);
            return "Error: " + e.getMessage();
        }
    }
    
    private void executePython(String code) {
        try {
            Log.d("PythonExecution", "Starting Python execution with code: " + code);
            Python py = Python.getInstance();
            PyObject pyobj = py.getModule("myhelper");
            String executedOutput = pyobj.callAttr("main", code).toString();
            Log.d("PythonExecution", "Python execution completed. Output: " + executedOutput);
            
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

    private void updateUserScore() {
        userRef.child("score").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer currentScore = dataSnapshot.getValue(Integer.class);
                if (currentScore == null) {
                    currentScore = 0;
                }
                
                // Update score
                userRef.child("score").setValue(currentScore + exerciseScore)
                    .addOnSuccessListener(aVoid -> {
                        // Show success message
                        new Handler(requireActivity().getMainLooper()).post(() -> {
                            Toast.makeText(getContext(), 
                                "Congratulations! You earned " + exerciseScore + " score!", 
                                Toast.LENGTH_LONG).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CodeEditor", "Failed to update score: " + e.getMessage());
                        new Handler(requireActivity().getMainLooper()).post(() -> {
                            Toast.makeText(getContext(), 
                                "Failed to update score: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        });
                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CodeEditor", "Failed to get current score: " + databaseError.getMessage());
                new Handler(requireActivity().getMainLooper()).post(() -> {
                    Toast.makeText(getContext(), 
                        "Failed to get current score", 
                        Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showShareDialog() {
        codeEditor.evaluateJavascript("javascript:sendCode()", code -> {
            if (code != null && !code.equals("null")) {
                // Properly escape the code for JavaScript
                final String codeText = code.substring(1, code.length() - 1)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("`", "\\`")
                    .replace("\\", "\\\\"); // Escape backslashes
                
                // Get the current Exercise title
                final String exerciseTitle = exercise != null ? exercise.getTitle() : "Shared Code";
                
                // Get current User's friends
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(currentUserId)
                    .child("friends");
                
                friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> friendIds = new ArrayList<>();
                        List<String> friendNames = new ArrayList<>();
                        
                        for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                            String friendId = friendSnapshot.getKey();
                            String friendName = friendSnapshot.getValue(String.class);
                            if (friendId != null && friendName != null) {
                                friendIds.add(friendId);
                                friendNames.add(friendName);
                            }
                        }
                        
                        if (friendIds.isEmpty()) {
                            Toast.makeText(getContext(), "You need to add friends first to share code", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        // Show friend selection dialog
                        String[] friendArray = friendNames.toArray(new String[0]);
                        boolean[] checkedItems = new boolean[friendIds.size()];
                        
                        new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Select Friends to Share With")
                            .setMultiChoiceItems(friendArray, checkedItems, (dialog, which, isChecked) -> {
                                checkedItems[which] = isChecked;
                            })
                            .setPositiveButton("Share", (dialog, which) -> {
                                // Share with selected friends
                                final int[] shareCount = {0};
                                for (int i = 0; i < checkedItems.length; i++) {
                                    if (checkedItems[i]) {
                                        final int finalIndex = i;
                                        String friendId = friendIds.get(finalIndex);
                                        String conversationId = getConversationId(currentUserId, friendId);
                                        
                                        // Create Message with type "code" to indicate it's a code message
                                        Message codeMessage = new Message(exerciseTitle,
                                            currentUserId,
                                            exerciseTitle,
                                            codeText);
                                        codeMessage.setType("code"); // Set message type to code
                                        
                                        // Share in chat
                                        FirebaseDatabase.getInstance().getReference()
                                            .child("conversations")
                                            .child(conversationId)
                                            .child("messages")
                                            .push()
                                            .setValue(codeMessage)
                                            .addOnSuccessListener(aVoid -> {
                                                shareCount[0]++;
                                                if (shareCount[0] == 1) {
                                                    Toast.makeText(getContext(), "Code shared successfully!", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), 
                                                    "Failed to share with " + friendNames.get(finalIndex) + ": " + e.getMessage(), 
                                                    Toast.LENGTH_SHORT).show();
                                            });
                                    }
                                }
                                
                                if (shareCount[0] == 0) {
                                    Toast.makeText(getContext(), "No friends selected", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load friends: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private String getConversationId(String uid1, String uid2) {
        return uid1.compareTo(uid2) < 0 ? uid1 + "_" + uid2 : uid2 + "_" + uid1;
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
