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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import ro.ubbcluj.map.proiectraokko4.Message.Message;
import ro.ubbcluj.map.proiectraokko4.controller.MessageAlert;
import ro.ubbcluj.map.proiectraokko4.domain.Prietenie;
import ro.ubbcluj.map.proiectraokko4.domain.Tuple;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
import ro.ubbcluj.map.proiectraokko4.service.UtilizatorService;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
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
    private ListView<Message> ChatMsg;
    @FXML
    private JFXListView<String> FRList;
    @FXML
    private DatePicker DatePicker;
    @FXML
    private JFXComboBox<Utilizator> FriendsBox;






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
        FriendsBox.getItems().addAll(service.Friends(UserId));
        FriendsBox.setCellFactory(x -> new ListCell<Utilizator>(){
            protected void updateItem(Utilizator item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getUsername());
            }
        });
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

//        ChatMsg.setBackground(new Background(
//                new BackgroundFill(
//                        new LinearGradient(0, 0, 0, 1, true,
//                                CycleMethod.NO_CYCLE,
//                                new Stop(0, Color.web("#4568DC")),
//                                new Stop(1, Color.web("#B06AB3"))
//                        ), CornerRadii.EMPTY, Insets.EMPTY
//                ),
//                new BackgroundFill(
//                        new ImagePattern(
//                                new Image("https://edencoding.com/resources/wp-content/uploads/2021/02/Stars_128.png"),
//                                0, 0, 128, 128, false
//                        ), CornerRadii.EMPTY, Insets.EMPTY
//                ),
//                new BackgroundFill(
//                        new RadialGradient(
//                                0, 0, 0.5, 0.5, 0.5, true,
//                                CycleMethod.NO_CYCLE,
//                                new Stop(0, Color.web("#FFFFFF33")),
//                                new Stop(1, Color.web("#00000033"))),
//                        CornerRadii.EMPTY, Insets.EMPTY
//                )
//        ));
        ChatMsg.setItems(modelChatMsg);
        ChatMsg.setCellFactory(x ->{
            ListCell<Message> cell = new ListCell<Message>(){
                Label lblUserLeft = new Label();
                Label lblTextLeft = new Label();
                HBox hBoxLeft = new HBox(lblUserLeft, lblTextLeft);

                Label lblUserRight = new Label();
                Label lblTextRight = new Label();
                HBox hBoxRight = new HBox(lblTextRight, lblUserRight);

                {
                    hBoxLeft.setAlignment(Pos.CENTER_LEFT);
                    hBoxLeft.setSpacing(5);
                    hBoxRight.setAlignment(Pos.CENTER_RIGHT);
                    hBoxRight.setSpacing(5);
                }
                @Override
                protected void updateItem(Message item, boolean empty) {
                    super.updateItem(item, empty);

                    if(empty)
                    {
                        setText(null);
                        setGraphic(null);
                    }
                    else{
                        if(item.getFrom().getId().equals(UserId))
                        {
                            lblTextRight.setText(item.getMsg());
                            setGraphic(hBoxRight);

                        }
                        else{
                            lblUserLeft.setText(item.getFrom() + ":");
                            lblTextLeft.setText(item.getMsg());
                            setGraphic(hBoxLeft);
                        }
                    }
                }
            };
            return cell;
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

    public void handleExportPrivateButtonAction(ActionEvent actionEvent) {
        Utilizator selectedFriend = FriendsBox.getSelectionModel().getSelectedItem();
        if(DatePicker.getValue() == null)
            MessageAlert.showErrorMessage(null,"No selected dates!");
        else if(selectedFriend != null){
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage( page );

            try {
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.beginText();
                contentStream.setFont(PDType1Font.COURIER, 12);
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 700);

                contentStream.showText("Private chat with " + selectedFriend.getFirstName() + " " + selectedFriend.getLastName() + " in this period of time:");
                contentStream.newLine();

                List<Message> messages = service.MsgToFriend(UserId, selectedFriend.getId());
                extractMessagesForExport(contentStream, messages);
                document.save("src/Export2.pdf");
                document.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        else MessageAlert.showErrorMessage(null,"No selected user!");
    }

    public void handleExportActivityButtonAction(ActionEvent actionEvent) {
        if(DatePicker.getValue() == null )
            MessageAlert.showErrorMessage(null,"No selected dates!");
        else {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);
            try {


                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                contentStream.beginText();
                contentStream.setFont(PDType1Font.COURIER, 12);
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Friendships made in this period of time:");
                contentStream.newLine();

                List<Tuple<Utilizator, Date>> friendsMade = service.getFriends(UserId);
                List<Tuple<Utilizator, Date>> friends = StreamSupport.stream(friendsMade.spliterator(), false).collect(Collectors.toList());
                friends.forEach(f -> {
                    if (f.getRight().toLocalDate().isEqual(DatePicker.getValue())) {
                        try {
                            contentStream.newLine();
                            contentStream.showText(f.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                contentStream.newLine();
                contentStream.newLine();
                contentStream.showText("Messages sent and receved in this period of time:");
                contentStream.newLine();

                List<Message> messages = service.getAllMessages(UserId);
                extractMessagesForExport(contentStream, messages);

                document.save("src/Export1.pdf");
                document.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }
    private void extractMessagesForExport(PDPageContentStream contentStream, List<Message> messages) throws IOException {
        messages.sort(Comparator.comparing(Message::getId));
        messages.forEach(m -> {
            if(m.getData().toLocalDate().isEqual(DatePicker.getValue())){
                try {
                    contentStream.newLine();
                    contentStream.showText(m.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        contentStream.endText();

        contentStream.close();
    }
}
