package graduate.itdreams.android.ui.base.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;
import graduate.itdreams.android.MVVMApplication;
import graduate.itdreams.android.R;
import graduate.itdreams.android.constant.Constants;
import graduate.itdreams.android.data.socket.KittyRealtimeEvent;
import graduate.itdreams.android.data.socket.dto.Message;
import graduate.itdreams.android.di.component.ActivityComponent;
import graduate.itdreams.android.di.component.DaggerActivityComponent;
import graduate.itdreams.android.di.module.ActivityModule;
import graduate.itdreams.android.utils.DialogUtils;

public abstract class BaseActivity<B extends ViewDataBinding, V extends BaseViewModel> extends AppCompatActivity implements KittyRealtimeEvent {

    protected B viewBinding;

    @Inject
    protected V viewModel;

    @Inject
    protected Context application;

    @Named("access_token")
    @Inject
    protected String token;

    @Named("device_id")
    @Inject
    protected String deviceId;

    private Dialog progressDialog;
    // Listen all action from local
    private BroadcastReceiver globalApplicationReceiver;
    private IntentFilter filterGlobalApplication;
    Dialog socketDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        performDependencyInjection(getBuildComponent());
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN        // ẩn status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        performDataBinding();
        updateCurrentAcitivity();

        viewModel.setToken(token);
        viewModel.setDeviceId(deviceId);
        viewModel.mIsLoading.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback(){

            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if(((ObservableBoolean)sender).get()){
                    showProgressbar(getResources().getString(R.string.msg_loading));
                }else{
                    hideProgress();
                }
            }
        });
        viewModel.mErrorMessage.observe(this, toastMessage -> {
            if(toastMessage!=null){
                toastMessage.showMessage(getApplicationContext());
            }
        });
//        viewModel.progressBarMsg.observe(this, progressBarMsg ->{
//            if (progressBarMsg != null){
//                changeProgressBarMsg(progressBarMsg);
//            }
//        });
        filterGlobalApplication = new IntentFilter();
        filterGlobalApplication.addAction(Constants.ACTION_EXPIRED_TOKEN);
        globalApplicationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action==null){
                    return;
                }
                if (action.equals(Constants.ACTION_EXPIRED_TOKEN)){
                    doExpireSession();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.hideLoading();
        LocalBroadcastManager.getInstance(this).registerReceiver(globalApplicationReceiver, filterGlobalApplication);
        updateCurrentAcitivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(globalApplicationReceiver);
    }

    public abstract @LayoutRes int getLayoutId();

    public abstract int getBindingVariable();

    public void doExpireSession() {
        //implement later

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void performDataBinding() {
        viewBinding = DataBindingUtil.setContentView(this, getLayoutId());
        viewBinding.setVariable(getBindingVariable(), viewModel);
        viewBinding.executePendingBindings();
    }

    public void showProgressbar(String msg) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        progressDialog = DialogUtils.createDialogLoading(this);
        progressDialog.show();

        int size = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());

        Window window = progressDialog.getWindow();
        if (window != null) {
            window.setLayout(size, size);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.05f;
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setAttributes(lp);
        }
    }

//    public void changeProgressBarMsg(String msg){
//        if (progressDialog != null){
//            ((TextView)progressDialog.findViewById(R.id.progressbar_msg)).setText(msg);
//        }
//    }

    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    private ActivityComponent getBuildComponent() {
        return DaggerActivityComponent.builder()
                .appComponent(((MVVMApplication)getApplication()).getAppComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }

    public abstract void performDependencyInjection(ActivityComponent buildComponent);

    private void updateCurrentAcitivity(){
        MVVMApplication mvvmApplication = (MVVMApplication)application;
        mvvmApplication.setCurrentActivity(this);
    }

    public boolean showHeader(){
        return false;
    }

    ObservableField<String> leftTitle;
    ObservableField<String> centerTitle;
    public void setCenterTitle(String msg){
        if (centerTitle == null){
            centerTitle = new ObservableField<>(msg);
        } else {
            centerTitle.set(msg);
        }
    }
    public void setLeftTitle(String msg){
        if (leftTitle == null){
            leftTitle = new ObservableField<>(msg);
        } else {
            leftTitle.set(msg);
        }
    }
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        clearFocusFromAllEditTexts(viewBinding.getRoot());
    }
    public void clearFocusFromAllEditTexts(View view) {
        if (view instanceof EditText) {
            view.clearFocus();
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                clearFocusFromAllEditTexts(child);
            }
        }
    }
    @Override
    public void onConnectionFailed() {
        runOnUiThread(()->{
            viewModel.hideLoading();
            if(socketDialog != null){
                if(!socketDialog.isShowing()){
                    socketDialog.show();
                }
            }else {
//                showConnectionError();
            }
        });
    }
    @Override
    public void onMessageReceived(Message message) {
        viewModel.messageReceived(message);
    }
    @Override
    public void onConnectionOpened() {
        runOnUiThread(()->{
            viewModel.hideLoading();
            if(socketDialog != null && socketDialog.isShowing()){
                socketDialog.dismiss();
            }
        });
    }
    protected void showConnectionError(){
        socketDialog = new Dialog(this);
        socketDialog.setCanceledOnTouchOutside(false);
        socketDialog.show();

    }
}
