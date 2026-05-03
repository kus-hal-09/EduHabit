package com.kushal.eduhabit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kushal.eduhabit.databinding.FragmentProfileBinding;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private SessionManager session;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        session = new SessionManager(requireContext());

        setupImagePicker();
        
        binding.avatarContainer.setOnClickListener(v -> pickImage());
        binding.btnChangePic.setOnClickListener(v -> pickImage());
        
        // Settings/Account clicks
        binding.btnNotifications.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Notification settings coming soon!", Toast.LENGTH_SHORT).show();
        });

        binding.btnChangePassword.setOnClickListener(v -> {
            showChangePasswordDialog();
        });

        binding.btnPreferences.setOnClickListener(v -> {
            Toast.makeText(getContext(), "User preferences coming soon!", Toast.LENGTH_SHORT).show();
        });

        binding.btnLogout.setOnClickListener(v -> logout());

        loadProfile();
    }

    private void showChangePasswordDialog() {
        if (mAuth.getCurrentUser() == null || mAuth.getCurrentUser().getEmail() == null) return;
        
        new AlertDialog.Builder(getContext())
                .setTitle("Reset Password")
                .setMessage("A password reset link will be sent to " + mAuth.getCurrentUser().getEmail() + ". Do you want to proceed?")
                .setPositiveButton("Send Link", (dialog, which) -> {
                    mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail())
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Reset email sent!", Toast.LENGTH_LONG).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout() {
        new AlertDialog.Builder(getContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout", (dialog, which) -> {
                mAuth.signOut();
                session.clearSession();
                Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                if (getActivity() != null) getActivity().finish();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) uploadProfileImage(imageUri);
                    }
                }
        );
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadProfileImage(Uri uri) {
        if (mAuth.getCurrentUser() == null || getContext() == null) return;
        String userId = mAuth.getCurrentUser().getUid();
        StorageReference ref = storage.getReference().child("profile_pics/" + userId + ".jpg");

        binding.btnChangePic.setEnabled(false);
        Toast.makeText(getContext(), "Updating profile photo...", Toast.LENGTH_SHORT).show();

        ref.putFile(uri)
            .continueWithTask(task -> ref.getDownloadUrl())
            .addOnSuccessListener(downloadUri -> {
                db.collection("users").document(userId).update("profileImageUrl", downloadUri.toString())
                    .addOnSuccessListener(aVoid -> {
                        if (isAdded() && binding != null) {
                            binding.btnChangePic.setEnabled(true);
                            binding.profileImg.setVisibility(View.VISIBLE);
                            Glide.with(this).load(downloadUri).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(binding.profileImg);
                            Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                        }
                    });
            })
            .addOnFailureListener(e -> {
                if (isAdded() && binding != null) binding.btnChangePic.setEnabled(true);
                Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void loadProfile() {
        // Instant load from session
        String name = session.getName();
        binding.tvName.setText(name);
        binding.tvEmailTop.setText(session.getEmail());
        binding.tvEmail.setText(session.getEmail());
        binding.tvProgram.setText(session.getCourse());
        binding.tvSemester.setText(session.getSemester());
        binding.tvSemesterBadge.setText(session.getSemester().isEmpty() ? "--" : session.getSemester());

        // Set initials
        if (name != null && !name.isEmpty()) {
            String[] parts = name.split(" ");
            String initials = "";
            if (parts.length > 0) initials += parts[0].substring(0, 1).toUpperCase();
            if (parts.length > 1) initials += parts[1].substring(0, 1).toUpperCase();
            binding.tvAvatarInitials.setText(initials);
        }

        String userId = session.getUid();
        if (userId.isEmpty()) return;

        // Then refresh from Firestore
        db.collection("users").document(userId).get()
            .addOnSuccessListener(doc -> {
                if (doc != null && doc.exists() && binding != null) {
                    binding.tvName.setText(doc.getString("name"));
                    
                    String profileUrl = doc.getString("profileImageUrl");
                    if (profileUrl != null && !profileUrl.isEmpty()) {
                        binding.profileImg.setVisibility(View.VISIBLE);
                        Glide.with(this).load(profileUrl).placeholder(R.drawable.ic_profile).into(binding.profileImg);
                    } else {
                        binding.profileImg.setVisibility(View.GONE);
                    }
                    
                    fetchRealStats(userId);
                }
            });
    }

    private void fetchRealStats(String userId) {
        db.collection("submissions").whereEqualTo("studentId", userId).get()
            .addOnSuccessListener(snaps -> {
                if (binding == null) return;
                int gradedCount = 0;
                double totalPoints = 0;

                for (DocumentSnapshot doc : snaps.getDocuments()) {
                    if ("graded".equals(doc.getString("status"))) {
                        Object gradeObj = doc.get("grade");
                        if (gradeObj instanceof Number) {
                            totalPoints += ((Number) gradeObj).doubleValue();
                            gradedCount++;
                        } else if (gradeObj instanceof String) {
                            double val = gradeToNumeric((String) gradeObj);
                            if (val > 0) { totalPoints += val; gradedCount++; }
                        }
                    }
                }
                
                binding.tvStatGrade.setText(gradedCount > 0 ? numericToGrade(totalPoints / gradedCount) : "N/A");
                
                db.collection("assignments")
                    .whereEqualTo("course", session.getCourse())
                    .get().addOnSuccessListener(aSnaps -> {
                    if (binding != null) {
                        int pending = Math.max(0, aSnaps.size() - snaps.size());
                        binding.tvStatPending.setText(String.valueOf(pending));
                    }
                });
            });
    }

    private double gradeToNumeric(String g) {
        if (g == null) return 0.0;
        g = g.toUpperCase();
        if (g.contains("A")) return 4.0; if (g.contains("B")) return 3.0;
        if (g.contains("C")) return 2.0; return 0.0;
    }

    private String numericToGrade(double n) {
        if (n >= 3.5) return "A"; if (n >= 2.5) return "B";
        return "C";
    }

    @Override
    public void onDestroyView() { super.onDestroyView(); binding = null; }
}
