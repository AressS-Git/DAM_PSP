package edu.thepower.practicarExamen2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

public class ServidorWebLibros {

    // Plantilla HTML básica
    private static final String HTML_TEMPLATE = """
            <html>
                <head><title>Biblioteca DAM</title></head>
                <body>
                    <h1>Servidor de Libros DAM</h1>
                    <div style='border: 1px solid #ccc; padding: 10px;'>
                        %s
                    </div>
                    <p><a href='/'>Inicio</a> | <a href='/catalogo'>Ver Catálogo</a> | <a href='/hora'>Hora</a></p>
                </body>
            </html>
            """;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(8080)) {
            System.out.println("Servidor Web escuchando en http://localhost:8080");
            
            while (true) {
                Socket socket = server.accept();
                new Thread(() -> atenderPeticion(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void atenderPeticion(Socket socket) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream())) {

            String linea = br.readLine();
            if (linea != null && !linea.isEmpty()) {
                System.out.println("Petición recibida: " + linea);
                // linea es tipo: "GET /catalogo HTTP/1.1"
                String[] partes = linea.split("\\s+");
                String metodo = partes[0];
                String ruta = partes[1];

                // Consumir el resto de cabeceras HTTP (necesario para limpiar el buffer)
                while (br.ready() && (linea = br.readLine()) != null && !linea.isEmpty()) { 
                    // No hacemos nada con las cabeceras extra
                }

                String contenidoCuerpo = "";
                String estado = "200 OK";

                if (metodo.equals("GET")) {
                    switch (ruta) {
                        case "/":
                            contenidoCuerpo = "<p>Bienvenido al examen de Servicios en Red.</p>";
                            break;
                        case "/catalogo":
                            contenidoCuerpo = """
                                <ul>
                                    <li><b>Java a Fondo</b> - S. M.</li>
                                    <li><b>Redes de Computadoras</b> - Tanenbaum</li>
                                    <li><b>Código Limpio</b> - R. C. Martin</li>
                                </ul>
                                """;
                            break;
                        case "/hora":
                            contenidoCuerpo = "<p>La hora del servidor es: " + LocalDateTime.now() + "</p>";
                            break;
                        default:
                            estado = "404 Not Found";
                            contenidoCuerpo = "<h2 style='color:red'>Error 404: Página no encontrada</h2>";
                    }
                } else {
                    estado = "405 Method Not Allowed";
                    contenidoCuerpo = "Método no permitido";
                }

                enviarRespuestaHTTP(pw, estado, contenidoCuerpo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void enviarRespuestaHTTP(PrintWriter pw, String estado, String cuerpoMensaje) {
        // Inyectamos el contenido en la plantilla
        String htmlFinal = String.format(HTML_TEMPLATE, cuerpoMensaje);
        
        // Cabeceras obligatorias
        pw.println("HTTP/1.1 " + estado);
        pw.println("Content-Type: text/html; charset=UTF-8");
        pw.println("Content-Length: " + htmlFinal.getBytes().length);
        pw.println(); // Línea en blanco obligatoria entre headers y body
        pw.println(htmlFinal);
        pw.flush();
    }
}