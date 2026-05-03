package com.kushal.eduhabit;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.kushal.eduhabit.databinding.FragmentGrammarPracticeBinding;

public class GrammarPracticeFragment extends Fragment {

    private FragmentGrammarPracticeBinding binding;
    private GrammarLessonViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGrammarPracticeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(GrammarLessonViewModel.class);

        viewModel.getSelectedLesson().observe(getViewLifecycleOwner(), lesson -> {
            if (lesson != null && !lesson.quizQuestions.isEmpty()) {
                setupQuiz(lesson.quizQuestions.get(0));
            }
        });
    }

    private void setupQuiz(Lesson.QuizQuestion q) {
        binding.questionText.setText(q.question);
        binding.option1.setText(q.options.get(0));
        binding.option2.setText(q.options.get(1));
        binding.option3.setText(q.options.get(2));
        binding.option4.setText(q.options.get(3));

        binding.optionsGroup.clearCheck();
        resetOptionStyles();

        binding.checkAnswerBtn.setOnClickListener(v -> {
            int checkedId = binding.optionsGroup.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(getContext(), "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedIndex = -1;
            if (checkedId == binding.option1.getId()) selectedIndex = 0;
            else if (checkedId == binding.option2.getId()) selectedIndex = 1;
            else if (checkedId == binding.option3.getId()) selectedIndex = 2;
            else if (checkedId == binding.option4.getId()) selectedIndex = 3;

            if (selectedIndex == q.correctIndex) {
                highlightOption(checkedId, true);
                Toast.makeText(getContext(), "Correct! ✨", Toast.LENGTH_SHORT).show();
            } else {
                highlightOption(checkedId, false);
                highlightOption(getOptionIdByIndex(q.correctIndex), true);
                Toast.makeText(getContext(), "Wrong Answer ❌", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetOptionStyles() {
        binding.option1.setBackgroundResource(R.drawable.option_bg_selector);
        binding.option2.setBackgroundResource(R.drawable.option_bg_selector);
        binding.option3.setBackgroundResource(R.drawable.option_bg_selector);
        binding.option4.setBackgroundResource(R.drawable.option_bg_selector);
        binding.option1.setTextColor(Color.BLACK);
        binding.option2.setTextColor(Color.BLACK);
        binding.option3.setTextColor(Color.BLACK);
        binding.option4.setTextColor(Color.BLACK);
    }

    private void highlightOption(int viewId, boolean isCorrect) {
        RadioButton rb = binding.getRoot().findViewById(viewId);
        if (rb != null) {
            rb.setBackgroundResource(isCorrect ? R.drawable.option_correct : R.drawable.option_wrong);
            rb.setTextColor(Color.WHITE);
        }
    }

    private int getOptionIdByIndex(int index) {
        switch (index) {
            case 0: return binding.option1.getId();
            case 1: return binding.option2.getId();
            case 2: return binding.option3.getId();
            case 3: return binding.option4.getId();
            default: return -1;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
