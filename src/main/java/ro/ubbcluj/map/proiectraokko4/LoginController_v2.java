package ro.ubbcluj.map.proiectraokko4;

import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.ValidationException;
import ro.ubbcluj.map.proiectraokko4.service.FriendshipService;
import ro.ubbcluj.map.proiectraokko4.service.MessageService;
import ro.ubbcluj.map.proiectraokko4.service.UtilizatorService;
import ro.ubbcluj.map.proiectraokko4.utils.Crypt;

import java.io.IOException;

public class LoginController_v2 {

    UtilizatorService userService;
    FriendshipService friendshipService;
    MessageService messageService;

    private String pass = "", passReg = "";

    @FXML
    private AnchorPane LoginPage, RegisterPage;
    @FXML
    private TextArea username, password, username1, password1, firstname, lastname;
    @FXML
    private Text errorText;
    @FXML
    private JFXCheckBox VP, VP1;

    public void setService(UtilizatorService userService, FriendshipService friendshipService, MessageService messageService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        LoginPage.setVisible(true);
        RegisterPage.setVisible(false);
    }

    private void hidePassword() {

    }

    void initialize() {
    }



    public void handleLoginButton(ActionEvent actionEvent) {
        String userName = username.getText();
        Long id = userService.Login(userName, pass);
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

    public void togglevisiblePassrod(ActionEvent actionEvent) {
        if(VP.isSelected()){
            password.setText(pass);
        } else {
            String rez = "";
            for(int i=0;i<pass.length();i++)
                rez += "*";
            password.setText(rez);
        }
    }

    public void changeValue(KeyEvent keyEvent) {
        String ch = keyEvent.getCharacter();
        if(keyEvent.getCode() == KeyCode.BACK_SPACE) {
            if(pass.length()>0) {
                StringBuffer sb= new StringBuffer(pass);
                sb.deleteCharAt(sb.length()-1);
                pass = sb.toString();
            }
        } else {
            pass+=ch;
        }

        String rez = "";
        for(int i=0;i<pass.length();i++)
            rez += "*";
        password.setText(rez);
    }
    public void togglevisiblePassrod1(ActionEvent actionEvent) {
        if(VP1.isSelected()){
            password1.setText(passReg);
        } else {
            String rez = "";
            for(int i=0;i<passReg.length();i++)
                rez += "*";
            password1.setText(rez);
        }
    }

    public void changeValue1(KeyEvent keyEvent) {
        String ch = keyEvent.getCharacter();
        if(keyEvent.getCode().equals(KeyCode.BACK_SPACE)) {
            if(passReg.length()>0) {
                StringBuffer sb= new StringBuffer(passReg);
                sb.deleteCharAt(sb.length()-1);
                passReg = sb.toString();
            }
        } else {
            passReg+=ch;
        }

        String rez = "";
        for(int i=0;i<passReg.length();i++)
            rez += "*";
        password1.setText(rez);
    }

    public void handleOpenRegister(MouseEvent mouseEvent) {
        LoginPage.setVisible(false);
        RegisterPage.setVisible(true);
    }

    public void handleRegisterButton(ActionEvent actionEvent) {
        String firstName = firstname.getText();
        String lastName = lastname.getText();
        String username = username1.getText();
        try{
            String salt = Crypt.gensalt(12);
            passReg = Crypt.hashpw(passReg, salt);
            Utilizator user = userService.addUtilizator(username, firstName, lastName, passReg);
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("views/home_v3.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                UI_v2_Controller userInterface = fxmlLoader.getController();
                userInterface.setService(userService, friendshipService, messageService, user.getId());
                stage.show();
                ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
}
