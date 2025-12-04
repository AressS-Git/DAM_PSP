package edu.thepower.u3comunicacionesEnRed;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class U306ClienteUDP {
    public static void main(String[] args) {
        try(DatagramSocket ds = new DatagramSocket()) {
            // Envío de mensaje al servidor
            String mensaje = "Soy un cliente";
            byte[] data = mensaje.getBytes();
            InetAddress ip = InetAddress.getByName("localhost");
            int puerto = 2100;
            DatagramPacket dp = new DatagramPacket(data, data.length, ip, puerto);
            ds.send(dp);
            // Recepción del ack(respuesta del servidor)
            byte[] dataAck = new byte[1024];
            DatagramPacket dpAck = new DatagramPacket(dataAck, dataAck.length);
            ds.receive(dpAck);
            String ack = new String(dpAck.getData(), 0, dpAck.getLength());
            System.out.println("ACK recibido: " + ack);
        } catch(IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
