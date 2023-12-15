module com.hui.tcp_client {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.hui.tcp_client to javafx.fxml;
    exports com.hui.tcp_client;
}