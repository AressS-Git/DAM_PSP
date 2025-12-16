package edu.thepower.practicarExamen2;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;

public class ServidorWebConcesionario {

    private static final int PUERTO = 8080;
    
    // Plantilla HTML básica (text block)
    private static final String HTML_TEMPLATE = """
            <html>
                <head><title>Concesionario Web</title></head>
                <body>
                    <h1>Concesionario The Power</h1>
                    <nav><a href="/">Inicio</a> | <a href="/coches">Ver Stock</a></nav>
                    <hr>
                    %s
                    <footer>Generado a las: %s</footer>
                </body>
            </html>""";

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor Web escuchando en http://localhost:" + PUERTO);
            while (true) {
                Socket socket = servidor.accept();
                new Thread(() -> manejarPeticion(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void manejarPeticion(Socket socket) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream())) {

            String linea = br.readLine();
            if (linea == null) return;
            
            System.out.println("Petición recibida: " + linea);
            String[] partes = linea.split("\\s+");
            String metodo = partes[0];
            String ruta = partes.length > 1 ? partes[1] : "/";

            // Consumir resto de cabeceras
            while (br.ready() && (linea = br.readLine()) != null && !linea.isEmpty()) { }

            String contenidoRespuesta = "";
            String estado = "200 OK";

            if (metodo.equals("GET")) {
                switch (ruta) {
                    case "/":
                        contenidoRespuesta = "<p>Bienvenido a nuestro portal de venta de coches.</p>";
                        break;
                    case "/coches":
                        contenidoRespuesta = """
                            <h3>Listado de Vehículos</h3>
                            <ul>
                                <li>Toyota Corolla - 25.000€</li>
                                <li>Ford Mustang - 45.000€</li>
                                <li>Tesla Model 3 - 39.000€</li>
                            </ul>
                            """;
                        break;
                    default:
                        estado = "404 Not Found";
                        contenidoRespuesta = "<h2>Error 404: Página no encontrada</h2>";
                }
            } else {
                estado = "405 Method Not Allowed";
                contenidoRespuesta = "Método no permitido";
            }

            enviarRespuesta(pw, estado, contenidoRespuesta);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void enviarRespuesta(PrintWriter pw, String estado, String contenido) {
        // Formatear el HTML final insertando el contenido y la fecha
        String htmlFinal = String.format(HTML_TEMPLATE, contenido, LocalDateTime.now());
        
        // Cabeceras HTTP obligatorias
        pw.println("HTTP/1.1 " + estado);
        pw.println("Content-Type: text/html; charset=UTF-8");
        pw.println("Content-Length: " + htmlFinal.getBytes().length);
        pw.println(); // Línea en blanco obligatoria entre headers y body
        pw.println(htmlFinal);
        pw.flush();
    }
}