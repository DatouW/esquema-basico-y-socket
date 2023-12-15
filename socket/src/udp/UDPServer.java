package udp;

import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class UDPServer {
    @Test
    public void upd1() throws IOException {

        //1. Crear un objeto de socket y registrar el puerto
        DatagramSocket socket = new DatagramSocket(8888);
        System.out.println("=====UPD Server running on port 8888 =====");

        //2. Crear un objeto para recibir datos
        byte[] buffer = new byte[1024 * 64];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);

        while (true) {
            //3. Esperar y recibir datos
            socket.receive(paquete);

            //4. Extraer los datos recibidos
            // Leer la cantidad exacta de datos recibidos
            int len = paquete.getLength();
            String data = new String(buffer, 0, len);
            System.out.println("Sender ip: " + paquete.getAddress() + ", port: " + paquete.getPort() + ", message: " + data);
        }

    }
}


