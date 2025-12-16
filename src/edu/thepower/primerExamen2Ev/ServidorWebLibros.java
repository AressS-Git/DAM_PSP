package edu.thepower.primerExamen2Ev;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

public class ServidorWebLibros {
    private final static String HTML_POR_DEFECTO = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
             <title>Catálogo de libros</title>
            </head>
            <body>
             <h1>Catálogo de libros</h1>
             <p>Opciones disponibles:</p>
             <ul>
             <li><a href="/libros">Ver lista completa de libros</a></li>
             <li><a href="/libros_total">Número total de libros</a></li>
             </ul>
            </body>
            </html>
            """;

    private final static String HTML_LISTA_LIBROS = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
             <meta charset="UTF-8">
             <title>Lista de libros</title>
            </head>
            <body>
             <h1>Lista de libros</h1>
             <ul>
             <li>%s</li>
             </ul>
             <p><a href="/">Volver al inicio</a></p>
            </body>
            </html>
            """;

    private final static String HTML_TOTAL_LIBROS = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
             <meta charset="UTF-8">
             <title>Total de libros</title>
            </head>
            <body>
             <p>Total de libros: %s</p>
             <p><a href="/">Volver al inicio</a></p>
            </body>
            </html>
            """;
    // --- Declaración de constantes
    // Creación de la lista de libros e inserción de valores
    private static final Map<String, String> listaLibros = new TreeMap<>();
    static {
        String[] libros = {"El Quijote", "Cien años de soledad", "1984", "Pantaleón y las visitadoras", "Dune"};
        String[] autores = {"Miguel de Cervantes", "Gabriel García Márquez", " George Orwell", "Mario Vargas Llosa", "Frank Herbert"};
        for(int i = 0; i < libros.length; i++) {
           listaLibros.put(libros[i], autores[i]);
        }
    }
    private static final String CONTENIDO_LISTA_LIBROS = listaLibros.toString();
    private static final String CANTIDAD_LIBROS = String.valueOf(listaLibros.size());
    private static final int PUERTO = 8080;
    private static final String HTTP_ESTADO_OKAY = "200 OK";
    private static final String HTTP_ESTADO_NOT_FOUND = "404 Not Found";
    private static final String HTTP_ESTADO_NOT_ALLOWED = "405 Method Not Allowed";

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
        // Esta variable me servirá luego para mostar una página u otra, obtendrá el valor de las constantes
        String html_a_mostrar = HTML_POR_DEFECTO;
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
                    System.out.println("Devolviendo respuesta HTML: ");
                    estado = HTTP_ESTADO_OKAY;
                    respuesta = switch (ruta) {
                        case "/" -> {
                            html_a_mostrar = HTML_POR_DEFECTO;
                            yield null;
                        }
                        case "/libros", "/libros/" -> {
                            html_a_mostrar = HTML_LISTA_LIBROS;
                            yield CONTENIDO_LISTA_LIBROS;
                        }
                        case "/libros/total", "/libros/total/", "/libros_total", "libros/total/" -> {
                            html_a_mostrar = HTML_TOTAL_LIBROS;
                            yield CANTIDAD_LIBROS;
                        }
                        default -> {
                            estado = HTTP_ESTADO_NOT_FOUND;
                            yield "Error: ruta no permitida";
                        }
                    };
                } else {
                    estado = HTTP_ESTADO_NOT_ALLOWED;
                    respuesta = "Error: método no permitido";
                }
                devolverRespuesta(pw, estado, respuesta, html_a_mostrar);
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor " + e.getMessage());
        }
    }

    private static void devolverRespuesta(PrintWriter pw, String estado, String mensaje, String html_a_mostar) {
        StringBuffer respuesta = new StringBuffer();
        respuesta.append(String.format(html_a_mostar, mensaje));
        pw.println("HTTP/1.1 " + estado);
        pw.println("Content-Type: text/html;charset=UTF-8");
        pw.println("Content-Length: " + respuesta.toString().getBytes().length);
        pw.println();
        pw.println(respuesta.toString());

        pw.flush();
    }
}
