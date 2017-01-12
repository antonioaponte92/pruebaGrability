package com.aponte.antonio.grability.actitivies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aponte.antonio.grability.R;
import com.aponte.antonio.grability.adapters.AppsAdapter;
import com.aponte.antonio.grability.adapters.HorizontalAdapter;
import com.aponte.antonio.grability.interfaces.AplicacionesInterface;
import com.aponte.antonio.grability.models.Aplicaciones;
import com.aponte.antonio.grability.models.Object;
import com.aponte.antonio.grability.utils.UtilidadesGenerales;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.gitonway.lee.niftynotification.lib.Configuration;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;
import java.util.ArrayList;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseActivity {
    private Effects effects;
    @Bind(R.id.swipe)           SwipeRefreshLayout refreshLayout;
    @Bind(R.id.recycler)        RecyclerView recyclerView;
    @Bind(R.id.recyclerCat)     RecyclerView categorias;
    @Bind(R.id.LinearNoData)    LinearLayout noData;
    ArrayList <Aplicaciones> lista;     //aplicaciones
    ArrayList <String> listaCat;        //categorías de aplicaciones
    AppsAdapter adapter;
    HorizontalAdapter horizontalAdapter;
    String categoriaSeleccionada;       //string que guarda la categoría seleccionada de manera global

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.logo);
        ButterKnife.bind(this);
        mostrarCategorias();                //Lleno el recylcerView horizontal con las categorías
        establecerPantalla();               //verifico si es teléfono o tablet y establezco el tipo de pantalla
        tieneInternet();                    //muestro un mensaje en el action bar si no tiene internet
        prepareRecycler();                  //le agrego el LayoutManager al recyclerView de las apps dependiendo de la pantalla
        setData(categoriaSeleccionada," "); //lleno por primera vez el listado de las apps
        prepareAdapter();                   //creo, lleno y agrego el adapter al reclyclerView

        /**
         * si se arrastra hacia abajo el listado de aplicaciones, se intentará sincronizar la base
         * de datos local con el servicio web, si no se tiene conexión a internet, se mostrará un
         * Toast avisándole al usuario
         */
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (UtilidadesGenerales.tieneInternet(MainActivity.this)){
                    volverAConsumirServicio();
                }else {
                    Toast.makeText(MainActivity.this,getString(R.string.noInternet),Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search){
            final EditText editText = new EditText(this);
            editText.setGravity(View.TEXT_ALIGNMENT_CENTER);
            new MaterialStyledDialog.Builder(this)
                    .setTitle(getString(R.string.action_search))
                    .setIcon(R.drawable.ic_search)
                    .setPositiveText(getString(R.string.ok))
                    .setNegativeText(getString(R.string.cancel))
                    .setDescription(getString(R.string.search2))
                    .setHeaderColor(R.color.colorPrimaryDark)
                    .setCustomView(editText)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    setData(categoriaSeleccionada,editText.getText().toString());
                    getSupportActionBar().setSubtitle(getString(R.string.search3)+editText.getText().toString()+"\"");
                    adapter.CambiarData(lista);
                }
            }).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * creé este método equivalente al getFeed() de BaseActivity porque no quería volver a llamar
     * a startActivity al momento de que el usuario quiera actualizar la lista de apps.
     */
    private void volverAConsumirServicio() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(getString(R.string.url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AplicacionesInterface aplicacionesInterface = retrofit.create(AplicacionesInterface.class);
        aplicacionesInterface.getApps().enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                getSupportActionBar().setTitle(getString(R.string.app_name));
                entries = response.body().getFeed().getEntry();
                poblarDB();
                setData(categoriaSeleccionada," ");
                refreshLayout.setRefreshing(false);
                adapter.CambiarData(lista);
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                refreshLayout.setRefreshing(false);
                getSupportActionBar().setTitle(getString(R.string.app_name)+" "+getString(R.string.errorRetrofit));
                Toast.makeText(MainActivity.this,getString(R.string.errorRetrofit),Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Inicio el adapter del listado de aplicaciones y utilizo el listenner de AppsAdapter para
     * comunicarle a MainActivity que se ha presionado una aplicación. Le paso el idApp a DetailActivity
     * a través del Intent para que la segunda Actividad sepa cuál aplicación mostrar.
     *
     * Usé el overridePendingTransition para darle animación al paso entre activities de una manera
     * que fuese compatible con versiones Android 4.+
     */
    private void prepareAdapter() {
        adapter = new AppsAdapter(this, lista, esTelefono(), new AppsAdapter.AdapterListener() {
            @Override
            public void onClickApp(int idApp) {
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("idApp", idApp);
                startActivity(intent);
                overridePendingTransition(R.layout.fade_in,R.layout.fade_out);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    /**
     * Define si el recyclerView de las aplicaciones mostrará una sola fila (simulando una lista) o
     * 7 filas (simulando un Grid)
     *
     * Utilicé StaggeredGridLayoutManager para poder aplicarles animaciones a ambos RecyclerView que
     * usé en MainActivity
     */
    private void prepareRecycler() {
        if (esTelefono()){
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        }else{
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(7,StaggeredGridLayoutManager.VERTICAL));
        }
    }

    /**
     * Carga el listado de categorías que soporta la aplicación. Además, utiliza el listenner creado
     * en el HorizontalAdapter para que MainActivity sea capaz de saber cuál categoría se ha
     * presionado
     */
    private void mostrarCategorias() {
        listaCat = new ArrayList<String>();
        String[] aux = getResources().getStringArray(R.array.categorias);
        for (int i = 0;i<aux.length;i++)
            listaCat.add(aux[i]);
        categoriaSeleccionada = listaCat.get(0);
        horizontalAdapter = new HorizontalAdapter(this, listaCat, new HorizontalAdapter.AdapterListenerMenu() {
            @Override
            public void onClickCat(String cat) {
                categoriaSeleccionada = cat;
                setData(cat," ");                               //creo la nueva lista de apps para esa categoria
                adapter.CambiarData(lista);                 //le notifico al adapter que su información cambió
                getSupportActionBar().setSubtitle(cat);     //cambio el subtitulo del action bar para mostrar cuál categoría está seleccionada
            }
        });
        categorias.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        categorias.setAdapter(horizontalAdapter);
    }

    /**
     * cambia el contenido del ArrayList que contiene la información de todas las aplicaciones
     *
     * @param cat contiene el nombre de la categoría que se quiere mostrar
     * @param nom contiene el nombre completo o parcial de la aplicación que se desea buscar, si este
     *            campo viene igual a " " hará la consulta de las categorías, de lo contrario
     *            hará la búsqueda por el nombre
     */
    public void setData(String cat,String nom){
        iniciarRealm();
        RealmResults<Aplicaciones> results = null;
        if (nom.equals(" ")) {
            if (cat.equals("All"))
                results = realm.where(Aplicaciones.class).findAll();
            else
                results = realm.where(Aplicaciones.class).equalTo("category", cat).findAll();
        }else{
            results = realm.where(Aplicaciones.class).beginGroup()
                    .contains("name",nom)
                    .or()
                    .contains("name",nom.toLowerCase())
                    .or()
                    .contains("name",nom.toUpperCase())
                    .endGroup().findAll();
        }
        lista = new ArrayList<>(results);
        if (lista.isEmpty()){
            recyclerView.setVisibility(View.INVISIBLE);
            noData.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            noData.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * Si no hay conexión a internet o si retrofit no pudo consumir la data del servicio, este método
     * muestra un mensaje en el subtitle del ActionBar, además, muestra un CustomToast avisando que
     * no hay internet
     *
     *Nota: no está incluido en el BaseActivity porque es solo en MainActivity que utilizo el Title y
     * el subTitle del ActionBar
     */
    private void tieneInternet() {
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setSubtitle(categoriaSeleccionada);
        if (getIntent().getBooleanExtra("tieneInternet",true)){
        }else{
            Configuration cfg=new Configuration.Builder()
                    .setAnimDuration(700)
                    .setDispalyDuration(2000)
                    .setBackgroundColor("#F44336")
                    .setTextColor("#FFFFFF")
                    .setIconBackgroundColor("#F44336")
                    .setTextPadding(5)                      //dp
                    .setViewHeight(20)                      //dp
                    .setTextLines(1)                        //You had better use setViewHeight and setTextLines together
                    .setTextGravity(Gravity.CENTER)         //only text def  Gravity.CENTER,contain icon Gravity.CENTER_VERTICAL
                    .build();
            Log.d("AntonioMain","no tiene internet");
            effects=Effects.thumbSlider;
            getSupportActionBar().setTitle(getString(R.string.app_name)+" "+getString(R.string.noConexion));
            NiftyNotificationView.build(this,getString(R.string.noInternet),effects,R.id.mLyout,cfg)
                    .setIcon(R.drawable.ic_internet).show();
        }
        if (getIntent().getBooleanExtra("errorRetro",false)){
            getSupportActionBar().setTitle(getString(R.string.app_name)+" "+getString(R.string.errorRetrofit));
        }
    }
}
