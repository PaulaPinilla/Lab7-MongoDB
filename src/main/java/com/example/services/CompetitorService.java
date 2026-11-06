/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Competitor;
import com.example.models.CompetitorDTO;
import com.example.models.Product;
import com.example.models.ProductDTO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Servicios REST de los competidores (MongoDB).
 *
 * @author Mauricio
 */
@Path("/competitors")
@Produces(MediaType.APPLICATION_JSON)
public class CompetitorService {

    // Unidad de persistencia de MongoDB
    @PersistenceContext(unitName = "mongoPU")
    EntityManager entityManager;

    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Consulta SIN parametros: lista todos los competidores.
     * GET http://localhost:8082/competitors
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        Query q = entityManager.createQuery("select u from Competitor u order by u.surname ASC");
        List<Competitor> competitors = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(competitors).build();
    }

    /**
     * Registra un competidor (con su producto OBLIGATORIO y vehiculo opcional).
     * POST http://localhost:8082/competitors/add
     */
    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompetitor(CompetitorDTO competitor) {
        return saveCompetitor(competitor);
    }

    /**
     * Registra un competidor con la informacion de su vehiculo (guia del laboratorio).
     * POST http://localhost:8082/competitors/vehicle
     */
    @POST
    @Path("/vehicle")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompetitorWithVehicle(CompetitorDTO competitor) {
        return saveCompetitor(competitor);
    }

    /**
     * ACTIVIDAD 4: nombres de los competidores que empiezan por la letra 'A'.
     * GET http://localhost:8082/competitors/letraA
     * (Va antes que /{name}: en JAX-RS una ruta fija tiene prioridad sobre una con parametro)
     */
    @GET
    @Path("/letraA")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompetitorNamesStartingWithA() {
        Query q = entityManager.createQuery("SELECT c FROM Competitor c ORDER BY c.name ASC");
        List<Competitor> competitors = q.getResultList();

        JSONArray names = new JSONArray();
        for (Competitor c : competitors) {
            String name = c.getName();
            if (name != null && name.trim().toUpperCase().startsWith("A")) {
                names.add(name);
            }
        }

        JSONObject rta = new JSONObject();
        rta.put("total", names.size());
        rta.put("names", names);
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
    }

    /**
     * Consulta CON parametros: competidores que tengan el nombre indicado.
     * GET http://localhost:8082/competitors/{nombre}
     */
    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCompetitorsByName(@PathParam("name") String name) {
        TypedQuery<Competitor> query = (TypedQuery<Competitor>) entityManager.createQuery("SELECT c FROM Competitor c WHERE c.name = :name");
        List<Competitor> competitors = query.setParameter("name", name).getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin", "*").entity(competitors).build();
    }

    /**
     * Logica comun para guardar un competidor con su producto (obligatorio) y su vehiculo.
     */
    private Response saveCompetitor(CompetitorDTO competitor) {
        JSONObject rta = new JSONObject();

        // ===== ACTIVIDAD 2: el producto es OBLIGATORIO =====
        ProductDTO productDTO = competitor.getProduct();
        if (productDTO == null
                || isEmpty(productDTO.getName())
                || isEmpty(productDTO.getBrand())
                || isEmpty(productDTO.getPurchaseDate())) {
            rta.put("error", "El competidor DEBE tener un producto con name, brand y purchaseDate (yyyy-MM-dd)");
            return Response.status(400).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
        }

        // Convertir la fecha de compra (texto) a Date
        Date purchaseDate;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setLenient(false);
            purchaseDate = format.parse(productDTO.getPurchaseDate());
        } catch (ParseException e) {
            rta.put("error", "La fecha de compra debe tener el formato yyyy-MM-dd, por ejemplo 2026-11-06");
            return Response.status(400).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
        }

        // Crear el producto
        Product p = new Product();
        p.setName(productDTO.getName());
        p.setBrand(productDTO.getBrand());
        p.setPurchaseDate(purchaseDate);

        // Crear el competidor
        Competitor c = new Competitor();
        c.setAddress(competitor.getAddress());
        c.setAge(competitor.getAge());
        c.setCellphone(competitor.getCellphone());
        c.setCity(competitor.getCity());
        c.setCountry(competitor.getCountry());
        c.setName(competitor.getName());
        c.setSurname(competitor.getSurname());
        c.setTelephone(competitor.getTelephone());
        c.setVehicle(competitor.getVehicle());
        c.setProduct(p);

        int status = 200;
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(p);   // se guarda en la coleccion PRODUCT
            entityManager.persist(c);   // se guarda en la coleccion COMPETITOR
            entityManager.getTransaction().commit();
            rta.put("competitor_id", c.getId());
            rta.put("product_id", p.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            status = 500;
            rta.put("error", "No se pudo guardar el competidor: " + t.getMessage());
        } finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(status).header("Access-Control-Allow-Origin", "*").entity(rta.toJSONString()).build();
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    @OPTIONS
    public Response cors(@javax.ws.rs.core.Context HttpHeaders requestHeaders) {
        return Response.status(200).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS").header("Access-Control-Allow-Headers", "AUTHORIZATION, content-type, accept").build();
    }
}