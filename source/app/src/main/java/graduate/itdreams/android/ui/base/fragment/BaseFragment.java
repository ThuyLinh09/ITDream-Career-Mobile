package graduate.itdreams.android.ui.base.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.R;
import graduate.itdreams.android.di.component.DaggerFragmentComponent;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.di.module.FragmentModule;
import graduate.itdreams.android.utils.DialogUtils;

import javax.inject.Inject;
import javax.inject.Named;


public abstract class BaseFragment <B extends ViewDataBinding,V extends BaseFragmentViewModel> extends Fragment {

    protected B binding;
    @Inject
    protected V viewModel;

    private Dialog progressDialog;


    @Named("access_token")
    @Inject
    protected String token;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        performDependencyInjection(getBuildComponent());
        binding = DataBindingUtil.inflate(inflater,getLayoutId(),container,false);
        binding.setVariable(getBindingVariable(),viewModel);
        performDataBinding();
        viewModel.setToken(token);
        viewModel.mErrorMessage.observe(getViewLifecycleOwner(),toastMessage -> {
            if (toastMessage!=null){
                toastMessage.showMessage(requireContext());
            }
        });
        viewModel.mIsLoading.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback(){

            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if(((ObservableBoolean)sender).get()){
                    showProgressbar(getResources().getString(R.string.msg_loading));
                }else{
                    hideProgress();
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public abstract int getBindingVariable();

    protected abstract int getLayoutId();

    protected abstract void performDataBinding();

    protected abstract void performDependencyInjection(FragmentComponent buildComponent);

    private FragmentComponent getBuildComponent(){
        return DaggerFragmentComponent.builder()
                .appComponent(((MVVMApplication) requireActivity().getApplication()).getAppComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }
    public void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
    public void showProgressbar(String msg){
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = DialogUtils.createDialogLoading(requireContext());
        progressDialog.show();

        int size = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());

        Window window = progressDialog.getWindow();
        if (window != null) {
            window.setLayout(size, size);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.05f;
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setAttributes(lp);
        }
    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

}
