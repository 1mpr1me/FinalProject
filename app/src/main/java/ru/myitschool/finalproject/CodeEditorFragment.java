package ru.myitschool.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

public class CodeEditorFragment extends Fragment {

    private SlidingPaneLayout slidingPane;
    private ImageButton toggleButton;
    private WebView codeEditor;
    private Button runCodeButton;
    private boolean isDescriptionOpen = false; // Track state

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_editor, container, false);

        // Initialize UI elements
        slidingPane = view.findViewById(R.id.sliding_pane);
        toggleButton = view.findViewById(R.id.toggle_description);
        codeEditor = view.findViewById(R.id.codeEditor);
        runCodeButton = view.findViewById(R.id.run_code_button);

        // Load a sample code editor (replace with actual URL if needed)
        codeEditor.getSettings().setJavaScriptEnabled(true);
        codeEditor.loadUrl("file:///android_asset/codemirror.html"); // Load local HTML file

        // Handle toggle button click
        toggleButton.setOnClickListener(v -> toggleDescriptionPanel());

        return view;
    }

    private void toggleDescriptionPanel() {
        if (isDescriptionOpen) {
            slidingPane.closePane(); // Close description
            toggleButton.setImageResource(R.drawable.arrow_right); // Change to right arrow
        } else {
            slidingPane.openPane(); // Open description
            toggleButton.setImageResource(R.drawable.arrow_left); // Change to left arrow
        }
        isDescriptionOpen = !isDescriptionOpen; // Toggle state
    }
}
