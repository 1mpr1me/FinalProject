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
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class CreatePostFragment extends Fragment {
    private TextInputEditText descriptionInput;
    private MaterialButton attachCodeButton;
    private MaterialButton postButton;
    private WebView codeEditor;
    private String codeContent;
    private DatabaseReference postsRef;
    private WebAppInterface webAppInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postsRef = FirebaseDatabase.getInstance().getReference("posts");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_post, container, false);
        
        // Initialize views
        descriptionInput = view.findViewById(R.id.postDescriptionInput);
        attachCodeButton = view.findViewById(R.id.attachCodeButton);
        postButton = view.findViewById(R.id.postButton);
        codeEditor = view.findViewById(R.id.code_editor);
        
        // Initialize WebAppInterface
        webAppInterface = new WebAppInterface(requireContext());
        webAppInterface.setOnCodeReceivedListener(code -> {
            codeContent = code;
            Log.d("CreatePostFragment", "Code received, length: " + (code != null ? code.length() : 0));
        });
        
        // Setup code editor
        setupCodeEditor();

        attachCodeButton.setOnClickListener(v -> {
            if (codeEditor.getVisibility() == View.GONE) {
                codeEditor.setVisibility(View.VISIBLE);
                attachCodeButton.setText("Remove Code");
                // Load initial code editor content
                codeEditor.evaluateJavascript("javascript:initializeEditor()", null);
            } else {
                codeEditor.setVisibility(View.GONE);
                attachCodeButton.setText("Attach Code");
                codeContent = null;
            }
        });

        postButton.setOnClickListener(v -> createPost());

        return view;
    }

    private void setupCodeEditor() {
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
                // Initialize the editor after page load
                codeEditor.evaluateJavascript("javascript:initializeEditor()", null);
            }
        });
        
        codeEditor.setWebChromeClient(new WebChromeClient());
        codeEditor.addJavascriptInterface(webAppInterface, "Android");
        codeEditor.loadUrl("file:///android_asset/codemirror.html");
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

    private void createPost() {
        if (getContext() == null) {
            Toast.makeText(getContext(), "Context is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Please log in to create a post", Toast.LENGTH_SHORT).show();
            return;
        }

        String description = descriptionInput != null ? descriptionInput.getText().toString().trim() : "";
        if (description.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }

        postButton.setEnabled(false);

        if (codeEditor.getVisibility() == View.VISIBLE) {
            try {
                // Get code content from the editor
                codeEditor.evaluateJavascript("javascript:getCode()", value -> {
                    try {
                        if (value != null && !value.equals("null")) {
                            // Process the code content
                            String processedCode = value.substring(1, value.length() - 1)
                                    .replace("\\n", "\n")
                                    .replace("\\\"", "\"")
                                    .replace("\\'", "'")
                                    .replace("\\\\", "\\");
                            
                            Log.d("CreatePostFragment", "Processed code length: " + processedCode.length());
                            savePost(description, processedCode);
                        } else {
                            savePost(description, null);
                        }
                    } catch (Exception e) {
                        Log.e("CreatePostFragment", "Error processing code: " + e.getMessage());
                        Toast.makeText(getContext(), "Error processing code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        postButton.setEnabled(true);
                    }
                });
            } catch (Exception e) {
                Log.e("CreatePostFragment", "Error getting code: " + e.getMessage());
                Toast.makeText(getContext(), "Error getting code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                postButton.setEnabled(true);
            }
        } else {
            savePost(description, null);
        }
    }

    private void savePost(String description, String code) {
        try {
            String postId = UUID.randomUUID().toString();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            
            Post post = new Post();
            post.setId(postId);
            post.setUserId(userId);
            post.setDescription(description);
            post.setTimestamp(System.currentTimeMillis());
            
            if (code != null && !code.isEmpty()) {
                post.setCodeContent(code);
            }
            
            String displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            post.setUserName(displayName != null ? displayName : "Anonymous");
            
            if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null) {
                post.setUserAvatar(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
            }

            postsRef.child(postId).setValue(post)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Post created successfully!", Toast.LENGTH_SHORT).show();
                    
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                NavHostFragment.findNavController(this).navigateUp();
                            } catch (Exception e) {
                                Log.e("CreatePostFragment", "Navigation error: " + e.getMessage());
                                if (getActivity() != null) {
                                    getActivity().finish();
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CreatePostFragment", "Error saving post: " + e.getMessage());
                    Toast.makeText(getContext(), "Error creating post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    postButton.setEnabled(true);
                });
        } catch (Exception e) {
            Log.e("CreatePostFragment", "Error in savePost: " + e.getMessage());
            Toast.makeText(getContext(), "Error creating post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            postButton.setEnabled(true);
        }
    }
} 