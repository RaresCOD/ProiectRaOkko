package ro.ubbcluj.map.proiectraokko4;

import com.jfoenix.controls.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ro.ubbcluj.map.proiectraokko4.Message.Message;
import ro.ubbcluj.map.proiectraokko4.domain.Prietenie;
import ro.ubbcluj.map.proiectraokko4.domain.Tuple;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
import ro.ubbcluj.map.proiectraokko4.service.UtilizatorService;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UI_v2_Controller {

    UtilizatorService service;
    ObservableList<String> model = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelUsers = FXCollections.observableArrayList();
    private Long UserId;
    ObservableList<String> modelUsersFriendRequests = FXCollections.observableArrayList();
    ObservableList<Message> modelChatMsg = FXCollections.observableArrayList();
    private String friendUM, UM;
    private List<String> friends = new ArrayList<>();


    @FXML
    private ImageView SearchImg, HomeImg, ChatImg;
    @FXML
    private Text TextUsername, test, Username, Chats, Friends;
    @FXML
    private JFXTextArea MsgText, SearchUsers;
    @FXML
    private AnchorPane SearchPage, HomePage, ChatPage, EditPage, BottomBar, TopBar, FriendRequestPage, Chat;
    @FXML
    private JFXListView<Utilizator> SearchList;
    @FXML
    private JFXListView<String> ChatList;
    @FXML
    private JFXListView<Message> ChatMsg;
    @FXML
    private JFXListView<String> FRList;





    public void setService(UtilizatorService service, Long userId) {

        this.service = service;
        this.UserId = userId;
        this.UM = service.finduser(UserId).getUsername();
        load();
    }

    private void load() {
        List<Tuple<Utilizator, Date>> friends = service.getFriends(UserId);
        List<String> fList = service.allChats(UserId);
        model.setAll(fList);

        Iterable<Utilizator> users = service.getAll();
        List<Utilizator> uList = StreamSupport.stream(users.spliterator(), false)
                .collect(Collectors.toList());

        modelUsers.setAll(uList);

        List<Tuple<Utilizator, Prietenie>> friendRequests = service.getFriendRequests(UserId);
        List<String> frlist = friendRequests.stream()
                        .map(x -> x.getLeft().getUsername())
                        .collect(Collectors.toList());
        modelUsersFriendRequests.setAll(frlist);

        Utilizator user = service.finduser(UserId);
        TextUsername.setText(user.getUsername());
        List<Message> allMsgs = service.getAllMessages(UserId);

        modelChatMsg.setAll(allMsgs);
        SearchUsers.textProperty().addListener(x -> handleUsersFilter());
        Username.setText(UM);
        Chats.setText(String.valueOf(service.allChats(UserId).size()));
        Friends.setText(String.valueOf(service.getFriends(UserId).size()));
    }

    private void startHP() {
        HomePage.setVisible(true);
        Chat.setVisible(false);
        TopBar.setVisible(true);
        BottomBar.setVisible(true);
        ChatPage.setVisible(false);
        SearchPage.setVisible(false);
        EditPage.setVisible(false);
        FriendRequestPage.setVisible(false);
    }

    private void startChat() {
        Chat.setVisible(true);
        SearchPage.setVisible(false);
        ChatPage.setVisible(false);
        HomePage.setVisible(false);
        EditPage.setVisible(false);
        FriendRequestPage.setVisible(false);
    }

    public void initialize() {
        startHP();

        ChatList.setItems(model);
        ChatMsg.setItems(modelChatMsg);
        ChatMsg.setCellFactory(x -> new ListCell<Message>() {
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getFrom().getUsername().concat(": " + item.getMsg()));
            }
        });
        SearchList.setItems(modelUsers);
        SearchList.setCellFactory(x -> new ListCell<Utilizator>(){
            protected void updateItem(Utilizator item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getUsername());
            }
        });
        FRList.setItems(modelUsersFriendRequests);

        SearchList.setExpanded(true);
        SearchList.depthProperty().set(1);
    }



    public void handleButtonAction(javafx.scene.input.MouseEvent mouseEvent) {
        if(mouseEvent.getTarget() == SearchImg) {
            SearchPage.setVisible(true);
            ChatPage.setVisible(false);
            HomePage.setVisible(false);
            EditPage.setVisible(false);
            Chat.setVisible(false);
            FriendRequestPage.setVisible(false);
        } else if (mouseEvent.getTarget() == HomeImg) {
            HomePage.setVisible(true);
            ChatPage.setVisible(false);
            SearchPage.setVisible(false);
            EditPage.setVisible(false);
            Chat.setVisible(false);
            FriendRequestPage.setVisible(false);
        } else if (mouseEvent.getTarget() == ChatImg) {
            ChatPage.setVisible(true);
            HomePage.setVisible(false);
            SearchPage.setVisible(false);
            EditPage.setVisible(false);
            Chat.setVisible(false);
            FriendRequestPage.setVisible(false);
        }
    }

    public void handleFriendRequests(MouseEvent mouseEvent) {
        FriendRequestPage.setVisible(true);
        EditPage.setVisible(false);
        SearchPage.setVisible(false);
        ChatPage.setVisible(false);
        Chat.setVisible(false);
        HomePage.setVisible(false);
    }

    public void handleEditProfile(ActionEvent actionEvent) {
        EditPage.setVisible(true);
        SearchPage.setVisible(false);
        ChatPage.setVisible(false);
        HomePage.setVisible(false);
        Chat.setVisible(false);
        FriendRequestPage.setVisible(false);
    }

    public void doThis(MouseEvent mouseEvent) {
        String currentSelection = ChatList.getSelectionModel().getSelectedItem();

        startChat();
        friends.clear();
        List<String> friendsNou = new ArrayList<>(List.of(currentSelection.split(" ")));
        friends.addAll(friendsNou);
        System.out.println(friends);
        friends.add(UM);
        handleChatFilter();
    }

    public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }

    private void handleChatFilter() {
        Predicate<Message> p1 = x -> {
            String username = x.getFrom().getUsername();
            List<Utilizator> to = x.getTo();
            List<String> grup = to.stream()
                    .map(Utilizator::getUsername)
                    .collect(Collectors.toList());
            grup.add(username);
            if(listEqualsIgnoreOrder(grup, friends)) {
                return  true;
            }
            return false;
        };

        modelChatMsg.setAll(service.getAllMessages(UserId)
                .stream()
                .filter(p1)
                .collect(Collectors.toList()));
    }

    private void handleUsersFilter() {
        Predicate<Utilizator> p = x -> x.getUsername().startsWith(SearchUsers.getText());
        Iterable<Utilizator> users = service.getAll();
        List<Utilizator> uList = StreamSupport.stream(users.spliterator(), false)
                .filter(p)
                .collect(Collectors.toList());
        modelUsers.setAll(uList);
    }

    public void handleSendMessage(MouseEvent mouseEvent) {
        String msg = MsgText.getText();
        Long FriendId = service.getUserId(friendUM);
        if(friends.size() == 1) {
            service.addMessage(UserId, FriendId, msg);
        } else {
            List<Long> to = new ArrayList<>();
            for(String curent:friends) {
                if(!curent.equals(UM))
                    to.add(service.getUserId(curent));
            }
            service.addGroupMessage(UserId, to, msg);
        }

        MsgText.clear();
    }
}
