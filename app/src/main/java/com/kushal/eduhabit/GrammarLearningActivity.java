package com.kushal.eduhabit;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.kushal.eduhabit.databinding.ActivityGrammarLearningBinding;
import com.kushal.eduhabit.databinding.ItemStudyMcqBinding;
import com.kushal.eduhabit.grammar.model.GrammarTopic;
import com.kushal.eduhabit.grammar.model.QuizQuestion;
import java.util.List;

public class GrammarLearningActivity extends AppCompatActivity {

    private ActivityGrammarLearningBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGrammarLearningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        if (getIntent().hasExtra("chapter")) {
            loadChapterFullContent();
        } else if (getIntent().hasExtra("chapter_title")) {
            loadSimpleChapterNotes();
        } else {
            loadGrammarTopic();
        }
    }

    private void loadChapterFullContent() {
        Chapter chapter = (Chapter) getIntent().getSerializableExtra("chapter");
        if (chapter == null) return;

        binding.tvTopicTitle.setText(chapter.getTitle());
        
        // Premium Core Notes
        binding.tvExplanation.setText(chapter.getNotes());
        
        // Real-world Examples
        binding.llExamplesContainer.removeAllViews();
        if (chapter.getExamples() != null && !chapter.getExamples().isEmpty()) {
            TextView tvExample = new TextView(this);
            tvExample.setText(chapter.getExamples());
            tvExample.setTextColor(getResources().getColor(R.color.text_primary));
            tvExample.setLineSpacing(0, 1.3f);
            tvExample.setTextSize(15);
            binding.llExamplesContainer.addView(tvExample);
            binding.llExamplesContainer.setVisibility(View.VISIBLE);
        } else {
            binding.llExamplesContainer.setVisibility(View.GONE);
        }

        // --- INTERACTIVE MCQs (Answers are HIDDEN until selected) ---
        setupInteractiveMCQs(chapter.getMcqs());

        binding.btnStartPractice.setText("Expert Board Exam Focus");
        binding.btnStartPractice.setOnClickListener(v -> {
            showBoardQuestionsDialog(chapter);
        });

        // Hide legacy/unused UI elements
        binding.learningProgress.setVisibility(View.GONE);
        binding.btnBookmark.setVisibility(View.GONE);
        binding.tvMistakes.setVisibility(View.GONE); 
    }

    private void setupInteractiveMCQs(List<QuizQuestion> questions) {
        binding.llMCQContainer.removeAllViews();
        if (questions == null || questions.isEmpty()) {
            binding.tvMCQHeader.setVisibility(View.GONE);
            binding.llMCQContainer.setVisibility(View.GONE);
            return;
        }

        binding.tvMCQHeader.setVisibility(View.VISIBLE);
        binding.llMCQContainer.setVisibility(View.VISIBLE);

        for (QuizQuestion q : questions) {
            ItemStudyMcqBinding mcqBinding = ItemStudyMcqBinding.inflate(getLayoutInflater(), binding.llMCQContainer, false);
            mcqBinding.tvQuestion.setText(q.getQuestion());

            // Add Options dynamically - answers are NOT shown here
            mcqBinding.rgOptions.removeAllViews();
            for (int i = 0; i < q.getOptions().size(); i++) {
                RadioButton rb = (RadioButton) getLayoutInflater().inflate(R.layout.layout_quiz_option, mcqBinding.rgOptions, false);
                rb.setText(q.getOptions().get(i));
                rb.setId(i); // Index used as ID to compare with correctIndex
                mcqBinding.rgOptions.addView(rb);
            }

            mcqBinding.rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
                // Lock the question once answered
                for (int i = 0; i < group.getChildCount(); i++) {
                    group.getChildAt(i).setEnabled(false);
                }

                RadioButton selectedRb = group.findViewById(checkedId);
                boolean isCorrect = (checkedId == q.getCorrectIndex());

                if (isCorrect) {
                    selectedRb.setBackgroundResource(R.drawable.option_correct);
                    selectedRb.setTextColor(Color.WHITE);
                } else {
                    selectedRb.setBackgroundResource(R.drawable.option_wrong);
                    selectedRb.setTextColor(Color.WHITE);
                    
                    // Show user the right path by highlighting the correct answer
                    RadioButton correctRb = (RadioButton) group.getChildAt(q.getCorrectIndex());
                    correctRb.setBackgroundResource(R.drawable.option_correct);
                    correctRb.setTextColor(Color.WHITE);
                }

                // REVEAL the answer explanation ONLY after the user interacts
                mcqBinding.llExplanation.setVisibility(View.VISIBLE);
                mcqBinding.tvExplanationText.setText("Answer: Option " + (q.getCorrectIndex() + 1) + "\n\n" + q.getExplanation());
            });

            binding.llMCQContainer.addView(mcqBinding.getRoot());
        }
    }

    private void showBoardQuestionsDialog(Chapter chapter) {
        StringBuilder sb = new StringBuilder();
        sb.append("Frequently Asked Board Exam Questions:\n\n");
        if (chapter.getBoardQuestions() != null && !chapter.getBoardQuestions().isEmpty()) {
            for (String q : chapter.getBoardQuestions()) {
                sb.append("• ").append(q).append("\n\n");
            }
        } else {
            sb.append("No specific board questions documented for this unit yet.");
        }

        new android.app.AlertDialog.Builder(this)
                .setTitle("Board Exam Guidance")
                .setMessage(sb.toString())
                .setPositiveButton("Got it, thanks!", null)
                .show();
    }

    private void loadSimpleChapterNotes() {
        String title = getIntent().getStringExtra("chapter_title");
        String content = getIntent().getStringExtra("chapter_content");

        binding.tvTopicTitle.setText(title);
        binding.tvExplanation.setText(content);
        
        binding.learningProgress.setVisibility(View.GONE);
        binding.btnBookmark.setVisibility(View.GONE);
        binding.btnStartPractice.setVisibility(View.GONE);
        binding.llExamplesContainer.removeAllViews();
        binding.llMCQContainer.setVisibility(View.GONE);
        binding.tvMCQHeader.setVisibility(View.GONE);
    }

    private void loadGrammarTopic() {
        GrammarTopic topic = (GrammarTopic) getIntent().getSerializableExtra("topic");
        if (topic == null) {
            finish();
            return;
        }

        binding.tvTopicTitle.setText(topic.getName());
        binding.learningProgress.setProgress(topic.getProgress());
    }
}
