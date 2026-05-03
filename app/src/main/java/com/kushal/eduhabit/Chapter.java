package com.kushal.eduhabit;

import com.kushal.eduhabit.grammar.model.QuizQuestion;
import java.io.Serializable;
import java.util.List;

public class Chapter implements Serializable {
    private String title;
    private String topicsCovered;
    private String notes;
    private String examples;
    private List<QuizQuestion> mcqs; 
    private List<String> boardQuestions;
    private String resources;
    private int weightage;
    private int studyHours;
    private boolean isCompleted;
    private boolean isExpanded;

    public Chapter(String title, String topicsCovered, String notes, String examples, 
                   List<QuizQuestion> mcqs, List<String> boardQuestions, 
                   String resources, int weightage, int studyHours) {
        this.title = title;
        this.topicsCovered = topicsCovered;
        this.notes = notes;
        this.examples = examples;
        this.mcqs = mcqs;
        this.boardQuestions = boardQuestions;
        this.resources = resources;
        this.weightage = weightage;
        this.studyHours = studyHours;
        this.isCompleted = false;
        this.isExpanded = false;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public String getTopicsCovered() { return topicsCovered; }
    public String getNotes() { return notes; }
    public String getExamples() { return examples; }
    public List<QuizQuestion> getMcqs() { return mcqs; }
    public List<String> getBoardQuestions() { return boardQuestions; }
    public String getResources() { return resources; }
    public int getWeightage() { return weightage; }
    public int getStudyHours() { return studyHours; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public boolean isExpanded() { return isExpanded; }
    public void setExpanded(boolean expanded) { isExpanded = expanded; }
}
