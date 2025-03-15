package ru.myitschool.finalproject;

public class Exercise {
    private String title;
    private String description;
    private String url;
    private String difficulty;

    public Exercise(String title, String description, String url, String difficulty) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.difficulty = difficulty;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getUrl() { return url; }
    public String getDifficulty() { return difficulty; }
}
