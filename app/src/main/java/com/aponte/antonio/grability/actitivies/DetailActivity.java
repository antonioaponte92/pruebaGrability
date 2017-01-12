package com.aponte.antonio.grability.actitivies;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.aponte.antonio.grability.R;
import com.aponte.antonio.grability.models.Aplicaciones;
import com.bumptech.glide.Glide;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends BaseActivity {
    @Bind(R.id.tituloDetalle)       TextView titulo;
    @Bind(R.id.companiaDetalle)     TextView compania;
    @Bind(R.id.categoryDetalle)     TextView category;
    @Bind(R.id.summaryDetalle)      TextView summary;
    @Bind(R.id.fotoDetalle)         ImageView imagen;
    @Bind(R.id.botonDetalle)        Button botonInstall;

    private int idApp;
    private Aplicaciones aplicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.detalleApp));
        establecerPantalla();
        leerAplicacion();
        mostrarDatos();
    }

    private void leerAplicacion() {
        iniciarRealm();
        idApp = getIntent().getIntExtra("idApp",0);
        aplicacion = realm.where(Aplicaciones.class).equalTo("id",idApp).findFirst();
    }

    private void mostrarDatos() {
        Glide.with(this).load(aplicacion.getUrlIm()).into(imagen);
        titulo.setText(aplicacion.getName());
        compania.setText(aplicacion.getCompany());
        category.setText(aplicacion.getCategory());
        summary.setText(aplicacion.getSummary());
        if (aplicacion.getPrecio().equals("Gratis")){
            botonInstall.setText(getString(R.string.install));
        }else{
            botonInstall.setText(aplicacion.getPrecio());
        }

    }

    @OnClick(R.id.botonDetalle)
    void Comprar(){
        Toast.makeText(DetailActivity.this,getString(R.string.install),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.layout.fade_in,R.layout.fade_out);
    }
}
