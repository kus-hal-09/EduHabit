package com.kushal.eduhabit.grammar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kushal.eduhabit.R;
import com.kushal.eduhabit.databinding.FragmentGrammarDashboardBinding;
import java.util.ArrayList;
import java.util.List;

/**
 * Clean, isolated dashboard for Grammar Lessons.
 */
public class GrammarDashboardFragment extends Fragment {

    private FragmentGrammarDashboardBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGrammarDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        List<GrammarLesson> lessons = new ArrayList<>();
        lessons.add(new GrammarLesson("1", "Subjunctive Mood", "Hypothetical scenarios and suggestions.", "Content for Subjunctive Mood...", 45, false));
        lessons.add(new GrammarLesson("2", "Relative Clauses", "Mastering who, which, and that.", "Content for Relative Clauses...", 10, false));
        lessons.add(new GrammarLesson("3", "Inversion", "Emphasizing sentences through word order.", "Content for Inversion...", 0, false));

        GrammarLessonAdapter adapter = new GrammarLessonAdapter(lessons, lesson -> {
            // Navigate to Detail Fragment - Using R.id.grammar_fragment_container from activity_grammar.xml
            GrammarLessonDetailFragment detailFragment = GrammarLessonDetailFragment.newInstance(lesson.getTitle(), lesson.getContent());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.grammar_fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.rvLessons.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvLessons.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
