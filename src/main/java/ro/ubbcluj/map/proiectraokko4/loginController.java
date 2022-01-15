package ro.ubbcluj.map.proiectraokko4;

import com.jfoenix.controls.JFXCheckBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ro.ubbcluj.map.proiectraokko4.domain.User;
import ro.ubbcluj.map.proiectraokko4.domain.validators.ValidationException;
import ro.ubbcluj.map.proiectraokko4.service.*;
import ro.ubbcluj.map.proiectraokko4.utils.Crypt;

import java.io.IOException;

public class loginController {

    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    EventService eventService;
    RefreshThreadService refreshService;

    @FXML
    private AnchorPane LoginPage, RegisterPage;
    @FXML
    private TextField username, usernameRegister, firstname, lastname, passwordVisible, passwordRegisterVisible;
    @FXML
    private Text errorText, errorTextRegister;
    @FXML
    private JFXCheckBox VP, VPRegister;
    @FXML
    private PasswordField password, passwordRegister;

    public void setService(UserService userService, FriendshipService friendshipService, MessageService messageService, EventService eventService, RefreshThreadService refreshService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.eventService = eventService;
        this.refreshService = refreshService;
        LoginPage.setVisible(true);
        RegisterPage.setVisible(false);
    }

    public void handleLoginButton(ActionEvent actionEvent) {
        String userName = username.getText();
        Long id = userService.Login(userName, password.getText());
        if(id != null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("views/main.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                mainController userInterface = fxmlLoader.getController();
                userInterface.setService(userService, friendshipService, messageService, eventService, refreshService, id);
                stage.show();
                ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            errorText.setText("Username or password is invalid");
        }
    }

    public void toggleVisiblePassword(ActionEvent actionEvent) {
        if(VP.isSelected()){
            password.setVisible(false);
            passwordVisible.setVisible(true);
            passwordVisible.setText(password.getText());
        } else {
            password.setVisible(true);
            passwordVisible.setVisible(false);
            password.setText(passwordVisible.getText());
        }
    }

    public void toggleVisiblePasswordRegister(ActionEvent actionEvent) {
        if(VPRegister.isSelected()){
            passwordRegister.setVisible(false);
            passwordRegisterVisible.setVisible(true);
            passwordRegisterVisible.setText(passwordRegister.getText());
        } else {
            passwordRegister.setVisible(true);
            passwordRegisterVisible.setVisible(false);
            passwordRegister.setText(passwordRegisterVisible.getText());
        }
    }
    
    public void handleOpenRegister(MouseEvent event) {
        LoginPage.setVisible(false);
        RegisterPage.setVisible(true);
    }

    public void handleOpenLogin(MouseEvent event){
        LoginPage.setVisible(true);
        RegisterPage.setVisible(false);
    }

    public void handleRegisterButton(ActionEvent actionEvent) {
        String firstName = firstname.getText();
        String lastName = lastname.getText();
        String username = usernameRegister.getText();
        String pass;
        if(VPRegister.isSelected()) pass = passwordRegisterVisible.getText();
        else pass = passwordRegister.getText();
        try{
            String salt = Crypt.gensalt(12);
            pass = Crypt.hashpw(pass, salt);
            User user = userService.addUtilizator(username, firstName, lastName, pass);
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("views/main.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                mainController userInterface = fxmlLoader.getController();
                userInterface.setService(userService, friendshipService, messageService, eventService, refreshService, user.getId());
                stage.setResizable(false);
                stage.show();
                ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ValidationException e) {
            errorTextRegister.setText(e.getMessage());
        }
    }
}
