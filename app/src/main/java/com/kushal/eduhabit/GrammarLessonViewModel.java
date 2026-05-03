package com.kushal.eduhabit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class GrammarLessonViewModel extends ViewModel {
    private final MutableLiveData<List<Lesson>> lessons = new MutableLiveData<>();
    private final MutableLiveData<Lesson> selectedLesson = new MutableLiveData<>();

    public GrammarLessonViewModel() {
        loadData();
    }

    public LiveData<List<Lesson>> getLessons() {
        return lessons;
    }

    public LiveData<Lesson> getSelectedLesson() {
        return selectedLesson;
    }

    public void selectLesson(Lesson lesson) {
        selectedLesson.setValue(lesson);
    }

    private void loadData() {
        List<Lesson> list = new ArrayList<>();

        // Lesson 1
        List<Lesson.QuizQuestion> q1 = new ArrayList<>();
        q1.add(new Lesson.QuizQuestion("Which is correct?", List.of("If I was you", "If I were you", "If I am you", "If I be you"), 1));
        q1.add(new Lesson.QuizQuestion("I suggest that he ___ here.", List.of("stay", "stays", "stayed", "will stay"), 0));
        list.add(new Lesson("1. Subjunctive Mood", "6H-f_LY_688", 
                "The subjunctive mood is used to explore hypothetical situations or to express wishes, suggestions, or commands.", q1));

        // Lesson 2
        List<Lesson.QuizQuestion> q2 = new ArrayList<>();
        q2.add(new Lesson.QuizQuestion("The man ___ lives next door is a doctor.", List.of("which", "who", "whom", "whose"), 1));
        list.add(new Lesson("2. Relative Clauses", "o_v9W4X_X_Y", 
                "Relative clauses give us more information about a person or thing. We use relative pronouns to introduce them.", q2));

        // Lesson 3
        List<Lesson.QuizQuestion> q3 = new ArrayList<>();
        q3.add(new Lesson.QuizQuestion("Never ___ seen such beauty.", List.of("I have", "have I", "I had", "did I"), 1));
        list.add(new Lesson("3. Inversion", "trMVny_6X_Y", 
                "Inversion happens when we reverse the normal order of the subject and the verb for emphasis.", q3));

        // Lesson 4
        List<Lesson.QuizQuestion> q4 = new ArrayList<>();
        q4.add(new Lesson.QuizQuestion("___ I need is a coffee.", List.of("It", "What", "Which", "That"), 1));
        list.add(new Lesson("4. Cleft Sentences", "v_X4W4_X_Y", 
                "A cleft sentence is a sentence in which some constituent is moved from its regular position to give it greater emphasis.", q4));

        lessons.setValue(list);
        selectedLesson.setValue(list.get(0));
    }
}
