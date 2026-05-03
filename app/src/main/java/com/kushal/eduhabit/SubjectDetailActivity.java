package com.kushal.eduhabit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kushal.eduhabit.databinding.ActivitySubjectDetailBinding;
import java.util.List;

public class SubjectDetailActivity extends AppCompatActivity {

    private ActivitySubjectDetailBinding binding;
    private String subjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubjectDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        subjectName = getIntent().getStringExtra("subject_name");
        if (subjectName == null) subjectName = getString(R.string.default_subject_details);

        // Professional UI: Set titles for both collapsed and expanded states
        binding.tvToolbarTitle.setText(subjectName);
        binding.tvToolbarTitleLarge.setText(subjectName);
        
        // Use the Toolbar's navigation icon for the back action
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        loadSubjectData();
    }

    private void loadSubjectData() {
        Subject subject = BCAContentProvider.getSubjectByName(subjectName);
        
        if (subject != null) {
            // Populate Overview using the updated Professional IDs
            binding.tvOverviewCredits.setText(String.valueOf(subject.getCredits()));
            binding.tvOverviewHours.setText(String.valueOf(subject.getContactHours()));
            binding.tvOverviewPattern.setText(subject.getExamPattern());
            binding.tvOverviewType.setText(subject.getType().toUpperCase());
            binding.tvOverviewPrereq.setText("Prerequisites: " + subject.getPrerequisites());

            // Setup Chapters List
            List<Chapter> chapters = subject.getSyllabus();
            ChapterAdapter adapter = new ChapterAdapter(chapters);
            binding.rvChapters.setLayoutManager(new LinearLayoutManager(this));
            binding.rvChapters.setAdapter(adapter);
        }
    }
}
