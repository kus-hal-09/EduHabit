package com.kushal.eduhabit;

import java.util.List;

public class GrammarModule {
    public String title;
    public String description;
    public String videoId;
    public String theory;
    public List<String> takeaways;
    public List<QuizQuestion> questions;

    public GrammarModule(String title, String description, String videoId, String theory, List<String> takeaways, List<QuizQuestion> questions) {
        this.title = title;
        this.description = description;
        this.videoId = videoId;
        this.theory = theory;
        this.takeaways = takeaways;
        this.questions = questions;
    }

    public static class QuizQuestion {
        public String question;
        public List<String> options;
        public int correctIndex;

        public QuizQuestion(String question, List<String> options, int correctIndex) {
            this.question = question;
            this.options = options;
            this.correctIndex = correctIndex;
        }
    }
}
