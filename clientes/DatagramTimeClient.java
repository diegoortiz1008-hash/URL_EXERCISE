package clientes;

import java.net.*;
import java.util.concurrent.*;
import java.util.logging.*;

public class DatagramTimeClient {

    private static final int TIMEOUT_SECONDS = 3;
    private static final int INTERVAL_SECONDS = 5;

    public static void main(String[] args) {
        // Pool de 1 hilo para controlar el timeout
        ExecutorService pool = Executors.newFixedThreadPool(1);
        String lastTime = "Sin hora aún";

        System.out.println("Cliente iniciado. Pidiendo hora cada "
            + INTERVAL_SECONDS + " segundos...");

        while (true) {
            // Tarea: pedir la hora al servidor
            Future<String> tarea = pool.submit(() -> {
                byte[] buf = new byte[256];
                try (DatagramSocket socket = new DatagramSocket()) {
                    socket.setSoTimeout(TIMEOUT_SECONDS * 1000);

                    InetAddress address = InetAddress.getByName("127.0.0.1");

                    // Enviar solicitud
                    DatagramPacket request = new DatagramPacket(
                        buf, buf.length, address, 4445);
                    socket.send(request);

                    // Esperar respuesta
                    DatagramPacket response = new DatagramPacket(buf, buf.length);
                    socket.receive(response);

                    return new String(response.getData(), 0, response.getLength());
                }
            });

            try {
                // Espera máximo TIMEOUT_SECONDS la respuesta
                String newTime = tarea.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                lastTime = newTime;
                System.out.println("Hora actualizada: " + lastTime);
            } catch (TimeoutException e) {
                System.out.println("Servidor no disponible. "
                    + "Manteniendo última hora: " + lastTime);
                tarea.cancel(true);
            } catch (Exception e) {
                System.out.println("Error de conexión. "
                    + "Manteniendo última hora: " + lastTime);
            }

            // Espera 5 segundos antes de la siguiente solicitud
            try {
                Thread.sleep(INTERVAL_SECONDS * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}