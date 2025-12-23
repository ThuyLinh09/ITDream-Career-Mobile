package graduate.itdreams.android.ui.main.account;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import eu.davidea.flexibleadapter.databinding.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.request.student.StudentSignUpRequest;
import graduate.itdreams.android.data.model.api.request.student.StudentUpdateProfileRequest;
import graduate.itdreams.android.data.socket.dto.Message;
import graduate.itdreams.android.databinding.ActivityEditProfileBinding;
import graduate.itdreams.android.di.component.ActivityComponent;
import graduate.itdreams.android.ui.base.activity.BaseActivity;
import graduate.itdreams.android.ui.main.login.SimpleTextWatcher;

public class EditProfileActivity extends BaseActivity<ActivityEditProfileBinding, EditProfileViewModel> {
    private Calendar selectedBirthDate = null;
    private File selectedImageFile;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding.setA(this);
        viewBinding.setVm(viewModel);
        viewBinding.setLifecycleOwner(this);
        validatePhone();

        viewModel.loadProfile();
        viewBinding.toolbar.setNavigationOnClickListener(v -> finish());
        setUpBirthDate();
        imagePickerLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        viewBinding.avatar.setImageURI(imageUri);
                        selectedImageFile = convertUriToFile(imageUri);
                    }
                });
        viewBinding.avatar.setOnClickListener(v -> openImagePicker());
        viewModel.avatarLiveData.observe(this, bitmap -> {
            if (bitmap != null) {
                viewBinding.avatar.setImageBitmap(bitmap);
            }
        });
        viewModel.isSuccess.observe(this, success -> {
            finish();
        });
    }
    public Boolean isValidForm() {
        String name = viewBinding.name.getText().toString().trim();
        String username = viewBinding.username.getText().toString().trim();
        String phone = viewBinding.phone.getText().toString().trim();

        boolean noError = true;

        if (name.isEmpty()) {
            setError(viewBinding.name, viewBinding.mgsErName, getString(R.string.err_name));
            noError = false;
        }

        if (phone.isEmpty()) {
            setError(viewBinding.phone, viewBinding.mgsErPhone, getString(R.string.err_phone));
            noError = false;
        } else if (!phone.matches("^0[0-9]{9}$")) {
            setError(viewBinding.phone, viewBinding.mgsErPhone, getString(R.string.err_phone_2));
            noError = false;
        }

        return noError;
    }
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
    private File convertUriToFile(Uri uri) {
        try {
            File file = new File(getCacheDir(), "temp_image.jpg");
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
            return file;
        } catch (Exception e) {
            Log.e("CreateContactActivity", "Lỗi khi chuyển Uri thành File: " + e.getMessage());
            return null;
        }
    }
    public void onUpdateProfile() {

        hideKeyboard();
        if (!isValidForm()) return;

        String fullname = viewModel.fullname.toString().trim();
        String username = viewModel.username.toString().trim();
        String phone = viewModel.phone.toString().trim();
        StudentUpdateProfileRequest request = new StudentUpdateProfileRequest();
        request.setFullName(fullname);
        request.setUsername(username);
        request.setPhone(phone);
        if (selectedBirthDate != null) {
            SimpleDateFormat apiFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String formattedBirthDate = apiFormat.format(selectedBirthDate.getTime());
            request.setBirthday(formattedBirthDate);
        }
        viewModel.onConfirmClicked(selectedImageFile);
    }

    private void setUpBirthDate() {
        viewBinding.etBirthdate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedBirthDate = Calendar.getInstance();
                        selectedBirthDate.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        viewBinding.etBirthdate.setText(sdf.format(selectedBirthDate.getTime()));
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });
    }
    private void validatePhone() {
        viewBinding.phone.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String phone = viewBinding.phone.getText().toString().trim();
                if (phone.isEmpty()) {
                    setError(viewBinding.phone, viewBinding.mgsErPhone, getString(R.string.err_phone));
                } else if (!phone.matches("^0[0-9]{9}$")) {
                    setError(viewBinding.phone, viewBinding.mgsErPhone, getString(R.string.err_phone_2));
                }
            }
        });

        viewBinding.phone.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = s.toString().trim();
                if (!phone.isEmpty()) {
                    clearError(viewBinding.phone, viewBinding.mgsErPhone);
                }

            }
        });
    }
    private void clearError(EditText editText, TextView errorText) {
        editText.setBackgroundResource(R.drawable.bg_text_box_un_select);
        errorText.setVisibility(View.GONE);
    }
    private void setError(EditText editText, TextView errorText, String message) {
        editText.setBackgroundResource(R.drawable.bg_text_box_select);
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }
    @Override
    public int getLayoutId () {
        return R.layout.activity_edit_profile;
    }

    @Override
    public int getBindingVariable () {
        return BR.vm;
    }

    @Override
    public void performDependencyInjection (ActivityComponent buildComponent){
        buildComponent.inject(this);
    }

    @Override
    public void onMessageReceived(Message message) {

    }

    @Override
    public void onConnectionClosed() {

    }

    @Override
    public void onConnectionClosing() {

    }
}

