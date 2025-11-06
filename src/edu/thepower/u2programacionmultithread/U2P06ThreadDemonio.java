package edu.thepower.u2programacionmultithread;

public class U2P06ThreadDemonio {

    public static void main(String[] args) {
        long tiempo = System.currentTimeMillis() + 10_000;
        Thread t1 = new Thread(() -> {
            while(tiempo > System.currentTimeMillis()) {
                System.out.println("T1:saludos");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread t2 = new Thread(() -> {
            while(tiempo > System.currentTimeMillis()) {
                System.out.println("T2:saludos");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //El demonio es un thread que desaparece cuándo los threads "normales" mueren
        Thread latido = new Thread(() -> {
            while(true) {
                System.out.println("Boom boom");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        latido.setDaemon(true);
        System.out.println("Inicio de ejecución threads");
        t1.start();
        //Se pueden declarar threads con mayor prioridad para que se ejecuten primero
        t1.setPriority(Thread.MAX_PRIORITY);
        t2.start();
        latido.start();
        System.out.println("Threads ejecutándose");
    }
}
