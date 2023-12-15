package tcp.server;

import java.net.Socket;
import java.util.*;

public class CheckClientConnections extends TimerTask implements Subject{
    private Map<String, ClientHandlerThread> clientConnections; // Assuming this is your map of client sockets
    Vector<Observer> observers = new Vector<>();
    public CheckClientConnections(Map<String, ClientHandlerThread> clientConnections) {
        this.clientConnections = clientConnections;
    }

    @Override
    public void run() {
        System.out.println("checking connections...");
        Iterator<Map.Entry<String, ClientHandlerThread>> iterator = clientConnections.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ClientHandlerThread> entry = iterator.next();
            Socket socket = entry.getValue().getSocket();
            if (socket.isClosed() || !socket.isConnected()) {
                System.out.println("Client with ID " + entry.getKey() + " has disconnected");
                notifiyObservers(entry.getKey());
                iterator.remove(); // Uso de iterador seguro para evitar ConcurrentModificationException
            }
        }
    }

    public static void startChecking(Map<String, ClientHandlerThread> clientConnections) {
        Timer timer = new Timer();
        timer.schedule(new CheckClientConnections(clientConnections), 0, 1000);
    }

    @Override
    public void add(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void delete(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifiyObservers(Object o) {
        for (Observer observer : observers) {
            observer.update(Event.ON_DISCONNECT,o);
        }
    }
}
