package com.kushal.eduhabit;

import java.util.List;

public class Lesson {
    public String title;
    public String videoId;
    public String theoryContent;
    public List<QuizQuestion> quizQuestions;

    public Lesson(String title, String videoId, String theoryContent, List<QuizQuestion> quizQuestions) {
        this.title = title;
        this.videoId = videoId;
        this.theoryContent = theoryContent;
        this.quizQuestions = quizQuestions;
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
