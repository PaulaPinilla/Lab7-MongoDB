package com.example.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.NoSql;

/**
 * Entidad Producto (Actividad del laboratorio).
 * Atributos: nombre, marca y fecha de compra.
 * Es @Entity: se guarda en su propia coleccion "PRODUCT" en MongoDB.
 */
@NoSql(dataFormat = DataFormatType.MAPPED)
@Entity
@XmlRootElement
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    // En MongoDB el id no es un numero (long) sino un texto alfanumerico
    @Id
    @GeneratedValue
    @Field(name = "_id")
    private String id;

    // Nombre del producto
    private String name;

    // Marca del producto
    private String brand;

    // Fecha de compra
    @Temporal(TemporalType.DATE)
    private Date purchaseDate;

    public Product() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /**
     * Devuelve la fecha de compra en formato legible (yyyy-MM-dd)
     * para que se vea bien en las respuestas JSON.
     */
    public String getPurchaseDateText() {
        if (purchaseDate == null) {
            return null;
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(purchaseDate);
    }
}