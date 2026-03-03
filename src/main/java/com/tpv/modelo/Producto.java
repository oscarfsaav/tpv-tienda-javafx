package com.tpv.modelo;

/**
 * Clase Modelo que representa un producto en el sistema (POJO).
 * Según el patrón MVC, esta clase transporta los datos entre la DB y la Vista.
 */
public class Producto {
    private int id;
    private String codigoBarras;
    private String nombre;
    private double precio;
    private int stock;
    private String categoria;
    
    // ¡NUEVO! Variable para saber cuántos llevamos en el ticket
    private int cantidad = 1;

    // Constructor completo
    public Producto(int id, String codigoBarras, String nombre, double precio, int stock, String categoria) {
        this.id = id;
        this.codigoBarras = codigoBarras;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
    }

    // Getters y Setters 
    // (Importante: JavaFX TableView necesita los Getters para mostrar los datos)
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    // ¡NUEVO! Getters y Setters para la cantidad
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    @Override
    public String toString() {
        return nombre + " (" + precio + "€)";
    }
}