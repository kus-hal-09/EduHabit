package com.kushal.eduhabit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kushal.eduhabit.databinding.FragmentGrammarCurriculumBinding;

public class GrammarCurriculumFragment extends Fragment {

    private FragmentGrammarCurriculumBinding binding;
    private GrammarLessonViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGrammarCurriculumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(GrammarLessonViewModel.class);

        binding.rvCurriculum.setLayoutManager(new LinearLayoutManager(getContext()));
        
        viewModel.getLessons().observe(getViewLifecycleOwner(), lessons -> {
            LessonCurriculumAdapter adapter = new LessonCurriculumAdapter(lessons, lesson -> {
                viewModel.selectLesson(lesson);
            });
            binding.rvCurriculum.setAdapter(adapter);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
