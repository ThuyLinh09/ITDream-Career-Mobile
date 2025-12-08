package graduate.itdreams.android;





import static graduate.itdreams.android.data.socket.scarlet.websocket.okhttp.OkHttpClientUtils.newWebSocketFactory;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;


import graduate.itdreams.android.data.socket.scarlet.websocket.lifecycle.android.AndroidLifecycle;
import graduate.itdreams.android.data.socket.scarlet.websocket.lifecycle.android.LifecycleOwnerResumedLifecycle;
import graduate.itdreams.android.data.socket.scarlet.websocket.messageadapter.gson.GsonMessageAdapter;
import graduate.itdreams.android.data.socket.scarlet.websocket.streamadapter.rxjava2.RxJava2StreamAdapterFactory;
import com.tinder.scarlet.Scarlet;
import com.tinder.scarlet.WebSocket;
import com.tinder.scarlet.retry.ExponentialWithJitterBackoffStrategy;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import graduate.itdreams.android.constant.Constants;
import graduate.itdreams.android.data.socket.Command;
import graduate.itdreams.android.data.socket.KittyRealtimeEvent;
import graduate.itdreams.android.data.socket.KittyService;
import graduate.itdreams.android.data.socket.dto.App;
import graduate.itdreams.android.data.socket.dto.Message;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import lombok.Getter;
import lombok.Setter;
import graduate.itdreams.android.di.component.AppComponent;
import graduate.itdreams.android.di.component.DaggerAppComponent;
import graduate.itdreams.android.others.MyTimberDebugTree;
import graduate.itdreams.android.utils.DialogUtils;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

public class MVVMApplication extends Application implements LifecycleObserver {
    @Setter
    @Getter
    private AppCompatActivity currentActivity;

    @Getter
    private AppComponent appComponent;
    private Boolean inBackground;
    private KittyService kittyService;
    private Scarlet scarletInstance;
    LifecycleOwnerResumedLifecycle lifecycle;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Message lastMessage;
    private Disposable disposablePingSocket;


    @Override
    public void onCreate() {
        super.onCreate();

//        // Enable firebase log
//        FirebaseCrashlytics firebaseCrashlytics = FirebaseCrashlytics.getInstance();
//        firebaseCrashlytics.setCrashlyticsCollectionEnabled(true);
//
//
//        if (BuildConfig.DEBUG) {
//            Timber.plant(new MyTimberDebugTree());
//        }else{
//            Timber.plant(new MyTimberReleaseTree(firebaseCrashlytics));
//        }

        appComponent = DaggerAppComponent.builder()
                .application(this)
                .build();
        appComponent.inject(this);

        // Init Toasty
        Toasty.Config.getInstance()
                .allowQueue(false)
                .apply();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
//        insertMock();
//        startOrderSchedule();
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        //App in background
        Timber.d("APP IN BACKGROUND");
        inBackground = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        // App in foreground
        Timber.d("APP IN FOREGROUND");
        inBackground = false;
    }


    public void getUser(){
        appComponent.getRepository().getRoomService().userDao().loadAll()
                .subscribeOn(Schedulers.io())
                .subscribe();
    }


    public PublishSubject<Integer> showDialogNoInternetAccess(){
        final PublishSubject<Integer> subject = PublishSubject.create();
        currentActivity.runOnUiThread(() ->
                DialogUtils.dialogConfirm(currentActivity, currentActivity.getResources().getString(R.string.newtwork_error),
                        currentActivity.getResources().getString(R.string.newtwork_error_button_retry),
                        (dialogInterface, i) -> subject.onNext(1), currentActivity.getResources().getString(R.string.newtwork_error_button_exit),
                        (dialogInterface, i) -> System.exit(0))
        );
        return subject;
    }
    @NonNull
    private Disposable ObservablePingSocket() {
        return Observable.interval(30, 30, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .subscribe(
                        o -> {
                            // Check token
                            String token = appComponent.getRepository().getSharedPreferences().getToken();
                            if (token != null && !token.isEmpty() && !token.equals("NULL") && !token.equals("null")) {
                                Message message = new Message();
                                message.setCmd(Command.COMMAND_CLIENT_PING);
                                message.setData(new App(Constants.APP));
                                message.setToken(appComponent.getRepository().getSharedPreferences().getToken());
                                kittyService.request(message);
                            }
                        },
                        Timber::e
                );
    }

    void createKittyService() {
        kittyService = scarletInstance.create(KittyService.class);
    }
    @SuppressLint("CheckResult")
    public void createSocket(String url){
        Timber.d(url);
        createScarletInstance(url);
        createKittyService();
        compositeDisposable.clear();
        observeWebSocketEvent();
    }

    public void sendMessage(Message message){
        Timber.d(message.toString());
        Timber.tag("WS CMD").i(currentActivity.getClass().getName());
        kittyService.request(message);
        if (message.getCmd().equals(Command.COMMAND_CLIENT_PING)) return;
        if (message.getCmd().equals(Command.PROCESS_HASH)) return;
//        if (message.getSubCmd() != null && message.getSubCmd().equals(
//                SubCommand.CMD_MPOS_GET_LIST_TABLE)) return;
        lastMessage = message;
    }
    public void deleteSocket(){
        if(lifecycle != null){
            lifecycle.closeConnection();
        }
        scarletInstance = null;
        kittyService = null;
        compositeDisposable.clear();
    }
    private void startPing(){
        disposablePingSocket = ObservablePingSocket();
        compositeDisposable.add(disposablePingSocket);
    }

    public void stopPing(){
        if (disposablePingSocket == null) return;
        disposablePingSocket.dispose();
        compositeDisposable.remove(disposablePingSocket);
    }
    void createScarletInstance(String url) {
        lifecycle = AndroidLifecycle.ofLifecycleOwnerForeground(this, ProcessLifecycleOwner.get()).getFirst();
        scarletInstance = new Scarlet.Builder().webSocketFactory(
                        newWebSocketFactory(new OkHttpClient.Builder()
                                .addInterceptor(
                                        new HttpLoggingInterceptor().setLevel(
                                                HttpLoggingInterceptor.Level.BODY))
                                .build(), url))
                .lifecycle(lifecycle)
                .addMessageAdapterFactory(new GsonMessageAdapter.Factory())
                .addStreamAdapterFactory(new RxJava2StreamAdapterFactory())
                .backoffStrategy(new ExponentialWithJitterBackoffStrategy(500L,10000L,new Random()))
                .build();
    }
    public void observeWebSocketEvent() {
        Flowable<WebSocket.Event> share = kittyService.observeWebSocketEvent()
                .observeOn(Schedulers.io())
                .share();
        compositeDisposable.add(share.subscribe(o -> {
            Timber.d(o.toString());
            KittyRealtimeEvent kittyRealtimeEvent = (KittyRealtimeEvent) currentActivity;
            if (kittyRealtimeEvent == null) return;
            if (o instanceof WebSocket.Event.OnConnectionOpened) {
                Timber.tag("State-socket").d("OnConnectionOpened");
                kittyRealtimeEvent.onConnectionOpened();
//                sendLastMessage();
                startPing();
            } else if (o instanceof WebSocket.Event.OnConnectionClosed) {
                Timber.tag("State-socket").d("OnConnectionClosed");
                kittyRealtimeEvent.onConnectionClosed();
                stopPing();
            } else if (o instanceof WebSocket.Event.OnConnectionClosing) {
                Timber.tag("State-socket").d("OnConnectionClosing");
                kittyRealtimeEvent.onConnectionClosing();
            } else if (o instanceof WebSocket.Event.OnConnectionFailed) {
                Timber.tag("State-socket").d("OnConnectionFailed");
                kittyRealtimeEvent.onConnectionFailed();
                stopPing();
            }
        }));

        compositeDisposable.add(kittyService.message()
                        .subscribeOn(Schedulers.io())
                        .subscribe(o -> {
                            KittyRealtimeEvent kittyRealtimeEvent = (KittyRealtimeEvent) currentActivity;
                            if (kittyRealtimeEvent == null) return;
                            if (o != null && Objects.equals(o.getCmd(), Command.COMMAND_CLIENT_VERIFY_TOKEN)) {
                                if (o.getResponseCode() == 400) {
                                    currentActivity.runOnUiThread(()->{
                                        Toasty.error(currentActivity, R.string.socket_error).show();
                                    });
                                    appComponent.getRepository().getSharedPreferences().setToken(null);
                                }
                            }
//                            if (o!= null && Objects.equals(o.getCmd(),Command.CMD_TABLE_CHANGED) && currentActivity instanceof TableActivity) {
//                        ((TableActivity)currentActivity).showTableChange();
//                                ((TableActivity)currentActivity).resumeTables(true);
//                            }
                            if (o != null && lastMessage != null && o.getCmd().equals(lastMessage.getCmd())) {
                                if (o.getSubCmd() != null && o.getSubCmd().equals(lastMessage.getSubCmd())) {
                                    Timber.d("CLEAR LAST MESSAGE");
                                    this.lastMessage = null;
                                }
                            }
                            // Check if the message is lock device
                            if (o != null && Objects.equals(o.getCmd(), Command.COMMAND_LOCK_DEVICE)) {
                                // Check if the current activity is login activity
//                                if (currentActivity != null && currentActivity instanceof LoginActivity)
//                                    return;
//                                currentActivity.runOnUiThread(this::lockDevice);
                                return;
                            }
                            kittyRealtimeEvent.onMessageReceived(o);
                        })
        );
    }
}
