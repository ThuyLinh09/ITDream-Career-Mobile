package graduate.itdreams.android.ui.main.simulation.rate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.data.Repository;
import graduate.itdreams.android.data.model.api.response.simulation.RateResponse;
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

public class RateViewModel extends BaseFragmentViewModel {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<List<RateResponse>> _rateList = new MutableLiveData<>();
    public LiveData<List<RateResponse>> getRateList() {
        return _rateList;
    }
    public RateViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }
    public void fetchRateList(Long simulationId){
        showLoading();
        compositeDisposable.add(repository.getApiService().getRateList(simulationId)
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
                            List<RateResponse> rateList = response.getData().getContent();

                            _rateList.setValue(rateList);

                        }, throwable -> {
                            hideLoading();
                            Timber.e(throwable);
                            if (throwable instanceof HttpException) {
                                int code = ((HttpException) throwable).code();

                                if (code == 401 || code == 403) {
                                    // Token invalid / expired → force logout
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
