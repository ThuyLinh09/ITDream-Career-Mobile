package graduate.itdreams.android.data.socket;


import graduate.itdreams.android.data.socket.dto.Message;

public interface KittyRealtimeEvent {
    void onMessageReceived(Message message);
    void onConnectionClosed();
    void onConnectionClosing();
    void onConnectionFailed();
    void onConnectionOpened();
}
