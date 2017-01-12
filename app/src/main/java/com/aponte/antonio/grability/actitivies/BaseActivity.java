package com.aponte.antonio.grability.actitivies;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import com.aponte.antonio.grability.R;
import com.aponte.antonio.grability.interfaces.AplicacionesInterface;
import com.aponte.antonio.grability.models.Aplicaciones;
import com.aponte.antonio.grability.models.Entry;
import com.aponte.antonio.grability.models.Object;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import io.realm.Realm;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Antonio on 7/1/2017.
 * En esta clase se incluyen todos los métodos comunes que deben tener todas las actividades del
 * proyecto
 */
public class BaseActivity extends AppCompatActivity {
    public List<Entry> entries;
    Realm realm;

    /**
     * Método que crea la instancia necesaria para poder usar Realm
     */
    public void iniciarRealm(){
        Realm.init(this);
        realm = Realm.getDefaultInstance();
    }

    /**
     * @return true si es teléfono, false si es tablet
     */
    public boolean esTelefono() {
        boolean ret = getResources().getBoolean(R.bool.esTelefono);
        return ret;
    }

    /**
     * si esTelefono, se establece que la pantalla tendrá una orientación vertical, sino será
     * horizontal
     */
    public  void establecerPantalla() {
        boolean aux = esTelefono();
        if (aux) {
            super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    /**
     * Método para llenar la base de datos local
     *
     * Nota: revisar la documentación de la clase Aplicaciones incluida en el paquete modelos
     */
    public void poblarDB() {
        iniciarRealm();
        realm.beginTransaction();
        realm.deleteAll();//limpio la base de datos
        realm.commitTransaction();
        for (int i=0;i<entries.size();i++){
            realm.beginTransaction();
            Aplicaciones aplicaciones = new Aplicaciones();
            aplicaciones.setId(i);
            aplicaciones.setName(entries.get(i).getImName().getLabel());//título de la app
            aplicaciones.setSummary(entries.get(i).getSummary().getLabel());
            aplicaciones.setCategory(entries.get(i).getCategory().getAttributes().getTerm());//categoría
            aplicaciones.setCompany(entries.get(i).getImArtist().getLabel());//compañía/artista
            aplicaciones.setCurrency(entries.get(i).getImPrice().getAttributes().getCurrency());//moneda
            aplicaciones.setPrice(entries.get(i).getImPrice().getAttributes().getAmount());//precio
            aplicaciones.setUrl(entries.get(i).getLink().getAttributes().getHref());//enlace real a la tienda
            aplicaciones.setUrlIm(entries.get(i).getImImage().get(2).getLabel());//enlace de la imagen
            realm.copyToRealm(aplicaciones);
            realm.commitTransaction();
        }

    }

    /**
     * método para leer los entries del servicio web, la información de todas las aplicaciones
     * se guarda en "entries"
     */
    public void getFeed(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AplicacionesInterface aplicacionesInterface = retrofit.create(AplicacionesInterface.class);
        aplicacionesInterface.getApps().enqueue(new Callback<Object>() {
            @Override
            public void onResponse(retrofit2.Call<Object> call, Response<Object> response) {
                entries = response.body().getFeed().getEntry();
                poblarDB();
                startActivity(new Intent(BaseActivity.this,MainActivity.class));
                finish();
            }
            @Override
            public void onFailure(retrofit2.Call<Object> call, Throwable t) {
                final Intent intent = new Intent(BaseActivity.this,MainActivity.class);
                intent.putExtra("errorRetro",true);
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
        });
    }
}
