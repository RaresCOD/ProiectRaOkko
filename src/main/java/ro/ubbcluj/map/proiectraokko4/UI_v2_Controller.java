package ro.ubbcluj.map.proiectraokko4;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ro.ubbcluj.map.proiectraokko4.domain.Prietenie;
import ro.ubbcluj.map.proiectraokko4.domain.Tuple;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
import ro.ubbcluj.map.proiectraokko4.service.UtilizatorService;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UI_v2_Controller {

    UtilizatorService service;
    ObservableList<String> model = FXCollections.observableArrayList();
    ObservableList<String> modelUsers = FXCollections.observableArrayList();
    private Long UserId;
    ObservableList<String> modelUsersFriendRequests = FXCollections.observableArrayList();

    @FXML
    private ImageView SearchImg, HomeImg, ChatImg;
    @FXML
    private Text TextUsername;
    @FXML
    private AnchorPane SearchPage, HomePage, ChatPage, EditPage, BottomBar, TopBar, FriendRequestPage;
    @FXML
    private JFXListView<String> SearchList,ChatList;
    @FXML
    private JFXListView<String> FRList;



    public void setService(UtilizatorService service, Long userId) {

        this.service = service;
        this.UserId = userId;
        load();
    }

    private void load() {
        List<Tuple<Utilizator, Date>> friends = service.getFriends(UserId);
        List<String> fList = friends.stream()
                .map(x -> x.getLeft().getUsername())
                .collect(Collectors.toList());
        model.setAll(fList);

        Iterable<Utilizator> users = service.getAll();
        List<String> uList = StreamSupport.stream(users.spliterator(), false)
                .map(x -> x.getUsername())
                .collect(Collectors.toList());
        modelUsers.setAll(uList);

        List<Tuple<Utilizator, Prietenie>> friendRequests = service.getFriendRequests(UserId);
        List<String> frlist = friendRequests.stream()
                        .map(x -> x.getLeft().getUsername())
                        .collect(Collectors.toList());
        modelUsersFriendRequests.setAll(frlist);

        Utilizator user = service.finduser(UserId);
        TextUsername.setText(user.getUsername());
    }

    private void startHP() {
        HomePage.setVisible(true);
        TopBar.setVisible(true);
        BottomBar.setVisible(true);
        ChatPage.setVisible(false);
        SearchPage.setVisible(false);
        EditPage.setVisible(false);
        FriendRequestPage.setVisible(false);
    }

    public void initialize() {
//        ChatList.setItems(model);
//        ChatList.getItems().add(new Utilizator("a","a", "a"));
        startHP();

        ChatList.setItems(model);
        SearchList.setItems(modelUsers);
        FRList.setItems(modelUsersFriendRequests);

    }


    public void handleButtonAction(javafx.scene.input.MouseEvent mouseEvent) {
        if(mouseEvent.getTarget() == SearchImg) {
            SearchPage.setVisible(true);
            ChatPage.setVisible(false);
            HomePage.setVisible(false);
            EditPage.setVisible(false);
            FriendRequestPage.setVisible(false);
        } else if (mouseEvent.getTarget() == HomeImg) {
            HomePage.setVisible(true);
            ChatPage.setVisible(false);
            SearchPage.setVisible(false);
            EditPage.setVisible(false);
            FriendRequestPage.setVisible(false);
        } else if (mouseEvent.getTarget() == ChatImg) {
            ChatPage.setVisible(true);
            HomePage.setVisible(false);
            SearchPage.setVisible(false);
            EditPage.setVisible(false);
            FriendRequestPage.setVisible(false);
        }
    }


//    public void handleLoginButton(ActionEvent actionEvent) {
//        String userName = username.getText();
//        Long id = service.Login(userName);
//        if( id != null) {
//            HomePage.setVisible(true);
//            ChatPage.setVisible(false);
//            SearchPage.setVisible(false);
//            LoginPage.setVisible(false);
//            EditPage.setVisible(false);
//            FriendRequestPage.setVisible(false);
//            TextUsername.setText(userName);
//            UserId = id;
//            load();
//        } else {
//            errorText.setText("Username or password is invalid");
//        }
//    }

    public void handleFriendRequests(MouseEvent mouseEvent) {
        FriendRequestPage.setVisible(true);
        EditPage.setVisible(false);
        SearchPage.setVisible(false);
        ChatPage.setVisible(false);
        HomePage.setVisible(false);
    }

    public void handleEditProfile(ActionEvent actionEvent) {
        EditPage.setVisible(true);
        SearchPage.setVisible(false);
        ChatPage.setVisible(false);
        HomePage.setVisible(false);
        FriendRequestPage.setVisible(false);
    }

    public void handleGoBack(MouseEvent mouseEvent) {

    }
}
