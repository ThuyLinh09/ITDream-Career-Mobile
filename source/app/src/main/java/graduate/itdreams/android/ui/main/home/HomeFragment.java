package graduate.itdreams.android.ui.main.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import graduate.itdreams.android.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.response.simulation.SimulationResponse;
import graduate.itdreams.android.databinding.FragmentHomeBinding;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;
import graduate.itdreams.android.ui.main.login.LoginActivity;
import graduate.itdreams.android.ui.main.simulation.SimulationOverviewActivity;

public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeViewModel> {
    @Override
    protected void performDataBinding() {
        binding.setF(this);
        binding.setVm(viewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        performDataBinding();

        customBtnSearch();

        loadJobs();
        viewModel.forceLogout.observe(this, isLogout -> {
            if (Boolean.TRUE.equals(isLogout)) {
                viewModel.logout();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.fetchSimulationList(); // gọi lại API
        });

        return binding.getRoot();
    }
    private void loadJobs() {
        viewModel.fetchSimulationList();
        viewModel.getPostList().observe(getViewLifecycleOwner(), postList -> {
            if (postList == null || postList.isEmpty()) return;

            SimulationAdapter adapter = new SimulationAdapter(viewModel, item -> {
                Intent intent = new Intent(getContext(), SimulationOverviewActivity.class);
                intent.putExtra("item_id", item);
                startActivity(intent);
            });
            adapter.setData(postList);
            binding.recycleview.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recycleview.setAdapter(adapter);
            binding.swipeRefresh.setRefreshing(false);
        });

    }

    private void customBtnSearch() {
        View searchPlate = binding.searchView.findViewById(androidx.appcompat.R.id.search_plate);
        if (searchPlate != null) {
            searchPlate.setBackground(null);
        }
        ImageView searchIcon = binding.searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        searchIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.bg_btn));

    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }
    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
}
