package graduate.itdreams.android.ui.main.simulation.rate;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.davidea.flexibleadapter.databinding.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.response.task.SubTaskResponse;
import graduate.itdreams.android.databinding.FragmentRateBinding;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;
import graduate.itdreams.android.ui.main.home.SimulationAdapter;
import graduate.itdreams.android.ui.main.simulation.SimulationOverviewActivity;
import graduate.itdreams.android.ui.main.simulation.SimulationOverviewViewModel;


public class RateFragment extends BaseFragment<FragmentRateBinding, RateViewModel> {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        performDataBinding();
        observeSimulationId();
        loadRating();
        return binding.getRoot();
    }
    private void observeSimulationId(){
        SimulationOverviewViewModel activityViewModel = new ViewModelProvider(requireActivity()).get(SimulationOverviewViewModel.class);
        activityViewModel.getSimulationId().observe(getViewLifecycleOwner(), id -> {
            if (id != null) {
                viewModel.fetchRateList(id);
            }
        });
    }
    public void loadRating() {
        viewModel.getRateList().observe(getViewLifecycleOwner(), rateList -> {
            if (rateList == null || rateList.isEmpty()) return;

            RateAdapter adapter = new RateAdapter();
            adapter.setData(rateList);
            binding.rcvRate.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rcvRate.setAdapter(adapter);
        });
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rate;
    }

    @Override
    protected void performDataBinding() {
        binding.setF(this);
        binding.setVm(viewModel);
        RateAdapter adapter = new RateAdapter();
    }

    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
}