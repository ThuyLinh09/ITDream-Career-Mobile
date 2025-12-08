package graduate.itdreams.android.ui.main.account;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import graduate.itdreams.android.data.model.api.request.student.StudentUpdateProfileRequest;
import graduate.itdreams.android.data.socket.dto.Message;
import graduate.itdreams.android.databinding.ActivityEditProfileBinding;
import graduate.itdreams.android.di.component.ActivityComponent;
import graduate.itdreams.android.ui.base.activity.BaseActivity;

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
        String fullname = viewBinding.name.getText().toString().trim();
        String username = viewBinding.username.getText().toString().trim();
        StudentUpdateProfileRequest request = new StudentUpdateProfileRequest();
        request.setFullName(fullname);
        request.setUsername(username);
        if (selectedBirthDate != null) {
            SimpleDateFormat apiFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String formattedBirthDate = apiFormat.format(selectedBirthDate.getTime());
            request.setBirthday(formattedBirthDate);
        }
        viewModel.onConfirmClicked(selectedImageFile, request);
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

