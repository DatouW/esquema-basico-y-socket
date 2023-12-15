package com.hui.tcp_client;

import java.io.*;

public class StreamUtils {

    public static String readLine(PushbackInputStream in) throws IOException {
        char buf[] = new char[128];
        int room = buf.length;
        int offset = 0;
        int c;
        loop:
        while (true) {
            switch (c = in.read()) {
                case -1:
                case '\n':
                    break loop;
                case '\r':
                    int c2 = in.read();
                    if ((c2 != '\n') && (c2 != -1)) in.unread(c2);
                    break loop;
                default:
                    if (--room < 0) {
                        char[] lineBuffer = buf;
                        buf = new char[offset + 128];
                        room = buf.length - offset - 1;
                        System.arraycopy(lineBuffer, 0, buf, 0, offset);

                    }
                    buf[offset++] = (char) c;
                    break;
            }
        }
        if ((c == -1) && (offset == 0)) return null;
        return String.copyValueOf(buf, 0, offset);
    }

    public static byte[] streamToByteArray(InputStream is) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        byte[] array = bos.toByteArray();
        bos.close();
        return array;
    }

    public static String streamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line + "\r\n");
        }
        return builder.toString();

    }

    public static void writeFile(String path,Object o) throws IOException {
        File file = new File(path);

        // Verificar si el archivo existe, si no, crearlo
        if (!file.exists()) {
            file.createNewFile();
        }

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(o);
        oos.close();
    }

    public static Object readFile(String path) throws IOException, ClassNotFoundException {
        File file = new File(path);

        // Verificar si el archivo existe
        if (!file.exists()) {
            System.out.println("El archivo no existe.");
            return null;
        }

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
        Object db =  ois.readObject();
        ois.close();
        return db;
    }



}



