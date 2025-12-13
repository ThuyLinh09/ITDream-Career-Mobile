package graduate.itdreams.android.ui.main;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import graduate.itdreams.android.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.socket.dto.Message;
import graduate.itdreams.android.databinding.ActivityMainBinding;
import graduate.itdreams.android.di.component.ActivityComponent;
import graduate.itdreams.android.ui.base.activity.BaseActivity;
import graduate.itdreams.android.ui.main.account.AccountFragment;
import graduate.itdreams.android.ui.main.account.AccountUnLoginFragment;

import graduate.itdreams.android.ui.main.achievement.AchievementFragment;
import graduate.itdreams.android.ui.main.home.HomeFragment;
import graduate.itdreams.android.ui.main.login.LoginActivity;
import graduate.itdreams.android.ui.main.notification.NotificationFragment;


public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {
    private Fragment active;
    private FragmentManager fm;
    private HomeFragment homeFragment;
    private NotificationFragment cvProfileFragment;
    private AchievementFragment topCommentFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;
    private AccountUnLoginFragment accountUnLoginFragment;
    private static final String HOME = "HOME";
    private static final String CV_PROFILE = "CV_PROFILE";
    private static final String ACHIEVEMENT = "TOP_COMMENT";
    private static final String NOTIFICATION = "NOTIFICATION";
    private static final String ACCOUNT = "ACCOUNT";
    private static final String ACCOUNT_UN_LOGIN = "ACCOUNT_UN_LOGIN";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // lay ssid tu share ref
        super.onCreate(savedInstanceState);
        viewBinding.setA(this);
        viewBinding.setVm(viewModel);
        initFragments();
        viewModel.notification.observe(this, response -> {
            showNotification(response.getMessage());
        });
        viewBinding.bottomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    handleFragment(HOME);
                    return true;
                case R.id.top_comment:
                    handleFragment(ACHIEVEMENT);
                    return true;
                case R.id.notification:
                    handleFragment(NOTIFICATION);
                    return true;
                case R.id.account:
                    if (viewModel.isLogin()) {
                        handleFragment(ACCOUNT);
                    } else {
                        handleFragment(ACCOUNT_UN_LOGIN);
                    }
                    return true;
            }
            return false;
        });


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (!Environment.isExternalStorageManager()){
//                Intent intent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
//                startActivityForResult(intent, 1);
//            } else {
//                viewModel.getApplication().getUser();
//            }
//        } else {
//            viewModel.getApplication().getUser();
//        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {
        if (intent == null) return;

        if (intent.getBooleanExtra("open_notification", false)) {
            viewBinding.bottomNav.setSelectedItemId(R.id.notification);
            handleFragment(NOTIFICATION);
        }
    }
    private void showNotification(String message) {
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "socket_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(channelId, "Socket Messages",
                            NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("open_notification", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notificate_green)
                        .setContentTitle("Phản hồi bài mô phỏng")
                        .setContentText(message)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
    private void initFragments() {
        homeFragment = new HomeFragment();
        fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.fragment_container, homeFragment, HOME)
                .commit();
        active = homeFragment;
    }

    public void handleFragment(String tag) {
        if (fm == null) fm = getSupportFragmentManager();

        if (homeFragment == null) homeFragment = new HomeFragment();
        if (cvProfileFragment == null) cvProfileFragment = new NotificationFragment();
        if (topCommentFragment == null) topCommentFragment = new AchievementFragment();
        if (notificationFragment == null) notificationFragment = new NotificationFragment();
        if (accountFragment == null) accountFragment = new AccountFragment();
        if (accountUnLoginFragment == null) accountUnLoginFragment = new AccountUnLoginFragment();

        Fragment target = null;
        switch (tag) {
            case HOME:
                target = homeFragment;
                break;
            case CV_PROFILE:
                target = cvProfileFragment;
                break;
            case ACHIEVEMENT:
                target = topCommentFragment;
                break;
            case NOTIFICATION:
                target = notificationFragment;
                break;
            case ACCOUNT:
                target = accountFragment;
                break;
            case ACCOUNT_UN_LOGIN:
                target = accountUnLoginFragment;
                break;
        }

        if (target == null || active == target) return;

        if (!target.isAdded()) {
            fm.beginTransaction()
                    .hide(active)
                    .add(R.id.fragment_container, target, tag)
                    .commit();
        } else {
            fm.beginTransaction()
                    .hide(active)
                    .show(target)
                    .commit();
        }
        active = target;
    }

    public void navigateToLogin() {
        Intent it = new Intent(this, LoginActivity.class);
        startActivity(it);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra("from_login", false)) {
            handleFragment(HOME);
            getIntent().removeExtra("from_login");
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }
    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);
    }

    @Override
    public void onMessageReceived(Message message) {
        super.onMessageReceived(message);
    }

    @Override
    public void onConnectionClosed() {

    }

    @Override
    public void onConnectionClosing() {

    }
}
