package com.kushal.eduhabit.grammar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import com.kushal.eduhabit.databinding.FragmentGrammarDetailBinding;

/**
 * Isolated Lesson Detail Fragment.
 * Displays textual content and a Knowledge Check quiz.
 */
public class GrammarLessonDetailFragment extends Fragment {

    private FragmentGrammarDetailBinding binding;
    private String lessonTitle;
    private String lessonContent;

    public static GrammarLessonDetailFragment newInstance(String title, String content) {
        GrammarLessonDetailFragment fragment = new GrammarLessonDetailFragment();
        Bundle args = new Bundle();
        args.putString("arg_title", title);
        args.putString("arg_content", content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lessonTitle = getArguments().getString("arg_title");
            lessonContent = getArguments().getString("arg_content");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGrammarDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbarDetail.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        binding.tvDetailTitle.setText(lessonTitle);
        binding.tvDetailContent.setText(lessonContent);

        setupQuiz();
    }

    private void setupQuiz() {
        // Dynamic Question for demonstration
        binding.tvQuizQuestion.setText("Which of the following correctly uses the concept explained in '" + lessonTitle + "'?");
        binding.rbOption1.setText("Correct usage example");
        binding.rbOption2.setText("Common mistake example");
        binding.rbOption3.setText("Irrelevant usage");

        binding.btnSubmitQuiz.setOnClickListener(v -> {
            int selectedId = binding.rgQuizOptions.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Snackbar.make(v, "Please select an answer", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (selectedId == binding.rbOption1.getId()) {
                Snackbar.make(v, "Correct! Well done.", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(android.R.color.holo_green_dark))
                        .show();
            } else {
                Snackbar.make(v, "Incorrect. Re-read the content above.", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(android.R.color.holo_red_dark))
                        .show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
