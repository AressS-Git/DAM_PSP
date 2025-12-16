package edu.thepower.u4serviciosEnRed;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class U4P01PoolServer {

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        try(ServerSocket svs = new ServerSocket(2777)){
            System.out.println("Escuchando en el puerto " + svs.getLocalPort());
            while(true){
                Socket sc = svs.accept();
                pool.submit(() ->{
                    try(BufferedReader br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
                        PrintWriter pw = new PrintWriter(sc.getOutputStream(), true);){
                        String linea;
                        while((linea = br.readLine()) != null){
                            System.out.println("Recibido del cliente: " + linea);
                            pw.println(linea.toLowerCase());
                        }
                    } catch (IOException e){
                        System.err.println("Error en el servidor " + e.getMessage());
                    }
                });
            }
        } catch (IOException e){
            System.err.println("Error en el servidor " + e.getMessage());
        }
    }
}