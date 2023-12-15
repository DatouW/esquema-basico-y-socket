package udp;

import org.junit.Test;
import tcp.server.ClientHandlerThread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;

public class UDPClient {
    public static final int receiverPort = 8888;

    @Test
    public void updclient() throws IOException, InterruptedException {
        InetAddress receiverIp = InetAddress.getByName("192.168.56.1");

        //1. Crear un objeto de socket de envío (el socket del remitente tiene un puerto por defecto)
        DatagramSocket socket = new DatagramSocket();
        System.out.println("=====Sender running on port =====" + socket.getLocalPort());

        for (int i = 0; i < 3; i++){
            String msg = "Hello world " + i;

            //2. encapsular los datos
            byte[] buffer = msg.getBytes();
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length,receiverIp , receiverPort);

            //3. Enviar los datos
            socket.send(paquete);
        }
        socket.close();
    }

    public static class ClientThreadsManager {
        private static HashMap<String, ClientHandlerThread> threads = new HashMap<>();

        public static HashMap<String, ClientHandlerThread> getThreads() {
            return threads;
        }

        public static void addClientThread(String userId, ClientHandlerThread clientHandlerThread) {
            threads.put(userId, clientHandlerThread);
        }

        public static ClientHandlerThread getClientThread(String userAddr) {
            return threads.get(userAddr);
        }


        public static void removeClientThread(String clientAddr) {
            threads.remove(clientAddr);
        }

        public static String getOnlineUser() {

            Iterator<String> iterator = threads.keySet().iterator();
            String onlineUserList = "";
            while (iterator.hasNext()) {
                onlineUserList += iterator.next() + " ";
            }
            return onlineUserList;
        }

    }
}
