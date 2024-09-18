package br.com.rafaelamorim.service;


// MyBoundService.java
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyBoundService extends Service {

    private final IBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        MyBoundService getService() {
            return MyBoundService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("MyBoundService.onUnbind","Desconectando o binder do serviço");
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public String getHelloMessage() {
        return "Hello from Bound Service!";
    }
}
