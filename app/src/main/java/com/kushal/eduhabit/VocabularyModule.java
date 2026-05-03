package com.kushal.eduhabit;

import java.util.List;

public class VocabularyModule {
    public String moduleTitle;
    public String videoId;
    public String wordCount;
    public String difficulty;
    public int iconResId;
    public List<Word> powerWords;
    public List<Question> quizSet;

    public VocabularyModule(String title, String videoId, String count, String diff, int icon, List<Word> powerWords, List<Question> quizSet) {
        this.moduleTitle = title;
        this.videoId = videoId;
        this.wordCount = count;
        this.difficulty = diff;
        this.iconResId = icon;
        this.powerWords = powerWords;
        this.quizSet = quizSet;
    }

    public static class Word {
        public String term;
        public String definition;
        public String sentence;
        public boolean isAddedToFlashcard;

        public Word(String term, String definition, String sentence) {
            this.term = term;
            this.definition = definition;
            this.sentence = sentence;
            this.isAddedToFlashcard = false;
        }
    }

    public static class Question {
        public String question;
        public List<String> options;
        public int correctOptionIndex;
        public String explanation;

        public Question(String question, List<String> options, int correctOptionIndex, String explanation) {
            this.question = question;
            this.options = options;
            this.correctOptionIndex = correctOptionIndex;
            this.explanation = explanation;
        }
    }
}
