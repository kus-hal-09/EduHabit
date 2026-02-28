package com.kushal.eduhabit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kushal.eduhabit.databinding.LayoutNotificationBottomSheetBinding;
import java.util.ArrayList;
import java.util.List;

public class NotificationBottomSheet extends BottomSheetDialogFragment {

    private LayoutNotificationBottomSheetBinding binding;
    private FirebaseFirestore db;
    private NotificationAdapter adapter;
    private List<DocumentSnapshot> notifications = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutNotificationBottomSheetBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();
        
        setupRecyclerView();
        fetchNotifications();
        
        binding.btnMarkAllRead.setOnClickListener(v -> markAllAsRead());
        
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(notifications, doc -> {
            db.collection("notifications").document(doc.getId()).update("read", true);
        });
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvNotifications.setAdapter(adapter);
    }

    private void fetchNotifications() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        db.collection("notifications")
                .whereEqualTo("teacherId", uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (snap != null) {
                        notifications.clear();
                        notifications.addAll(snap.getDocuments());
                        adapter.notifyDataSetChanged();
                        binding.tvEmptyNotifications.setVisibility(notifications.isEmpty() ? View.VISIBLE : View.GONE);
                    }
                });
    }

    private void markAllAsRead() {
        for (DocumentSnapshot doc : notifications) {
            if (Boolean.FALSE.equals(doc.getBoolean("read"))) {
                db.collection("notifications").document(doc.getId()).update("read", true);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onContextItemSelected(null);
        binding = null;
    }
}