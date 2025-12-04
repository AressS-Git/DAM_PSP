package edu.thepower.u3comunicacionesEnRed;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class U307ServidorUDPBroadcasting {

    public static void main(String[] args) {
        try(DatagramSocket ds = new DatagramSocket(2200)) {
            System.out.println("Servidor escuchando en el puerto 2200");
            byte[] dataRecived = new byte[1024];
            DatagramPacket packetRecived = new DatagramPacket(dataRecived, dataRecived.length);
            ds.receive(packetRecived);
            String message = new String(packetRecived.getData(), 0, packetRecived.getLength());
            System.out.println("Mensaje recibido: " + message);
        } catch(IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

    }
}
