package edu.thepower.practicarExamen2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class ServidorImagenesUDP {
    // Creo una variable 'Random' para simular tiempos aleatorios de procesamiento de imágenes
    public static Random random = new Random();

    public static void procesarImagenes(DatagramPacket paqueteRecibido, DatagramSocket datagramSocket) {
        // Extraer la información del paquete recibido
        String nombreArchivo = new String(paqueteRecibido.getData(), 0, paqueteRecibido.getLength());

        // Simular el tiempo de procesamiento de la imagen en el servidor
        try {
            Thread.sleep(random.nextInt(1000) + 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Extraer la información del cliente utilizando el paquete que nons ha llegado
        String respuestaConNombreArchivoMaysuculas = nombreArchivo.toUpperCase() + " PROCESADO";
        byte[] dataRespuesta = respuestaConNombreArchivoMaysuculas.getBytes();
        InetAddress ipCliente = paqueteRecibido.getAddress();
        int puertoCliente = paqueteRecibido.getPort();

        // Empaquetamos la respuesta y se la enviamos al cliente
        DatagramPacket paqueteRespuesta = new DatagramPacket(dataRespuesta, dataRespuesta.length, ipCliente, puertoCliente);
        try {
            datagramSocket.send(paqueteRespuesta);
            System.out.println("Respuesta enviada al cliente");
        } catch (IOException e) {
            System.err.println("Error al enviar el paquete de respuesta");
        }
    }

    public static void main(String[] args) {
        try (DatagramSocket ds = new DatagramSocket(4500)) {
            System.out.println("Servidor escuchando en el puerto 4500...");
            // El servidor recibirá el nombre de los archivos y creará hilos para que estos se encarguen de procesarlos y responder al servidor
            while(true) {
                // Guardar la información del paquete recibido
                byte[] data = new byte[1024];
                DatagramPacket paqueteRecibido = new DatagramPacket(data, data.length);
                ds.receive(paqueteRecibido);
                // Lanzar un hilo para procesar la imagen, este hilo llamará a la función 'procesarImagen'
                Thread thread = new Thread(() -> procesarImagenes(paqueteRecibido, ds));
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}
