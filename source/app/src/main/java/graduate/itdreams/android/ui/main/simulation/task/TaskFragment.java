package graduate.itdreams.android.ui.main.simulation.task;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import eu.davidea.flexibleadapter.databinding.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.response.ItemTitleContentResponse;
import graduate.itdreams.android.data.model.api.response.task.TaskResponse;
import graduate.itdreams.android.databinding.FragmentTaskBinding;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;
import graduate.itdreams.android.ui.main.simulation.SimulationOverviewActivity;
import graduate.itdreams.android.ui.main.simulation.SimulationOverviewViewModel;
import graduate.itdreams.android.ui.main.taskdetail.TaskDetailActivity;


public class TaskFragment extends BaseFragment<FragmentTaskBinding, TaskViewModel> {

    private LinearLayout tabContainer;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabContainer = binding.tabContainer;
        setupTabObserver();
        observeSimulationId();
    }

    private void setupTabObserver(){
        viewModel.getTaskTabs().observe(getViewLifecycleOwner(), taskTabs -> {
            tabContainer.removeAllViews();
            boolean isTabEnd = false;
            for (int i = 0; i < taskTabs.size(); i++) {
                TaskResponse tab = taskTabs.get(i);
                if (i == taskTabs.size() - 1){
                    isTabEnd = true;
                }
                TextView tabView = createTabView(tab, i, isTabEnd);

                tabContainer.addView(tabView);
            }
            if (!taskTabs.isEmpty()) {
                selectTab(0);
            }
        });
    }
    private TextView createTabView(TaskResponse tab, int index, boolean isTabEnd){
        TextView tabView = new TextView(requireContext());

//        Gson gson = new Gson();
//        ItemTitleContentResponse itemName = gson.fromJson(tab.getName(), ItemTitleContentResponse.class);
        tabView.setText(tab.getName());
        tabView.setTypeface(null, Typeface.BOLD);

        if(isTabEnd){
            tabView.setBackgroundResource(R.drawable.tab_background_without_underline);
        }else {
            tabView.setBackgroundResource(R.drawable.tab_background_with_underline);
        }
        tabView.setTextColor(Color.BLACK);
        tabView.setPadding(24, 34, 24, 34);
        tabView.setClickable(true);
        tabView.setFocusable(true);
        tabView.setOnClickListener(v -> selectTab(index));

        return tabView;
    }
    private void observeSimulationId(){
        SimulationOverviewViewModel activityViewModel = new ViewModelProvider(requireActivity()).get(SimulationOverviewViewModel.class);
        activityViewModel.getSimulationId().observe(getViewLifecycleOwner(), id -> {
            if (id != null) {
                viewModel.fetchListTask(id);
            }
        });
    }
    private void selectTab(int index) {
        int count = tabContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            TextView tab = (TextView) tabContainer.getChildAt(i);
            tab.setSelected(i == index);
        }

        TaskResponse selectedTask = viewModel.getTaskTabs().getValue().get(index);

        TaskItemFragment fragment = TaskItemFragment.newInstance(selectedTask);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.tab_content_frame, fragment)
                .commit();
    }

    @Override
    protected void performDataBinding() {
        binding.setF(this);
        binding.setVm(viewModel);
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_task;
    }


    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
}