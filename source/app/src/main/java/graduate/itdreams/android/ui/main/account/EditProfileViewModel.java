package graduate.itdreams.android.ui.main.account;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import graduate.itdreams.android.data.model.api.request.student.StudentUpdateProfileRequest;
import graduate.itdreams.android.data.model.api.response.file.UploadResponse;
import graduate.itdreams.android.utils.ImageUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.Repository;
import graduate.itdreams.android.ui.base.activity.BaseViewModel;
import graduate.itdreams.android.utils.NetworkUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.HttpException;
import timber.log.Timber;

public class EditProfileViewModel extends BaseViewModel {
    public final MutableLiveData<String> fullname = new MutableLiveData<>("");
    public final MutableLiveData<String> email = new MutableLiveData<>("");
    public final MutableLiveData<String> birthday = new MutableLiveData<>("");
    public final MutableLiveData<String> username = new MutableLiveData<>("");
    public MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();

    MutableLiveData<Bitmap> avatarLiveData = new MutableLiveData<>();
    public EditProfileViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void updateProfile(StudentUpdateProfileRequest request) {
        showLoading();
        compositeDisposable.add(repository.getApiService().update(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(throwable ->
                        throwable.flatMap((Function<Throwable, ObservableSource<?>>) throwable1 -> {
                            if (NetworkUtils.checkNetworkError(throwable1)) {
                                hideLoading();
                                return application.showDialogNoInternetAccess();
                            } else {
                                return Observable.error(throwable1);
                            }
                        })
                )
                .subscribe(
                        response -> {
                            hideLoading();
                            showNormalMessage(getApplication().getString(R.string.update_success));
                            isSuccess.setValue(true);
                        }, throwable -> {
                            hideLoading();
                            Timber.e(throwable);
                            if (throwable instanceof HttpException && ((HttpException) throwable).code() == 400) {
                                HttpException httpException = (HttpException) throwable;
                                if (httpException.code() == 400) {
                                }
                            }
                            showNormalMessage(getApplication().getString(R.string.update_un_success));
                        }));
    }
    public void loadAvatar(String url){

        if (url.startsWith("https://") || url.startsWith("http://")) {
            new Thread(() -> {
                try {
                    URL imageUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);
                    input.close();
                    if (bitmap != null) {
                        avatarLiveData.setValue(bitmap);
                    }
                } catch (Exception e) {
                    Log.e("ProfileViewModel", "Lỗi khi tải ảnh từ URL: " + e.getMessage());
                }
            }).start();
        }else {
            compositeDisposable.add(repository.getUploadApiService().loadFile(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseBody -> {
                        InputStream inputStream = responseBody.byteStream();
                        Bitmap bitmap = ImageUtils.getBitmap(inputStream);
                        if (bitmap != null) {
                            avatarLiveData.setValue(bitmap);
                        } else {
                            Log.e("ProfileViewModel", "Lỗi: Bitmap rỗng");
                        }
                    }, throwable -> {
                        Log.e("ProfileViewModel", "Lỗi khi tải ảnh: " + throwable.getMessage());
                    })
            );
        }

    }
    public void loadProfile() {
        showLoading();
        compositeDisposable.add(repository.getApiService().getProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(throwable ->
                        throwable.flatMap((Function<Throwable, ObservableSource<?>>) throwable1 -> {
                            if (NetworkUtils.checkNetworkError(throwable1)) {
                                hideLoading();
                                return application.showDialogNoInternetAccess();
                            } else {
                                return Observable.error(throwable1);
                            }
                        })
                )
                .subscribe(
                        response -> {
                            hideLoading();
                            fullname.setValue(response.getData().getProfileAccountDto().getFullName());
                            email.setValue(response.getData().getProfileAccountDto().getEmail());
                            username.setValue(response.getData().getProfileAccountDto().getUsername());
                            loadAvatar(response.getData().getProfileAccountDto().getAvatar());

                            String birthdayStr = response.getData().getBirthday();

                            SimpleDateFormat fullFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                            SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                            try {
                                Date date = fullFormat.parse(birthdayStr);
                                String formattedDate = dateOnlyFormat.format(date);
                                birthday.setValue(formattedDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                birthday.setValue(birthdayStr);
                            }


                        }, throwable -> {
                            hideLoading();
                            Timber.e(throwable);
                            if (throwable instanceof HttpException && ((HttpException) throwable).code() == 400) {
                                HttpException httpException = (HttpException) throwable;
                                if (httpException.code() == 400) {
                                }
                            }
                        }));
    }
    public void onConfirmClicked(File imageFile, StudentUpdateProfileRequest request) {

        if (imageFile != null) {
            uploadImage(imageFile, true, request);
        } else {
            updateProfile(request);
        }
    }
    public void uploadImage(File imageFile, boolean isForContact, StudentUpdateProfileRequest infoStudent) {
        if (imageFile == null || !imageFile.exists()) {
            showNormalMessage(getApplication().getString(R.string.invalid_image));
            return;
        }

        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "AVATAR");

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);
        showLoading();
        compositeDisposable.add(repository.getUploadApiService().uploadFile(type, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    hideLoading();
                    if (response.isResult() && response.getData() != null) {
                        UploadResponse uploadedUrl = response.getData();
                        infoStudent.setAvatarPath(uploadedUrl.getFilePath());
                        if (isForContact) {
                            updateProfile(infoStudent);
                        }
                    }
                }, throwable -> {
                    hideLoading();
                    Timber.e(throwable);
                    if (throwable instanceof HttpException && ((HttpException) throwable).code() == 400) {
                        HttpException httpException = (HttpException) throwable;
                        if (httpException.code() == 400) {
                        }
                    }
                })
        );
    }
}

