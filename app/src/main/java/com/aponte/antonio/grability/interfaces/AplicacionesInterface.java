package com.aponte.antonio.grability.interfaces;

import com.aponte.antonio.grability.models.Object;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Antonio on 9/1/2017.
 * Interfaz de retrofit que uso para recibir la información del servicio web
 */
public interface AplicacionesInterface {

    @GET("/us/rss/topfreeapplications/limit=20/json")
    Call<Object> getApps();

}
