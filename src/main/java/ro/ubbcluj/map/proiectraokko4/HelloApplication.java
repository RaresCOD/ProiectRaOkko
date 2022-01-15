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
import ro.ubbcluj.map.proiectraokko4.controller.MessageAlert;
import ro.ubbcluj.map.proiectraokko4.domain.Event;
import ro.ubbcluj.map.proiectraokko4.domain.Prietenie;
import ro.ubbcluj.map.proiectraokko4.domain.Tuple;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.EventValidator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.FriendshipValidator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.MessageValidator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.UtilizatorValidator;
import ro.ubbcluj.map.proiectraokko4.repository.Repository;
import ro.ubbcluj.map.proiectraokko4.repository.db.EventDbRepository;
import ro.ubbcluj.map.proiectraokko4.repository.db.FriendshipDbRepository;
import ro.ubbcluj.map.proiectraokko4.repository.db.MessageDbRepository;
import ro.ubbcluj.map.proiectraokko4.repository.db.UtilizatorDbRepository;
import ro.ubbcluj.map.proiectraokko4.repository.paging.PagingRepository;
import ro.ubbcluj.map.proiectraokko4.service.*;

import java.io.IOException;
import java.lang.reflect.InaccessibleObjectException;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {

    RefreshThreadService refreshService;

    @Override
    public void start(Stage primaryStage) throws IOException {
        PagingRepository<Long, Utilizator> repoDb = new UtilizatorDbRepository("jdbc:postgresql://localhost:5432/Tema1", "postgres", "kokonel1002", new UtilizatorValidator());
        PagingRepository<Tuple<Long, Long>, Prietenie> repoFDb = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/Tema1", "postgres", "kokonel1002", new FriendshipValidator());
        Repository<Long, Message> repoMsgDb = new MessageDbRepository("jdbc:postgresql://localhost:5432/Tema1", "postgres", "kokonel1002", new MessageValidator());
        Repository<Long, Event> repoEvent = new EventDbRepository("jdbc:postgresql://localhost:5432/Tema1", "postgres", "kokonel1002", new EventValidator());
        UtilizatorService userService = new UtilizatorService(repoDb);
        FriendshipService friendshipService = new FriendshipService(repoDb, repoFDb);
        MessageService messageService = new MessageService(repoDb, repoMsgDb);
        EventService eventService = new EventService(repoDb, repoEvent);
        refreshService = new RefreshThreadService();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/login_v2.fxml"));
        AnchorPane rootLayout = fxmlLoader.load();
        LoginController_v2 loginController = fxmlLoader.getController();
        loginController.setService(userService, friendshipService, messageService, eventService, refreshService);
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        refreshService.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}