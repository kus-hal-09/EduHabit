package com.kushal.eduhabit;

import java.util.List;

public class Subject {
    private String id;
    private String name;
    private String code;
    private String description;
    private String type; // Theory, Practical, Elective
    private int credits;
    private int contactHours;
    private String examPattern;
    private String prerequisites;
    private int semester;
    private int iconResId;
    private List<Chapter> syllabus;
    private int progress;

    public Subject(String id, String name, String code, String description, String type, 
                   int credits, int contactHours, String examPattern, String prerequisites, 
                   int semester, int iconResId, List<Chapter> syllabus) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.type = type;
        this.credits = credits;
        this.contactHours = contactHours;
        this.examPattern = examPattern;
        this.prerequisites = prerequisites;
        this.semester = semester;
        this.iconResId = iconResId;
        this.syllabus = syllabus;
        this.calculateProgress();
    }

    public void calculateProgress() {
        if (syllabus == null || syllabus.isEmpty()) {
            this.progress = 0;
            return;
        }
        int completed = 0;
        for (Chapter c : syllabus) {
            if (c.isCompleted()) completed++;
        }
        this.progress = (completed * 100) / syllabus.size();
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public int getCredits() { return credits; }
    public int getContactHours() { return contactHours; }
    public String getExamPattern() { return examPattern; }
    public String getPrerequisites() { return prerequisites; }
    public int getSemester() { return semester; }
    public int getIconResId() { return iconResId; }
    public List<Chapter> getSyllabus() { return syllabus; }
    public int getProgress() { return progress; }
}
