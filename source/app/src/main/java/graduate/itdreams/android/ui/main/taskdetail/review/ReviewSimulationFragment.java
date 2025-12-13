package graduate.itdreams.android.ui.main.taskdetail.review;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import eu.davidea.flexibleadapter.databinding.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.request.review.ReviewSimulationRequest;
import graduate.itdreams.android.databinding.FragmentReviewSimulationBinding;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;

public class ReviewSimulationFragment extends BaseFragment<FragmentReviewSimulationBinding, ReviewSimulationViewModel> {
    private Long simulationId;
    public void onReviewClick() {
        if(binding.ratingBar.getRating() == 0f){
            Toast.makeText(
                    binding.getRoot().getContext(),
                    "Bạn cần chọn số sao",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        if(binding.edtComment.getText().toString().trim().isEmpty()){
            Toast.makeText(
                    binding.getRoot().getContext(),
                    "Bạn cần nhập nội dung",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        ReviewSimulationRequest request = new ReviewSimulationRequest();
        request.setSimulationId(simulationId);
        request.setStar((int) binding.ratingBar.getRating());
        request.setComment(binding.edtComment.getText().toString());
        viewModel.submitReview(request);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            simulationId = getArguments().getLong("simulation_id", -1);
        }
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_review_simulation;
    }

    @Override
    protected void performDataBinding() {
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setF(this);
        binding.setVm(viewModel);

    }

    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
}