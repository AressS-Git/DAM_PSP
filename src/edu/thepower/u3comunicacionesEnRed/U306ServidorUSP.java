package edu.thepower.u3comunicacionesEnRed;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class U306ServidorUSP {

    public static void main(String[] args) {
        try(DatagramSocket ds = new DatagramSocket(2100)) {
            System.out.println("Servidor escuchando en puerto 2100");
            byte[] data = new byte[1024];
            DatagramPacket dp = new DatagramPacket(data, data.length);
            ds.receive(dp);
            System.out.println("Mensaje recibido");
        } catch(IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}
