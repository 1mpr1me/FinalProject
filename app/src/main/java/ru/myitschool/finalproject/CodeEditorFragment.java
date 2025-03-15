package ru.myitschool.finalproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import io.github.kbiakov.codeview.CodeView;

public class CodeEditorFragment extends Fragment {

    private CodeView codeEditor;
    private SlidingPaneLayout slidingPaneLayout;
    private ImageButton toggleDescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_editor, container, false);

        codeEditor = view.findViewById(R.id.code_editor);
        slidingPaneLayout = view.findViewById(R.id.exercise_description_panel);
        toggleDescription = view.findViewById(R.id.toggle_description);

        setupCodeEditor();
        setupSlidingPanel();

        return view;
    }

    private void setupCodeEditor() {
        codeEditor.setCode("# Write your Python code here\nprint('Hello, world!')", "py"); // will work faster!
    }

    private void setupSlidingPanel() {
        toggleDescription.setOnClickListener(v -> {
            if (slidingPaneLayout.isOpen()) {
                slidingPaneLayout.close();
            } else {
                slidingPaneLayout.open();
            }
        });
    }
}
