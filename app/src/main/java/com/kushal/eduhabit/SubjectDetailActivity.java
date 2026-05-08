package com.kushal.eduhabit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kushal.eduhabit.databinding.ActivitySubjectDetailBinding;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SubjectDetailActivity extends AppCompatActivity {

    private static final String TAG = "SubjectDetailActivity";
    private ActivitySubjectDetailBinding binding;
    private String subjectName;
    private Subject currentSubject;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<String> createPdfLauncher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("application/pdf"),
            uri -> {
                if (uri != null) {
                    generatePdfInBackground(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubjectDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Handle status bar overlap for Android 15+ edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.appBar, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        subjectName = getIntent().getStringExtra("subject_name");
        if (subjectName == null) subjectName = getString(R.string.default_subject_details);

        binding.tvToolbarTitle.setText(subjectName);
        binding.tvToolbarTitleLarge.setText(subjectName);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        loadSubjectData();

        binding.fabDownloadSyllabus.setOnClickListener(v -> {
            if (currentSubject != null) {
                String fileName = subjectName.replaceAll("\\s+", "_") + "_Syllabus.pdf";
                createPdfLauncher.launch(fileName);
            }
        });
    }

    private void loadSubjectData() {
        currentSubject = BCAContentProvider.getSubjectByName(subjectName);
        
        if (currentSubject != null) {
            binding.tvOverviewCredits.setText(String.valueOf(currentSubject.getCredits()));
            binding.tvOverviewHours.setText(String.valueOf(currentSubject.getContactHours()));
            binding.tvOverviewPattern.setText(currentSubject.getExamPattern());
            if (currentSubject.getType() != null) {
                binding.tvOverviewType.setText(currentSubject.getType().toUpperCase());
            }
            binding.tvOverviewPrereq.setText(getString(R.string.prerequisites_format, currentSubject.getPrerequisites()));

            java.util.List<Chapter> chapters = currentSubject.getSyllabus();
            ChapterAdapter adapter = new ChapterAdapter(chapters);
            binding.rvChapters.setLayoutManager(new LinearLayoutManager(this));
            binding.rvChapters.setAdapter(adapter);
        }
    }

    private void generatePdfInBackground(Uri uri) {
        binding.fabDownloadSyllabus.setEnabled(false);
        Toast.makeText(this, R.string.generating_pdf_toast, Toast.LENGTH_SHORT).show();
        
        executorService.execute(() -> {
            boolean success = exportToPdf(uri);
            runOnUiThread(() -> {
                binding.fabDownloadSyllabus.setEnabled(true);
                if (success) {
                    Toast.makeText(this, R.string.pdf_export_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.pdf_export_failed, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private boolean exportToPdf(Uri uri) {
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream == null) return false;

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Title
            document.add(new Paragraph(getString(R.string.syllabus_title_format, currentSubject.getName()))
                    .setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(getString(R.string.course_code_format, currentSubject.getCode()))
                    .setTextAlignment(TextAlignment.CENTER));
            
            document.add(new Paragraph("\n"));

            // Overview
            document.add(new Paragraph(getString(R.string.course_overview_header)).setBold().setFontSize(16));
            document.add(new Paragraph(getString(R.string.credits_format, currentSubject.getCredits())));
            document.add(new Paragraph(getString(R.string.hours_format, currentSubject.getContactHours())));
            document.add(new Paragraph(getString(R.string.type_format, currentSubject.getType())));
            document.add(new Paragraph(getString(R.string.prerequisites_format, currentSubject.getPrerequisites())));

            document.add(new Paragraph("\n"));

            // Syllabus
            document.add(new Paragraph(getString(R.string.subject_syllabus_header)).setBold().setFontSize(16));
            
            for (Chapter chapter : currentSubject.getSyllabus()) {
                document.add(new Paragraph(chapter.getTitle()).setBold().setFontSize(14).setMarginTop(10));
                document.add(new Paragraph(getString(R.string.weightage_hours_format, chapter.getWeightage(), chapter.getStudyHours())));
                document.add(new Paragraph(getString(R.string.topics_format, chapter.getTopicsCovered())));
                
                document.add(new Paragraph(getString(R.string.notes_label)).setItalic());
                document.add(new Paragraph(chapter.getNotes()));
                
                if (chapter.getBoardQuestions() != null && !chapter.getBoardQuestions().isEmpty()) {
                    document.add(new Paragraph(getString(R.string.board_questions_label)).setBold());
                    com.itextpdf.layout.element.List itextList = new com.itextpdf.layout.element.List();
                    for (String q : chapter.getBoardQuestions()) {
                        itextList.add(new ListItem(q));
                    }
                    document.add(itextList);
                }
            }

            document.close();
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error exporting PDF", e);
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
