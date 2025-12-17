package graduate.itdreams.android.ui.main.simulation.task;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import graduate.itdreams.android.data.model.api.response.ItemTitleContentResponse;
import graduate.itdreams.android.databinding.ItemTitleContentBinding;

public class TaskContentItemAdapter extends RecyclerView.Adapter<TaskContentItemAdapter.ViewHolder> {
    private List<ItemTitleContentResponse> taskContentItemList = new ArrayList<>();

    public TaskContentItemAdapter(List<ItemTitleContentResponse> taskContentItemList) {
        this.taskContentItemList = taskContentItemList;
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
    public TaskContentItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemTitleContentBinding binding = ItemTitleContentBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(TaskContentItemAdapter.ViewHolder holder, int position) {
        ItemTitleContentResponse item = taskContentItemList.get(position);
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
        return taskContentItemList != null ? taskContentItemList.size() : 0;
    }

}

