package com.aponte.antonio.grability.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Antonio on 7/1/2017.
 *
 * Generalmente, en esta clase escribo los métodos que son comunes entre muchas clases de mis proyectos
 */
public class UtilidadesGenerales {

    /**
     * Método que confirma si te tiene internet
     * @param context   El context de la clase que lo utiliza
     * @return          true si tiene internet, false si no tiene
     */
    public static boolean tieneInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo !=null && networkInfo.isConnected();
    }
}
