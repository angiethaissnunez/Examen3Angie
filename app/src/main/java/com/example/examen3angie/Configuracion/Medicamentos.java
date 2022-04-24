package com.example.examen3angie.Configuracion;

import java.io.Serializable;

public class Medicamentos implements Serializable {

    private int id;
    private String descripcion;
    private String cantidad;
    private String periocidad;
    private String tiempo;
    private String imagen;

    public Medicamentos(int id, String descripcion, String cantidad, String periocidad, String tiempo, String imagen) {
        this.id = id;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.periocidad = periocidad;
        this.tiempo = tiempo;
        this.imagen = imagen;
    }

    public Medicamentos() {
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCantidad() { return cantidad; }

    public void setCantidad(String cantidad) { this.cantidad = cantidad; }

    public String getPeriocidad() { return periocidad; }

    public void setPeriocidad(String periocidad) { this.periocidad = periocidad; }

    public String getTiempo() { return tiempo; }

    public void setTiempo(String tiempo) { this.tiempo = tiempo; }

    public String getImagen() { return imagen; }

    public void setImagen(String imagen) { this.imagen = imagen; }

    @Override
    public String toString() {
        return  descripcion + " | " + cantidad;
    }


}
