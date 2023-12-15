package tcp.server;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class ClientHandlerThread extends Thread {
    private Socket socket;
    private Map<String, FileLog> datas;

    public ClientHandlerThread(Socket socket) {
        this.socket = socket;
        try {
            datas = (HashMap<String, FileLog>)StreamUtils.readFile();
            if(datas == null){
                datas = new HashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return this.socket;
    }

    @Override
    public void run() {
        try {
            PushbackInputStream inStream = new PushbackInputStream(socket.getInputStream());
            //Obtener la primera línea del protocolo:
            // Content-Length=143253434;filename=xxx.3gp;size=56;sourceid=
            //Si el usuario sube un archivo por primera vez, el valor de sourceid está vacío.
            String head = StreamUtils.readLine(inStream);
            System.out.println(head);
            if (head != null) {
                handleFileUpload(head,inStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
            }
        }
    }

    private void handleFileUpload(String head, PushbackInputStream inStream) throws IOException {
        //1. se extrae el valor de cada parámetros del protocolo
        String[] items = head.split(";");
        String filelength = items[0].substring(items[0].indexOf("=") + 1);
        String filename = items[1].substring(items[1].indexOf("=") + 1);
        String size = items[2].substring(items[2].indexOf("=") + 1);
        String sourceid = items[3].substring(items[3].indexOf("=") + 1);
        String id = UUID.randomUUID().toString();
        FileLog log = null;
        if (sourceid != null && !"".equals(sourceid)) {
            id = sourceid;
            log = find(id); // Buscar si ya existe el archivo
        }

        File file;
        int position = 0;

        //si no existe un log del archivo subido, crear un registro y archivo (primera vez que sube el archivo)
        if (log == null) {
            file = createFile(filename);
            save(id, file);
        } else {
            // Si hay un registro, lee la longitud de los datos cargados.
            //obtener la ruta del archivo desde log(registro)
            file = new File(log.getPath());
            if (file.exists()) {
                position = readLogFilePosition(file);
                System.out.println("position: "+ position);
            }
        }

        //Después de que el servidor recibe la petición del cliente,
        // devuelve una respuesta al cliente: sourceid=1274773833264;position=0
        //sourceid es generado por el servidor, identificando unívocamente el archivo a subir,
        // y position indica dónde empieza el cliente a subir el archivo.
        sendResponse(id, position);

        handleFileContent(file, inStream, position,Integer.valueOf(filelength), id,Integer.valueOf(size));
    }

    private File createFile(String filename) throws IOException {
        String path = new SimpleDateFormat("yyyy/MM/dd/HH/mm").format(new Date());
        File dir = new File("file/" + path);

        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, filename);

        if (file.exists()) {
            filename = filename.substring(0, filename.indexOf(".") - 1) + dir.listFiles().length + filename.substring(filename.indexOf("."));
            file = new File(dir, filename);
        }

        return file;
    }

    private int readLogFilePosition(File file) throws IOException {
        File logFile = new File(file.getParentFile(), file.getName() + ".log");
        int position = 0;

        if (logFile.exists()) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(logFile));
            position = Integer.valueOf(properties.getProperty("length"));
        }

        return position;
    }

    private void sendResponse(String id, int position) throws IOException {
        OutputStream outStream = socket.getOutputStream();
        String response = "sourceid=" + id + ";position=" + position + "\r\n";
        outStream.write(response.getBytes());
    }

    private void handleFileContent(File file, PushbackInputStream inStream, int position, int filelength,String id, int size) throws IOException {
        RandomAccessFile fileOutStream = new RandomAccessFile(file, "rwd");
        // establecer la longitud del archivo
        if (position == 0) fileOutStream.setLength(Integer.valueOf(filelength));
        //Especifica que los datos deben escribirse desde una ubicación específica del archivo.
        fileOutStream.seek(position);

        byte[] buffer = new byte[size * 1024];
        int len;
        int length = position;

        while ((len = inStream.read(buffer)) != -1) {
            fileOutStream.write(buffer, 0, len);
            length += len;
            updateLogFile(file, length);
        }

        if (length == fileOutStream.length()) delete(id);

        fileOutStream.close();
    }

    private void updateLogFile(File file, int length) throws IOException {
        Properties properties = new Properties();
        properties.put("length", String.valueOf(length));
        FileOutputStream logFile = new FileOutputStream(new File(file.getParentFile(), file.getName() + ".log"));
        properties.store(logFile, null);//Registro de la longitud de los archivos recibidos
        logFile.close();
    }

    private FileLog find(String sourceid) {
        System.out.println("sourceid:" + sourceid +"find: " + datas.get(sourceid));
        datas.entrySet().forEach(entry -> {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        });
        return datas.get(sourceid);
    }

    //guardar el registro de archivo
    private void save(String id, File saveFile) {
        datas.put(id, new FileLog(id, saveFile.getAbsolutePath()));
        try {
            StreamUtils.writeFile(datas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //eliminar log, si el archivo ha subido completamente
    private void delete(String sourceid) {
        if (datas.containsKey(sourceid)) {
            datas.remove(sourceid);
            try {
                StreamUtils.writeFile(datas);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    @Override
//    public void run() {
//        try {
//            PushbackInputStream inStream = new PushbackInputStream(socket.getInputStream());
//            //Obtener la primera línea del protocolo:
//            // Content-Length=143253434;filename=xxx.3gp;sourceid=
//            //Si el usuario sube un archivo por primera vez, el valor de sourceid está vacío.
//            String head = StreamUtils.readLine(inStream);
//            System.out.println(head);
//            if (head != null) {
//                //se extrae el valor de cada parámetros del protocolo
//                String[] items = head.split(";");
//                String filelength = items[0].substring(items[0].indexOf("=") + 1);
//                String filename = items[1].substring(items[1].indexOf("=") + 1);
//                String sourceid = items[2].substring(items[2].indexOf("=") + 1);
//                String id = UUID.randomUUID().toString();
//                FileLog log = null;
//                if (sourceid != null && !"".equals(sourceid)) {
//                    log = find(id);//buscar si ya existe el archivo
//                }
//                File file = null;
//                int position = 0;
//                if (log == null) {//si no existe, agregar un registro
//                    String path = new SimpleDateFormat("dd/MM/yyyy/HH/mm").format(new Date());
//                    File dir = new File("file/" + path);
//                    if (!dir.exists()) dir.mkdirs();
//                    file = new File(dir, filename);
//                    if (file.exists()) {//si ya existe el nombre de archivo, cambiar de nombre
//                        filename = filename.substring(0, filename.indexOf(".") - 1) + dir.listFiles().length + filename.substring(filename.indexOf("."));
//                        file = new File(dir, filename);
//                    }
//                    save(id, file);
//                } else {// Si hay un registro, lee la longitud de los datos cargados.
//                    file = new File(log.getPath());//obtener la ruta del archivo desde log(registro)
//                    if (file.exists()) {
//                        File logFile = new File(file.getParentFile(), file.getName() + ".log");
//                        if (logFile.exists()) {
//                            Properties properties = new Properties();
//                            properties.load(new FileInputStream(logFile));
//                            position = Integer.valueOf(properties.getProperty("length"));//读取已经上传的数据长度
//                        }
//                    }
//                }
//                OutputStream outStream = socket.getOutputStream();
//                String response = "sourceid=" + id + ";position=" + position + "\r\n";
//                //服务器收到客户端的请求信息后，给客户端返回响应信息：sourceid=1274773833264;position=0
//                //sourceid由服务器端生成，唯一标识上传的文件，position指示客户端从文件的什么位置开始上传
//                outStream.write(response.getBytes());
//
//                RandomAccessFile fileOutStream = new RandomAccessFile(file, "rwd");
//                if (position == 0) fileOutStream.setLength(Integer.valueOf(filelength));//设置文件长度
//                fileOutStream.seek(position);//指定从文件的特定位置开始写入数据
//                byte[] buffer = new byte[1024];
//                int len = -1;
//                int length = position;
//                while ((len = inStream.read(buffer)) != -1) {//从输入流中读取数据写入到文件中
//                    fileOutStream.write(buffer, 0, len);
//                    length += len;
//                    Properties properties = new Properties();
//                    properties.put("length", String.valueOf(length));
//                    FileOutputStream logFile = new FileOutputStream(new File(file.getParentFile(), file.getName() + ".log"));
//                    properties.store(logFile, null);//实时记录已经接收的文件长度
//                    logFile.close();
//                }
//                if (length == fileOutStream.length()) delete(id);
//                fileOutStream.close();
//                inStream.close();
//                outStream.close();
//                file = null;
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (socket != null && !socket.isClosed()) socket.close();
//            } catch (IOException e) {
//            }
//        }
//    }

    //    @Override
//    public void run(){
//        try {
//            String target = "src\\files\\" + System.currentTimeMillis()+".png";
//            // Obtener un flujo de entrada de bytes desde el canal de comunicación del socket
//            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));
//            byte[] bytes = StreamUtils.streamToByteArray(bis);
//            bos.write(bytes);
//            System.out.println("Imagen guardada con exito！");
//
//            //
//            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            bufferedWriter.write("Imagen recibida");
//            bufferedWriter.flush();
//            socket.shutdownOutput();
//
//            bufferedWriter.close();
//            bos.close();
//            bis.close();
//        } catch (Exception e) {
//            System.out.println(socket.getRemoteSocketAddress() + ": se desconectó.");
//            e.printStackTrace();
//        }finally {
//            System.out.println(socket.getRemoteSocketAddress() + ": se desconectó.");
//            UDPClient.ClientThreadsManager.removeClientThread(socket.getRemoteSocketAddress().toString());
//        }
//
//    }
}
