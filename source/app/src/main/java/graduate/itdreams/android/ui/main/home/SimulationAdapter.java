package graduate.itdreams.android.ui.main.home;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import graduate.itdreams.android.data.model.api.response.simulation.SimulationResponse;
import graduate.itdreams.android.databinding.ItemSimulationBinding;

public class SimulationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<SimulationResponse> simulationList = new ArrayList<>();
    private final OnPostClickListener listener;
    private final HomeViewModel viewModel;

    public interface OnPostClickListener {
        void onItemClick(Long postId);
    }

    public SimulationAdapter(HomeViewModel viewModel, OnPostClickListener listener) {
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
        for (int i = 0; i < simulationList.size(); i++) {
            if (simulationList.get(i).getId().equals(itemId)) return i;
        }
        return -1;
    }

    public void setData(List<SimulationResponse> newData) {
        simulationList.clear();
        if (newData != null) {
            simulationList.addAll(newData);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemSimulationBinding binding = ItemSimulationBinding.inflate(inflater, parent, false);
        return new SimulationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("SimulationAdapter", "Binding item at position: " + position);
        if (holder instanceof SimulationViewHolder) {
            ((SimulationViewHolder) holder).bind(simulationList.get(position), viewModel, listener);
        }
    }

    @Override
    public int getItemCount() {
        return simulationList.size();
    }

    static class SimulationViewHolder extends RecyclerView.ViewHolder {
        private final ItemSimulationBinding binding;

        public SimulationViewHolder(ItemSimulationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SimulationResponse item, HomeViewModel viewModel, OnPostClickListener listener) {
            binding.tvJobPosition.setText(item.getTitle());
            binding.tvCompanyName.setText(item.getEducator().getProfileAccountDto().getFullName());
            binding.ratingBar.setRating(item.getAvgRating());
            binding.tvEstimatedTime.setText(item.getTotalEstimatedTime());
            if(item.getPercent() == null){
                binding.layoutProgress.setVisibility(View.INVISIBLE);
            }else {
                binding.progressBar.setProgress(item.getPercent().intValue());
                binding.tvProgress.setText(String.format("%.1f%%", item.getPercent()));
            }

            Bitmap bitmap = viewModel.getBitmapFromCache(item.getId());
            if (bitmap != null) {
                binding.ivLogo.setImageBitmap(bitmap);
            } else {
                viewModel.loadImageForItem(item.getId(), item.getImagePath());
            }

            binding.getRoot().setOnClickListener(v -> {
                if (item != null) {
                    listener.onItemClick(item.getId());
                }
            });
        }
    }
}
