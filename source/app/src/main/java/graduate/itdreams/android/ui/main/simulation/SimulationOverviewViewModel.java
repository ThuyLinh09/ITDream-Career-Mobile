package graduate.itdreams.android.ui.main.simulation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.Repository;
import graduate.itdreams.android.data.model.api.response.feedback.FeedbackResponse;
import graduate.itdreams.android.data.model.api.response.simulation.SimulationDetailResponse;
import graduate.itdreams.android.ui.base.activity.BaseViewModel;
import graduate.itdreams.android.utils.ImageUtils;
import graduate.itdreams.android.utils.NetworkUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;
import timber.log.Timber;

public class SimulationOverviewViewModel extends BaseViewModel {
    public final MutableLiveData<Bitmap> imageLiveData = new MutableLiveData<>();
    public final MutableLiveData<String> title = new MutableLiveData<>("");
    public final MutableLiveData<String> author = new MutableLiveData<>("");
    public final MutableLiveData<String> estimatedTime = new MutableLiveData<>("");
    public final MutableLiveData<String> rating = new MutableLiveData<>("");
    public final MutableLiveData<Long> participantQuantity = new MutableLiveData<>(0L);

    private final MutableLiveData<SimulationDetailResponse> simulationDetail = new MutableLiveData<>();
    public LiveData<SimulationDetailResponse> getSimulationDetail() {
        return simulationDetail;
    }
    private final MutableLiveData<FeedbackResponse> feedback = new MutableLiveData<>();
    public LiveData<FeedbackResponse> getFeedback() {
        return feedback;
    }

    private final MutableLiveData<Long> simulationId = new MutableLiveData<>();
    public LiveData<Long> getSimulationId() {
        return simulationId;
    }
    public SimulationOverviewViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void fetchSimulationDetail(Long id) {
        showLoading();
        compositeDisposable.add(repository.getApiService().getSimulationDetail(id)
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
                            title.setValue(response.getData().getTitle());
                            author.setValue(response.getData().getEducator().getProfileAccountDto().getFullName());
                            estimatedTime.setValue(response.getData().getTotalEstimatedTime());
                            rating.setValue(response.getData().getAvgRating().toString().trim());
                            participantQuantity.setValue(response.getData().getParticipantQuantity());
                            simulationDetail.setValue(response.getData());
                            simulationId.setValue(id);
                            loadImage(response.getData().getImagePath());
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

    public void getFeedback(Long id) {
        showLoading();
        compositeDisposable.add(repository.getApiService().getFeedback(id)
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
                            feedback.setValue(response.getData());
                        }, throwable -> {
                            hideLoading();
                            showNormalMessage(getApplication().getString(R.string.feedback_fail));

                            Timber.e(throwable);
                            if (throwable instanceof HttpException && ((HttpException) throwable).code() == 400) {
                                HttpException httpException = (HttpException) throwable;
                                if (httpException.code() == 400) {
                                }
                            }
                        }));
    }
    public void loadImage(String url){
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
                        imageLiveData.setValue(bitmap);
                    }
                } catch (Exception e) {
                    Log.e("ProfileViewModel", "Lỗi khi tải ảnh từ URL: " + e.getMessage());
                }
            }).start();
        }else {
            compositeDisposable.add(repository.getUploadApiService().loadFile(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( responseBody ->  {
                        InputStream inputStream = responseBody.byteStream();
                        Bitmap bitmap = ImageUtils.getBitmap(inputStream);
                        if (bitmap != null) {
                            imageLiveData.setValue(bitmap);
                        } else {
                            Log.e("ProfileViewModel", "Lỗi: Bitmap rỗng");
                        }
                    }, throwable -> {
                        Log.e("ProfileViewModel", "Lỗi khi tải ảnh: " + throwable.getMessage());
                    })
            );
        }


    }
}
