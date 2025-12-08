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
        //application.createSocket(BuildConfig.WS_URL);

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
//                case Command.COMMAND_PROCESS_TABLE:
//                    switch (message.getSubCmd()){
//                        case SubCommand.CMD_MPOS_CREATE_TABLE:
//                            application.getCurrentActivity().runOnUiThread(() -> {
//                                hideLoading();
//                                setIsCreateTable(true);
//                            });
//                            break;
//                        case SubCommand.CMD_MPOS_GET_LIST_TABLE:
//                            break;
//                        case SubCommand.CMD_MPOS_VIEW_TABLE:
//                            application.getCurrentActivity().runOnUiThread(this::hideLoading);
//                            if (isCreateTable.get() || currentTableNumber.get() == null || currentTableNumber.get().isEmpty()){
//                                application.getCurrentActivity().runOnUiThread(() -> {
//                                    if (application.getCurrentActivity() instanceof TableActivity){
//                                        ((TableActivity)application.getCurrentActivity()).setTableCreateEmpty();
//                                    }
//                                });
//                            }
//                            String jsonTable = ApiModelUtils.GSON.toJson(message.getData());
//                            ResponseWrapper<TableModel> responseTable = ApiModelUtils.GSON.fromJson(jsonTable, new TypeToken<ResponseWrapper<TableModel>>(){}.getType());
//                            if (Storage.isImBiss){
//                                application.getCurrentActivity().runOnUiThread(() -> {
//                                    Intent intent = new Intent();
//                                    intent.putExtra(Constants.TABLE_MODEL, responseTable.getData());
//                                    if (application.getCurrentActivity() instanceof TableActivity){
//                                        application.getCurrentActivity().setResult(Constants.RESULT_FASTFOOD, intent);
//                                        application.getCurrentActivity().finish();
//                                    }
//                                });
//                            }
//                            else {
//                                application.getCurrentActivity().runOnUiThread(() -> {
//                                    Intent intent = new Intent(application.getCurrentActivity(), TableDetailActivity.class);
//                                    intent.putExtra(Constants.TABLE_MODEL, responseTable.getData());
//                                    if (application.getCurrentActivity() instanceof TableActivity){
//                                        ((TableActivity)application.getCurrentActivity()).activityResultLauncher.launch(intent);
//                                    }
//                                });
//                            }
//                            break;
//                        case SubCommand.CMD_MPOS_CHECK_TABLE_NUMBER:
//                            application.getCurrentActivity().runOnUiThread(this::hideLoading);
//                            String jsonCheckTable = ApiModelUtils.GSON.toJson(message.getData());
//                            ResponseWrapper<TableCheckResponse> responseCheckTable = ApiModelUtils.GSON.fromJson(jsonCheckTable, new TypeToken<ResponseWrapper<TableCheckResponse>>(){}.getType());
//                            application.getCurrentActivity().runOnUiThread(() -> {
//                                if (currentTableNumber.get() == null || currentTableNumber.get().isEmpty()) {
//                                    tableDetail.set(new TableCheckResponse());
//                                } else {
//                                    tableDetail.set(responseCheckTable.getData());
//                                }
//                            });
//                            if (tableDetail.get() != null
//                                    && tableDetail.get().getTables() != null
//                                    && tableDetail.get().getTables().getEmployeeOwner() != null
//                                    && !tableDetail.get().getTables().getEmployeeOwner().equals(fullName.get())
//                                    && !((TableActivity)application.getCurrentActivity()).checkPermission(Permission.EMPL_ALLOW_SERVE_OTHER_TABLE))
//                            {
//                                application.getCurrentActivity().runOnUiThread(() -> {
//                                    isHavePermission.set(false);
//                                });
//                                return;
//                            } else {
//                                application.getCurrentActivity().runOnUiThread(() -> {
//                                    isHavePermission.set(true);
//                                });
//                            }
//
//                            // check allow to navigate to table detail
//                            application.getCurrentActivity().runOnUiThread(() -> {
//                                if (tableDetail.get() == null || tableDetail.get().getTables() == null ||
//                                        tableDetail.get().getTables().getEmployeeControl() == null) {
//                                    canGoToTableDetail.set(true);
//                                    return;
//                                }
//                                if (tableDetail.get().getTables().getEmployeeOwner() != null &&
//                                        !tableDetail.get().getTables().getEmployeeOwner().equals(fullName.get())
//                                        && !((TableActivity)application.getCurrentActivity()).checkPermission(Permission.EMPL_ALLOW_SERVE_OTHER_TABLE)) {
//                                    canGoToTableDetail.set(false);
//                                    return;
//                                }
//                                // people others control table
//                                if ((tableDetail.get().getTables().getEmployeeControl() != null && !tableDetail.get().getTables().getEmployeeControl().equals(fullName.get()))
//                                ) {
//                                    canGoToTableDetail.set(false);
//                                }
//                                else {
//                                    if (tableDetail.get().getTables().getIsPayment() != null && tableDetail.get().getTables().getIsPayment() == 2) {
//                                        // check permission if people control that table when isPayment = 2
//                                        if (tableDetail.get().getTables().getEmployeeControl() != null
//                                                && tableDetail.get().getTables().getEmployeeControl().equals(fullName.get())) {
//                                            canGoToTableDetail.set(true);
//                                        }
//                                        else {
//                                            canGoToTableDetail.set(false);
//                                        }
//                                    }
//                                    else {
//                                        canGoToTableDetail.set(true);
//                                    }
//                                }
//                            });
//                            break;
//                        default:
//                            break;
//
//                    }
//                    break;
//                case Command.PROCESS_HASH: {
//                    switch (message.getSubCmd()) {
//                        case SubCommand.CMD_MPOS_HASH_MENU:
//                            String menuHash = message.getData().toString();
//                            if(checksumMenu.get() != null && !checksumMenu.get().equals(menuHash)){
//                                application.getCurrentActivity().runOnUiThread(this::getListMenu);
//                            }
//                            checksumMenu.set(menuHash);
//                            break;
//                        case SubCommand.CMD_MPOS_HASH_GROUP_FOOD:
//                            String groupFoodHash = message.getData().toString();
//                            if(checksumGroupFood.get() != null && !checksumGroupFood.get().equals(groupFoodHash)){
//                                application.getCurrentActivity().runOnUiThread(this::getListGroupFood);
//                            }
//                            checksumGroupFood.set(groupFoodHash);
//                            break;
//                        case SubCommand.CMD_MPOS_HASH_GROUP_FOOD_MENU:
//                            String groupFoodMenuHash = message.getData().toString();
//                            if(checksumGroupFoodMenu.get() != null && !checksumGroupFoodMenu.get().equals(groupFoodMenuHash)){
//                                application.getCurrentActivity().runOnUiThread(this::getListGroupFoodMenu);
//                            }
//                            checksumGroupFoodMenu.set(groupFoodMenuHash);
//                            break;
//                    }
//                    break;
//                }
                default:
                    break;
            }
        }else {
            application.getCurrentActivity().runOnUiThread(this::hideLoading);
        }
    }
}
