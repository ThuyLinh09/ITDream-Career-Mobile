package graduate.itdreams.android.ui.main;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.reflect.TypeToken;

import graduate.itdreams.android.data.model.api.ApiModelUtils;

import graduate.itdreams.android.BuildConfig;
import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.data.Repository;
import graduate.itdreams.android.data.model.api.ResponseWrapper;
import graduate.itdreams.android.data.model.api.response.notification.NotificationResponse;
import graduate.itdreams.android.data.socket.Command;
import graduate.itdreams.android.data.socket.dto.Message;
import graduate.itdreams.android.ui.base.activity.BaseViewModel;
import timber.log.Timber;

public class MainViewModel extends BaseViewModel {
    private MutableLiveData<NotificationResponse> _notification = new MutableLiveData<>();
    public LiveData<NotificationResponse> notification = _notification;
    public MainViewModel(Repository repository, MVVMApplication application) {
        super(repository, application);
        application.createSocket(BuildConfig.WS_URL);

    }
//    public void doLogin(){
//        LoginRequest request = new LoginRequest();
//        request.setPosId(deviceId);
//        showLoading();
//        compositeDisposable.add(repository.getApiService().login(request)
//                                        .subscribeOn(Schedulers.io())
//                                        .observeOn(AndroidSchedulers.mainThread())
//                                        .retryWhen(throwable ->
//                                                           throwable.flatMap((Function<Throwable, ObservableSource<?>>) throwable1 -> {
//                                                               if (NetworkUtils.checkNetworkError(throwable1)) {
//                                                                   hideLoading();
//                                                                   return application.showDialogNoInternetAccess();
//                                                               }else{
//                                                                   return Observable.error(throwable1);
//                                                               }
//                                                           })
//                                        )
//                                        .subscribe(
//                                                response -> {
//                                                    hideLoading();
//                                                    repository.getSharedPreferences().setToken(response.getData().getAccess_token());
//                                                    showSuccessMessage("Login success");
//                                                }, throwable -> {
//                                                    hideLoading();
//                                                    Timber.e(throwable);
//                                                    if (throwable instanceof HttpException && ((HttpException) throwable).code() == 400){
//                                                        HttpException httpException = (HttpException) throwable;
//                                                        if (httpException.code() == 400) {
//                                                        }
//                                                        showErrorMessage("Login failed");
//                                                    } else{
//                                                        showErrorMessage("Login failed");
//                                                    }
//                                                }));
//    }

    @Override
    public void messageReceived(Message message) {
        super.messageReceived(message);
        if(message != null && message.getResponseCode() == 200 || message.getResponseCode() == 101) {
            switch (message.getCmd()) {
                case Command.COMMAND_CLIENT_PING:
                case Command.COMMAND_CLIENT_INFO:
                    break;
                case Command.CLIENT_RECEIVED_PUSH_NOTIFICATION:
                    String jsonNotification = ApiModelUtils.GSON.toJson(message.getData());
                    NotificationResponse responseNotification = ApiModelUtils.GSON.fromJson(jsonNotification, new TypeToken<NotificationResponse>(){}.getType());

                    _notification.postValue(responseNotification);
                default:
                    break;
            }
        }else {
            application.getCurrentActivity().runOnUiThread(this::hideLoading);
        }
    }
}
