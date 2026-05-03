package com.kushal.eduhabit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.FragmentCoursesBinding;
import java.util.ArrayList;
import java.util.List;

public class CoursesFragment extends Fragment {

    private FragmentCoursesBinding binding;
    private SessionManager session;
    private FirebaseFirestore db;
    private SubjectAdapter adapter;
    private List<Subject> subjectList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCoursesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        db = FirebaseFirestore.getInstance();
        session = new SessionManager(requireContext());
        subjectList = new ArrayList<>();
        
        setupUI();
        loadStudentCourses();
    }

    private void setupUI() {
        adapter = new SubjectAdapter(getContext(), subjectList);
        binding.rvSubjects.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSubjects.setAdapter(adapter);

        // Hide semester tabs as per requirements (students only see their own semester)
        binding.semesterTabLayout.setVisibility(View.GONE);

        binding.searchSubjects.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void loadStudentCourses() {
        String studentSemesterStr = session.getSemester();
        int semester = parseSemester(studentSemesterStr);
        
        // Load only the subjects for the student's current semester
        List<Subject> semesterSubjects = BCAContentProvider.getSubjectsForSemester(semester);
        
        subjectList.clear();
        subjectList.addAll(semesterSubjects);
        
        // Update header
        binding.tvHeaderTitle.setText("BCA " + studentSemesterStr + " Semester");
        binding.tvHeaderSubtitle.setText("Your Prescribed University Syllabus");
        
        if (subjectList.isEmpty()) {
            binding.llEmptyState.setVisibility(View.VISIBLE);
            binding.rvSubjects.setVisibility(View.GONE);
        } else {
            binding.llEmptyState.setVisibility(View.GONE);
            binding.rvSubjects.setVisibility(View.VISIBLE);
        }

        adapter.updateList(subjectList);
    }

    private int parseSemester(String sem) {
        if (sem == null) return 1;
        sem = sem.toLowerCase();
        if (sem.contains("1") || sem.contains("first")) return 1;
        if (sem.contains("2") || sem.contains("second")) return 2;
        if (sem.contains("3") || sem.contains("third")) return 3;
        if (sem.contains("4") || sem.contains("fourth")) return 4;
        if (sem.contains("5") || sem.contains("fifth")) return 5;
        if (sem.contains("6") || sem.contains("sixth")) return 6;
        if (sem.contains("7") || sem.contains("seventh")) return 7;
        if (sem.contains("8") || sem.contains("eighth")) return 8;
        return 1;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
