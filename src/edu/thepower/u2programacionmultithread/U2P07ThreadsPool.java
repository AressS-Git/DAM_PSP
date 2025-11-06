package edu.thepower.u2programacionmultithread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class U2P07ThreadsPool {
    public static void main(String[] args) {
        //Mapa para llevar un conteo de las veces que un thread hace algo
        Map<String, AtomicInteger> usosThreads = new ConcurrentHashMap<>();
        //Se agrupan los threads en pools para optimizar el uso de recursos
        final int MAX_POOL_SIZE = 10;
        //Creación de pool
        ExecutorService pool = Executors.newFixedThreadPool(MAX_POOL_SIZE);

        for (int i = 0; i < 50; i++) {
            //Un thread del pool hará una tarea
            pool.submit(() -> {
                //Insertar en el mapa el nombre del thread y el número de usos (0 si es nulo y uso++ si no lo es)
                usosThreads.computeIfAbsent(Thread.currentThread().getName(), k -> new AtomicInteger()).incrementAndGet();
                System.out.println("[" + Thread.currentThread().getName() + "] manda saludos");
            });
        }
        //No acepta más trabajos y termina de forma ordenada
        pool.shutdown();
        //Join en pool
        try {
            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
        }
        //Recorrer el mapa para mostrar usos de threads
        usosThreads.forEach((key, value) -> {
            System.out.println("El thread " + key + " se ha ejecutado " + value + " veces");
        });
        //Hacer una suma de todas los valores del mapa con un stream
        System.out.println("Total ejecuciones Threads: " + usosThreads.values().stream().mapToInt(v -> v.get()).sum());
    }
}
