package com.kushal.eduhabit.speaking;

import java.io.Serializable;

public class SpeakingTopic implements Serializable {
    public String id;
    public String title;
    public String description;
    public String difficulty;
    public String targetSentence;
    public int iconResId;
    public int progress;

    public SpeakingTopic(String id, String title, String description, String difficulty, String targetSentence, int iconResId, int progress) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.targetSentence = targetSentence;
        this.iconResId = iconResId;
        this.progress = progress;
    }
}
