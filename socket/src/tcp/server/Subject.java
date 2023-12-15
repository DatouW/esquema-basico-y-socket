package tcp.server;

import java.net.Socket;

public interface Subject {
    void add(Observer o);
    void delete(Observer o);
    void notifiyObservers(Object o);
}

