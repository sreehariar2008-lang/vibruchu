package com.vibruchu.app;

import android.app.*;
import android.content.*;
import android.os.*;
import androidx.core.app.NotificationCompat;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

public class VibrationService extends Service {

    private WebSocketClient webSocketClient;
    private Vibrator vibrator;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {
        super.onCreate();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Vibruchu:WakeLock");
        wakeLock.acquire();

        startForegroundService();
        connectWebSocket();
    }

    private void startForegroundService() {
        String channelId = "vibruchu_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Vibruchu Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            getSystemService(NotificationManager.class)
                    .createNotificationChannel(channel);
        }

        Notification notification =
                new NotificationCompat.Builder(this, channelId)
                        .setContentTitle("Vibruchu Running")
                        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                        .build();

        startForeground(1, notification);
    }

    private void connectWebSocket() {
        try {
            URI uri = new URI("wss://vibr-r7w1.onrender.com");

            webSocketClient = new WebSocketClient(uri) {

                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    send("{\"type\":\"register\",\"userId\":\"receiver1\"}");
                }

                @Override
                public void onMessage(String message) {
                    if (message.contains("vibrate")) {
                        vibrateNow();
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {}

                @Override
                public void onError(Exception ex) {}
            };

            webSocketClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void vibrateNow() {
        long[] pattern = {0, 300, 200, 300};

        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
        } else {
            vibrator.vibrate(pattern, -1);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
