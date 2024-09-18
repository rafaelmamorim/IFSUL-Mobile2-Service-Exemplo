package br.com.rafaelamorim.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyShortService extends Service {

    private static final String TAG = "MyShortService";

    private static final int tempoEspera = 10000;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Serviço curto iniciado");
        Toast.makeText(this, "Short service iniciado", Toast.LENGTH_SHORT).show();
        // Simulação de uma tarefa rápida
        for (int i = 0; i < 10; i++) {
            Log.i(TAG, "Executando tarefa curta... " + i);
            try {
                Thread.sleep(tempoEspera);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Parar o serviço após a conclusão da tarefa e notificar o usuário
        stopSelf();
        Toast.makeText(this, "Short service finalizado", Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
