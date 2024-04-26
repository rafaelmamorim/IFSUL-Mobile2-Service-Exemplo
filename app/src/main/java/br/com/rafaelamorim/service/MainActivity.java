package br.com.rafaelamorim.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                Toast.makeText(this, "Foreground Service está em execução", Toast.LENGTH_SHORT).show();
                Log.i("MainActivity.executaForegroundService", "O serviço " + serviceName + " já está em execução");
            }
            return true;
        }
        return false;
    }
}
