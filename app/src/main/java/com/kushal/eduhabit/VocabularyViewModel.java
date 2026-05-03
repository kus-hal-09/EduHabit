package com.kushal.eduhabit;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class VocabularyViewModel extends ViewModel {
    public MutableLiveData<VocabularyModule> selectedModule = new MutableLiveData<>();
    public MutableLiveData<Integer> quizScore = new MutableLiveData<>(0);
    public MutableLiveData<Boolean> isQuizStarted = new MutableLiveData<>(false);
    public MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>(0);
}
