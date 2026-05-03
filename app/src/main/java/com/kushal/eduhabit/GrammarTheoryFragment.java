package com.kushal.eduhabit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.kushal.eduhabit.databinding.FragmentGrammarTheoryBinding;

public class GrammarTheoryFragment extends Fragment {

    private FragmentGrammarTheoryBinding binding;
    private GrammarLessonViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGrammarTheoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(GrammarLessonViewModel.class);

        viewModel.getSelectedLesson().observe(getViewLifecycleOwner(), lesson -> {
            if (lesson != null) {
                binding.tvTheoryTitle.setText(lesson.title + " - Core Concepts");
                binding.tvTheoryContent.setText(lesson.theoryContent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
