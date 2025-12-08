package graduate.itdreams.android.ui.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import graduate.itdreams.android.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Gửi token lên server để server gửi thông báo
        Log.d("FCM", "New token: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Xử lý notification
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            showNotification(title, body);
        }

        // Xử lý data message
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            // update UI hoặc LiveData nếu app mở
            Log.d("FCM", "Data message: " + data);
        }
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "default_channel_id";
        String channelName = "Default Channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notificate_green)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }
}

