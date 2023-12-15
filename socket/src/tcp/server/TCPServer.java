package tcp.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class TCPServer implements Observer {
    private ServerSocket serverSocket;
    private Map<String, ClientHandlerThread> clients = new HashMap<>();

    public TCPServer(int port)  {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("server listening on port " + port + "...");
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    private void start(){
        ConnectionThread accept = new ConnectionThread(serverSocket);
        accept.add(this);
        CheckClientConnections.startChecking(clients);
        accept.start();
    }

    @Override
    public void update(Event e, Object o) {
        switch (e){
            case ON_CONNECT:
                onConnect(o);
                break;
            case ON_DISCONNECT:
                onDisconnect(o);
                break;
        }
    }

    public void onConnect(Object o) {

        // Crear un nuevo hilo para ejecutar la lógica de onConnect
        Thread onConnectThread = new Thread(() -> {
            Socket socket = (Socket) o;
            String id = UUID.randomUUID().toString();
            ClientHandlerThread client = new ClientHandlerThread(socket);
            clients.put(id, client);
            client.start();
        });
        // Iniciar el nuevo hilo
        onConnectThread.start();
    }

    public void onDisconnect(Object id) {
        // Crear un nuevo hilo para ejecutar la lógica de onDisconnect
        Thread onDisconnectThread = new Thread(() -> {
            clients.remove(id);
        });
        // Iniciar el nuevo hilo
        onDisconnectThread.start();
    }
}
