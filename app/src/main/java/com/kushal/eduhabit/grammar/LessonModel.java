package com.kushal.eduhabit.grammar;

import java.util.List;

/**
 * Clean POJO for Grammar Lessons.
 * Zero dependency on media or legacy logic.
 */
public class LessonModel {
    private final String id;
    private final String title;
    private final String description;
    private final int progress;
    private final List<QuizQuestion> questions;

    public LessonModel(String id, String title, String description, int progress, List<QuizQuestion> questions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.progress = progress;
        this.questions = questions;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getProgress() { return progress; }
    public List<QuizQuestion> getQuestions() { return questions; }

    public static class QuizQuestion {
        private final String questionText;
        private final List<String> options;
        private final int correctOptionIndex;

        public QuizQuestion(String questionText, List<String> options, int correctOptionIndex) {
            this.questionText = questionText;
            this.options = options;
            this.correctOptionIndex = correctOptionIndex;
        }

        public String getQuestionText() { return questionText; }
        public List<String> getOptions() { return options; }
        public int getCorrectOptionIndex() { return correctOptionIndex; }
    }
}
