package ro.ubbcluj.map.proiectraokko4;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ro.ubbcluj.map.proiectraokko4.Message.Message;
import ro.ubbcluj.map.proiectraokko4.domain.Prietenie;
import ro.ubbcluj.map.proiectraokko4.domain.ProfilePage;
import ro.ubbcluj.map.proiectraokko4.domain.Tuple;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
import ro.ubbcluj.map.proiectraokko4.service.FriendshipService;
import ro.ubbcluj.map.proiectraokko4.service.MessageService;
import ro.ubbcluj.map.proiectraokko4.service.UtilizatorService;
import ro.ubbcluj.map.proiectraokko4.utils.observer.Observer;
import ro.ubbcluj.map.proiectraokko4.utils.observer.TypeOfObservation;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UI_v2_Controller implements Observer {

    UtilizatorService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    private Long UserId;

    ObservableList<String> ChatFriendsModel = FXCollections.observableArrayList();
    ObservableList<String> UsersModel = FXCollections.observableArrayList();
    ObservableList<String> ProfilePageFriendsModel = FXCollections.observableArrayList();
    ObservableList<String> PendingFriendsModel = FXCollections.observableArrayList();
    ObservableList<String> FriendsModel = FXCollections.observableArrayList();

    //
    ObservableList<Message> modelChatMsg = FXCollections.observableArrayList();
    private String friendUM, UM;
    private List<String> friends = new ArrayList<>();
    //

    @FXML
    private ImageView SearchImg, HomeImg, ChatImg, FriendsImg;
    @FXML
    private Text TextUsername, test, Username, ProfilePageName, Chats, Friends;
    @FXML
    private JFXTextArea MsgText, SearchUsers;
    @FXML
    private AnchorPane SearchPage, HomePage, ChatPage, EditPage, BottomBar, TopBar, FriendsPage, Chat, ProfilePageAnchor;
    @FXML
    private JFXListView<String> SearchList, ChatList;
    @FXML
    private JFXListView<Message> ChatMsg;
    @FXML
    private JFXListView<String> FriendsList, PendingFriendsList;
    @FXML
    private TextArea SearchText;
    @FXML
    ListView<String> ProfilePageFriendsList;
    @FXML
    Button ProfilePageAddFriendButton, ProfilePageRejectFriendButton;



    public void setService(UtilizatorService userService, FriendshipService friendshipService, MessageService messageService, Long userId) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.UserId = userId;
        messageService.addObserver(this);
        friendshipService.addObserver(this);
        messageService.addObserver(this);
        load();
    }

    private void load()
    {
        Utilizator user = userService.finduser(UserId);
        TextUsername.setText(user.getUsername());

        initChatFriendsModel();
        initUsersModel(userService.getUsersOnPageWithUsername(0, SearchText.getText()));
        initFriendsModel(friendshipService.getFriendsOnListPageWithIdAndStatus(0 ,UserId, 2));
        initPendingFriendsModel(friendshipService.getFriendsOnPendingListPageWithIdAndStatus(0, UserId, 1));

        List<Message> allMsgs = userService.getAllMessages(UserId);

        modelChatMsg.setAll(allMsgs);
        SearchUsers.textProperty().addListener(x -> handleUsersFilter());
        Username.setText(UM);
        Chats.setText(String.valueOf(userService.allChats(UserId).size()));
        Friends.setText(String.valueOf(userService.getFriends(UserId).size()));
    }

    private void initChatFriendsModel()
    {
        List<String> fList = userService.allChats(UserId);
        ChatFriendsModel.setAll(fList);
    }

    private void initUsersModel(List<Utilizator> list)
    {
        if(list == null) return;
        //list.removeIf(x -> x.getUsername().equals(TextUsername.getText()));
        List<String> uList = StreamSupport.stream(list.spliterator(), false)
                .filter(x -> !x.getUsername().equals(TextUsername.getText()))
                .map(x -> x.getUsername())
                .collect(Collectors.toList());
        UsersModel.setAll(uList);
    }

    private void initFriendsModel(List<Tuple<Utilizator, Date>> list)
    {
        if(list == null) return;
        List<String> uList = StreamSupport.stream(list.spliterator(), false)
                .map(x -> x.getLeft().getUsername() + " (since " + x.getRight().toString() + ")")
                .collect(Collectors.toList());
        FriendsModel.setAll(uList);
    }

    private void initPendingFriendsModel(List<Tuple<Utilizator, Date>> list)
    {
        if(list == null) return;
        List<String> uList = StreamSupport.stream(list.spliterator(), false)
                .map(x -> x.getLeft().getUsername() + " (since " + x.getRight().toString() + ")")
                .collect(Collectors.toList());
        PendingFriendsModel.setAll(uList);
    }

    private void initProfilePageFriendsModel(List<Tuple<Utilizator, Date>> list)
    {
        if(list == null) return;
        List<String> uList = StreamSupport.stream(list.spliterator(), false)
                .map(x -> x.getLeft().getUsername() + " (since " + x.getRight().toString() + ")")
                .collect(Collectors.toList());
        ProfilePageFriendsModel.setAll(uList);
    }

    private void initHomePage() {
        HomePage.setVisible(true);
        Chat.setVisible(false);
        TopBar.setVisible(true);
        BottomBar.setVisible(true);
        ChatPage.setVisible(false);
        SearchPage.setVisible(false);
        EditPage.setVisible(false);
        FriendsPage.setVisible(false);
        ProfilePageAnchor.setVisible(false);
    }

    public void initialize() {
//        ChatList.setItems(model);
//        ChatList.getItems().add(new Utilizator("a","a", "a"));
        initHomePage();
        ChatList.setItems(ChatFriendsModel);

        ChatMsg.setItems(modelChatMsg);
        ChatMsg.setCellFactory(x -> new ListCell<Message>() {
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getFrom().getUsername().concat(": " + item.getMsg()));
            }
        });

        SearchList.setItems(UsersModel);
        FriendsList.setItems(FriendsModel);
        PendingFriendsList.setItems(PendingFriendsModel);
        ProfilePageFriendsList.setItems(ProfilePageFriendsModel);

        //SearchList.setExpanded(true);
        //SearchList.depthProperty().set(1);
    }


    public void handleButtonAction(javafx.scene.input.MouseEvent mouseEvent) {
        if(mouseEvent.getTarget() == SearchImg) {
            SearchPage.setVisible(true);
            ChatPage.setVisible(false);
            HomePage.setVisible(false);
            EditPage.setVisible(false);
            FriendsPage.setVisible(false);
            Chat.setVisible(false);
            ProfilePageAnchor.setVisible(false);
        } else if (mouseEvent.getTarget() == HomeImg) {
            HomePage.setVisible(true);
            ChatPage.setVisible(false);
            SearchPage.setVisible(false);
            EditPage.setVisible(false);
            FriendsPage.setVisible(false);
            Chat.setVisible(false);
            ProfilePageAnchor.setVisible(false);
        } else if (mouseEvent.getTarget() == ChatImg) {
            ChatPage.setVisible(true);
            HomePage.setVisible(false);
            SearchPage.setVisible(false);
            EditPage.setVisible(false);
            FriendsPage.setVisible(false);
            Chat.setVisible(false);
            ProfilePageAnchor.setVisible(false);
        }
        else if(mouseEvent.getTarget() == FriendsImg) {
            ChatPage.setVisible(false);
            HomePage.setVisible(false);
            SearchPage.setVisible(false);
            EditPage.setVisible(false);
            FriendsPage.setVisible(true);
            Chat.setVisible(false);
            ProfilePageAnchor.setVisible(false);
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
//            FriendsPage.setVisible(false);
//            TextUsername.setText(userName);
//            UserId = id;
//            load();
//        } else {
//            errorText.setText("Username or password is invalid");
//        }
//    }

    public void handleFriendRequests(MouseEvent mouseEvent) {
        FriendsPage.setVisible(true);
        EditPage.setVisible(false);
        SearchPage.setVisible(false);
        ChatPage.setVisible(false);
        HomePage.setVisible(false);
        Chat.setVisible(false);
        ProfilePageAnchor.setVisible(false);
    }

    public void handleEditProfile(ActionEvent actionEvent) {
        EditPage.setVisible(true);
        SearchPage.setVisible(false);
        ChatPage.setVisible(false);
        HomePage.setVisible(false);
        FriendsPage.setVisible(false);
        Chat.setVisible(false);
        ProfilePageAnchor.setVisible(false);
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

        modelChatMsg.setAll(userService.getAllMessages(UserId)
                .stream()
                .filter(p1)
                .collect(Collectors.toList()));
    }

    private void handleUsersFilter() {
        Predicate<Utilizator> p = x -> x.getUsername().startsWith(SearchUsers.getText());
        Iterable<Utilizator> users = userService.getAll();
        List<Utilizator> uList = StreamSupport.stream(users.spliterator(), false)
                .filter(p)
                .collect(Collectors.toList());
        modelUsers.setAll(uList);
    }

    public void handleSendMessage(MouseEvent mouseEvent) {
        String msg = MsgText.getText();
        Long FriendId = userService.getUserId(friendUM);
        if(friends.size() == 1) {
            userService.addMessage(UserId, FriendId, msg);
        } else {
            List<Long> to = new ArrayList<>();
            for(String curent:friends) {
                if(!curent.equals(UM))
                    to.add(userService.getUserId(curent));
            }
            userService.addGroupMessage(UserId, to, msg);
        }

        MsgText.clear();
    }

    public void handleGoBack(MouseEvent mouseEvent) {

    }

    public void onKeyTypedSearchText(KeyEvent event)
    {
        initUsersModel(userService.getUsersOnPageWithUsername(0, SearchText.getText()));
    }

    public void onMouseClickedNextPageSearchPage(MouseEvent event)
    {
        initUsersModel(userService.getNextUsers(SearchText.getText()));
    }

    public void onMouseClickedPreviousPageSearchPage(MouseEvent event)
    {
        initUsersModel(userService.getPreviousUsers(SearchText.getText()));
    }

    public void onMouseClickedSearchList(MouseEvent event)
    {
        if(SearchList.getSelectionModel().getSelectedItem() == null) return;
        loadProfilePage(SearchList.getSelectionModel().getSelectedItem());
    }

    public void onMouseClickedPreviousPagePendingFriends(MouseEvent event)
    {
        friendshipService.getPendingListPreviousFriends(UserId, 1);
    }

    public void onMouseClickedNextPagePendingFriends(MouseEvent event)
    {
        friendshipService.getPendingListNextFriends(UserId, 1);
    }

    public void onMouseClickedPendingFriendsList(MouseEvent event)
    {
        if(PendingFriendsList.getSelectionModel().getSelectedItem() == null) return;
        String username = PendingFriendsList.getSelectionModel().getSelectedItem();
        username = username.substring(0, username.indexOf(" "));
        loadProfilePage(username);
    }

    public void onMouseClickedPreviousPageFriends(MouseEvent event)
    {
        friendshipService.getListPreviousFriends(UserId, 2);
    }

    public void onMouseClickedNextPageFriends(MouseEvent event)
    {
        friendshipService.getListNextFriends(UserId, 2);
    }

    public void onMouseClickedFriendsList(MouseEvent event)
    {
        if(FriendsList.getSelectionModel().getSelectedItem() == null) return;
        String username = FriendsList.getSelectionModel().getSelectedItem();
        username = username.substring(0, username.indexOf(" "));
        loadProfilePage(username);
    }

    private void loadProfilePage(String username)
    {
        FriendsPage.setVisible(false);
        EditPage.setVisible(false);
        SearchPage.setVisible(false);
        ChatPage.setVisible(false);
        HomePage.setVisible(false);
        ProfilePageAnchor.setVisible(true);
        ProfilePageRejectFriendButton.setVisible(false);
        ProfilePageAddFriendButton.setLayoutY(119);

        ProfilePage page = friendshipService.getProfilePage(UserId, userService.getUserId(username));
        //friendshipService.setPageSize(8);

        ProfilePageName.setText(page.getFirstName() + " " + page.getLastName());
        Username.setText(page.getUsername());
        initProfilePageFriendsModel(page.getFriends());

        switch(page.getFriendshipStatus())
        {
            case 0:
            case 5:
                ProfilePageAddFriendButton.setText("Add Friend");
                ProfilePageAddFriendButton.setDisable(false);
                break;
            case 1:
                ProfilePageAddFriendButton.setLayoutY(97);
                ProfilePageAddFriendButton.setText("Pending");
                ProfilePageAddFriendButton.setDisable(true);
                ProfilePageRejectFriendButton.setText("Cancel");
                ProfilePageRejectFriendButton.setVisible(true);
                break;
            case 2:
                ProfilePageAddFriendButton.setText("Unfriend");
                ProfilePageAddFriendButton.setDisable(false);
                break;
            case 3:
                ProfilePageAddFriendButton.setText("Rejected");
                ProfilePageAddFriendButton.setDisable(true);
                break;
            case 4:
                ProfilePageAddFriendButton.setLayoutY(97);
                ProfilePageAddFriendButton.setText("Accept");
                ProfilePageAddFriendButton.setDisable(false);
                ProfilePageRejectFriendButton.setText("Reject");
                ProfilePageRejectFriendButton.setVisible(true);
                break;
        }
    }

    public void onProfilePageAddFriendButtonClicked(MouseEvent event)
    {
        if(ProfilePageAddFriendButton.getText().equals("Add Friend"))
        {
            friendshipService.addFriend(UserId, userService.getUserId(Username.getText()));
        }
        else if(ProfilePageAddFriendButton.getText().equals("Unfriend"))
        {
            friendshipService.deleteFriend(UserId, userService.getUserId(Username.getText()));
        }
        else if(ProfilePageAddFriendButton.getText().equals("Accept"))
        {
            ProfilePageRejectFriendButton.setVisible(false);
            ProfilePageAddFriendButton.setLayoutY(119);
            friendshipService.answerFriendRequest(UserId, userService.getUserId(Username.getText()), 2);
        }
    }

    public void onProfilePageRejectFriendButtonClicked(MouseEvent event)
    {
        ProfilePageRejectFriendButton.setVisible(false);
        ProfilePageAddFriendButton.setLayoutY(119);
        if(ProfilePageRejectFriendButton.getText().equals("Reject"))
            friendshipService.answerFriendRequest(UserId, userService.getUserId(Username.getText()), 3);
        else if(ProfilePageRejectFriendButton.getText().equals("Cancel"))
            friendshipService.deleteFriend(UserId, userService.getUserId(Username.getText()));
    }

    @Override
    public void update(TypeOfObservation type) {
        switch(type) {
            case USER:
                initUsersModel(userService.getUsersOnPageWithUsername(0, SearchText.getText()));
                break;
            case FRIENDSHIP:
                if (ProfilePageAnchor.isVisible()) {
                    loadProfilePage(Username.getText());
                }

                initFriendsModel(friendshipService.getFriendsOnListPageWithIdAndStatus(0, UserId, 2));
                initPendingFriendsModel(friendshipService.getFriendsOnPendingListPageWithIdAndStatus(0, UserId, 1));

                break;
            case MESSAGE:
                break;
        }
    }
}
