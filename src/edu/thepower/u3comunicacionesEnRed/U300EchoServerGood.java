package edu.thepower.u3comunicacionesEnRed;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//Metodo que comprueba si el puerto pasado por argumento es un solo argumento tipo int y esta dentro del rango de puertos disponibles
class Validation {
    public static int validarPuerto(String[] args) {
        if(args.length != 1){
            throw new IllegalArgumentException("Sólo se debe pasar un número");
        }
        int puerto = Integer.parseInt(args[0]);
        if(puerto < 1024 || puerto > 65535){
            throw new IllegalArgumentException("Puerto invalido, rango viable [1024-65535]");
        }
        return puerto;
    }
}
public class U300EchoServerGood {
    public static void main(String[] args) {
        int puerto = 0;
        try {
            puerto = Validation.validarPuerto(args);
        } catch (Exception e) {
            System.err.println("Error en el formato del puerto: " + e.getMessage());
            System.exit(1);
        }
        try(ServerSocket servidor = new ServerSocket(puerto);) {
            System.out.println("Servidor activo, esperando conexiones por el puerto: " + puerto);
            Socket socket = servidor.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            String line = null;
            while((line = br.readLine()) != null) {
                System.out.println("Recibido del cliente: " + line);
                pw.println(line.toLowerCase());
            }

        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}
