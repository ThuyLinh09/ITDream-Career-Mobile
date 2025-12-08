package graduate.itdreams.android.ui.main.simulation.rate;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import graduate.itdreams.android.data.model.api.response.simulation.RateResponse;
import graduate.itdreams.android.databinding.ItemRateBinding;

public class RateAdapter extends RecyclerView.Adapter<RateAdapter.RateViewHolder> {
    private final List<RateResponse> rateList = new ArrayList<>();

    @NonNull
    @Override
    public RateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRateBinding binding = ItemRateBinding.inflate(inflater, parent, false);
        return new RateViewHolder(binding);
    }
    public void setData(List<RateResponse> newData) {
        rateList.clear();
        if (newData != null) {
            rateList.addAll(newData);
        }
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull RateViewHolder holder, int position) {
        if (holder instanceof RateViewHolder) {
            ((RateViewHolder) holder).bind(rateList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return rateList.size();
    }

    static class RateViewHolder extends RecyclerView.ViewHolder {
        private final ItemRateBinding binding;
        public RateViewHolder(ItemRateBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(RateResponse item) {
            binding.tvUsername.setText(item.getStudent().getProfileAccountDto().getUsername());
            binding.rating.setRating((float) item.getStar());
            binding.tvComment.setText(item.getComment());
        }
    }
}

