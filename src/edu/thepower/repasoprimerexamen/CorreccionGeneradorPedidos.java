package edu.thepower.repasoprimerexamen;

import java.security.Key;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CorreccionGeneradorPedidos {
    static class Pedido {
        private static AtomicInteger generadorID;

        //Iniciador de clase: Se ejecuta una vez al iniciar el primer objeto
        static {
            generadorID = new AtomicInteger(0);
        }

        private String id;
        private String cliente;
        private long fecha;

        public Pedido(String cliente) {
            this.id = String.valueOf(generadorID.incrementAndGet());
            this.cliente = cliente;
            this.fecha = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "Pedido cuyo id es: " + id + " || cliente: " + cliente + " || fecha: " + fecha;
        }
    }

    public static void main(String[] args) {
        final int MAX_THREADS = 10;
        final int NUM_PEDIDOS = 10;
        List<Pedido> pedidos = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        Map<String, AtomicInteger> pedidosPorCliente = new ConcurrentHashMap<>();
        Random random = new Random();
        for (int i = 0; i < MAX_THREADS; i++) {
            Thread hilo = new Thread(() -> {
                for (int j = 0; j < NUM_PEDIDOS; j++) {
                    String cliente = "Cliente-" + random.nextInt(0, 10);
                    Pedido pedido = new Pedido("Cliente-" + random.nextInt(0, 10));
                    synchronized (pedidos){
                        pedidos.add(pedido);
                    }
                    pedidosPorCliente.putIfAbsent(cliente, new AtomicInteger());
                }
            });
            //Ejecución del hilo y almacenamiento en lista de Threads
            hilo.start();
            threads.add(hilo);
        }
        System.out.println("Todos lo threads está en ejecución");

        //Esperar a que los threads acaben antes de mostrar estadísticas
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Todos los Threads han finalizado sus tareas");

        //Mostrar estadísticas
        System.out.println("***Pedidos realizados por los clientes");
        for (Pedido pedido : pedidos) {
            System.out.println(pedido);
        }
        System.out.println("Número total de pedidos: " + pedidos.size());

        //Mostrar estadísticas
        int contador = 0;
        System.out.println("Cantidad de pedidos por cada cliente");
        for (String cliente : pedidosPorCliente.keySet()) {
            System.out.println("El cliente " + cliente + " ha hecho " +  pedidosPorCliente.get(cliente).get() + " pedidos");
        }
    }
    //queda código para solucionarlo pero no me ha dado tiempo a copiarlo
}
