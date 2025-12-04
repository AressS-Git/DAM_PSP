package edu.thepower.u3comunicacionesEnRed;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class U307ClienteUDPBroadcasting {
    public static void main(String[] args) {
        try(DatagramSocket ds = new DatagramSocket()) {
            String messageToSend = "Soy un cliente";
            byte[] data = messageToSend.getBytes();
            // Dirección IP broadcast de la red(Sale al mezclar la máscara de subred con la IP)
            InetAddress ipDestiny = InetAddress.getByName("10.255.255.255");
            int port = 2200;
            DatagramPacket packetToSend = new DatagramPacket(data, data.length, ipDestiny, port);
            ds.setBroadcast(true);
            ds.send(packetToSend);
        } catch(IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
