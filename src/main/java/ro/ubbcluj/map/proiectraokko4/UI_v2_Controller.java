package ro.ubbcluj.map.proiectraokko4;

import com.jfoenix.controls.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.Notifications;
import ro.ubbcluj.map.proiectraokko4.Message.Message;
import ro.ubbcluj.map.proiectraokko4.controller.MessageAlert;
import ro.ubbcluj.map.proiectraokko4.domain.*;
import ro.ubbcluj.map.proiectraokko4.domain.validators.ValidationException;
import ro.ubbcluj.map.proiectraokko4.service.*;
import ro.ubbcluj.map.proiectraokko4.utils.observer.Observer;
import ro.ubbcluj.map.proiectraokko4.utils.observer.TypeOfObservation;

import java.io.IOException;
import java.sql.Date;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class UI_v2_Controller implements Observer {

    UtilizatorService userService;
    FriendshipService friendshipService;
    MessageService messageService;
    EventService eventService;
    RefreshThreadService refreshService;
    private Long UserId;
    private Long EventId;

    ObservableList<String> ChatsModel = FXCollections.observableArrayList();
    ObservableList<String> UsersModel = FXCollections.observableArrayList();
    ObservableList<String> ProfilePageFriendsModel = FXCollections.observableArrayList();
    ObservableList<String> ProfilePageEventsModel = FXCollections.observableArrayList();
    ObservableList<String> PendingFriendsModel = FXCollections.observableArrayList();
    ObservableList<String> FriendsModel = FXCollections.observableArrayList();
    ObservableList<String> EventPageParticipantsModel = FXCollections.observableArrayList();
    ObservableList<String> EventsPageListModel = FXCollections.observableArrayList();
    ObservableList<String> modelChatMsg = FXCollections.observableArrayList();

    private String currentUsername;
    private List<String> usersInChat = new ArrayList<>();

    @FXML
    private DatePicker DatePicker;
    @FXML
    private JFXComboBox<Utilizator> FriendsBox;
    @FXML
    private ImageView SearchImg, HomeImg, ChatImg, EventsImg, ChatSendImg;
    @FXML
    private Text TextUsername, Username, ProfilePageName, ProfilePageUsername, Chats, Friends, EventPageName, EventPageLocation, EventPageDescription, EventPageDate;
    @FXML
    private JFXTextArea MsgText;
    @FXML
    private AnchorPane SearchPage, HomePage, ChatPage, EditPage, BottomBar, TopBar, FriendsPage, Chat, ProfilePageAnchor, EventPage, EventsListPage, EventAddPage, ChatAddPage, NotificationsPage;
    @FXML
    private JFXListView<String> SearchList, ChatList, EventsList, ChatMsg;
    @FXML
    private JFXListView<String> FriendsList, PendingFriendsList;
    @FXML
    private TextArea SearchText, AddEventName, AddEventLocation, AddEventDescription, AddEventTime, AddChatUsernames;
    @FXML
    ListView<String> ProfilePageFriendsList, ProfilePageEventsList, EventPageParticipantsList, NotificationsList;
    @FXML
    Button ProfilePageAddFriendButton, ProfilePageRejectFriendButton, EventPageSignButton;


    public void setService(UtilizatorService userService, FriendshipService friendshipService, MessageService messageService, EventService eventService, RefreshThreadService refreshService, Long userId) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.UserId = userId;
        this.eventService = eventService;
        this.refreshService = refreshService;
        this.currentUsername = userService.finduser(UserId).getUsername();
        refreshService.addObserver(this);
        messageService.addObserver(this);
        friendshipService.addObserver(this);
        messageService.addObserver(this);
        load();
    }

    private void load()
    {
        Utilizator user = userService.finduser(UserId);
        TextUsername.setText(user.getUsername());

        formatList(SearchList);
        formatList(ChatList);
        formatList(EventsList);
        formatList(ChatMsg);
        formatList(FriendsList);
        formatList(PendingFriendsList);
        formatList(ProfilePageEventsList);
        formatList(ProfilePageFriendsList);
        formatList(EventPageParticipantsList);
        formatList(NotificationsList);

        EventId = -1L;
        refreshService.start();
        initHomePage();
    }

    private void formatList(ListView list)
    {
        list.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setStyle("-fx-control-inner-background:  #6b6b99;");
                        setText(item);
                        setWrapText(true);
                        setFont(new Font("SansSerif", 12));
                    }
                };
            }
        });
    }


    private void initChatsModel()
    {
        List<String> fList = messageService.allChats(UserId);
        ChatsModel.setAll(fList);
    }

    private void initChatMsgModel()
    {
        List<Long> destinationIds = new ArrayList<>();
        for(String username : usersInChat)
        {
            destinationIds.add(userService.getUserId(username));
        }
        destinationIds.add(UserId);
        List<Message> messages = messageService.findMsgs(destinationIds);
        List<String> showList = messages.stream().map(x->x.getFrom().getUsername() + " (" + x.getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "): " + x.getMsg()).toList();
        modelChatMsg.setAll(showList);
    }

    private void initUsersModel(List<Utilizator> list)
    {
        if(list == null) return;;
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
                .sorted(new Comparator<Tuple<Utilizator, Date>>() {
                    @Override
                    public int compare(Tuple<Utilizator, Date> e1, Tuple<Utilizator, Date> e2) {
                        return e1.getLeft().getUsername().compareTo(e2.getLeft().getUsername());
                    }
                })
                .map(x -> x.getLeft().getUsername() + " (since " + x.getRight().toString() + ")")
                .collect(Collectors.toList());
        FriendsModel.setAll(uList);
    }

    private void initEventParticipantsModel()
    {
        if(EventId == -1) return;
        Event event = eventService.getOne(EventId);
        if(event.getParticipants() == null) { EventPageParticipantsModel.setAll(new ArrayList<>()); return; }
        List<Utilizator> participants = Arrays.stream(event.getParticipants().split(";")).map(x->userService.finduser(Long.valueOf(x))).toList();
        participants.forEach(System.out::println);
        List<String> uList = StreamSupport.stream(participants.spliterator(), false)
                .sorted(new Comparator<Utilizator>() {
                    @Override
                    public int compare(Utilizator e1, Utilizator e2) {
                        return e1.getUsername().compareTo(e2.getUsername());
                    }
                })
                .map(x -> x.getUsername())
                .collect(Collectors.toList());
        EventPageParticipantsModel.setAll(uList);
    }

    private void initProfilePageEventsModel(Long userId)
    {
        List<Event> events = eventService.getEventsForUser(userId);
        if(events == null) return;
        List<String> uList = StreamSupport.stream(events.spliterator(), false)
                .sorted(new Comparator<Event>() {
                    @Override
                    public int compare(Event e1, Event e2) {
                        return e1.getDate().compareTo(e2.getDate());
                    }
                })
                .map(x -> x.getName() + " (" + x.getId() + ")")
                .collect(Collectors.toList());
        ProfilePageEventsModel.setAll(uList);
    }

    private void initPendingFriendsModel(List<Tuple<Utilizator, Date>> list)
    {
        if(list == null) return;
        List<String> uList = StreamSupport.stream(list.spliterator(), false)
                .sorted(new Comparator<Tuple<Utilizator, Date>>() {
                    @Override
                    public int compare(Tuple<Utilizator, Date> e1, Tuple<Utilizator, Date> e2) {
                        return e1.getLeft().getUsername().compareTo(e2.getLeft().getUsername());
                    }
                })
                .map(x -> x.getLeft().getUsername() + " (since " + x.getRight().toString() + ")")
                .collect(Collectors.toList());
        PendingFriendsModel.setAll(uList);
    }

    private void initProfilePageFriendsModel(List<Tuple<Utilizator, Date>> list)
    {
        if(list == null) return;
        List<String> uList = StreamSupport.stream(list.spliterator(), false)
                .sorted(new Comparator<Tuple<Utilizator, Date>>() {
                    @Override
                    public int compare(Tuple<Utilizator, Date> e1, Tuple<Utilizator, Date> e2) {
                        return e1.getLeft().getUsername().compareTo(e2.getLeft().getUsername());
                    }
                })
                .map(x -> x.getLeft().getUsername() + " (since " + x.getRight().toString() + ")")
                .collect(Collectors.toList());
        ProfilePageFriendsModel.setAll(uList);
    }

    private void initEventsPageListModel()
    {
        List<Event> events = eventService.getAllEvents();
        if(events == null || events.size() == 0) return;
        List<String> uList = StreamSupport.stream(events.spliterator(), false)
                .sorted(new Comparator<Event>() {
                    @Override
                    public int compare(Event e1, Event e2) {
                        return e1.getDate().compareTo(e2.getDate());
                    }
                })
                .map(x -> x.getName() + " (" + x.getId() + ")")
                .collect(Collectors.toList());
        EventsPageListModel.setAll(uList);
    }

    public void onEventPageSignButtonClicked(MouseEvent event)
    {
        if(EventPageSignButton.getText().equals("Sign Up"))
        {
            eventService.addEventParticipant(EventId, UserId);
        }
        else if(EventPageSignButton.getText().equals("Sign Out"))
        {
            eventService.removeEventParticipant(EventId, UserId);
        }
        else if(EventPageSignButton.getText().equals("Delete"))
        {
            Long eventId = EventId;
            EventId = -1L;
            eventService.deleteEvent(eventId);
            ChatPage.setVisible(false);
            HomePage.setVisible(false);
            SearchPage.setVisible(false);
            EditPage.setVisible(false);
            FriendsPage.setVisible(false);
            Chat.setVisible(false);
            ProfilePageAnchor.setVisible(false);
            EventPage.setVisible(false);
            EventAddPage.setVisible(false);
            EventsListPage.setVisible(true);
            ChatAddPage.setVisible(false);
            NotificationsPage.setVisible(false);
            initEventsPageListModel();
        }
    }

    private void notifyUserIfEventIsComingSoon()
    {
        List<Event> events = eventService.getEventsForUser(UserId);
        events.stream().forEach(x -> {
            Long hoursDiff = ChronoUnit.HOURS.between(LocalDateTime.now(), x.getDate());
            if(hoursDiff <= 24 && hoursDiff >= 0) addEventComingSoonNotification(x);
        });
    }

    private void addEventComingSoonNotification(Event event) {
        String text = "Evenimentul '" + event.getName() + "' se apropie (" + event.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + ").";
        NotificationsList.getItems().add(text);
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
        EventPage.setVisible(false);
        EventsListPage.setVisible(false);
        EventAddPage.setVisible(false);
        ChatAddPage.setVisible(false);
        NotificationsPage.setVisible(false);

        Utilizator thisUser = userService.finduser(UserId);
        Username.setText(thisUser.getFirstName() + " " + thisUser.getLastName());
        Chats.setText(String.valueOf(messageService.allChats(UserId).size()));
        Friends.setText(String.valueOf(friendshipService.getFriends(UserId).size()));

        FriendsBox.getItems().addAll(friendshipService.getFriends(UserId).stream().map(x->x.getLeft()).toList());
        FriendsBox.setCellFactory(x -> new ListCell<Utilizator>(){
            protected void updateItem(Utilizator item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getUsername());
            }
        });
    }

    private void startChat() {
        initChatMsgModel();
        Chat.setVisible(true);
        SearchPage.setVisible(false);
        ChatPage.setVisible(false);
        HomePage.setVisible(false);
        EditPage.setVisible(false);
        FriendsPage.setVisible(false);
        ProfilePageAnchor.setVisible(false);
        EventPage.setVisible(false);
        EventsListPage.setVisible(false);
        EventAddPage.setVisible(false);
        ChatAddPage.setVisible(false);
        MsgText.setDisable(false);
        MsgText.setText("");
        ChatSendImg.setDisable(false);
        if(usersInChat.size() == 1 && !friendshipService.areFriends(UserId, userService.getUserId(usersInChat.get(0))))
        {
            MsgText.setDisable(true);
            ChatSendImg.setDisable(true);
            MsgText.setText("Nu poti raspunde deoarece nu mai sunteti prieteni.");
        }
    }

    public void initialize() {
        ChatList.setItems(ChatsModel);
        ChatMsg.setItems(modelChatMsg);
        SearchList.setItems(UsersModel);
        FriendsList.setItems(FriendsModel);
        PendingFriendsList.setItems(PendingFriendsModel);
        ProfilePageFriendsList.setItems(ProfilePageFriendsModel);
        EventPageParticipantsList.setItems(EventPageParticipantsModel);
        ProfilePageEventsList.setItems(ProfilePageEventsModel);
        EventsList.setItems(EventsPageListModel);
    }

    public void onKeyPressedMsgText(KeyEvent event)
    {
        if(!MsgText.isDisable() && event.getCode().equals(KeyCode.ENTER))
        {
            handleSendMessage(null);
        }
    }

    public void handleButtonAction(MouseEvent mouseEvent) {
        if(mouseEvent.getTarget() == SearchImg) {
            SearchPage.setVisible(true);
            ChatPage.setVisible(false);
            HomePage.setVisible(false);
            EditPage.setVisible(false);
            FriendsPage.setVisible(false);
            Chat.setVisible(false);
            ProfilePageAnchor.setVisible(false);
            EventPage.setVisible(false);
            EventsListPage.setVisible(false);
            EventAddPage.setVisible(false);
            ChatAddPage.setVisible(false);
            NotificationsPage.setVisible(false);
            initUsersModel(userService.getUsersOnPageWithUsername(0, SearchText.getText()));
        } else if (mouseEvent.getTarget() == HomeImg) {
            initHomePage();
        } else if (mouseEvent.getTarget() == ChatImg) {
            ChatPage.setVisible(true);
            HomePage.setVisible(false);
            SearchPage.setVisible(false);
            EditPage.setVisible(false);
            FriendsPage.setVisible(false);
            Chat.setVisible(false);
            ProfilePageAnchor.setVisible(false);
            EventPage.setVisible(false);
            EventAddPage.setVisible(false);
            EventsListPage.setVisible(false);
            ChatAddPage.setVisible(false);
            NotificationsPage.setVisible(false);
            initChatsModel();
        }
        else if(mouseEvent.getTarget() == EventsImg)
        {
            ChatPage.setVisible(false);
            HomePage.setVisible(false);
            SearchPage.setVisible(false);
            EditPage.setVisible(false);
            FriendsPage.setVisible(false);
            Chat.setVisible(false);
            ProfilePageAnchor.setVisible(false);
            EventPage.setVisible(false);
            EventAddPage.setVisible(false);
            EventsListPage.setVisible(true);
            ChatAddPage.setVisible(false);
            NotificationsPage.setVisible(false);
            initEventsPageListModel();
        }
    }

    public void handleFriendRequests(MouseEvent mouseEvent) {
        FriendsPage.setVisible(true);
        EditPage.setVisible(false);
        SearchPage.setVisible(false);
        ChatPage.setVisible(false);
        HomePage.setVisible(false);
        Chat.setVisible(false);
        ProfilePageAnchor.setVisible(false);
        EventPage.setVisible(false);
        EventsListPage.setVisible(false);
        ChatAddPage.setVisible(false);
        NotificationsPage.setVisible(false);
        initFriendsModel(friendshipService.getFriendsOnListPageWithIdAndStatus(0, UserId, 2));
        initPendingFriendsModel(friendshipService.getFriendsOnPendingListPageWithIdAndStatus(0, UserId, 1));
    }

    public void handleNotifications(MouseEvent mouseEvent)
    {
        HomePage.setVisible(false);
        Chat.setVisible(false);
        TopBar.setVisible(true);
        BottomBar.setVisible(true);
        ChatPage.setVisible(false);
        SearchPage.setVisible(false);
        EditPage.setVisible(false);
        FriendsPage.setVisible(false);
        ProfilePageAnchor.setVisible(false);
        EventPage.setVisible(false);
        EventsListPage.setVisible(false);
        EventAddPage.setVisible(false);
        ChatAddPage.setVisible(false);
        NotificationsList.getItems().clear();
        NotificationsPage.setVisible(true);
        notifyUserIfEventIsComingSoon();
    }

    public void handleAddEvent(MouseEvent event)
    {
        try {
            eventService.addEvent(AddEventName.getText(), AddEventDescription.getText(), AddEventLocation.getText(), LocalDateTime.parse(AddEventTime.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        } catch (DateTimeParseException e)
        {
            MessageAlert.showErrorMessage(null, "Formatul datei este gresit.");
        }
        catch (Exception e)
        {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        ChatPage.setVisible(false);
        HomePage.setVisible(false);
        SearchPage.setVisible(false);
        EditPage.setVisible(false);
        FriendsPage.setVisible(false);
        Chat.setVisible(false);
        ProfilePageAnchor.setVisible(false);
        EventPage.setVisible(false);
        EventAddPage.setVisible(false);
        EventsListPage.setVisible(true);
        ChatAddPage.setVisible(false);
        NotificationsPage.setVisible(false);
        initEventsPageListModel();
    }

    public void handleAddEventPage(MouseEvent event)
    {
        EventAddPage.setVisible(true);
        HomePage.setVisible(false);
        Chat.setVisible(false);
        TopBar.setVisible(true);
        BottomBar.setVisible(true);
        ChatPage.setVisible(false);
        SearchPage.setVisible(false);
        EditPage.setVisible(false);
        FriendsPage.setVisible(false);
        ProfilePageAnchor.setVisible(false);
        EventPage.setVisible(false);
        EventsListPage.setVisible(false);
        ChatAddPage.setVisible(false);
        NotificationsPage.setVisible(false);
    }

    public void handleAddChatPage(MouseEvent event)
    {
        ChatAddPage.setVisible(true);
        ChatPage.setVisible(false);
        HomePage.setVisible(false);
        SearchPage.setVisible(false);
        EditPage.setVisible(false);
        FriendsPage.setVisible(false);
        Chat.setVisible(false);
        ProfilePageAnchor.setVisible(false);
        EventPage.setVisible(false);
        EventAddPage.setVisible(false);
        EventsListPage.setVisible(false);
        NotificationsPage.setVisible(false);
    }

    public void handleAddChat(MouseEvent event)
    {
        String usernames = AddChatUsernames.getText();
        if(usernames.isEmpty() || usernames.isBlank())
        {
            MessageAlert.showErrorMessage(null, "Nu ai introdus niciun username");
            return;
        }
        List<Long> usernamesIds = new ArrayList<>();
        for(String username : Arrays.stream(usernames.split(";")).toList())
        {
            Long id;
            id = userService.getUserId(username);
            if(id == null) {
                MessageAlert.showErrorMessage(null, "User-ul " + username + " nu exista");
                return;
            }
            if(!friendshipService.areFriends(UserId, id)) {
                MessageAlert.showErrorMessage(null, "Nu esti prieten cu user-ul " + username);
                return;
            }
            usernamesIds.add(id);
        }
        messageService.addMessage(UserId, usernamesIds, null);
        ChatAddPage.setVisible(false);
        ChatPage.setVisible(true);
        HomePage.setVisible(false);
        SearchPage.setVisible(false);
        EditPage.setVisible(false);
        FriendsPage.setVisible(false);
        Chat.setVisible(false);
        ProfilePageAnchor.setVisible(false);
        EventPage.setVisible(false);
        EventAddPage.setVisible(false);
        EventsListPage.setVisible(false);
        NotificationsPage.setVisible(false);
        initChatsModel();
    }

    public void handleEditProfile(ActionEvent actionEvent) {
        EditPage.setVisible(true);
        SearchPage.setVisible(false);
        ChatPage.setVisible(false);
        HomePage.setVisible(false);
        FriendsPage.setVisible(false);
        Chat.setVisible(false);
        ProfilePageAnchor.setVisible(false);
        EventPage.setVisible(false);
        EventsListPage.setVisible(false);
        EventAddPage.setVisible(false);
        ChatAddPage.setVisible(false);
        NotificationsPage.setVisible(false);
    }

    public void handleChatListOnMouseClick(MouseEvent mouseEvent) {
        String currentSelection = ChatList.getSelectionModel().getSelectedItem();
        if(currentSelection == null) return;
        usersInChat = new ArrayList<>(List.of(currentSelection.split(";")));
        startChat();
    }

    public void handleSendMessage(MouseEvent mouseEvent) {
        String msg = MsgText.getText().substring(0, MsgText.getText().length() - 1);
            List<Long> to = new ArrayList<>();
            for(String curent:usersInChat) to.add(userService.getUserId(curent));
            try {
                messageService.addMessage(UserId, to, msg);
            }
            catch (ValidationException e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        MsgText.clear();
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
        String selection = SearchList.getSelectionModel().getSelectedItem();
        if(selection == null) return;
        loadProfilePage(selection);
    }

    public void onMouseClickedProfilePageFriendsList(MouseEvent event)
    {
        String selection = ProfilePageFriendsList.getSelectionModel().getSelectedItem();
        if(selection == null) return;
        selection = selection.substring(0, selection.indexOf("(") - 1);
        System.out.println("'" + selection + "'");
        loadProfilePage(selection);
    }

    private void loadEventPage(Long id)
    {
        if(id == -1) return;
        Event event = eventService.getOne(id);
        EventPage.setVisible(true);
        ChatPage.setVisible(false);
        HomePage.setVisible(false);
        SearchPage.setVisible(false);
        EditPage.setVisible(false);
        FriendsPage.setVisible(false);
        Chat.setVisible(false);
        ProfilePageAnchor.setVisible(false);
        EventsListPage.setVisible(false);
        EventAddPage.setVisible(false);
        ChatAddPage.setVisible(false);
        NotificationsPage.setVisible(false);

        boolean isEventPassed = event.getDate().compareTo(LocalDateTime.now()) < 0;
        EventPageName.setText(event.getName());
        EventPageLocation.setText("Location: " + event.getLocation());
        if(isEventPassed) EventPageDate.setText("Date: " + event.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + " (passed)");
        else EventPageDate.setText("Date: " + event.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        EventPageDescription.setText("Description: " + event.getDescription());

        boolean isUserParticipant = eventService.isUserParticipant(EventId, UserId);
        if(isEventPassed && isUserParticipant) { EventPageSignButton.setText("Delete"); EventPageSignButton.setDisable(false); }
        else if(isEventPassed && !isUserParticipant) { EventPageSignButton.setText("Sign Up"); EventPageSignButton.setDisable(true); }
        else if(isUserParticipant) { EventPageSignButton.setText("Sign Out"); EventPageSignButton.setDisable(false); }
        else { EventPageSignButton.setText("Sign Up"); EventPageSignButton.setDisable(false); }
        initEventParticipantsModel();
    }

    public void onMouseClickedEventsList(MouseEvent event)
    {
        String selection = EventsList.getSelectionModel().getSelectedItem();;
        if(selection == null) return;
        String id = selection.substring(selection.indexOf('(') + 1, selection.indexOf(')'));
        EventId = Long.valueOf(id);
        loadEventPage(EventId);
    }

    public void onMouseClickedProfilePageEventsList(MouseEvent event)
    {
        String selection = ProfilePageEventsList.getSelectionModel().getSelectedItem();;
        if(selection == null) return;
        String id = selection.substring(selection.indexOf('(') + 1, selection.indexOf(')'));
        EventId = Long.valueOf(id);
        loadEventPage(EventId);
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

    public void handleExportPrivateButtonAction(ActionEvent actionEvent) {
        Utilizator selectedFriend = FriendsBox.getSelectionModel().getSelectedItem();
        if(DatePicker.getValue() == null)
            MessageAlert.showErrorMessage(null,"Nu ai selectat o data.");
        else if(selectedFriend != null){
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            try {
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.beginText();
                contentStream.setFont(PDType1Font.COURIER, 8);
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 700);

                contentStream.showText("All chats with " + selectedFriend.getFirstName() + " " + selectedFriend.getLastName() + " in this date (" + DatePicker.getValue() + "):");
                contentStream.newLine();

                List<Long> ids = new ArrayList<>();
                ids.add(UserId);
                ids.add(selectedFriend.getId());
                List<Message> messages = messageService.findMsgs(ids);
                extractMessagesForExport(contentStream, messages);
                contentStream.endText();
                contentStream.close();
                document.save("ExportFriend_" + selectedFriend.getUsername() + ".pdf");
                MessageAlert.showMessage(null, "Info", "Export-ul a fost creat cu numele " + "ExportFriend_" + selectedFriend.getUsername() + ".pdf");
            } catch (Exception e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
            finally{
                try {
                    document.close();
                } catch (IOException e) {
                    MessageAlert.showErrorMessage(null, e.getMessage());
                }
            }
        }
        else MessageAlert.showErrorMessage(null,"Nu ai selectat un prieten.");
    }

    public void handleExportActivityButtonAction(ActionEvent actionEvent) {
        if(DatePicker.getValue() == null )
            MessageAlert.showErrorMessage(null,"Nu ai selectat o data.");
        else {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);
            try {
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                contentStream.beginText();
                contentStream.setFont(PDType1Font.COURIER, 8);
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Friendships made in this date (" + DatePicker.getValue() + "):");
                contentStream.newLine();

                List<Tuple<Utilizator, Date>> friendsMade = friendshipService.getFriends(UserId);
                List<Tuple<Utilizator, Date>> friends = StreamSupport.stream(friendsMade.spliterator(), false).collect(Collectors.toList());
                friends.forEach(f -> {
                    if (f.getRight().toLocalDate().isEqual(DatePicker.getValue())) {
                        try {
                            contentStream.newLine();
                            String text = f.toString();
                            text = text.replace("\n", "").replace("\r", "");
                            contentStream.showText(text);
                            contentStream.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Messages sent and receved in this date (" + DatePicker.getValue() + "):");
                contentStream.newLine();

                List<Message> messages = messageService.getAllMessages(UserId);
                extractMessagesForExport(contentStream, messages);
                contentStream.endText();
                contentStream.close();
                document.save("ExportPersonalActivity.pdf");
                MessageAlert.showMessage(null, "Info", "Export-ul a fost creat cu numele " + "ExportPersonalActivity.pdf");
            } catch (Exception e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
            finally
            {
                try {
                    document.close();
                } catch (IOException e) {
                    MessageAlert.showErrorMessage(null, e.getMessage());
                }
            }
        }

    }

    private void extractMessagesForExport(PDPageContentStream contentStream, List<Message> messages) throws IOException {
        for(Message m : messages)
        {    if(m.getData().toLocalDate().isEqual(DatePicker.getValue())){
                try {
                    String text = m.toString();
                    text = text.replace("\n", "").replace("\r", "");
                    contentStream.newLine();
                    contentStream.showText(text);
                } catch (IOException e) {
                    MessageAlert.showErrorMessage(null, e.getMessage());
                }
            }
        }
    }

    private void loadProfilePage(String username)
    {
        if(username.equals(currentUsername)) return;
        FriendsPage.setVisible(false);
        EditPage.setVisible(false);
        SearchPage.setVisible(false);
        ChatPage.setVisible(false);
        HomePage.setVisible(false);
        ProfilePageAnchor.setVisible(true);
        EventPage.setVisible(false);
        EventsListPage.setVisible(false);
        EventAddPage.setVisible(false);
        ChatAddPage.setVisible(false);
        NotificationsPage.setVisible(false);

        Long profileUserId = userService.getUserId(username);
        ProfilePage page = friendshipService.getProfilePage(UserId, profileUserId);

        ProfilePageName.setText(page.getFirstName() + " " + page.getLastName());
        ProfilePageUsername.setText(page.getUsername());
        initProfilePageFriendsModel(page.getFriends());
        initProfilePageEventsModel(profileUserId);

        switch(page.getFriendshipStatus())
        {
            case 0:
            case 5:
                ProfilePageAddFriendButton.setLayoutY(119);
                ProfilePageRejectFriendButton.setVisible(false);
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
                ProfilePageRejectFriendButton.setVisible(false);
                ProfilePageAddFriendButton.setLayoutY(119);
                ProfilePageAddFriendButton.setDisable(false);
                break;
            case 3:
                ProfilePageAddFriendButton.setLayoutY(119);
                ProfilePageRejectFriendButton.setVisible(false);
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
            friendshipService.addFriend(UserId, userService.getUserId(ProfilePageUsername.getText()));
        }
        else if(ProfilePageAddFriendButton.getText().equals("Unfriend"))
        {
            friendshipService.deleteFriend(UserId, userService.getUserId(ProfilePageUsername.getText()));
        }
        else if(ProfilePageAddFriendButton.getText().equals("Accept"))
        {
            ProfilePageRejectFriendButton.setVisible(false);
            ProfilePageAddFriendButton.setLayoutY(119);
            friendshipService.answerFriendRequest(UserId, userService.getUserId(ProfilePageUsername.getText()), 2);
        }
    }

    public void onProfilePageRejectFriendButtonClicked(MouseEvent event)
    {
        ProfilePageRejectFriendButton.setVisible(false);
        ProfilePageAddFriendButton.setLayoutY(119);
        if(ProfilePageRejectFriendButton.getText().equals("Reject"))
            friendshipService.answerFriendRequest(UserId, userService.getUserId(ProfilePageUsername.getText()), 3);
        else if(ProfilePageRejectFriendButton.getText().equals("Cancel"))
            friendshipService.deleteFriend(UserId, userService.getUserId(ProfilePageUsername.getText()));
    }

    @Override
    public void update(TypeOfObservation type) {
        switch(type) {
            case USER:
                initUsersModel(userService.getUsersOnPageWithUsername(0, SearchText.getText()));
                break;
            case FRIENDSHIP:
                if (ProfilePageAnchor.isVisible()) loadProfilePage(ProfilePageUsername.getText());
                if(FriendsPage.isVisible()) {
                    initFriendsModel(friendshipService.getFriendsOnListPageWithIdAndStatus(-1, UserId, 2));
                    initPendingFriendsModel(friendshipService.getFriendsOnPendingListPageWithIdAndStatus(-1, UserId, 1));
                }
                break;
            case MESSAGE:
                if(Chat.isVisible()) initChatMsgModel();
                if(ChatPage.isVisible()) initChatsModel();
                break;
            case EVENT:
                if(EventsList.isVisible()) initEventsPageListModel();
                if(EventPage.isVisible()) loadEventPage(EventId);
                break;
            case REFRESH:
                if(Chat.isVisible()) initChatMsgModel();
                if(ChatPage.isVisible()) initChatsModel();
                if(FriendsPage.isVisible()) { initFriendsModel(friendshipService.getFriendsOnListPageWithIdAndStatus(-1, UserId, 2)); initPendingFriendsModel(friendshipService.getFriendsOnPendingListPageWithIdAndStatus(-1, UserId, 1)); }
                if(ProfilePageAnchor.isVisible()) loadProfilePage(ProfilePageUsername.getText());
                if(SearchPage.isVisible()) initUsersModel(userService.getUsersOnPageWithUsername(-1, SearchText.getText()));
                if(EventsList.isVisible()) initEventsPageListModel();
                if(EventPage.isVisible()) loadEventPage(EventId);
                if(NotificationsPage.isVisible()) handleNotifications(null);
                break;
        }
    }
}
