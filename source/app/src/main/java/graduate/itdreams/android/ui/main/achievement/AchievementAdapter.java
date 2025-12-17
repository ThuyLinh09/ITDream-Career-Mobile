package graduate.itdreams.android.ui.main.achievement;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import graduate.itdreams.android.data.model.api.response.simulation.AchievementResponse;
import graduate.itdreams.android.databinding.ItemAchievementBinding;

public class AchievementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<AchievementResponse> achievementList = new ArrayList<>();
    private final OnPostClickListener listener;
    private final AchievementViewModel viewModel;

    public interface OnPostClickListener {
        void onItemClick(AchievementResponse achievement);
    }

    public AchievementAdapter(AchievementViewModel viewModel, OnPostClickListener listener) {
        this.viewModel = viewModel;
        this.listener = listener;

        // Reload item khi ảnh được load xong
        viewModel.imageLiveData.observeForever(pair -> {
            Long itemId = pair.first;
            Bitmap bitmap = pair.second;
            int position = findPositionById(itemId);
            if (position != -1) {
                notifyItemChanged(position);
            }
        });
    }

    private int findPositionById(Long itemId) {
        for (int i = 0; i < achievementList.size(); i++) {
            if (achievementList.get(i).getId().equals(itemId)) return i;
        }
        return -1;
    }

    public void setData(List<AchievementResponse> newData) {
        achievementList.clear();
        if (newData != null) {
            achievementList.addAll(newData);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemAchievementBinding binding = ItemAchievementBinding.inflate(inflater, parent, false);
        return new AchievementViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("SimulationAdapter", "Binding item at position: " + position);
        if (holder instanceof AchievementViewHolder) {
            ((AchievementViewHolder) holder).bind(achievementList.get(position), viewModel, listener);
        }
    }

    @Override
    public int getItemCount() {
        return achievementList.size();
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {
        private final ItemAchievementBinding binding;

        public AchievementViewHolder(ItemAchievementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AchievementResponse item, AchievementViewModel viewModel, OnPostClickListener listener) {
            if(item.getSimulation() != null){
                binding.tvJobPosition.setText(item.getSimulation().getTitle());
                binding.tvCompanyName.setText(item.getSimulation().getEducator().getProfileAccountDto().getFullName());
                binding.ratingBar.setRating(item.getSimulation().getAvgRating());
                binding.tvEstimatedTime.setText(item.getSimulation().getTotalEstimatedTime());

                Bitmap bitmap = viewModel.getBitmapFromCache(item.getId());
                if (bitmap != null) {
                    binding.ivLogo.setImageBitmap(bitmap);
                } else {
                    viewModel.loadImageForItem(item.getId(), item.getSimulation().getImagePath());
                }
            }

            binding.viewCertificate.setOnClickListener(v -> {
                if (item != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
