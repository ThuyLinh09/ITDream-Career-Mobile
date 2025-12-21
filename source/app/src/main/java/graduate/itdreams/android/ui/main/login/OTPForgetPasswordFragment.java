package graduate.itdreams.android.ui.main.login;

import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import graduate.itdreams.android.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.request.login.ChangePasswordRequest;
import graduate.itdreams.android.data.model.api.request.student.ResetOtpRequest;
import graduate.itdreams.android.databinding.FragmentOtpForgetPasswordBinding;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;

public class OTPForgetPasswordFragment extends BaseFragment<FragmentOtpForgetPasswordBinding, OTPForgetPasswordViewModel> {
    String idHash = null;
    String email = null;


    @Override
    protected void performDataBinding() {
        binding.setF(this);
        binding.setVm(viewModel);
        idHash = getArguments().getString("idHash");
        email = getArguments().getString("email");
        setUpValidation();
        setNoSpaceFilters();
        binding.login.setOnClickListener(v->requireActivity().finish());
    }
    private void setNoSpaceFilters() {
        InputFilter noSpaceFilter = (source, start, end, dest, dstart, dend) -> {
            if (source != null && source.toString().contains(" ")) {
                return source.toString().replace(" ", "");
            }
            return null;
        };

        binding.password.setFilters(new InputFilter[]{noSpaceFilter});
        binding.rePassword.setFilters(new InputFilter[]{noSpaceFilter});
    }

    private void goToLoginActivity() {
        requireActivity().finish();
    }

    public void onChangePasswordClick() {
        hideKeyboard();
        if (!isValidForm()) return;

        ChangePasswordRequest request = new ChangePasswordRequest();

        request.setNewPassword(binding.password.getText().toString().trim());
        request.setOtp(binding.otp.getText().toString().trim());
        request.setIdHash(idHash);
        viewModel.signUpCandidate(request);
        viewModel.isSuccess.observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                goToLoginActivity();            }
        });

    }

    public Boolean isValidForm() {
        String phone = binding.otp.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        String rePassword = binding.rePassword.getText().toString().trim();

        boolean noError = true;

        if (phone.isEmpty()) {
            setError(binding.otp, binding.mgsErOtp, "Hãy nhập mã xác thực");
            noError = false;
        }

        // Mật khẩu
        if (password.isEmpty()) {
            setError(binding.password, binding.mgsErPassword, getString(R.string.err_password));
            noError = false;
        } else if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$")) {
            setError(binding.password, binding.mgsErPassword, getString(R.string.err_password_2));
            noError = false;
        }

        // Nhập lại mật khẩu
        if (!rePassword.equals(password)) {
            setError(binding.rePassword, binding.mgsErRePassword, getString(R.string.err_password_3));
            noError = false;
        }
        return noError;
    }
    private void setUpValidation() {
        validatePhone();
        validatePassword();
        validateRePassword();
    }
    private void validatePhone() {
        binding.otp.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String phone = binding.otp.getText().toString().trim();
                if (phone.isEmpty()) {
                    setError(binding.otp, binding.mgsErOtp, "Hãy nhập mã Otp");
                }
            }
        });

        binding.otp.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = s.toString().trim();
                if (!phone.isEmpty()) {
                    clearError(binding.otp, binding.mgsErOtp);
                }

            }
        });
    }
    private void validatePassword() {
        binding.password.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String password = binding.password.getText().toString().trim();
                if (password.isEmpty()) {
                    setError(binding.password, binding.mgsErPassword, getString(R.string.err_password));
                } else if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$")) {
                    setError(binding.password, binding.mgsErPassword, getString(R.string.err_password_2));
                }
            }
        });

        binding.password.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString().trim();

                if (!password.isEmpty()) {
                    clearError(binding.password, binding.mgsErPassword);
                }
            }
        });
    }
    private void validateRePassword() {
        binding.rePassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String rePassword = binding.rePassword.getText().toString().trim();
                String password = binding.password.getText().toString().trim();
                if (!rePassword.equals(password)) {
                    setError(binding.rePassword, binding.mgsErRePassword, getString(R.string.err_password_3));
                }
            }
        });

        binding.rePassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String rePassword = s.toString().trim();
                String password = binding.password.getText().toString().trim();
                if (rePassword.equals(password)) {
                    clearError(binding.rePassword, binding.mgsErRePassword);
                }
            }
        });
    }

    private void setError(EditText editText, TextView errorText, String message) {
        editText.setBackgroundResource(R.drawable.bg_text_box_select);
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void clearError(EditText editText, TextView errorText) {
        editText.setBackgroundResource(R.drawable.bg_text_box_un_select);
        errorText.setVisibility(View.GONE);
    }

    public void onResetOtpClick(){
        ResetOtpRequest request = new ResetOtpRequest();
        request.setEmail(email);
        viewModel.resetOtp(request);
    }
    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_otp_forget_password;
    }

    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
}
