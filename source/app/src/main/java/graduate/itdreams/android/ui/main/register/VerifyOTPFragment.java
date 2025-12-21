package graduate.itdreams.android.ui.main.register;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import eu.davidea.flexibleadapter.databinding.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.request.student.ResetOtpRequest;
import graduate.itdreams.android.data.model.api.request.student.VerifyOtpRequest;
import graduate.itdreams.android.databinding.FragmentVerifyOtpBinding;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;

public class VerifyOTPFragment extends BaseFragment<FragmentVerifyOtpBinding, VerifyOTPViewModel> {
    String idHash = null;
    String email = null;


    @Override
    protected void performDataBinding() {
        binding.setF(this);
        binding.setVm(viewModel);
        idHash = getArguments().getString("idHash");
        email = getArguments().getString("email");
        View.OnClickListener focusListener = v -> {
            binding.otp.requestFocus();
            InputMethodManager imm =
                    (InputMethodManager) requireContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.otp, InputMethodManager.SHOW_IMPLICIT);
        };

        binding.tv1.setOnClickListener(focusListener);
        binding.tv2.setOnClickListener(focusListener);
        binding.tv3.setOnClickListener(focusListener);
        binding.tv4.setOnClickListener(focusListener);
        binding.tv5.setOnClickListener(focusListener);
        binding.tv6.setOnClickListener(focusListener);

        TextView[] boxes = {
                binding.tv1,
                binding.tv2,
                binding.tv3,
                binding.tv4,
                binding.tv5,
                binding.tv6
        };

        binding.otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Clear hết
                for (TextView tv : boxes) {
                    tv.setText("");
                }

                // Gán số vào từng ô
                for (int i = 0; i < s.length(); i++) {
                    boxes[i].setText(String.valueOf(s.charAt(i)));
                }

                // (Optional) Ẩn bàn phím khi đủ 6 số
                if (s.length() == 6) {
                    InputMethodManager imm =
                            (InputMethodManager) requireContext()
                                    .getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (imm != null) {
                        imm.hideSoftInputFromWindow(binding.otp.getWindowToken(), 0);
                    }
                }
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

    }
    public void onResetOtpClick(){
        ResetOtpRequest request = new ResetOtpRequest();
        request.setEmail(email);
        viewModel.resetOtp(request);
    }

    public void onConfirmClick(){
        String otp = binding.otp.getText().toString().trim();
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setOtp(otp);
        request.setIdHash(idHash);
        viewModel.conFirmOtp(request);
        viewModel.isSuccess.observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Xác thực thành công", Toast.LENGTH_SHORT).show();
                String idHash = viewModel.idHash.getValue();
                goToQuizJob();
            }
        });
    }

    private void goToQuizJob() {
        requireActivity().finish();
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_verify_otp;
    }

    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
}
