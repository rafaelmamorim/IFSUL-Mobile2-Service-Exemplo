package br.com.rafaelamorim.service;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MyBoundService extends Service {

    private final IBinder binder = new MyBinder();
    private OnTimeUpdateListener timeUpdateListener;
    private final Handler handler = new Handler();
    private Runnable runnable;

    public class MyBinder extends Binder {
        MyBoundService getService() {
            Log.i("MyBoundService.MyBinder", "Aqui no MyBinder");
            return MyBoundService.this;
        }
    }

    // Interface para enviar a hora para a MainActivity
    public interface OnTimeUpdateListener {
        void onTimeUpdate(String time);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        handler.removeCallbacks(runnable);
        return super.onUnbind(intent);
    }

    // Método para começar a atualizar a hora
    public void startUpdatingTime() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (timeUpdateListener != null) {
                    String currentTime = getCurrentTime();
                    timeUpdateListener.onTimeUpdate(currentTime);
                }
                handler.postDelayed(this, 1000); // Atualiza a cada 1 segundo
            }
        };
        handler.post(runnable);
    }

    public void setTimeUpdateListener(OnTimeUpdateListener listener) {
        this.timeUpdateListener = listener;
    }

    private String getCurrentTime() {
        return java.text.DateFormat.getTimeInstance().format(new java.util.Date());
    }
}
