package com.kushal.eduhabit.grammar.model;

import java.io.Serializable;
import java.util.List;

public class GrammarTopic implements Serializable {
    private String id;
    private String name;
    private String description;
    private String difficulty; // Beginner, Intermediate, Advanced
    private int totalLessons;
    private int completedLessons;
    private int progress; // 0-100
    private int iconResId;
    private List<LearningSection> learningSections;

    public GrammarTopic(String id, String name, String description, String difficulty, int totalLessons, int iconResId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.totalLessons = totalLessons;
        this.iconResId = iconResId;
        this.progress = 0;
        this.completedLessons = 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getDifficulty() { return difficulty; }
    public int getTotalLessons() { return totalLessons; }
    public int getCompletedLessons() { return completedLessons; }
    public int getProgress() { return progress; }
    public int getIconResId() { return iconResId; }
    public List<LearningSection> getLearningSections() { return learningSections; }
    public void setLearningSections(List<LearningSection> sections) { this.learningSections = sections; }
    public void setProgress(int progress) { this.progress = progress; }
}
