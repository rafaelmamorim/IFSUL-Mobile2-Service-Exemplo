package br.com.rafaelamorim.service;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyForegroundService extends Service {

    private static final String CHANNEL_ID = "4567890";
    private static final int NOTIFICATION_ID = 1;
    private static final int tempoEspera = 10000;
    private NotificationCompat.Builder notificationBuilder;


    private boolean isRunning = false; // Variável para controlar a execução da thread


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning){
            new Thread(() -> {
                while (isRunning) {
                    try {
                        Log.i("MyForegroundService", "Registrando log do MyForegroundService - " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                        Thread.sleep(tempoEspera);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        stopForeground(true);
                        stopSelf();
                        break;
                    }
                }
            }).start();
            isRunning = true;

            // Criar e exibir a notificação
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentTitle("Meu Foreground Service")
                    .setContentText("Executando tarefa em segundo plano")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent);

            createNotificationChannel(); // Criar canal de notificação (Android 8.0 Oreo e superior)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, notificationBuilder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
            } else {
                startForeground(NOTIFICATION_ID, notificationBuilder.build());
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isRunning = false;

        // Remover a notificação
        if (notificationBuilder != null) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.cancel(NOTIFICATION_ID);
        }

        // Remover a notificação
        stopForeground(true); // Remover o serviço do modo foreground
        stopSelf(); // Parar o serviço
        Log.i("MyForegroundService", "Parando o MyForegroundService");
        Toast.makeText(this, "Foreground Service foi parado", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "My Foreground Service Channel", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
