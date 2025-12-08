package graduate.itdreams.android.ui.main.login;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import graduate.itdreams.android.data.model.api.request.login.GoogleLoginRequest;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.Repository;
import graduate.itdreams.android.data.model.api.request.login.CandidateLoginRequest;
import graduate.itdreams.android.ui.base.activity.BaseViewModel;
import graduate.itdreams.android.utils.NetworkUtils;
import retrofit2.HttpException;
import timber.log.Timber;

public class LoginViewModel extends BaseViewModel {
    public MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();

    public LoginViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void candidateLogin(CandidateLoginRequest request) {
        showLoading();
        compositeDisposable.add(repository.getApiService().candidateLogin(request)
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
                            repository.getSharedPreferences().setToken(response.getAccess_token());
                            repository.getSharedPreferences().saveAccessTokenObject(response);
                            loginSuccess.setValue(true);
                            showNormalMessage(getApplication().getString(R.string.login_success));

                        }, throwable -> {
                            hideLoading();
                            Timber.e(throwable);
                            if (throwable instanceof HttpException && ((HttpException) throwable).code() == 400) {
                                HttpException httpException = (HttpException) throwable;
                                if (httpException.code() == 400) {
                                }
                            }
                            showNormalMessage(getApplication().getString(R.string.login_un_success));
                        }));
    }
    public void googleLogin(GoogleLoginRequest request) {
        showLoading();
        compositeDisposable.add(repository.getApiService().googleLogin(request)
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
                            repository.getSharedPreferences().setToken(response.getData());
                            loginSuccess.setValue(true);

                            showNormalMessage(getApplication().getString(R.string.login_success));

                        }, throwable -> {
                            hideLoading();
                            Timber.e(throwable);
                            if (throwable instanceof HttpException && ((HttpException) throwable).code() == 400) {
                                HttpException httpException = (HttpException) throwable;
                                if (httpException.code() == 400) {
                                }
                            }
                            Log.e("API_ERROR", "Google Login Error", throwable);
                            showNormalMessage(getApplication().getString(R.string.login_un_success));
                        }));
    }
}
