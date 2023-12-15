package tcp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ConnectionThread extends Thread implements Subject{
    private ServerSocket server;
    private Vector<Observer> observers = new Vector<>();

    public ConnectionThread(ServerSocket server){
        this.server = server;
    }

    @Override
    public void run() {
        System.out.println("Acceptando conexiones....");
        try {
            //aceptar conexiones de clientes en el socket del servidor
            while (true) {
                Socket socket = server.accept();
                notifiyObservers(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            observer.update(Event.ON_CONNECT,o);
        }
    }
}
