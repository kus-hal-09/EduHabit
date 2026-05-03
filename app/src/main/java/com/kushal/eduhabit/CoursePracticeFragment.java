package com.kushal.eduhabit;

import android.content.res.ColorStateList;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.FragmentCoursePracticeBinding;
import java.util.HashMap;
import java.util.Map;

public class CoursePracticeFragment extends Fragment {

    private FragmentCoursePracticeBinding binding;
    private String courseName;
    private int currentQuestionIndex = 0;
    private boolean isAnswered = false;

    private static final Map<String, String[]> QUESTIONS = new HashMap<>();
    private static final Map<String, String[][]> OPTIONS = new HashMap<>();
    private static final Map<String, int[]> ANSWERS = new HashMap<>();

    static {
        QUESTIONS.put("Advanced Grammar", new String[]{"'If I ___ you, I would go.'", "She suggests he ___ earlier."});
        OPTIONS.put("Advanced Grammar", new String[][]{{"was", "were", "am", "be"}, {"arrive", "arrives", "arrived", "arriving"}});
        ANSWERS.put("Advanced Grammar", new int[]{1, 0});

        QUESTIONS.put("Business English", new String[]{"Formal way to end an email:", "What is a 'win-win'?"});
        OPTIONS.put("Business English", new String[][]{{"Sincerely,", "Cheers,", "Later!", "Bye,"}, {"One wins", "Both benefit", "Both lose", "No winner"}});
        ANSWERS.put("Business English", new int[]{0, 1});
        
        QUESTIONS.put("IELTS Preparation", new String[]{"Maximum Band Score:", "Which section is first?"});
        OPTIONS.put("IELTS Preparation", new String[][]{{"7", "8", "9", "10"}, {"Listening", "Reading", "Writing", "Speaking"}});
        ANSWERS.put("IELTS Preparation", new int[]{2, 0});
    }

    public static CoursePracticeFragment newInstance(String courseName) {
        CoursePracticeFragment fragment = new CoursePracticeFragment();
        Bundle args = new Bundle();
        args.putString("courseName", courseName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCoursePracticeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            courseName = getArguments().getString("courseName");
            loadQuestion();
        }

        binding.btnCheck.setOnClickListener(v -> {
            if (isAnswered) {
                currentQuestionIndex++;
                loadQuestion();
            } else {
                checkAnswer();
            }
        });
    }

    private void loadQuestion() {
        String[] qns = QUESTIONS.getOrDefault(courseName, QUESTIONS.get("Advanced Grammar"));
        String[][] opts = OPTIONS.getOrDefault(courseName, OPTIONS.get("Advanced Grammar"));

        if (currentQuestionIndex < qns.length) {
            isAnswered = false;
            binding.tvQuestionNum.setText("Question " + (currentQuestionIndex + 1) + "/" + qns.length);
            binding.tvQuestionText.setText(qns[currentQuestionIndex]);
            binding.rbOption1.setText(opts[currentQuestionIndex][0]);
            binding.rbOption2.setText(opts[currentQuestionIndex][1]);
            binding.rbOption3.setText(opts[currentQuestionIndex][2]);
            binding.rbOption4.setText(opts[currentQuestionIndex][3]);
            binding.rgOptions.clearCheck();
            binding.btnCheck.setText("Check Answer");
            binding.btnCheck.setEnabled(true);
            resetStyles();
        } else {
            Toast.makeText(getContext(), "Course Practice Completed!", Toast.LENGTH_SHORT).show();
            currentQuestionIndex = 0;
            loadQuestion();
        }
    }

    private void checkAnswer() {
        int checkedId = binding.rgOptions.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(getContext(), "Please select an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        isAnswered = true;
        int[] answers = ANSWERS.getOrDefault(courseName, ANSWERS.get("Advanced Grammar"));
        RadioButton selected = getView().findViewById(checkedId);
        int selectedIndex = binding.rgOptions.indexOfChild(selected);

        if (selectedIndex == answers[currentQuestionIndex]) {
            selected.setBackgroundResource(R.drawable.option_correct);
            Toast.makeText(getContext(), "Correct! +10 XP", Toast.LENGTH_SHORT).show();
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid()).update("xp", FieldValue.increment(10));
        } else {
            selected.setBackgroundResource(R.drawable.option_wrong);
            Toast.makeText(getContext(), "Incorrect. Try next!", Toast.LENGTH_SHORT).show();
        }
        binding.btnCheck.setText("Next Question");
    }

    private void resetStyles() {
        binding.rbOption1.setBackgroundResource(R.drawable.option_bg_selector);
        binding.rbOption2.setBackgroundResource(R.drawable.option_bg_selector);
        binding.rbOption3.setBackgroundResource(R.drawable.option_bg_selector);
        binding.rbOption4.setBackgroundResource(R.drawable.option_bg_selector);
    }
}