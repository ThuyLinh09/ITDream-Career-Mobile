package graduate.itdreams.android.ui.main.account;

import android.content.Intent;

import eu.davidea.flexibleadapter.databinding.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.databinding.FragmentAccountBinding;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;
import graduate.itdreams.android.ui.main.login.LoginActivity;

public class AccountFragment extends BaseFragment<FragmentAccountBinding, AccountViewModel> {
    @Override
    protected void performDataBinding() {
        binding.setF(this);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        viewModel.avatarLiveData.observe(getViewLifecycleOwner(), bitmap -> {
            if (bitmap != null) {
                binding.ivAvatar.setImageBitmap(bitmap);
            }
        });

    }
    public void onLogoutClick(){
        viewModel.logout();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }
    public void onEditPrrofile(){
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }
    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_account;
    }

    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
}
