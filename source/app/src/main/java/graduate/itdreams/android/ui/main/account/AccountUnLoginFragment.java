package graduate.itdreams.android.ui.main.account;

import eu.davidea.flexibleadapter.databinding.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.databinding.FragmentAccountUnLoginBinding;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;
import graduate.itdreams.android.ui.main.MainActivity;

public class AccountUnLoginFragment extends BaseFragment<FragmentAccountUnLoginBinding, AccountUnLoginViewModel> {
    @Override
    protected void performDataBinding() {
        binding.setF(this);
        binding.setVm(viewModel);
    }

    public void onLoginClick() {
        ((MainActivity) requireActivity()).navigateToLogin();
    }
    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_account_un_login;
    }

    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
}
