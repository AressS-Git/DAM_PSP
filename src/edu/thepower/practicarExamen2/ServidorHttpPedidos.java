package edu.thepower.practicarExamen2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorHttpPedidos {

    private static final int PUERTO = 8080;

    // Simulamos unos datos para mostrar en la web
    // En un caso real, esto podría leer del mismo Map que el ejercicio 1 si estuvieran juntos.
    private static String generarHtmlPedidos() {
        return """
            <html>
                <head>
                    <title>Estado de Pedidos</title>
                    <style>
                        body { font-family: sans-serif; padding: 20px; }
                        table { border-collapse: collapse; width: 50%; }
                        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                        th { background-color: #4CAF50; color: white; }
                        tr:nth-child(even) { background-color: #f2f2f2; }
                    </style>
                </head>
                <body>
                    <h1>Listado de Pedidos Activos</h1>
                    <table>
                        <tr>
                            <th>ID</th>
                            <th>Producto</th>
                            <th>Estado</th>
                        </tr>
                        <tr>
                            <td>101</td>
                            <td>Portátil Gaming HP</td>
                            <td>Enviado</td>
                        </tr>
                        <tr>
                            <td>102</td>
                            <td>Ratón Logitech</td>
                            <td>Pendiente</td>
                        </tr>
                         <tr>
                            <td>103</td>
                            <td>Monitor 27 pulgadas</td>
                            <td>Procesando</td>
                        </tr>
                    </table>
                    <p><i>Página generada por Servidor Java DAM</i></p>
                </body>
            </html>
            """;
    }

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor HTTP iniciado en http://localhost:" + PUERTO);
            while (true) {
                Socket socket = servidor.accept();
                // Usamos un hilo para no bloquear el servidor
                new Thread(() -> manejarPeticionWeb(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error fatal: " + e.getMessage());
        }
    }

    private static void manejarPeticionWeb(Socket socket) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream())) {

            // Leemos la primera línea de la petición (GET / HTTP/1.1)
            // Es importante leerla aunque no la usemos para vaciar el buffer inicial
            String lineaSolicitud = br.readLine();
            System.out.println("Petición recibida: " + lineaSolicitud);
            
            // Si la línea es nula, cerramos
            if (lineaSolicitud == null) return;

            String contenidoHtml = generarHtmlPedidos();

            // Escribimos las cabeceras HTTP obligatorias
            pw.println("HTTP/1.1 200 OK");
            pw.println("Content-Type: text/html; charset=UTF-8");
            pw.println("Content-Length: " + contenidoHtml.getBytes().length);
            pw.println("Connection: close"); // Importante para navegadores
            pw.println(); // Línea en blanco obligatoria entre headers y body
            
            // Escribimos el contenido
            pw.println(contenidoHtml);
            pw.flush();

        } catch (IOException e) {
            System.err.println("Error atendiendo cliente web: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // Ignorar
            }
        }
    }
}