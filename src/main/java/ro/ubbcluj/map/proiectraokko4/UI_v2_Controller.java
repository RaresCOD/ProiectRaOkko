package ro.ubbcluj.map.proiectraokko4;

import com.jfoenix.controls.JFXDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.w3c.dom.events.MouseEvent;
import ro.ubbcluj.map.proiectraokko4.service.UtilizatorService;

import java.io.IOException;

public class UI_v2_Controller {

    UtilizatorService service;


    @FXML
    private ImageView SearchImg, HomeImg, ChatImg;

    @FXML
    private AnchorPane SearchPage, HomePage, ChatPage, LoginPage;
    @FXML
    private TextArea username, password;

    public void setService(UtilizatorService service) {
        this.service = service;
    }

    void initialize() {

    }


    public void handleButtonAction(javafx.scene.input.MouseEvent mouseEvent) {
        if(mouseEvent.getTarget() == SearchImg) {
            SearchPage.setVisible(true);
            ChatPage.setVisible(false);
            HomePage.setVisible(false);
        } else if (mouseEvent.getTarget() == HomeImg) {
            HomePage.setVisible(true);
            ChatPage.setVisible(false);
            SearchPage.setVisible(false);
        } else if (mouseEvent.getTarget() == ChatImg) {
            ChatPage.setVisible(true);
            HomePage.setVisible(false);
            SearchPage.setVisible(false);
        }
    }


    public void handleLoginButton(ActionEvent actionEvent) {
        String userName = username.getText();
        Long id = service.Login(userName);
        if( id != null) {
            HomePage.setVisible(true);
            ChatPage.setVisible(false);
            SearchPage.setVisible(false);
            LoginPage.setVisible(false);
        } else {

        }
    }
}
