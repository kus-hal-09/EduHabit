package com.kushal.eduhabit.grammar.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.kushal.eduhabit.grammar.data.LessonRepository;
import com.kushal.eduhabit.grammar.model.GrammarLesson;
import java.util.List;

public class GrammarViewModel extends ViewModel {
    private final MutableLiveData<List<GrammarLesson>> lessons = new MutableLiveData<>();
    private final MutableLiveData<GrammarLesson> selectedLesson = new MutableLiveData<>();
    private final LessonRepository repository = new LessonRepository();

    public void loadLessons() {
        List<GrammarLesson> data = repository.getGrammarLessons();
        lessons.setValue(data);
        if (!data.isEmpty() && selectedLesson.getValue() == null) {
            selectedLesson.setValue(data.get(0));
        }
    }

    public LiveData<List<GrammarLesson>> getLessons() {
        return lessons;
    }

    public LiveData<GrammarLesson> getSelectedLesson() {
        return selectedLesson;
    }

    public void selectLesson(GrammarLesson lesson) {
        selectedLesson.setValue(lesson);
    }
}
