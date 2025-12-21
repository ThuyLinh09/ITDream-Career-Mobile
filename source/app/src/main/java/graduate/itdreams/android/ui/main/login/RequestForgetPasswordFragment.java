package graduate.itdreams.android.ui.main.login;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import graduate.itdreams.android.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.request.login.RequestForgetPasswordRequest;
import graduate.itdreams.android.data.model.api.request.student.StudentSignUpRequest;
import graduate.itdreams.android.databinding.FragmentRequestForgetPasswordBinding;
import graduate.itdreams.android.databinding.FragmentSignupBinding;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;
import graduate.itdreams.android.ui.main.register.VerifyOTPFragment;

public class RequestForgetPasswordFragment extends BaseFragment<FragmentRequestForgetPasswordBinding, RequestForgetPasswordViewModel> {
    private Calendar selectedBirthDate = null;

    @Override
    protected void performDataBinding() {
        binding.setF(this);
        binding.setVm(viewModel);
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

        binding.email.setFilters(new InputFilter[]{noSpaceFilter});
    }

    private void goToOtpFragment(String idHash) {
        OTPForgetPasswordFragment otpFragment = new OTPForgetPasswordFragment();

        Bundle bundle = new Bundle();
        bundle.putString("idHash", idHash);
        bundle.putString("email", binding.email.getText().toString().trim());
        otpFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, otpFragment)
                .addToBackStack(null)
                .commit();
    }

    public void onForgetClick() {
        hideKeyboard();
        if (!isValidForm()) return;

        RequestForgetPasswordRequest request = new RequestForgetPasswordRequest();

        request.setEmail(binding.email.getText().toString().trim());
        viewModel.signUpCandidate(request, requireContext());
        viewModel.isSuccess.observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Gửi yêu cầu thành công", Toast.LENGTH_SHORT).show();
                String idHash = viewModel.idHash.getValue();
                goToOtpFragment(idHash);            }
        });

    }

    public Boolean isValidForm() {

        String email = binding.email.getText().toString().trim();

        boolean noError = true;

        if (email.isEmpty()) {
            setError(binding.email, binding.mgsErEmail, getString(R.string.err_email));
            noError = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setError(binding.email, binding.mgsErEmail, getString(R.string.err_email_2));
            noError = false;
        }
        return noError;
    }
    private void setUpValidation() {
        validateEmail();
    }

    private void validateEmail() {
        binding.email.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String email = binding.email.getText().toString().trim();
                if (email.isEmpty()) {
                    setError(binding.email, binding.mgsErEmail, getString(R.string.err_email));
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    setError(binding.email, binding.mgsErEmail, getString(R.string.err_email_2));
                }
            }
        });

        binding.email.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString().trim();

                if (!email.isEmpty()) {
                    clearError(binding.email, binding.mgsErEmail);
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
    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_request_forget_password;
    }

    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
}
