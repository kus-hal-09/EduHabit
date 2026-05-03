package com.kushal.eduhabit.grammar.model;

public class GrammarLesson {
    private String id;
    private String title;
    private String description;
    private String videoUrl;
    private int progress;

    public GrammarLesson(String id, String title, String description, String videoUrl, int progress) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.progress = progress;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getVideoUrl() { return videoUrl; }
    public int getProgress() { return progress; }
}
