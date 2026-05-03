package com.kushal.eduhabit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.kushal.eduhabit.databinding.FragmentGrammarTheoryBinding;

public class TheoryFragment extends Fragment {

    private FragmentGrammarTheoryBinding binding;
    private GrammarViewModel viewModel;

    public static TheoryFragment newInstance(String moduleTitle) {
        TheoryFragment fragment = new TheoryFragment();
        Bundle args = new Bundle();
        args.putString("module_title", moduleTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGrammarTheoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(GrammarViewModel.class);

        String title = getArguments() != null ? getArguments().getString("module_title") : "";
        
        viewModel.getModules().observe(getViewLifecycleOwner(), modules -> {
            if (modules != null) {
                for (GrammarModule module : modules) {
                    if (module.title.equals(title)) {
                        displayTheory(module);
                        break;
                    }
                }
            }
        });
    }

    private void displayTheory(GrammarModule module) {
        if (binding == null) return;
        binding.tvTheoryTitle.setText(module.title + " Concepts");
        binding.tvTheoryContent.setText(module.theory);
        
        binding.keyPointsContainer.removeAllViews();
        if (module.takeaways != null) {
            for (String point : module.takeaways) {
                TextView tv = new TextView(getContext());
                tv.setText("• " + point);
                tv.setPadding(0, 12, 0, 12);
                tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
                tv.setTextSize(14f);
                binding.keyPointsContainer.addView(tv);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
