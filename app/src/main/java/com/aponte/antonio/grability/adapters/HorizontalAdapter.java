package com.aponte.antonio.grability.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.aponte.antonio.grability.R;

import java.util.List;

/**
 * Created by Antonio on 10/1/2017.
 * Adapter que se usa para el recyclerView de las categorías
 */
public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {
    private Context context;
    private List<String> horizontalList;
    private AdapterListenerMenu listenerMenu;

    public interface AdapterListenerMenu {
        void onClickCat(String cat);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView;

        public MyViewHolder(View view) {
            super(view);
            txtView = (TextView) view.findViewById(R.id.txtView);
        }
    }

    public void animate(RecyclerView.ViewHolder viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(context, R.anim.bounce_interpolator);
        viewHolder.itemView.setAnimation(animAnticipateOvershoot);
    }

    public HorizontalAdapter(Context context,List<String> horizontalList,AdapterListenerMenu listenerMenu) {
        this.context = context;
        this.horizontalList = horizontalList;
        this.listenerMenu = listenerMenu;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_item_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        animate(holder);
        holder.txtView.setText(horizontalList.get(position));
        holder.txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Antonio","presiono "+horizontalList.get(position));
                listenerMenu.onClickCat(horizontalList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}