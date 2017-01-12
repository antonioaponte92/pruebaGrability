package com.aponte.antonio.grability.actitivies;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.aponte.antonio.grability.R;
import com.aponte.antonio.grability.utils.UtilidadesGenerales;
import com.splunk.mint.Mint;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Mint.initAndStartSession(this.getApplication(), "f2e6c522"); //para llevar un control de los errores que pueda dar durante la revisión de esta prueba
        establecerPantalla();
        verificarConexion();
    }

    /**
     * verifica si hay conexión a internet; de ser positivo, sincroniza la base de datos local
     * con la data que trae el servicio web. De lo contrario, espera 5 segundos para iniciar a
     * MainActivity junto con un flag que le avisa que no hay internet
     */
    private void verificarConexion() {
        final Intent intent = new Intent(SplashActivity.this,MainActivity.class);
        if (UtilidadesGenerales.tieneInternet(this)){
            //si tiene conexión a internet, se conectará al servicio para sincronizar la base de datos local
            Log.d("AntonioSplash","Tiene internet");
            getFeed();
        }else{
            //si no tiene conexión iniciará MainActivity sin tratar de conectarse, además le mandará el estado de la red
            Log.d("AntonioSplash","No tiene internet");
            intent.putExtra("tieneInternet",false);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    startActivity(intent);
                    finish();
                }
            };
            Timer timer = new Timer();
            timer.schedule(task,5000);
        }
    }

}
