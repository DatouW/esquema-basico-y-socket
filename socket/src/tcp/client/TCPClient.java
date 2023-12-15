package tcp.client;

import org.junit.Test;
import tcp.server.StreamUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.Socket;


public class TCPClient {
    public static final int serverPort = 9999;

    @Test
    public void client1() throws Exception {
        InetAddress serverIp = InetAddress.getByName("192.168.56.1");
        //
        String path = "src\\image1.jpg";
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(path));
        // convertir stream a byte
        byte[] bytes = StreamUtils.streamToByteArray(new FileInputStream(path));

        Socket socket = new Socket(serverIp, serverPort);

        //
        BufferedOutputStream bufferedOutputStream =  new BufferedOutputStream(socket.getOutputStream());
        bufferedOutputStream.write(bytes);

        socket.shutdownOutput();
        System.out.println("Cliente envía la imagen con éxito");

        String str = StreamUtils.streamToString(socket.getInputStream());
        System.out.println(str);

        socket.close();
        bufferedOutputStream.close();
        bufferedInputStream.close();
        System.out.println("client exit");
    }



}
