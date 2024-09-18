package br.com.rafaelamorim.service;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;

public class MainActivity extends AppCompatActivity {


    MyBoundService myService;
    boolean isBound = false;
    boolean isServiceRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the service
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
        if (isServiceRunning){
            pararMyBackgroundService(this.getCurrentFocus());
        }
    }

    /* SHORT SERVICE
    *
    * Serviço rápido, com duração máxima de 1 minuto
    * Não podem ser configurados como serviço de primeiro plano
    * Não pode iniciar outro serviço em primeiro plano
    * O aplicativo exibe mensagem de ANR se tempo limite for atingido sem o encerramento do short service
    *
    */
    public void executaShortService(View v) {
        if (checkRunningService(MyShortService.class.getName())) return;
        Intent intent = new Intent(MainActivity.this, MyShortService.class);
        startService(intent);
    }

    /* FOREGROUND SERVICE
    *
    * - Executa tarefa no background do SO, mas avisa o usuário através de uma notificação
    * - Deve ser utilizado para tarefas que exigem interação constante com o usuário ou que precisam ser executadas em primeiro plano, como players de mídia ou com uso de sensores do dispositivo
    * - Possui alta prioridade no sistema, garantindo execução em condições não favoráveis
    */
    public void executaForegroundService(View v) {
        if (checkRunningService(MyForegroundService.class.getName())) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        Intent foregroundServiceIntent = new Intent(this, MyForegroundService.class);
        startForegroundService(foregroundServiceIntent);
    }

    public void pararForegroundService(View v) {
        Intent foregroundServiceIntent = new Intent(this, MyForegroundService.class);
        stopService(foregroundServiceIntent);
    }

    /* BACKGROUND SERVICE
    *
    * Executa uma operação de longa duração no background
    * Não bloqueia a aplicação principal
    * Não carrega interface para o usuário
    *
    */
    public void executaMyBackgroundService(View v) {
        if (checkRunningService(MyBackgroundService.class.getName())) return;
        Intent backgroundServiceIntent = new Intent(this, MyBackgroundService.class);
        startService(backgroundServiceIntent);
    }

    public void pararMyBackgroundService(View v) {
        Intent backgroundServiceIntent = new Intent(this, MyBackgroundService.class);
        stopService(backgroundServiceIntent);
    }

    private boolean checkRunningService(String serviceName) {
        if (isServiceRunning){
            Toast.makeText(this, "Foreground Service está em execução", Toast.LENGTH_SHORT).show();
            Log.i("MainActivity.executaForegroundService", "O serviço " + serviceName + " já está em execução");
            return true;
        }
        return false;
    }


    /*
    * BOUNDSERVICE
    *
    * Tipo de serviço em Android que permite que componentes de uma aplicação se conectem a ele
    *  e interajam com suas funcionalidades. Diferente de um Started Service, que é iniciado
    *  e executado em segundo plano sem interação direta com outros componentes, um Bound Service
    *  é vinculado a um componente, como uma Activity, e permite que o componente interaja
    *  com o serviço.
    */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MyBoundService.MyBinder binder = (MyBoundService.MyBinder) service;
            myService = binder.getService();
            isBound = true;
            Log.i("MainActivity.connection", myService.getHelloMessage());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    public void executaBoundService(View v){
        // Bind to MyBoundService
        Intent intent = new Intent(this, MyBoundService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }
}
