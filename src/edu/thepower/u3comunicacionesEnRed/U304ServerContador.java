package edu.thepower.u3comunicacionesEnRed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class U304ServerContador {

    private static AtomicInteger contador = new AtomicInteger();

    static class GestorClientesContador implements Runnable {
        private Socket socket;
        private String clienteInfo;

        public GestorClientesContador(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            clienteInfo = "IP: " + socket.getInetAddress() + ":" + socket.getPort();
            System.out.println("[" + Thread.currentThread().getName() + "] " + clienteInfo);
            try(
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                    ){
                String comando;
                boolean continuar = true;
                String respuesta;
                while(continuar == true && (comando = br.readLine()) != null) {
                    respuesta = switch(comando.trim().toLowerCase()) {
                        case "inc" -> "Contador incrementado, valor actualizado: " + incrementarContador();
                        case "dec" -> "Contador decrementado, valor actualizado: " + decrementarContador();
                        case "get" -> "Valor: " + getContador();
                        case "bye" -> {
                            continuar = false;
                            yield "Bye"; //yield indica al switch que es lo que debe devolver, lo que será el valor de respuesta
                        }
                        default -> "Comando desconocido";
                    };
                    pw.println(respuesta);
                }
            } catch(IOException e) {
                System.err.println("Error en la conexión: " + e.getMessage());
            }
            System.out.println("Conexión con el cliente + " + clienteInfo + " finalizada");

        }
    }

    public static int getContador() {
        return contador.get();
    }

    public static int incrementarContador() {
        return contador.incrementAndGet();
    }

    public static int decrementarContador() {
        return contador.decrementAndGet();
    }

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(4000)) {
            System.out.println("Servidor escuchando peticiones por el puerto: 4000");
            //Bucle para que se atiendan todas las solicitudes entrantes y asignarlas a un Thread con su propio socket
            while(true) {
                Socket socket = serverSocket.accept();
                //Instanciamos un thread mediante el constructor de la clase Runnable "GestorClientesContador" que recibe un socket como argumento
                Thread thread = new Thread(new GestorClientesContador(socket));
                thread.start();
            }
        } catch(IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

    }
}
