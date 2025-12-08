package graduate.itdreams.android.ui.main.register;

import android.content.Intent;

import eu.davidea.flexibleadapter.databinding.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.request.student.ResetOtpRequest;
import graduate.itdreams.android.data.model.api.request.student.VerifyOtpRequest;
import graduate.itdreams.android.databinding.FragmentQuizJobBinding;
import graduate.itdreams.android.databinding.FragmentVerifyOtpBinding;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;
import graduate.itdreams.android.ui.main.MainActivity;

public class QuizJobFragment extends BaseFragment<FragmentQuizJobBinding, QuizJobViewModel> {


    @Override
    protected void performDataBinding() {
        binding.setF(this);
        binding.setVm(viewModel);
    }
    public void onConfirmClick(){
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }


        @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_quiz_job;
    }

    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
}
