package ro.ubbcluj.map.proiectraokko4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ro.ubbcluj.map.proiectraokko4.domain.Message;
import ro.ubbcluj.map.proiectraokko4.domain.Event;
import ro.ubbcluj.map.proiectraokko4.domain.Friendship;
import ro.ubbcluj.map.proiectraokko4.domain.Tuple;
import ro.ubbcluj.map.proiectraokko4.domain.User;
import ro.ubbcluj.map.proiectraokko4.domain.validators.EventValidator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.FriendshipValidator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.MessageValidator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.UserValidator;
import ro.ubbcluj.map.proiectraokko4.repository.Repository;
import ro.ubbcluj.map.proiectraokko4.repository.db.EventDbRepository;
import ro.ubbcluj.map.proiectraokko4.repository.db.FriendshipDbRepository;
import ro.ubbcluj.map.proiectraokko4.repository.db.MessageDbRepository;
import ro.ubbcluj.map.proiectraokko4.repository.db.UserDbRepository;
import ro.ubbcluj.map.proiectraokko4.repository.paging.PagingRepository;
import ro.ubbcluj.map.proiectraokko4.service.*;

import java.io.IOException;

public class MainApplication extends Application {

    RefreshThreadService refreshService;

    @Override
    public void start(Stage primaryStage) throws IOException {
        PagingRepository<Long, User> repoDb = new UserDbRepository("jdbc:postgresql://localhost:5432/Tema1", "postgres", "kokonel1002", new UserValidator());
        PagingRepository<Tuple<Long, Long>, Friendship> repoFDb = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/Tema1", "postgres", "kokonel1002", new FriendshipValidator());
        Repository<Long, Message> repoMsgDb = new MessageDbRepository("jdbc:postgresql://localhost:5432/Tema1", "postgres", "kokonel1002", new MessageValidator());
        Repository<Long, Event> repoEvent = new EventDbRepository("jdbc:postgresql://localhost:5432/Tema1", "postgres", "kokonel1002", new EventValidator());
        UserService userService = new UserService(repoDb);
        FriendshipService friendshipService = new FriendshipService(repoDb, repoFDb);
        MessageService messageService = new MessageService(repoDb, repoMsgDb);
        EventService eventService = new EventService(repoDb, repoEvent);
        refreshService = new RefreshThreadService();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("views/login.fxml"));
        AnchorPane rootLayout = fxmlLoader.load();
        loginController loginController = fxmlLoader.getController();
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