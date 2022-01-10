package ro.ubbcluj.map.proiectraokko4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ro.ubbcluj.map.proiectraokko4.Message.Message;
import ro.ubbcluj.map.proiectraokko4.domain.Prietenie;
import ro.ubbcluj.map.proiectraokko4.domain.Tuple;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.FriendshipValidator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.MessageValidator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.UtilizatorValidator;
import ro.ubbcluj.map.proiectraokko4.repository.Repository;
import ro.ubbcluj.map.proiectraokko4.repository.db.FriendshipDbRepository;
import ro.ubbcluj.map.proiectraokko4.repository.db.MessageDbRepository;
import ro.ubbcluj.map.proiectraokko4.repository.db.UtilizatorDbRepository;
import ro.ubbcluj.map.proiectraokko4.repository.paging.PagingRepository;
import ro.ubbcluj.map.proiectraokko4.service.FriendshipService;
import ro.ubbcluj.map.proiectraokko4.service.MessageService;
import ro.ubbcluj.map.proiectraokko4.service.UtilizatorService;

import java.io.IOException;

public class HelloApplication extends Application {

    UtilizatorService userService;
    FriendshipService friendshipService;
    MessageService messageService;

    @Override
    public void start(Stage primaryStage) throws IOException {
        PagingRepository<Long, Utilizator> repoDb = new UtilizatorDbRepository("jdbc:postgresql://localhost:5432/Tema1", "postgres", "kokonel1002", new UtilizatorValidator());
        PagingRepository<Tuple<Long, Long>, Prietenie> repoFDb = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/Tema1", "postgres", "kokonel1002", new FriendshipValidator());
        PagingRepository<Long, Message> repoMsgDb = new MessageDbRepository("jdbc:postgresql://localhost:5432/Tema1", "postgres", "kokonel1002", new MessageValidator());
        userService = new UtilizatorService(repoDb);
        friendshipService = new FriendshipService(repoDb, repoFDb);
        messageService = new MessageService(repoDb, repoMsgDb);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/login_v2.fxml"));
        AnchorPane rootLayout = (AnchorPane)fxmlLoader.load();
        LoginController_v2 loginController = fxmlLoader.getController();
//        rootLayout.setId("GridPane");
        loginController.setService(userService, friendshipService, messageService);
        Scene scene = new Scene(rootLayout);
//        scene.getStylesheets().addAll(this.getClass().getResource("css/BG.css").toExternalForm());
//        primaryStage.setTitle("Log in!");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    private void initView(Stage primaryStage) throws IOException {
    }
}