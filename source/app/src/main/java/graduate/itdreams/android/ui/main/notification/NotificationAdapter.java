package graduate.itdreams.android.ui.main.notification;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import graduate.itdreams.android.data.model.api.response.notification.NotificationResponse;
import graduate.itdreams.android.data.model.api.response.simulation.SimulationResponse;
import graduate.itdreams.android.databinding.ItemNotificationBinding;
import graduate.itdreams.android.databinding.ItemSimulationBinding;
import graduate.itdreams.android.ui.main.home.HomeViewModel;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<NotificationResponse> notificationList = new ArrayList<>();
    private final OnNotificationClickListener listener;
    private final NotificationViewModel viewModel;

    public interface OnNotificationClickListener {
        void onItemClick(Long id);
    }

    public NotificationAdapter(NotificationViewModel viewModel, OnNotificationClickListener listener) {
        this.viewModel = viewModel;
        this.listener = listener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(inflater, parent, false);
        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("NotificationAdapter", "Binding item at position: " + position);
        if (holder instanceof NotificationViewHolder) {
            ((NotificationViewHolder) holder).bind(notificationList.get(position), viewModel, listener);
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
    public void setData(List<NotificationResponse> newData) {
        notificationList.clear();
        if (newData != null) {
            notificationList.addAll(newData);
        }
        notifyDataSetChanged();
    }
    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private final ItemNotificationBinding binding;

        public NotificationViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(NotificationResponse item, NotificationViewModel viewModel, OnNotificationClickListener listener) {
            binding.tvTitle.setText(item.getTitle());
            binding.tvContent.setText(item.getMessage());
            binding.tvTime.setText(item.getCreatedDate());
        }
    }
}
