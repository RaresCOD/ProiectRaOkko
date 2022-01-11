//package ro.ubbcluj.map.proiectraokko4;
//
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.Scene;
//import javafx.scene.control.PasswordField;
//import javafx.scene.control.TextField;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;
//import ro.ubbcluj.map.proiectraokko4.service.UtilizatorService;
//
//import java.io.IOException;
//
//public class LoginController {
//
//    UtilizatorService service;
//
//    @FXML
//    private Text actiontarget;
//    @FXML
//    private TextField usernameField;
//    @FXML
//    private PasswordField passwordField;
//
//    public void setService(UtilizatorService service) {
//        this.service = service;
//    }
//
//    public void initialize(){
//
//    }
//
//    @FXML
//    public void handleSignInButton(ActionEvent actionEvent){
//        actiontarget.setText("Sign in button pressed");
//        String username = usernameField.getText();
//        System.out.println(username);
//        Long id = service.Login(username);
//        if( id != null) {
//            try{
//                FXMLLoader fxmlLoader = new FXMLLoader();
//                fxmlLoader.setLocation(getClass().getResource("views/UserInterface.fxml"));
//                Scene scene = new Scene(fxmlLoader.load(), 630, 400);
//                Stage stage = new Stage();
//                stage.setTitle("New Window");
//                stage.setScene(scene);
//                UserInterface userInterface = fxmlLoader.getController();
//                userInterface.setService(service, id);
//                stage.show();
//                ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            actiontarget.setText("Username invalid");
//        }
//
//    }
//}
