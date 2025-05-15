package com.example.quizapp.model;

public class Question {
    private int id;
    private int quizId;
    private String questionText;
    private String[] options;
    private String correctAnswer;
    private boolean answered;

    public Question() {
    }

    public Question(String questionText, String[] options, String correctAnswer) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.answered = false;
    }

    public Question(int id, int quizId, String questionText, String[] options, String correctAnswer) {
        this.id = id;
        this.quizId = quizId;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.answered = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
}
