package graduate.itdreams.android.ui.main.login;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.Repository;
import graduate.itdreams.android.data.model.api.request.login.ChangePasswordRequest;
import graduate.itdreams.android.data.model.api.request.student.ResetOtpRequest;
import graduate.itdreams.android.data.model.api.request.student.StudentSignUpRequest;
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

public class OTPForgetPasswordViewModel extends BaseFragmentViewModel {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isResetOtpSuccess = new MutableLiveData<>();


    public OTPForgetPasswordViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
    }

    public void signUpCandidate(ChangePasswordRequest request) {
        showLoading();
        compositeDisposable.add(repository.getApiService().changePassword(request)
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
                            isSuccess.setValue(true);
                            showNormalMessage("Đổi mật khẩu thành công");
                        }, throwable -> {
                            hideLoading();
                            isSuccess.setValue(false);
                            Timber.e(throwable);
                            if (throwable instanceof HttpException && ((HttpException) throwable).code() == 400) {
                                HttpException httpException = (HttpException) throwable;
                                if (httpException.code() == 400) {
                                }
                            }
                            showNormalMessage("OTP không chính xác");
                        }));
    }

    public void resetOtp(ResetOtpRequest request){
        showLoading();
        compositeDisposable.add(repository.getApiService().resetOtp(request)
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
                            isResetOtpSuccess.setValue(true);
                        }, throwable -> {
                            hideLoading();
                            isResetOtpSuccess.setValue(false);
                            Timber.e(throwable);
                            if (throwable instanceof HttpException && ((HttpException) throwable).code() == 400) {
                                HttpException httpException = (HttpException) throwable;
                                if (httpException.code() == 400) {
                                }
                            }
                        }));
    }
}
