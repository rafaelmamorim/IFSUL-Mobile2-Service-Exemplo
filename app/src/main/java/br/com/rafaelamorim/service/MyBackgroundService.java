package br.com.rafaelamorim.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Handler;
import android.widget.Toast;

public class MyBackgroundService extends Service {

    private static final int tempoEspera = 5000;

    private Handler handler;
    private Runnable logTask;

    @Override
    public void onCreate() {
        super.onCreate();
        
        handler = new Handler() ;
        
        logTask = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyBackgroundService.this, "Background Service em funcionamento", Toast.LENGTH_SHORT).show();
                Log.i("MyBackgroundService", "Mensagem do MyBackgroundService - " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
                handler.postDelayed(this, tempoEspera);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(logTask);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Remover a tarefa de log da fila de execução
        handler.removeCallbacks(logTask);

        Log.i("MyBackgroundService", "Serviço MyBackgroundService parado");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
