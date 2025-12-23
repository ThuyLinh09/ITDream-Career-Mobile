package graduate.itdreams.android.ui.main.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import eu.davidea.flexibleadapter.databinding.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.databinding.FragmentNotificationBinding;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;
import graduate.itdreams.android.ui.main.home.SimulationAdapter;
import graduate.itdreams.android.ui.main.simulation.SimulationOverviewActivity;

public class NotificationFragment extends BaseFragment<FragmentNotificationBinding, NotificationViewModel> {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        performDataBinding();
        loadNotifications();
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.fetchNotificationList();
        });
        return binding.getRoot();
    }

    private void loadNotifications() {
        viewModel.fetchNotificationList();
        viewModel.getNotificationList().observe(getViewLifecycleOwner(), notificationList -> {
            if (notificationList == null || notificationList.isEmpty()) {
                binding.recycleview.setVisibility(View.GONE);
                binding.nodata.setVisibility(View.VISIBLE);
                binding.swipeRefresh.setRefreshing(false);
                return;
            }
            binding.recycleview.setVisibility(View.VISIBLE);
            binding.nodata.setVisibility(View.GONE);
            NotificationAdapter adapter = new NotificationAdapter(viewModel, item -> {
                Intent intent = new Intent(getContext(), SimulationOverviewActivity.class);
                intent.putExtra("item_id", item);
                startActivity(intent);
            });
            adapter.setData(notificationList);
            binding.recycleview.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recycleview.setAdapter(adapter);
            binding.swipeRefresh.setRefreshing(false);
        });

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
        return R.layout.fragment_notification;
    }

    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
}
