package graduate.itdreams.android.ui.main.notification;

import android.graphics.Bitmap;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.data.Repository;
import graduate.itdreams.android.data.model.api.response.notification.NotificationResponse;
import graduate.itdreams.android.data.model.api.response.simulation.SimulationResponse;
import graduate.itdreams.android.ui.base.fragment.BaseFragmentViewModel;
import graduate.itdreams.android.utils.NetworkUtils;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;
import timber.log.Timber;

public class NotificationViewModel extends BaseFragmentViewModel {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<List<NotificationResponse>> _notificationList = new MutableLiveData<>();
    public LiveData<List<NotificationResponse>> getNotificationList() {
        return _notificationList;
    }
    public NotificationViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }
    public void fetchNotificationList(){
        showLoading();
        compositeDisposable.add(repository.getApiService().getNotificationList()
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
                            List<NotificationResponse> notificationList = response.getData();

                            _notificationList.setValue(notificationList);
                        }, throwable -> {
                            hideLoading();
                            Timber.e(throwable);
                            if (throwable instanceof HttpException) {
                                int code = ((HttpException) throwable).code();

                                if (code == 401 || code == 403) {
                                    // Token invalid / expired → force logout
//                                    _forceLogout.setValue(true);
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
}
