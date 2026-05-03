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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.kushal.eduhabit.databinding.FragmentGrammarPracticeBinding;

public class PracticeFragment extends Fragment {

    private FragmentGrammarPracticeBinding binding;
    private GrammarViewModel viewModel;
    private String moduleTitle;
    private GrammarModule currentModule;

    public static PracticeFragment newInstance(String moduleTitle) {
        PracticeFragment fragment = new PracticeFragment();
        Bundle args = new Bundle();
        args.putString("module_title", moduleTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGrammarPracticeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(GrammarViewModel.class);
        moduleTitle = getArguments() != null ? getArguments().getString("module_title") : "";

        viewModel.getModules().observe(getViewLifecycleOwner(), modules -> {
            for (GrammarModule m : modules) {
                if (m.title.equals(moduleTitle)) {
                    currentModule = m;
                    setupQuiz();
                    break;
                }
            }
        });
    }

    private void setupQuiz() {
        if (currentModule == null || currentModule.questions.isEmpty()) return;

        GrammarModule.QuizQuestion q = currentModule.questions.get(0);
        binding.questionText.setText(q.question);
        binding.option1.setText(q.options.get(0));
        binding.option2.setText(q.options.get(1));
        binding.option3.setText(q.options.get(2));
        binding.option4.setText(q.options.get(3));

        binding.checkAnswerBtn.setOnClickListener(v -> {
            int checkedId = binding.optionsGroup.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(getContext(), "Please select an option", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Incorrect. Try again!", Toast.LENGTH_SHORT).show();
            }
        });
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
