package com.example.quizapp.model;

public class Quiz {
    private int id;
    private String title;
    private String description;
    private int totalQuestions;
    private int completedQuestions;

    public Quiz() {
    }

    public Quiz(int id, String title, String description, int totalQuestions, int completedQuestions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.totalQuestions = totalQuestions;
        this.completedQuestions = completedQuestions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCompletedQuestions() {
        return completedQuestions;
    }

    public void setCompletedQuestions(int completedQuestions) {
        this.completedQuestions = completedQuestions;
    }

    public float getProgressPercentage() {
        if (totalQuestions == 0) return 0;
        return ((float) completedQuestions / totalQuestions) * 100;
    }
}
