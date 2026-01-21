package com.example.springapp.model;

public class QuestionProbability {

    private final String question;
    private final double probability;

    public QuestionProbability(String question, double probability) {
        this.question = question;
        this.probability = probability;
    }

    public String getQuestion() {
        return question;
    }

    public double getProbability() {
        return probability;
    }
}
