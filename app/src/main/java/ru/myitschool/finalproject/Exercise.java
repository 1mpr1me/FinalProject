package ru.myitschool.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Exercise implements Parcelable {
    private String title;
    private String description;
    private String difficulty;
    private String category;
    private String code;
    private List<TestCase> testCases;  // New field for test cases
    private List<String> hints;
    private String solutionMethodName;
    private int score;

    // Inner class to represent a test case
    public static class TestCase implements Parcelable {
        private List<String> inputs;  // List of inputs for the test case
        private String expectedOutput;  // Expected output for these inputs

        public TestCase() {
            this.inputs = new ArrayList<>();
        }

        public TestCase(List<String> inputs, String expectedOutput) {
            this.inputs = new ArrayList<>(inputs);  // Create a new ArrayList to avoid fixed-size list issues
            this.expectedOutput = expectedOutput;
        }

        protected TestCase(Parcel in) {
            inputs = new ArrayList<>();  // Create a new modifiable ArrayList
            in.readStringList(inputs);   // Read into the modifiable list
            expectedOutput = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringList(new ArrayList<>(inputs));  // Write a modifiable copy
            dest.writeString(expectedOutput);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<TestCase> CREATOR = new Creator<TestCase>() {
            @Override
            public TestCase createFromParcel(Parcel in) {
                return new TestCase(in);
            }

            @Override
            public TestCase[] newArray(int size) {
                return new TestCase[size];
            }
        };

        public List<String> getInputs() {
            return new ArrayList<>(inputs);  // Return a copy to ensure modifiability
        }

        public void setInputs(List<String> inputs) {
            this.inputs = inputs != null ? new ArrayList<>(inputs) : new ArrayList<>();
        }

        public String getExpectedOutput() {
            return expectedOutput;
        }

        public void setExpectedOutput(String expectedOutput) {
            this.expectedOutput = expectedOutput;
        }
    }

    // Required empty constructor for Firebase
    public Exercise() {
        this.hints = new ArrayList<>();
        this.testCases = new ArrayList<>();
    }

    public Exercise(String title, String description, String difficulty, String category, 
                   String code, List<TestCase> testCases, List<String> hints, 
                   String solutionMethodName, int score) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.category = category;
        this.code = code;
        this.testCases = testCases != null ? testCases : new ArrayList<>();
        this.hints = hints != null ? hints : new ArrayList<>();
        this.solutionMethodName = solutionMethodName;
        this.score = score;
    }

    protected Exercise(Parcel in) {
        title = in.readString();
        description = in.readString();
        difficulty = in.readString();
        category = in.readString();
        code = in.readString();
        testCases = new ArrayList<>();
        in.readTypedList(testCases, TestCase.CREATOR);
        hints = new ArrayList<>();
        in.readStringList(hints);
        solutionMethodName = in.readString();
        score = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(difficulty);
        dest.writeString(category);
        dest.writeString(code);
        dest.writeTypedList(testCases);
        dest.writeStringList(hints);
        dest.writeString(solutionMethodName);
        dest.writeInt(score);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getCategory() {
        return category;
    }

    public String getCode() {
        return code;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public List<String> getHints() {
        return hints;
    }

    public String getSolutionMethodName() {
        return solutionMethodName;
    }

    public int getScore() {
        return score;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases != null ? testCases : new ArrayList<>();
    }

    public void setHints(List<String> hints) {
        this.hints = hints != null ? hints : new ArrayList<>();
    }

    public void setSolutionMethodName(String solutionMethodName) {
        this.solutionMethodName = solutionMethodName;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "title='" + title + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", category='" + category + '\'' +
                ", score=" + score +
                '}';
    }
}
