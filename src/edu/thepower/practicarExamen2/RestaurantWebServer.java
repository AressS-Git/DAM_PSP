package edu.thepower.practicarExamen2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RestaurantWebServer {
    
    // HTML base con placeholder %s, estilo U4P03ServidorWebSencillo
    private static final String HTML_TEMPLATE = """
            <html>
                <head><title>Restaurante El Codigo</title></head>
                <body style='font-family: sans-serif; text-align: center; background-color: #f4f4f4;'>
                    <h1 style='color: navy;'>Restaurante El Código</h1>
                    <hr>
                    %s
                    <hr>
                    <footer>Hora del servidor: %s</footer>
                </body>
            </html>""";

    private static final int PUERTO = 8080;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PUERTO)) {
            System.out.println("Servidor Web iniciado en http://localhost:" + PUERTO);
            
            while (true) {
                Socket socket = server.accept();
                // Atendemos en un hilo nuevo
                new Thread(() -> manejarPeticionHTTP(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void manejarPeticionHTTP(Socket socket) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream())) {

            // Leemos la primera línea de la petición (Ej: "GET /menu HTTP/1.1")
            String linea = br.readLine();
            if (linea == null || linea.isBlank()) return;

            System.out.println("Petición recibida: " + linea);
            String[] partes = linea.split("\\s+");
            String metodo = partes[0];
            String ruta = partes.length > 1 ? partes[1] : "/";

            // Consumir el resto de cabeceras hasta la línea en blanco
            while (br.ready() && (linea = br.readLine()) != null && !linea.isEmpty()) { 
                // Ignoramos el resto de headers por ahora
            }

            String contenidoCuerpo = "";
            String codigoEstado = "200 OK";

            if (metodo.equalsIgnoreCase("GET")) {
                contenidoCuerpo = switch (ruta) {
                    case "/" -> "<h2>Bienvenido</h2><p>La mejor comida Java de la ciudad.</p><a href='/menu'>Ver Menú</a>";
                    case "/menu" -> """
                            <h2>Menú del Día</h2>
                            <ul>
                                <li>Hamburguesa Byte - 12.50€</li>
                                <li>Pizza TCP/IP - 10.00€</li>
                            </ul>
                            <a href='/'>Volver</a>
                            """;
                    case "/contacto" -> "<p>Email: chef@localhost</p><a href='/'>Volver</a>";
                    default -> {
                        codigoEstado = "404 Not Found";
                        yield "<h2 style='color:red;'>Error 404</h2><p>Plato no encontrado.</p>";
                    }
                };
            } else {
                codigoEstado = "405 Method Not Allowed";
                contenidoCuerpo = "Método no permitido.";
            }

            enviarRespuestaHTTP(pw, codigoEstado, contenidoCuerpo);

        } catch (IOException e) {
            System.err.println("Error atendiendo petición: " + e.getMessage());
        }
    }

    // Método auxiliar para construir las cabeceras HTTP correctamente
    private static void enviarRespuestaHTTP(PrintWriter pw, String estado, String contenido) {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String htmlFinal = String.format(HTML_TEMPLATE, contenido, fecha);
        byte[] bytesContenido = htmlFinal.getBytes();

        pw.println("HTTP/1.1 " + estado);
        pw.println("Content-Type: text/html; charset=UTF-8");
        pw.println("Content-Length: " + bytesContenido.length);
        pw.println("Connection: close");
        pw.println(); // Línea en blanco obligatoria entre headers y body
        pw.println(htmlFinal);
        pw.flush();
    }
}