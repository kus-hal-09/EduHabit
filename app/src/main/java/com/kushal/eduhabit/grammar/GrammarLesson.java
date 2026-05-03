package com.kushal.eduhabit.grammar;

/**
 * Isolated POJO for Grammar Lessons.
 */
public class GrammarLesson {
    private String id;
    private String title;
    private String description;
    private String content;
    private boolean isCompleted;
    private int progress; // 0 to 100

    public GrammarLesson(String id, String title, String description, String content, int progress, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.progress = progress;
        this.isCompleted = isCompleted;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContent() { return content; }
    public boolean isCompleted() { return isCompleted; }
    public int getProgress() { return progress; }
}
