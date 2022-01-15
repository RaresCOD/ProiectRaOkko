package ro.ubbcluj.map.proiectraokko4.utils;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MessageAlert {
    public static void showMessage(Stage owner, String header, String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.initOwner(owner);
        Window window = alert.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(e -> alert.hide());
        alert.showAndWait();
    }

    public static void showErrorMessage(Stage owner, String text){
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.initOwner(owner);
        message.setTitle("Error");
        message.setContentText(text);
        message.showAndWait();
    }
}

