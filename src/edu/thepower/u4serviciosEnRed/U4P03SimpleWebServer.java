package edu.thepower.u4serviciosEnRed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class U4P03SimpleWebServer {
    private static final String HTML_1 = """
            <html>
                <head>
                    <title>Simple Web Server</title>
                </head>
                <body>
                    <h1>Hola mundo</h1>
                    <p>Visitante número[""";
    private static final String HTML_2 = """
                    ]</p>
                </body>
            </html>
            """;
    private static final int PORT = 2100;
    private static AtomicInteger contador = new AtomicInteger(0);

    private static void atenderSolcitud(Socket socket) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream());) {
            String linea = br.readLine();
            System.out.println(linea);
            System.out.println("Devolviendo HTML...");
            StringBuffer respuesta = new StringBuffer();
            respuesta.append(HTML_1).append(contador.get()).append(HTML_2);
            pw.println("HTTP/1.1 200 OK");
            pw.println("Content-Type:text/html;charset=UTF-8");
            pw.println("Content-Length: " + respuesta.toString().getBytes().length);
            pw.println();
            pw.println(respuesta.toString());
            pw.flush();
        } catch (IOException e) {
            System.err.println("Error en el servidor " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        try(ServerSocket sc = new ServerSocket(PORT)) {
            System.out.println("Escuchando en el puerto " + PORT);
            while(true) {
                Socket socket = sc.accept();
                // Incrementamos el número de visitas en uno
                contador.incrementAndGet();
                Thread t = new Thread(() -> atenderSolcitud(socket));
                t.start();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
