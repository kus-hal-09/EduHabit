package com.kushal.eduhabit.grammar.model;

import java.io.Serializable;
import java.util.List;

public class LearningSection implements Serializable {
    public enum Type { INTRODUCTION, EXPLANATION, EXAMPLES, COMMON_MISTAKES, PRACTICE, QUIZ }

    private Type type;
    private String title;
    private String content;
    private List<String> bulletPoints;
    private List<Example> examples;
    private List<QuizQuestion> questions;

    public LearningSection(Type type, String title, String content) {
        this.type = type;
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public Type getType() { return type; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getBulletPoints() { return bulletPoints; }
    public void setBulletPoints(List<String> points) { this.bulletPoints = points; }
    public List<Example> getExamples() { return examples; }
    public void setExamples(List<Example> examples) { this.examples = examples; }
    public List<QuizQuestion> getQuestions() { return questions; }
    public void setQuestions(List<QuizQuestion> questions) { this.questions = questions; }

    public static class Example implements Serializable {
        public String text;
        public String explanation;
        public Example(String text, String explanation) {
            this.text = text;
            this.explanation = explanation;
        }
    }
}
