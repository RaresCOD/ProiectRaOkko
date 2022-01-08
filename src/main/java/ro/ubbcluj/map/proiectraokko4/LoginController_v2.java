package ro.ubbcluj.map.proiectraokko4;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ro.ubbcluj.map.proiectraokko4.service.FriendshipService;
import ro.ubbcluj.map.proiectraokko4.service.MessageService;
import ro.ubbcluj.map.proiectraokko4.service.UtilizatorService;

import java.io.IOException;

public class LoginController_v2 {

    UtilizatorService userService;
    FriendshipService friendshipService;
    MessageService messageService;

    @FXML
    private TextArea username, password;
    @FXML
    private Text errorText;

    public void setService(UtilizatorService userService, FriendshipService friendshipService, MessageService messageService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
    }

    void initialize() {

    }

    public void handleLoginButton(ActionEvent actionEvent) {
        String userName = username.getText();
        Long id = userService.Login(userName);
        if( id != null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("views/home_v3.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                UI_v2_Controller userInterface = fxmlLoader.getController();
                userInterface.setService(userService, friendshipService, messageService, id);
                stage.show();
                ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            errorText.setText("Username or password is invalid");
        }
    }

}
