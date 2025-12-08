package graduate.itdreams.android.ui.main.simulation.task;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.response.ItemTitleContentResponse;
import graduate.itdreams.android.data.model.api.response.task.TaskResponse;
import graduate.itdreams.android.databinding.FragmentTaskItemBinding;
import graduate.itdreams.android.ui.main.simulation.overview.OverviewAdapter;

public class TaskItemFragment extends Fragment {
    private TaskResponse task;
    private FragmentTaskItemBinding binding;
    public static TaskItemFragment newInstance(TaskResponse task) {
        TaskItemFragment fragment = new TaskItemFragment();
        Bundle args = new Bundle();
        args.putString("task_json", new Gson().toJson(task));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String json = getArguments().getString("task_json");
            task = new Gson().fromJson(json, TaskResponse.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskItemBinding.inflate(inflater, container, false);

        Gson gson = new Gson();
        //ItemTitleContentResponse itemName = gson.fromJson(task.getName(), ItemTitleContentResponse.class);
        if (task != null) {
            binding.tvTitleDescription.setText(task.getTitle());
            binding.tvDescription.setText(task.getDescription());

            String contentJson = task.getIntroduction();
            Type listType = new TypeToken<List<ItemTitleContentResponse>>(){}.getType();
            List<ItemTitleContentResponse> taskContentlist = gson.fromJson(contentJson, listType);

            TaskContentItemAdapter taskContentItemAdapter = new TaskContentItemAdapter(taskContentlist);
            binding.rcvContent.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.rcvContent.setAdapter(taskContentItemAdapter);
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
