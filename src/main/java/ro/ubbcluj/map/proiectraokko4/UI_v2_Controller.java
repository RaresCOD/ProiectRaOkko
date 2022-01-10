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
import ro.ubbcluj.map.proiectraokko4.service.FriendshipService;
import ro.ubbcluj.map.proiectraokko4.service.MessageService;
import ro.ubbcluj.map.proiectraokko4.service.UtilizatorService;
import ro.ubbcluj.map.proiectraokko4.utils.observer.Observer;
import ro.ubbcluj.map.proiectraokko4.utils.observer.TypeOfObservation;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UI_v2_Controller implements Observer {

    UtilizatorService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    private Long UserId;

    ObservableList<String> FriendsModel = FXCollections.observableArrayList();
    ObservableList<String> UsersModel = FXCollections.observableArrayList();
    ObservableList<String> FriendRequestsModel = FXCollections.observableArrayList();

    @FXML
    private ImageView SearchImg, HomeImg, ChatImg;
    @FXML
    private Text TextUsername;
    @FXML
    private AnchorPane SearchPage, HomePage, ChatPage, EditPage, BottomBar, TopBar, FriendRequestPage;
    @FXML
    private JFXListView<String> SearchList, ChatList;
    @FXML
    private JFXListView<String> FRList;



    public void setService(UtilizatorService userService, FriendshipService friendshipService, MessageService messageService, Long userId) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.UserId = userId;
        load();
    }

    private void load()
    {
        Utilizator user = userService.finduser(UserId);
        TextUsername.setText(user.getUsername());

        initFriendsModel();
        initUsersModel();
        initFriendRequestsModel();
    }

    private void initFriendsModel()
    {
        List<Tuple<Utilizator, Date>> friends = friendshipService.getFriends(UserId);
        List<String> fList = friends.stream()
                .map(x -> x.getLeft().getUsername())
                .collect(Collectors.toList());
        FriendsModel.setAll(fList);
    }

    private void initUsersModel()
    {
        Iterable<Utilizator> users = userService.getAll();
        List<String> uList = StreamSupport.stream(users.spliterator(), false)
                .map(x -> x.getUsername())
                .collect(Collectors.toList());
        UsersModel.setAll(uList);
    }

    private void initFriendRequestsModel()
    {
        List<Tuple<Utilizator, Prietenie>> friendRequests = friendshipService.getFriendRequests(UserId);
        List<String> frlist = friendRequests.stream()
                .map(x -> x.getLeft().getUsername())
                .collect(Collectors.toList());
        FriendRequestsModel.setAll(frlist);
    }

    private void initHomePage() {
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
        initHomePage();

        ChatList.setItems(FriendRequestsModel);
        SearchList.setItems(UsersModel);
        FRList.setItems(FriendRequestsModel);

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

    @Override
    public void update(TypeOfObservation type) {
        switch(type)
        {
            case USER:
                initUsersModel();
                break;
            case FRIENDSHIP:
                initFriendRequestsModel();
                initFriendsModel();
                break;
            case MESSAGE:
                break;
        }
    }
}
