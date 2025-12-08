package graduate.itdreams.android.ui.main.account;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import graduate.itdreams.android.utils.ImageUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.data.Repository;
import graduate.itdreams.android.ui.base.fragment.BaseFragmentViewModel;
import graduate.itdreams.android.utils.NetworkUtils;
import retrofit2.HttpException;
import timber.log.Timber;

public class AccountViewModel extends BaseFragmentViewModel {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    MutableLiveData<Bitmap> avatarLiveData = new MutableLiveData<>();

    public final MutableLiveData<String> fullName = new MutableLiveData<>("");
    public final MutableLiveData<String> email = new MutableLiveData<>("");

    public AccountViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
        fetchProfile();
    }
    public void fetchProfile() {
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
                            fullName.setValue(response.getData().getProfileAccountDto().getFullName());
                            email.setValue(response.getData().getProfileAccountDto().getEmail());
                            loadAvatar(response.getData().getProfileAccountDto().getAvatar());
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

    public void loadAvatar(String url){
        if (url == null || url.isEmpty()) return;

        if (url.startsWith("https://") || url.startsWith("http://")) {
            // URL trực tiếp (ví dụ Google)
            loadAvatarFromUrl(url);
        } else {
            // URL backend (nếu backend trả path nội bộ)
            compositeDisposable.add(repository.getUploadApiService().loadFile(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(responseBody -> {
                        InputStream inputStream = responseBody.byteStream();
                        Bitmap bitmap = ImageUtils.getBitmap(inputStream);
                        if (bitmap != null) {
                            avatarLiveData.setValue(bitmap);
                        }
                    }, throwable -> Log.e("ProfileViewModel", "Lỗi khi tải ảnh: " + throwable.getMessage()))
            );
        }
    }

    // Load trực tiếp từ URL HTTPS/HTTP
    private void loadAvatarFromUrl(String url) {
        new Thread(() -> {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();
                avatarLiveData.postValue(bitmap);
            } catch (Exception e) {
                Log.e("ProfileViewModel", "Lỗi khi tải ảnh từ URL: " + e.getMessage());
            }
        }).start();
    }

    public void logout(){
        repository.getSharedPreferences().setToken(null);
        repository.getSharedPreferences().saveAccessTokenObject(null);
    }
}
