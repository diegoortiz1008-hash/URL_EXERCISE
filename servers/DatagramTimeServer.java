package servers;

import java.io.IOException;
import java.net.*;
import java.util.Date;
import java.util.logging.*;

public class DatagramTimeServer {

    DatagramSocket socket;

    public DatagramTimeServer() {
        try {
            socket = new DatagramSocket(4445);
            System.out.println("Servidor de hora escuchando en puerto 4445...");
        } catch (SocketException ex) {
            Logger.getLogger(DatagramTimeServer.class.getName())
                .log(Level.SEVERE, null, ex);
        }
    }

    public void startServer() {
        byte[] buf = new byte[256];

        // Atiende múltiples solicitudes
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet); // espera solicitud

                // Responde con la hora actual
                String dString = new Date().toString();
                buf = dString.getBytes();

                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);

                System.out.println("Hora enviada: " + dString);

                buf = new byte[256]; // resetea el buffer
            } catch (IOException ex) {
                Logger.getLogger(DatagramTimeServer.class.getName())
                    .log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        DatagramTimeServer ds = new DatagramTimeServer();
        ds.startServer();
    }
}