package graduate.itdreams.android.ui.main.register;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import graduate.itdreams.android.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.request.student.StudentSignUpRequest;
import graduate.itdreams.android.databinding.FragmentSignupBinding;
import graduate.itdreams.android.di.component.FragmentComponent;
import graduate.itdreams.android.ui.base.fragment.BaseFragment;
import graduate.itdreams.android.ui.main.login.SimpleTextWatcher;

public class SignUpFragment extends BaseFragment<FragmentSignupBinding, SignUpViewModel> {
    private Calendar selectedBirthDate = null;

    @Override
    protected void performDataBinding() {
        binding.setF(this);
        binding.setVm(viewModel);
        setUpValidation();
        setUpBirthDate();
        setNoSpaceFilters();
    }
    private void setNoSpaceFilters() {
        InputFilter noSpaceFilter = (source, start, end, dest, dstart, dend) -> {
            if (source != null && source.toString().contains(" ")) {
                return source.toString().replace(" ", "");
            }
            return null;
        };

        binding.email.setFilters(new InputFilter[]{noSpaceFilter});
        binding.password.setFilters(new InputFilter[]{noSpaceFilter});
        binding.rePassword.setFilters(new InputFilter[]{noSpaceFilter});
    }

    private void goToOtpFragment(String idHash) {
        VerifyOTPFragment otpFragment = new VerifyOTPFragment();

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
    private void setUpBirthDate() {
        binding.birthdate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedBirthDate = Calendar.getInstance();
                        selectedBirthDate.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        binding.birthdate.setText(sdf.format(selectedBirthDate.getTime()));
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });
    }

    public void onSignUpClick() {
        hideKeyboard();
        if (!isValidForm()) return;

        StudentSignUpRequest request = new StudentSignUpRequest();

        request.setFullName(binding.name.getText().toString().trim());
        request.setEmail(binding.email.getText().toString().trim());
        request.setPhone(binding.phone.getText().toString().trim());
        request.setPassword(binding.password.getText().toString().trim());
        request.setUsername(binding.username.getText().toString().trim());
        if (selectedBirthDate != null) {
            SimpleDateFormat apiFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String formattedBirthDate = apiFormat.format(selectedBirthDate.getTime());
            request.setBirthday(formattedBirthDate);
        }
        viewModel.signUpCandidate(request, requireContext());
        viewModel.isSuccess.observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), R.string.signup_success, Toast.LENGTH_SHORT).show();
                String idHash = viewModel.idHash.getValue();
                goToOtpFragment(idHash);            }
        });

    }

    public Boolean isValidForm() {
        String name = binding.name.getText().toString().trim();
        String username = binding.username.getText().toString().trim();
        String email = binding.email.getText().toString().trim();
        String phone = binding.phone.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        String rePassword = binding.rePassword.getText().toString().trim();

        boolean noError = true;

        // Tên
        if (name.isEmpty()) {
            setError(binding.name, binding.mgsErName, getString(R.string.err_name));
            noError = false;
        }

        if (username.isEmpty()) {
            setError(binding.username, binding.mgsErUsername, getString(R.string.err_username));
            noError = false;
        }

        // Email
        if (email.isEmpty()) {
            setError(binding.email, binding.mgsErEmail, getString(R.string.err_email));
            noError = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setError(binding.email, binding.mgsErEmail, getString(R.string.err_email_2));
            noError = false;
        }

        // SĐT
        if (phone.isEmpty()) {
            setError(binding.phone, binding.mgsErPhone, getString(R.string.err_phone));
            noError = false;
        } else if (!phone.matches("^0[0-9]{9}$")) {
            setError(binding.phone, binding.mgsErPhone, getString(R.string.err_phone_2));
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
        validateName();
        validateUsername();
        validateEmail();
        validatePhone();
        validatePassword();
        validateRePassword();
    }

    private void validateName() {
        binding.name.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String name = binding.name.getText().toString().trim();
                if (name.isEmpty()) {
                    setError(binding.name, binding.mgsErName, getString(R.string.err_name));
                }
            }
        });

        binding.name.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    clearError(binding.name, binding.mgsErName);
                }
            }
        });
    }
    private void validateUsername() {
        binding.username.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String username = binding.username.getText().toString().trim();
                if (username.isEmpty()) {
                    setError(binding.username, binding.mgsErUsername, getString(R.string.err_name));
                }
            }
        });

        binding.username.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    clearError(binding.username, binding.mgsErUsername);
                }
            }
        });
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
    private void validatePhone() {
        binding.phone.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String phone = binding.phone.getText().toString().trim();
                if (phone.isEmpty()) {
                    setError(binding.phone, binding.mgsErPhone, getString(R.string.err_phone));
                } else if (!phone.matches("^0[0-9]{9}$")) {
                    setError(binding.phone, binding.mgsErPhone, getString(R.string.err_phone_2));
                }
            }
        });

        binding.phone.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = s.toString().trim();
                if (!phone.isEmpty()) {
                    clearError(binding.phone, binding.mgsErPhone);
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
    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_signup;
    }

    @Override
    protected void performDependencyInjection(FragmentComponent buildComponent) {
        buildComponent.inject(this);
    }
}
