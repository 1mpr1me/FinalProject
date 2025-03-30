package ru.myitschool.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PractiseFragment extends Fragment {
    private RecyclerView recyclerView;
    private ExerciseAdapter adapter;
    private List<Exercise> exercises;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practise, container, false);
        recyclerView = view.findViewById(R.id.exercise_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        loadExercises();
        adapter = new ExerciseAdapter(exercises, this::onExerciseClick);
        recyclerView.setAdapter(adapter);
        
        return view;
    }

    private void loadExercises() {
        exercises = new ArrayList<>();
        
        // Basic Python Exercises
        exercises.add(new Exercise(
            "Basic Calculator",
            "Create a calculator that can perform basic arithmetic operations (+, -, *, /) on two numbers.",
            "Easy",
            "Basic",
            "def calculator(a, b, operation):\n    if operation == '+':\n        return a + b\n    elif operation == '-':\n        return a - b\n    elif operation == '*':\n        return a * b\n    elif operation == '/':\n        return a / b if b != 0 else 'Error: Division by zero'\n\n# Test the function\nprint(calculator(10, 5, '+'))\nprint(calculator(10, 5, '-'))\nprint(calculator(10, 5, '*'))\nprint(calculator(10, 5, '/'))",
            "def calculator(a, b, operation):\n    if operation == '+':\n        return a + b\n    elif operation == '-':\n        return a - b\n    elif operation == '*':\n        return a * b\n    elif operation == '/':\n        return a / b if b != 0 else 'Error: Division by zero'\n\n# Test the function\nprint(calculator(10, 5, '+'))\nprint(calculator(10, 5, '-'))\nprint(calculator(10, 5, '*'))\nprint(calculator(10, 5, '/'))",
            List.of("Use if-elif statements", "Handle division by zero")
        ));

        exercises.add(new Exercise(
            "String Manipulation",
            "Write a function that takes a string and returns a new string with all vowels removed and consonants in reverse order.",
            "Medium",
            "Basic",
            "def manipulate_string(text):\n    # Remove vowels and get consonants\n    consonants = [char for char in text if char.lower() not in 'aeiou']\n    # Reverse consonants and join\n    return ''.join(consonants[::-1])\n\n# Test the function\nprint(manipulate_string('Hello, World!'))",
            "def manipulate_string(text):\n    # Remove vowels and get consonants\n    consonants = [char for char in text if char.lower() not in 'aeiou']\n    # Reverse consonants and join\n    return ''.join(consonants[::-1])\n\n# Test the function\nprint(manipulate_string('Hello, World!'))",
            List.of("Use list comprehension", "Use string slicing for reversal")
        ));

        // Control Flow Exercises
        exercises.add(new Exercise(
            "Number Guessing Game",
            "Create a number guessing game where the computer randomly selects a number between 1 and 100, and the user tries to guess it.",
            "Medium",
            "Control Flow",
            "import random\n\ndef guessing_game():\n    number = random.randint(1, 100)\n    attempts = 0\n    \n    while True:\n        guess = int(input('Guess the number (1-100): '))\n        attempts += 1\n        \n        if guess < number:\n            print('Too low!')\n        elif guess > number:\n            print('Too high!')\n        else:\n            print(f'Congratulations! You found the number in {attempts} attempts!')\n            break\n\n# Start the game\nguessing_game()",
            "import random\n\ndef guessing_game():\n    number = random.randint(1, 100)\n    attempts = 0\n    \n    while True:\n        guess = int(input('Guess the number (1-100): '))\n        attempts += 1\n        \n        if guess < number:\n            print('Too low!')\n        elif guess > number:\n            print('Too high!')\n        else:\n            print(f'Congratulations! You found the number in {attempts} attempts!')\n            break\n\n# Start the game\nguessing_game()",
            List.of("Use while loop", "Track number of attempts")
        ));

        // Functions Exercises
        exercises.add(new Exercise(
            "Recursive Directory Size",
            "Write a function that recursively calculates the total size of a directory and all its contents.",
            "Hard",
            "Functions",
            "import os\n\ndef get_directory_size(path):\n    total_size = 0\n    \n    for dirpath, dirnames, filenames in os.walk(path):\n        for filename in filenames:\n            file_path = os.path.join(dirpath, filename)\n            total_size += os.path.getsize(file_path)\n    \n    return total_size\n\n# Test the function\npath = '.'  # Current directory\nprint(f'Total size: {get_directory_size(path)} bytes')",
            "import os\n\ndef get_directory_size(path):\n    total_size = 0\n    \n    for dirpath, dirnames, filenames in os.walk(path):\n        for filename in filenames:\n            file_path = os.path.join(dirpath, filename)\n            total_size += os.path.getsize(file_path)\n    \n    return total_size\n\n# Test the function\npath = '.'  # Current directory\nprint(f'Total size: {get_directory_size(path)} bytes')",
            List.of("Use os.walk()", "Use os.path.join()")
        ));

        // Lists and Dictionaries Exercises
        exercises.add(new Exercise(
            "Matrix Operations",
            "Implement basic matrix operations (addition, multiplication) for 2D lists.",
            "Hard",
            "Data Structures",
            "def matrix_add(a, b):\n    return [[a[i][j] + b[i][j] for j in range(len(a[0]))] for i in range(len(a))]\n\ndef matrix_multiply(a, b):\n    result = [[0 for _ in range(len(b[0]))] for _ in range(len(a))]\n    for i in range(len(a)):\n        for j in range(len(b[0])):\n            for k in range(len(b)):\n                result[i][j] += a[i][k] * b[k][j]\n    return result\n\n# Test the functions\nmatrix1 = [[1, 2], [3, 4]]\nmatrix2 = [[5, 6], [7, 8]]\nprint('Addition:', matrix_add(matrix1, matrix2))\nprint('Multiplication:', matrix_multiply(matrix1, matrix2))",
            "def matrix_add(a, b):\n    return [[a[i][j] + b[i][j] for j in range(len(a[0]))] for i in range(len(a))]\n\ndef matrix_multiply(a, b):\n    result = [[0 for _ in range(len(b[0]))] for _ in range(len(a))]\n    for i in range(len(a)):\n        for j in range(len(b[0])):\n            for k in range(len(b)):\n                result[i][j] += a[i][k] * b[k][j]\n    return result\n\n# Test the functions\nmatrix1 = [[1, 2], [3, 4]]\nmatrix2 = [[5, 6], [7, 8]]\nprint('Addition:', matrix_add(matrix1, matrix2))\nprint('Multiplication:', matrix_multiply(matrix1, matrix2))",
            List.of("Use nested list comprehension", "Handle matrix dimensions")
        ));

        // File Handling Exercises
        exercises.add(new Exercise(
            "Log File Analyzer",
            "Write a program that analyzes a log file and counts the occurrences of different log levels (INFO, WARNING, ERROR).",
            "Hard",
            "File Handling",
            "import re\n\ndef analyze_log_file(filename):\n    log_counts = {'INFO': 0, 'WARNING': 0, 'ERROR': 0}\n    \n    try:\n        with open(filename, 'r') as file:\n            for line in file:\n                for level in log_counts.keys():\n                    if level in line:\n                        log_counts[level] += 1\n                        break\n        \n        print('Log Analysis Results:')\n        for level, count in log_counts.items():\n            print(f'{level}: {count}')\n    except FileNotFoundError:\n        print('Log file not found!')\n\n# Test the function\nanalyze_log_file('app.log')",
            "import re\n\ndef analyze_log_file(filename):\n    log_counts = {'INFO': 0, 'WARNING': 0, 'ERROR': 0}\n    \n    try:\n        with open(filename, 'r') as file:\n            for line in file:\n                for level in log_counts.keys():\n                    if level in line:\n                        log_counts[level] += 1\n                        break\n        \n        print('Log Analysis Results:')\n        for level, count in log_counts.items():\n            print(f'{level}: {count}')\n    except FileNotFoundError:\n        print('Log file not found!')\n\n# Test the function\nanalyze_log_file('app.log')",
            List.of("Use dictionary for counting", "Handle file errors")
        ));
    }

    private void onExerciseClick(Exercise exercise) {
        CodeEditorFragment codeEditorFragment = new CodeEditorFragment();
        Bundle args = new Bundle();
        args.putString("title", exercise.getTitle());
        args.putString("description", exercise.getDescription());
        args.putString("code", exercise.getCode());
        args.putString("solution", exercise.getSolution());
        args.putStringArrayList("hints", new ArrayList<>(exercise.getHints()));
        codeEditorFragment.setArguments(args);
        
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, codeEditorFragment)
                .addToBackStack(null)
                .commit();
    }
}
