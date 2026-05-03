package com.kushal.eduhabit;

public class Achievement {
    public String id;
    public String title;
    public String description;
    public int iconResId;
    public int targetValue;
    public int currentProgress;
    public boolean isUnlocked;

    public Achievement(String id, String title, String description, int iconResId, int targetValue) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
        this.targetValue = targetValue;
        this.isUnlocked = false;
        this.currentProgress = 0;
    }
}
