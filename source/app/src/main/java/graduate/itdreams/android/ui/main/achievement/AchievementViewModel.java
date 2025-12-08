package graduate.itdreams.android.ui.main.achievement;

import static kotlin.io.ByteStreamsKt.readBytes;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.data.Repository;
import graduate.itdreams.android.data.model.api.request.achievement.UpdateCertificateRequest;
import graduate.itdreams.android.data.model.api.request.achievement.UploadCertificateRequest;
import graduate.itdreams.android.data.model.api.response.simulation.AchievementResponse;
import graduate.itdreams.android.ui.base.fragment.BaseFragmentViewModel;
import graduate.itdreams.android.utils.ImageUtils;
import graduate.itdreams.android.utils.NetworkUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;
import timber.log.Timber;

public class AchievementViewModel extends BaseFragmentViewModel {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<Pair<Long, Bitmap>> imageLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<AchievementResponse>> _achievementList = new MutableLiveData<>();
    public LiveData<List<AchievementResponse>> getPostList() {
        return _achievementList;
    }
    private final MutableLiveData<String> certificateUrl = new MutableLiveData<>();
    public LiveData<String> getCertificateUrl() {
        return certificateUrl;
    }
    private MutableLiveData<Boolean> _forceLogout = new MutableLiveData<>();
    public LiveData<Boolean> forceLogout = _forceLogout;
    private MutableLiveData<byte[]> pdfData = new MutableLiveData<>();
    public LiveData<byte[]> getPdfData() { return pdfData; }
    public AchievementViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }
    public void fetchAchievementList(){
        showLoading();
        compositeDisposable.add(repository.getApiService().getAchievementList()
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
                            List<AchievementResponse> achievementList = response.getData().getContent();

                            _achievementList.setValue(achievementList);
                        }, throwable -> {
                            hideLoading();
                            Timber.e(throwable);
                            if (throwable instanceof HttpException) {
                                int code = ((HttpException) throwable).code();

                                if (code == 401 || code == 403) {
                                    // Token invalid / expired → force logout
                                    _forceLogout.setValue(true);
                                    return;
                                }
                            }
                            if (throwable instanceof HttpException && ((HttpException) throwable).code() == 400) {
                                HttpException httpException = (HttpException) throwable;
                                if (httpException.code() == 400) {
                                }
                            }
                        }));
    }
    public void uploadCertificate(UploadCertificateRequest request){
        showLoading();
        compositeDisposable.add(repository.getUploadApiService().uploadCertificate(request)
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
                            String filePath = response.getData().getFilePath();
                            String fixedPath = filePath.replace("\\", "/");
                            certificateUrl.setValue(fixedPath);
                        }, throwable -> {
                            hideLoading();
                            Timber.e(throwable);
                            if (throwable instanceof HttpException) {
                                int code = ((HttpException) throwable).code();

                                if (code == 401 || code == 403) {
                                    // Token invalid / expired → force logout
                                    _forceLogout.setValue(true);
                                    return;
                                }
                            }
                            if (throwable instanceof HttpException && ((HttpException) throwable).code() == 400) {
                                HttpException httpException = (HttpException) throwable;
                                if (httpException.code() == 400) {
                                }
                            }
                        }));
    }
    public void updateAchievement(UpdateCertificateRequest request){
        showLoading();
        compositeDisposable.add(repository.getApiService().updateAchievement(request)
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
                        }, throwable -> {
                            hideLoading();
                            Timber.e(throwable);
                            if (throwable instanceof HttpException) {
                                int code = ((HttpException) throwable).code();

                                if (code == 401 || code == 403) {
                                    // Token invalid / expired → force logout
                                    _forceLogout.setValue(true);
                                    return;
                                }
                            }
                            if (throwable instanceof HttpException && ((HttpException) throwable).code() == 400) {
                                HttpException httpException = (HttpException) throwable;
                                if (httpException.code() == 400) {
                                }
                            }
                        }));
    }
    public void loadDocument(String url){
        compositeDisposable.add(repository.getUploadApiService().loadFile(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( responseBody ->  {
                    InputStream inputStream = responseBody.byteStream();
                    byte[] bytes = readBytes(inputStream);
                    pdfData.postValue(bytes);
                }, throwable -> {
                    Log.e("ProfileViewModel", "Lỗi khi tải file: " + throwable.getMessage());
                })
        );

    }
    public void logout(){
        repository.getSharedPreferences().setToken(null);
        repository.getSharedPreferences().saveAccessTokenObject(null);
    }
    private final Map<Long, Bitmap> bitmapCache = new HashMap<>();

    public Bitmap getBitmapFromCache(Long itemId) {
        return bitmapCache.get(itemId);
    }

    public void loadImageForItem(Long itemId, String url) {
        if (bitmapCache.containsKey(itemId)) return; // đã có thì không tải lại

        compositeDisposable.add(repository.getUploadApiService().loadFile(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(responseBody -> {
                    InputStream inputStream = responseBody.byteStream();
                    Bitmap bitmap = ImageUtils.getBitmap(inputStream);
                    if (bitmap != null) {
                        bitmapCache.put(itemId, bitmap);
                        imageLiveData.setValue(new Pair<>(itemId, bitmap));
                    }
                }, throwable -> Log.e("ViewModel", "Lỗi tải ảnh: " + throwable.getMessage()))
        );
    }

}
