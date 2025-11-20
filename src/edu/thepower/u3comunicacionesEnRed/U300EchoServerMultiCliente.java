package edu.thepower.u3comunicacionesEnRed;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class GestorCliente implements Runnable {
    //Declarar el socket para poder pasarlo a run(), ya que run no acepta argumentos
    private Socket socket;

    GestorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String nombreThread = "[" + Thread.currentThread().getName() + "]";
        System.out.println("CLiente conectado " + socket.getInetAddress() + ":" + socket.getPort());
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println("Recibido del cliente: " + line);
                pw.println(line.toLowerCase());
            }
        } catch (IOException e) {
            System.err.println();
        }
        System.out.println("El cliente " + nombreThread + " se ha desconectado");
    }
}

public class U300EchoServerMultiCliente {
    public static void main(String[] args) {
        int puerto = 0;
        try {
            puerto = Validation.validarPuerto(args);
        } catch (Exception e) {
            System.err.println("Error en el formato del puerto: " + e.getMessage());
            System.exit(1);
        }
        try (ServerSocket servidor = new ServerSocket(puerto);) {
            System.out.println("Servidor activo, esperando conexiones por el puerto: " + puerto);
            //Bucle para aceptar varios clientes y crearles un socket a cada uno de ellos
            while(true) {
                Socket socket = servidor.accept();
                Thread thread = new Thread(new GestorCliente(socket));
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}
