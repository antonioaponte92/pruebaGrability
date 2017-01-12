package com.aponte.antonio.grability.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Antonio on 9/1/2017.
 *
 * Realm no soporta tipos de datos que son necesarios para recibir data desde el servicio,
 * por lo tanto, creo esta tabla y mediante un método creado por mi, convierto el Json recibido
 * desde el servicio a una tabla como esta
 *
 * Nota: esto lo hago así es por lo complejo que viene el Json desde el servicio.
 *
 * El resto de los modelos fue autogenerado con una herramienta que permite convertir un json a
 * modelos en java
 */
public class Aplicaciones extends RealmObject{
    @PrimaryKey
    private int id;
    private String name;
    private String company;
    private String category;
    private String urlIm;
    private String price;
    private String currency;
    private String summary;
    private String url;


    public Aplicaciones() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlIm() {
        return urlIm;
    }

    public void setUrlIm(String urlIm) {
        this.urlIm = urlIm;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrecio(){
        if (price.equals("0.00000"))
            return "Gratis";
        return currency+" "+price;
    }
}
