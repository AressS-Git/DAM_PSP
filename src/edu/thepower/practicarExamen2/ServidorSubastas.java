package edu.thepower.practicarExamen2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class ServidorSubastas {
    // --- Declaración de constantes, variables y métodos estáticos

    // Mapa que contiene los artículos a subastar (nombre y precio de cada artículo)
    private static Map<String, Integer> articulosSubasta = Collections.synchronizedMap(new TreeMap<>());

    // Añadir unos cuántos artículos al mapa para poder trabajar con él
    static {
        String[] nombreArticulos = {"Samsung TV", "Google Pixel", "iPad"};
        Integer[] precioArticulos = {1200, 600, 800};
        for(int i = 0; i < nombreArticulos.length; i++) {
            articulosSubasta.put(nombreArticulos[i], precioArticulos[i]);
        }
    }

    // Método para ver la información de un producto
    public static String verInformacionArticulo(String nombreArticulo) {
        if(articulosSubasta.containsKey(nombreArticulo)) {
            return "La puja actual por [" + nombreArticulo + "] es: [" + articulosSubasta.get(nombreArticulo) + "]";
        } else {
            return "El artículo no existe";
        }
    }

    // Método para pujar sobre un artículo
    public static String pujarSobreArticulo(String nombreArticulo, String cantidad) {
        int cantidadVerficada;
        try {
            cantidadVerficada = Integer.parseInt(cantidad);
            if(articulosSubasta.containsKey(nombreArticulo)) {
                if(cantidadVerficada > articulosSubasta.get(nombreArticulo)) {
                    // Si la puja es mayor que la existente para ese artículo se modifica el valor de ésta
                    articulosSubasta.put(nombreArticulo, cantidadVerficada);
                    return "Puja aceptada. Nueva puja máxima: [" + cantidadVerficada + "]";
                } else {
                    return "Puja rechazada. La puja actual es mayor o igual";
                }
            } else {
                return "El artículo no existe";
            }
        } catch (NumberFormatException e) {
            return "No se ha podido verificar la puja porque la cantidad introducida no es un número entero";
        }

    }

    // Método que formatea la lista de artículos en subasta para mostrarlos de manera ordenada
    public static String listarArticulosSubasta() {
        StringBuilder listaArticulos = new StringBuilder();
        listaArticulos.append("--- Lista de archivos en subasta ---\n");
        articulosSubasta.forEach((articulo, valor) -> {
            listaArticulos.append("Artículo: ").append(articulo).append(", valor: ").append(valor).append("\n");
        });
        return listaArticulos.toString();
    }

    // Clase que gestionará cada pujador que participe en la subasta
    public static void gestionarPujador(Socket socket) {
        try(
                // Crear canales de comunicación para leer y devolver información
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
                ) {
            // Procesar los comandos que nos lleguen mediante un 'switch'
            String comandoPujador;
            String respuesta;
            String[] comandoPujadorTrozeado;
            while((comandoPujador = bufferedReader.readLine()) != null) {
                // Trozear la línea del pujador para obtener el comano y el contenido de esta
                comandoPujadorTrozeado = comandoPujador.split("\\s+", 3);
                // Dependiendo del comando que el servidor reciba se utilizará un método u otro
                respuesta = switch(comandoPujadorTrozeado[0].trim().toLowerCase()) {
                    case "ver" -> verInformacionArticulo(comandoPujadorTrozeado[1]);
                    case "pujar" -> pujarSobreArticulo(comandoPujadorTrozeado[1], comandoPujadorTrozeado[2]);
                    case "listar" -> listarArticulosSubasta();
                    case "salir" -> "Hasta la vista!";
                    default -> "Comando introudcido desconocido, introduce otro comando";
                };
                printWriter.println(respuesta);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Puerto por el que el servidor escuchará peticiones
    private static final int PORT = 5555;

    // --- Iniciación del servidor y creación de sockets mediante hilos para escuchar múltiples peticiones a la vez
    public static void main(String[] args) {
        // Creación del 'socket' del servidor
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor escuchando por el puerto: " + PORT + "...");
            while(true) {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(() -> gestionarPujador(socket));
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}
