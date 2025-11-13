package edu.thepower.PracticarExamen;

import java.text.SimpleDateFormat;
import java.util.*;

//La clase pedidos solo contendra
class Pedido {
    private final int numPedido;
    private final String nombreCliente;
    private final String fechaPedido;

    Pedido(int numPedido, String nombreCliente, String fechaPedido) {
        this.numPedido = numPedido;
        this.nombreCliente = nombreCliente;
        this.fechaPedido = fechaPedido;
    }

    @Override
    public String toString() {
        return "Número de pedido: " + numPedido + ", Nombre del cliente: " + nombreCliente + ", Fecha: " + fechaPedido;
    }
}

public class ResgistradorDePedidos {
    //Identificador único de pedidos
    public static int numPedido = 0;
    //Mapa de clientes, nombre de cliente y uso de cliente, para mostrarlo despues
    public static Map<String, Integer> clientes = new HashMap<>();
    //Lista de pedidos con toda su info (toString), para mostrarlo despues
    public static List<String> pedidos = new ArrayList<>();

    //Función que comprueba el valor del identificador de pedidos y devuelve un nuevo valor, valor++
    public static synchronized int getNewNumPedido() {
        return numPedido++;
    }

    //Funcion para añadir clientes a la lista de clientes
    public static synchronized void addCliente() {
        //Añadir cliente a la lista, aumentar el integer asociado al cliente si ya existe si no añade el cliente con el integer = 0
        clientes.put(Thread.currentThread().getName(), clientes.getOrDefault(Thread.currentThread().getName(), 0) + 1);
    }

    public static void main(String[] args) {
        //Instanciar Random para luego generar enteros aleatorios
        Random randomInt = new Random();
        //Declarar el formato de la fecha
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        //Crear lista de hilos para luego utilizar join
        List<Thread> hilosClientes = new ArrayList<Thread>();

        //Generar 10 clientes/hilos que hagan 10 pedidos cada uno, con un bucle que genere clientes y otro dentro que genere pedidos
        for (int i = 0; i < 10; i++) {
            //Creo el hilo/cliente con el nombre generado arriba
            Thread newCliente = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    //Se hace un nuevo pedido
                    Pedido newPedido = new Pedido(getNewNumPedido(), Thread.currentThread().getName(), formato.format(System.currentTimeMillis()));
                    //Añadir cliente a la lista de clientes
                    addCliente();
                    //Añadir el pedido a la lista de pedidos
                    pedidos.add(newPedido.toString());
                }
            }, "Cliente-" + randomInt.nextInt(0, 10));
            hilosClientes.add(newCliente);
            newCliente.start();
        }

        //Hacer un bucle para recorrer el array de hilos y así hacer join a join
        for (Thread hiloCliente : hilosClientes) {
            try {
                hiloCliente.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //--- Mostrar estadisticas
        //Mostrar todos los pedidos realizados
        System.out.println("--- Mostrando todos los pedidos realizados");
        for (String pedido : pedidos) {
            System.out.println(pedido);
        }

        //Variable que sumara todos los pedidos hechos por cada cliente
        int numTotalPedidos = 0;
        //Mostrar clientes y los pedidos realizados por cada uno
        System.out.println("--- Mostrando los clientes y los pedidos que han realizado cada uno");
        for (String cliente : clientes.keySet()) {
            System.out.println("El cliente " + cliente + " [" +  clientes.get(cliente) + "]");
            numTotalPedidos += clientes.get(cliente);
        }
        System.out.println("***Número total de pedidos: " + numTotalPedidos);
    }
}
