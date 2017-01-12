package com.aponte.antonio.grability.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aponte.antonio.grability.R;
import com.aponte.antonio.grability.models.Aplicaciones;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Antonio on 10/1/2017.
 *
 * Adapter que se usa para el recyclerView de las aplicaciones
 */
public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Aplicaciones> listaApps;
    private Boolean tel;
    private AdapterListener listener;

    /**
     * listener que comunica al adapter con el activity que lo usa
     */
    public interface AdapterListener {
        void onClickApp(int idApp);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;
        public TextView title, count, price;
        public ImageView thumbnail, overflow;


        public MyViewHolder(View view) {
            super(view);
            linearLayout = (LinearLayout) view.findViewById(R.id.linearCard);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.descr);
            price = (TextView) view.findViewById(R.id.appPrice);
            thumbnail = (ImageView) view.findViewById(R.id.cardFoto);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }

    /**
     * Constructor del Adapter
     * @param mContext  el Context de la actividad que lo crea
     * @param albumList el listado de <Aplicaciones> que va a tener el adapter
     * @param tele      un boolean que sirve para establecer cuál tipo de pantalla se va a mostrar
     * @param listener  listener que comunica al adapter con su actividad
     */
    public AppsAdapter(Context mContext, ArrayList<Aplicaciones> albumList,Boolean tele,AdapterListener listener) {
        this.context = mContext;
        this.listaApps = albumList;
        this.tel = tele;
        this.listener = listener;
    }

    /**
     * Si la información del adapter cambia, le paso la nueva lista y le aviso que debe cambiar la
     * información que muestra
     * @param nueva ArrayList con las nuevas <Aplicaciones> que va a mostrar
     */
    public void CambiarData(ArrayList<Aplicaciones> nueva){
        this.listaApps = nueva;
        notifyDataSetChanged();
    }

    /**
     * Método que sirve para darle animación al recycler cuando se muestra
     * @param viewHolder contenedor de la vista de cada item del recyclerView
     */
    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.anticipateovershoot_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /**
         * Si inicializo a itemView dentro de un if Android Studio detecta esto como un error,
         * inicializar itemView = null podría traer problemas, así que decidí usar un condicional
         * dentro del método inflate() usando a la variable tel del adapter
         */
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(tel ? R.layout.item_aplicacion_lista : R.layout.item_aplicacion_grid, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        animate(holder);
        final Aplicaciones aplicaciones = listaApps.get(position);
        holder.title.setText(aplicaciones.getName());
        holder.count.setText(aplicaciones.getCompany());
        if (aplicaciones.getPrecio().equals("Gratis"))
            holder.price.setText(context.getString(R.string.gratis));
        else
            holder.price.setText(aplicaciones.getPrecio());
        Glide.with(context).load(aplicaciones.getUrlIm()).into(holder.thumbnail);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickApp(aplicaciones.getId());
            }
        });
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    /**
     * Se muestra un menú al presionar el imageView que muestra los tres puntos. Lo hice como muestra
     * de que se puede hacer esto dentro de un CardView. Es solo interfaz sin funcionalidad
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_app, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_comprar:
                    Toast.makeText(context, context.getString(R.string.install), Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_link:
                    Toast.makeText(context, context.getString(R.string.abrirEnlace), Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return listaApps.size();
    }
}
