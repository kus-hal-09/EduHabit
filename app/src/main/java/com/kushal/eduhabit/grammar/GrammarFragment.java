package com.kushal.eduhabit.grammar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kushal.eduhabit.R;
import com.kushal.eduhabit.databinding.FragmentGrammarIsolatedBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Senior-Level Isolated Grammar Module.
 * This is a self-contained feature that replaces video with interactive quizzes.
 */
public class GrammarFragment extends Fragment {

    private FragmentGrammarIsolatedBinding binding;
    private LessonAdapter adapter;
    private List<LessonModel> lessons;
    private LessonModel activeLesson;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGrammarIsolatedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupData();
        setupRecyclerView();
        
        binding.btnSubmitQuiz.setOnClickListener(v -> validateQuiz());
    }

    private void setupData() {
        lessons = new ArrayList<>();
        
        // Data for Lesson 1
        List<LessonModel.QuizQuestion> q1 = new ArrayList<>();
        q1.add(new LessonModel.QuizQuestion("If I ___ you, I would study harder.", Arrays.asList("was", "am", "were", "be"), 2));
        lessons.add(new LessonModel("1", "Subjunctive Mood", "Hypothetical scenarios and suggestions.", 45, q1));

        // Data for Lesson 2
        List<LessonModel.QuizQuestion> q2 = new ArrayList<>();
        q2.add(new LessonModel.QuizQuestion("The book ___ I bought is on the table.", Arrays.asList("who", "which", "whom", "whose"), 1));
        lessons.add(new LessonModel("2", "Relative Clauses", "Master who, which, and that.", 10, q2));
        
        // Data for Lesson 3
        List<LessonModel.QuizQuestion> q3 = new ArrayList<>();
        q3.add(new LessonModel.QuizQuestion("Never ___ seen such a beautiful sight.", Arrays.asList("I have", "have I", "I had", "did I"), 1));
        lessons.add(new LessonModel("3", "Inversion", "Subject-verb inversion for emphasis.", 0, q3));
    }

    private void setupRecyclerView() {
        adapter = new LessonAdapter(lessons, this::startKnowledgeCheck);
        binding.rvLessons.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvLessons.setAdapter(adapter);
    }

    private void startKnowledgeCheck(LessonModel lesson) {
        this.activeLesson = lesson;
        if (lesson.getQuestions().isEmpty()) {
            Toast.makeText(getContext(), "No quiz for this lesson yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        // UI Switch: Dashboard to Quiz
        binding.llDashboard.setVisibility(View.GONE);
        binding.cvQuiz.setVisibility(View.VISIBLE);

        // Load Question 1
        LessonModel.QuizQuestion q = lesson.getQuestions().get(0);
        binding.tvQuizTitle.setText("Assessment: " + lesson.getTitle());
        binding.tvQuestion.setText(q.getQuestionText());

        binding.rgQuiz.removeAllViews();
        for (int i = 0; i < q.getOptions().size(); i++) {
            RadioButton rb = new RadioButton(getContext());
            rb.setText(q.getOptions().get(i));
            rb.setPadding(16, 32, 16, 32);
            rb.setId(i);
            binding.rgQuiz.addView(rb);
        }
    }

    private void validateQuiz() {
        int checkedId = binding.rgQuiz.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(getContext(), "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        int correctIndex = activeLesson.getQuestions().get(0).getCorrectOptionIndex();
        if (checkedId == correctIndex) {
            Toast.makeText(getContext(), "Correct! ✨ Lesson Complete.", Toast.LENGTH_LONG).show();
            exitQuiz();
        } else {
            Toast.makeText(getContext(), "Incorrect. Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void exitQuiz() {
        binding.cvQuiz.setVisibility(View.GONE);
        binding.llDashboard.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
