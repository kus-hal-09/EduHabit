package com.kushal.eduhabit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kushal.eduhabit.databinding.FragmentCurriculumBinding;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurriculumFragment extends Fragment {

    private FragmentCurriculumBinding binding;
    private String courseName;
    private LessonAdapter adapter;

    private static final Map<String, List<LessonAdapter.Lesson>> LESSONS_MAP = new HashMap<>();
    static {
        List<LessonAdapter.Lesson> grammar = new ArrayList<>();
        grammar.add(new LessonAdapter.Lesson("1. Subjunctive Mood", "12m 45s", "completed", 100));
        grammar.add(new LessonAdapter.Lesson("2. Relative Clauses", "15m 20s", "in_progress", 45));
        grammar.add(new LessonAdapter.Lesson("3. Inversion", "10m 10s", "completed", 100));
        grammar.add(new LessonAdapter.Lesson("4. Cleft Sentences", "14m 30s", "in_progress", 20));
        LESSONS_MAP.put("Advanced Grammar", grammar);

        List<LessonAdapter.Lesson> business = new ArrayList<>();
        business.add(new LessonAdapter.Lesson("1. Meeting Etiquette", "18m 00s", "completed", 100));
        business.add(new LessonAdapter.Lesson("2. Executive Summaries", "22m 15s", "in_progress", 60));
        business.add(new LessonAdapter.Lesson("3. Negotiating Deals", "25m 40s", "in_progress", 10));
        LESSONS_MAP.put("Business English", business);

        List<LessonAdapter.Lesson> vocab = new ArrayList<>();
        vocab.add(new LessonAdapter.Lesson("1. Academic Word List", "15m 00s", "completed", 100));
        vocab.add(new LessonAdapter.Lesson("2. Greek & Latin Roots", "20m 30s", "in_progress", 30));
        vocab.add(new LessonAdapter.Lesson("3. Contextual Clues", "18m 45s", "in_progress", 5));
        LESSONS_MAP.put("Vocabulary Master", vocab);
    }

    public static CurriculumFragment newInstance(String courseName) {
        CurriculumFragment fragment = new CurriculumFragment();
        Bundle args = new Bundle();
        args.putString("courseName", courseName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCurriculumBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            courseName = getArguments().getString("courseName");
            setupRecyclerView();
        }
    }

    private void setupRecyclerView() {
        List<LessonAdapter.Lesson> lessons = LESSONS_MAP.get(courseName);
        if (lessons == null) lessons = new ArrayList<>();

        adapter = new LessonAdapter(lessons, (lesson, position) -> {
            if (getActivity() instanceof CoursePlayerActivity) {
                ((CoursePlayerActivity) getActivity()).playLessonVideo(lesson.title);
                adapter.setActivePosition(position);
                Toast.makeText(getContext(), "Playing: " + lesson.title, Toast.LENGTH_SHORT).show();
            }
        });

        binding.rvCurriculum.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCurriculum.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
