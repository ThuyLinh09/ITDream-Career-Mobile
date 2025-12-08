package graduate.itdreams.android.ui.main.taskdetail;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.response.task.SubTaskResponse;
import graduate.itdreams.android.data.model.api.response.task.TaskResponse;

public class TaskDrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_PARENT = 0;
    private static final int TYPE_SUB = 1;
    private static final int TYPE_RATING = 2;
    private static final Object RATING_ITEM = new Object();
    private final List<Object> displayList = new ArrayList<>();
    private final OnSubTaskClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private final List<TaskResponse> originalList = new ArrayList<>();
    private final Set<Long> expandedTaskIds = new HashSet<>();

    public interface OnSubTaskClickListener {
        void onSubTaskClick(TaskResponse task, SubTaskResponse subTask);
    }
    private final OnRatingClickListener reviewClickListener;
    public interface OnRatingClickListener {
        void onReviewClick();
    }
    public TaskDrawerAdapter(OnSubTaskClickListener listener, OnRatingClickListener ratingClickListener) {
        this.listener = listener;
        this.reviewClickListener = ratingClickListener;
    }



    public void setTaskItems(List<TaskResponse> taskItems) {
        originalList.clear();
        originalList.addAll(taskItems);
        rebuildDisplayList();
    }

    private void rebuildDisplayList() {
        displayList.clear();
        for (TaskResponse task : originalList) {
            displayList.add(task); // Parent
            if (expandedTaskIds.contains(task.getId())) {
                displayList.addAll(task.getSubTasks()); // Sub items
            }
        }
        displayList.add(RATING_ITEM);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = displayList.get(position);

        if (item instanceof TaskResponse) return TYPE_PARENT;
        if (item instanceof SubTaskResponse) return TYPE_SUB;
        if (item == RATING_ITEM) return TYPE_RATING;

        return TYPE_SUB;
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_PARENT)
            return new ParentViewHolder(inflater.inflate(R.layout.item_task_parent, parent, false));

        if (viewType == TYPE_SUB)
            return new SubTaskViewHolder(inflater.inflate(R.layout.item_task_parent, parent, false));

        // 👉 Rating item
        return new RatingViewHolder(inflater.inflate(R.layout.item_rating, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = displayList.get(position);

        if (holder instanceof SubTaskViewHolder) {
            holder.itemView.setBackgroundColor(
                    position == selectedPosition ? Color.LTGRAY : Color.TRANSPARENT
            );
            ((SubTaskViewHolder) holder).bind((SubTaskResponse) item);
        }else if(holder instanceof ParentViewHolder) {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            ((ParentViewHolder) holder).bind((TaskResponse) item);
        } else {
            holder.itemView.setBackgroundColor(
                    position == selectedPosition ? Color.LTGRAY : Color.TRANSPARENT
            );
            ((RatingViewHolder) holder).bind();
        }
    }

    class RatingViewHolder extends RecyclerView.ViewHolder {

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bind() {

            itemView.setOnClickListener(v -> {
                int oldPos = selectedPosition;
                selectedPosition = getAdapterPosition();

                if (oldPos != RecyclerView.NO_POSITION) {
                    notifyItemChanged(oldPos);
                }
                notifyItemChanged(selectedPosition);
                reviewClickListener.onReviewClick();
            });
        }
    }

    class ParentViewHolder extends RecyclerView.ViewHolder {
        TextView text1;
        ImageView expandIcon;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.text1);
            expandIcon = itemView.findViewById(R.id.expandIcon);
        }

        void bind(TaskResponse task) {
            text1.setText(task.getTitle());

            boolean isExpanded = expandedTaskIds.contains(task.getId());
            expandIcon.setImageResource(isExpanded ? R.drawable.ic_task_open : R.drawable.ic_task_close);

            itemView.setOnClickListener(v -> {
                if (isExpanded) {
                    expandedTaskIds.remove(task.getId());
                } else {
                    expandedTaskIds.add(task.getId());
                }
                rebuildDisplayList();
            });
        }
    }

    class SubTaskViewHolder extends RecyclerView.ViewHolder {
        TextView text1;
        ImageView icon;

        public SubTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(R.id.text1);
            icon = itemView.findViewById(R.id.expandIcon);
        }

        void bind(SubTaskResponse subTask) {
            text1.setText(subTask.getTitle());
            icon.setImageResource(R.drawable.ic_fries_menu);

            itemView.setOnClickListener(v -> {
                int oldPos = selectedPosition;
                selectedPosition = getAdapterPosition();

                if (oldPos != RecyclerView.NO_POSITION) {
                    notifyItemChanged(oldPos);
                }
                notifyItemChanged(selectedPosition);

                // Tìm cha
                int pos = getAdapterPosition();
                for (int i = pos - 1; i >= 0; i--) {
                    Object item = displayList.get(i);
                    if (item instanceof TaskResponse) {
                        listener.onSubTaskClick((TaskResponse) item, subTask);
                        break;
                    }
                }
            });
        }

    }
    public void selectDefaultSubTask(TaskResponse task, SubTaskResponse subTask) {
        // Mở task nếu chưa mở
        if (!expandedTaskIds.contains(task.getId())) {
            expandedTaskIds.add(task.getId());
            rebuildDisplayList();
        }

        // Tìm position của subTask trong displayList
        for (int i = 0; i < displayList.size(); i++) {
            Object item = displayList.get(i);
            if (item instanceof SubTaskResponse) {
                SubTaskResponse s = (SubTaskResponse) item;
                if (s.getId() == subTask.getId()) {
                    selectedPosition = i;
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

}
