package com.kushal.eduhabit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.kushal.eduhabit.databinding.ItemBadgeBinding;
import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private final List<Achievement> achievements;
    private final Context context;

    public AchievementAdapter(Context context, List<Achievement> achievements) {
        this.context = context;
        this.achievements = achievements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBadgeBinding binding = ItemBadgeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (achievements == null || position >= achievements.size()) return;
        
        Achievement achievement = achievements.get(position);
        if (achievement == null) return;

        holder.binding.badgeTitle.setText(achievement.title);

        if (achievement.isUnlocked) {
            holder.binding.badgeIcon.setImageResource(achievement.iconResId);
            holder.binding.badgeIcon.setAlpha(1.0f);
            holder.binding.badgeIcon.setColorFilter(null);
            holder.binding.badgeTitle.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
        } else {
            // Check if ic_lock exists, fallback to ic_achievements if not
            holder.binding.badgeIcon.setImageResource(R.drawable.ic_lock);
            holder.binding.badgeIcon.setAlpha(0.4f);
            holder.binding.badgeTitle.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
        }
    }

    @Override
    public int getItemCount() {
        return achievements != null ? achievements.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemBadgeBinding binding;

        public ViewHolder(ItemBadgeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
