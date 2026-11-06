package com.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 *
 * This class launches the web application in an embedded Jetty container.
 * This is the entry point to your application. The Java command that is used for
 * launching should fire this main method.
 *
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // JAXB antiguo (de Jersey 1.8) falla con Java nuevo al "optimizar" clases.
        // Esta propiedad desactiva esa optimizacion y evita el error HTTP 500.
        System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");

        String webappDirLocation = "src/main/webapp/";

        // Se usa el puerto 8082 para no chocar con GlassFish (que usa el 8080).
        // Si existe la variable de entorno PORT, se usa esa.
        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8082";
        }

        Server server = new Server(Integer.valueOf(webPort));
        WebAppContext root = new WebAppContext();

        root.setContextPath("/");
        root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
        root.setResourceBase(webappDirLocation);

        // Se crea la conexion con MongoDB al arrancar la aplicacion
        PersistenceManager.getInstance().getEntityManagerFactory();

        // Parent loader priority is a class loader setting that Jetty accepts.
        // Read more here: http://wiki.eclipse.org/Jetty/Reference/Jetty_Classloading
        root.setParentLoaderPriority(true);

        server.setHandler(root);

        System.out.println("\n*** Servidor iniciado en http://localhost:" + webPort + "/competitors");

        server.start();
        server.join();
    }
}