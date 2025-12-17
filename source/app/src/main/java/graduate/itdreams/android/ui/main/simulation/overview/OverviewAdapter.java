package graduate.itdreams.android.ui.main.simulation.overview;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import graduate.itdreams.android.data.model.api.response.ItemTitleContentResponse;
import graduate.itdreams.android.databinding.ItemTitleContentBinding;

public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> {
    private List<ItemTitleContentResponse> overviewItemList = new ArrayList<>();

    public OverviewAdapter(List<ItemTitleContentResponse> overviewItemList) {
        this.overviewItemList = overviewItemList;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemTitleContentBinding binding;

        public ViewHolder(ItemTitleContentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    @NonNull
    @Override
    public OverviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemTitleContentBinding binding = ItemTitleContentBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(OverviewAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(null);
        ItemTitleContentResponse item = overviewItemList.get(position);
        holder.binding.tvTitle.setText(item.getTitle());
        holder.binding.tvContent.getSettings().setJavaScriptEnabled(true);  // nếu cần JS
        String html =
                "<html>" +
                        "<head>" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "<style>" +
                        "html, body { " +
                        "   margin: 0; " +
                        "   padding: 0; " +
                        "} " +
                        "ul,ol{margin:0;padding-left:16px;}" +
                        "li{margin:0;padding:0;}" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        item.getContent() +
                        "</body>" +
                        "</html>";

        holder.binding.tvContent.loadDataWithBaseURL(
                null,
                html,
                "text/html",
                "utf-8",
                null
        );

    }

    @Override
    public int getItemCount() {
        return overviewItemList != null ? overviewItemList.size() : 0;
    }

}

