package com.example.models;

/**
 * DTO para recibir los datos del producto desde Postman (JSON).
 * La fecha de compra llega como texto en formato yyyy-MM-dd
 * (por ejemplo "2026-11-06") y el servicio la convierte a fecha.
 */
public class ProductDTO {

    private String name;

    private String brand;

    private String purchaseDate;

    public ProductDTO() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}