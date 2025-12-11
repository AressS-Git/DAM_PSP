package edu.thepower.u4serviciosEnRed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class U4P03SimpleWebServer {

    private static final String HTML = """
            <html>
                <head>
                    <title>Servidor web simple</title>
                </head>
                <body>
                    <h1>¡Hola mundo!</h1>
                    <p>Esto es un servidor web simple.</p>
                    <p>%s</p>
                </body>
            </html>""";

    private static final int PUERTO = 8080;
    private static final String NOMBRE_SERVIDOR = "Servidor Web Simple";
    private static AtomicInteger contador = new AtomicInteger();
    private static final String HTTP_ESTADO_OKAY = "200 OK";
    private static final String HTTP_ESTADO_NOT_FOUND = "404 Not Found";
    private static final String HTTP_ESTADO_NOT_ALLOWED = "405 Method Not Allowed";
    private static final DateTimeFormatter FECHA_ACTUAL = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm:ss");

    public static void main(String[] args) {
        try (ServerSocket svs = new ServerSocket(PUERTO)) {
            System.out.println("Servidor escuchando en el puerto " + PUERTO);
            while (true) {
                Socket socket = svs.accept();
                Thread t = new Thread(() -> atenderSolicitud(socket));
                t.start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    private static void atenderSolicitud(Socket socket) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream())) {
            String linea;
            linea = br.readLine();
            if (linea != null && !linea.isBlank()) {
                System.out.println(linea);
                String[] partes = linea.split("\\s");
                String metodo = partes[0];
                String ruta = partes.length > 1 ? partes[1].trim().toLowerCase() : "/";

                while ((linea = br.readLine()) != null && !linea.isBlank()) {
                    System.out.println(linea);
                }
                String estado = HTTP_ESTADO_OKAY;
                String respuesta = "";
                if (metodo.trim().equalsIgnoreCase("get")) {
                    // Desechar solicitudes GET del favicon
                    if (ruta.contains("favicon")) {
                        contador.incrementAndGet(); // Solo contabilizamos la visita si es de tipo GET y la ruta no incluye favicon
                    }
                    System.out.println("Devolviendo respuesta HTML: ");

                    estado = HTTP_ESTADO_OKAY;
                    respuesta = switch (ruta) {
                        case "/" -> "Eres el visitante número: " + contador.incrementAndGet();
                        case "/fecha", "/fecha/" ->
                                "Fecha y hora actuales: " + FECHA_ACTUAL.format(LocalDateTime.now());
                        case "/nombre", "/nombre/" -> "El nombre del servidor es: " + NOMBRE_SERVIDOR;
                        default -> {
                            estado = HTTP_ESTADO_NOT_FOUND;
                            yield "Error: ruta no permitida";
                        }
                    };
                } else {
                    estado = HTTP_ESTADO_NOT_ALLOWED;
                    respuesta = "Error: método no permitido";
                }
                devolverRespuesta(pw, estado, respuesta);
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor " + e.getMessage());
        }
    }

    private static void devolverRespuesta(PrintWriter pw, String estado, String mensaje) {
        StringBuffer respuesta = new StringBuffer();
        respuesta.append(String.format(HTML, mensaje));
        pw.println("HTTP/1.1 " + estado);
        pw.println("Content-Type: text/html;charset=UTF-8");
        pw.println("Content-Length: " + respuesta.toString().getBytes().length);
        pw.println();
        pw.println(respuesta.toString());

        pw.flush();
    }
}