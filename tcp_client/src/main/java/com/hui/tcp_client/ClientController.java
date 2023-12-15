package com.hui.tcp_client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientController {
    @FXML
    private Button upBtn;
    @FXML
    private Button stopBtn;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;
    @FXML
    private TextField filenameText;
    @FXML
    private Label infoLabel;
    @FXML
    private Label sizeLabel;

    private boolean flag = false;
    private Map<String, String> db;
    private File file;
    private static String filepath = "src\\main\\resources\\db.dat";

    @FXML
    public void initialize()  {
        try {
            upBtn.setDisable(true);
            stopBtn.setDisable(true);
            filenameText.setEditable(false);
            db = (Map<String, String>) StreamUtils.readFile(filepath);
            if(db == null){
                db = new HashMap<>();
            }
            System.out.println(db);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void uploadFile() {
        flag = true;
        if (file.exists()) {
            upload(file);
        } else {
            infoLabel.setText("No existe el archivo");
        }
    }

    private void upload(File file) {
        Thread uploadThread = new Thread(() -> {
            System.out.println(db);
            try {
                int size = 56;
                String sourceid = Helper.getKeyByValue(db, file.getAbsolutePath());
                Socket socket = new Socket("192.168.56.1", 9999);
                OutputStream outStream = socket.getOutputStream();
                String head = "Content-Length=" + file.length() + ";filename=" + file.getName()
                        + ";size=" + size + ";sourceid=" + (sourceid != null ? sourceid : "") + "\r\n";
                outStream.write(head.getBytes());

                PushbackInputStream inStream = new PushbackInputStream(socket.getInputStream());
                String response = StreamUtils.readLine(inStream);
                String[] items = response.split(";");
                String responseSourceid = items[0].substring(items[0].indexOf("=") + 1);
                String position = items[1].substring(items[1].indexOf("=") + 1);
                if (sourceid == null) {//si es primera que sube el archivo
                    db.put(responseSourceid, file.getAbsolutePath());
                    StreamUtils.writeFile(filepath, db);
                }

                RandomAccessFile fileOutStream = new RandomAccessFile(file, "r");
                fileOutStream.seek(Integer.valueOf(position));
                byte[] buffer = new byte[size * 1024];
                int len;
                int length = Integer.valueOf(position);
                while (flag && (len = fileOutStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                    length += len;//longitud acumulada del archivo
                    setProgress(length);
                }
                if (length == file.length()) {
                    db.remove(responseSourceid);
                    StreamUtils.writeFile(filepath, db);
                }
                fileOutStream.close();
                outStream.close();
                inStream.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        });
        uploadThread.start();
    }

    private void setProgress(int length) {
        System.out.println("length subido: " + length);
        float num = (float) length / (float) file.length();
        System.out.println(num);
        Platform.runLater(() -> {
            progressBar.setProgress(num);
            int result = (int) (num * 100);
            progressLabel.setText(result + "%");
            sizeLabel.setText(byteToMega(length) + "/" + byteToMega(file.length()) + " MB");
            if (progressBar.getProgress() == 1) {
                infoLabel.setText("Archivo subido con exito");
            }
        });
    }

    @FXML
    public void stopUpload() {
        flag = false;
    }

    @FXML
    public void chooseFile() {
        FileChooser chooser = new FileChooser();
        file = chooser.showOpenDialog(null);
        if (file != null) {
            filenameText.setText(file.getName());
            upBtn.setDisable(false);
            stopBtn.setDisable(false);
            progressBar.setProgress(0);
            progressLabel.setText("0%");
            infoLabel.setText("");
            sizeLabel.setText("0/"+byteToMega(file.length()) +" MB");
        }
    }

    private String byteToMega(long length){
        return String.format("%.1f",length/1024/1024.0);
    }
}