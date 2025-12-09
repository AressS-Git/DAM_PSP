package edu.thepower.u4serviciosEnRed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class U4P02SelfHealingServer {

    private static void arrancarServidor() {
        try (ServerSocket svs = new ServerSocket(2777)) {
            System.out.println("Escuchando en el puerto " + svs.getLocalPort());

            // Thread asesino, a los 5 segundos cierra el socket del servidor
            Thread killer = new Thread(() ->{
                System.out.println("Thread killer iniciándose...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
                try {
                    svs.close();
                } catch (IOException e) {
                }
            });
            killer.start();

            while (true) {
                Socket sc = svs.accept();
                Thread t = new Thread(() -> {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
                         PrintWriter pw = new PrintWriter(sc.getOutputStream(), true);) {
                        String linea;
                        while ((linea = br.readLine()) != null) {
                            System.out.println("Recibido del cliente: " + linea);
                            pw.println(linea.toLowerCase());
                        }
                    } catch (IOException e) {
                        System.err.println("Error en el servidor " + e.getMessage());
                    }
                });
                t.start();
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {
        while (true) {
            try {
                arrancarServidor();
            } catch (Exception e) {
                System.err.println("Server out of service: " + e.getMessage());
                System.out.println("Restarting in 2 secs...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                }
            }
        }
    }
}