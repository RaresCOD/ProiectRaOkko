package ro.ubbcluj.map.proiectraokko4;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ro.ubbcluj.map.proiectraokko4.controller.MessageAlert;
import ro.ubbcluj.map.proiectraokko4.domain.Prietenie;
import ro.ubbcluj.map.proiectraokko4.domain.Tuple;
import ro.ubbcluj.map.proiectraokko4.domain.Utilizator;
import ro.ubbcluj.map.proiectraokko4.domain.validators.ValidationException;
import ro.ubbcluj.map.proiectraokko4.service.UtilizatorService;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserInterface {

    UtilizatorService service;
    Long userId;
    ObservableList<Utilizator> model = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelUsers = FXCollections.observableArrayList();
    ObservableList<Tuple<Utilizator, Prietenie>> modelUsersFriendRequests = FXCollections.observableArrayList();
    ObservableList<Date> modelUsersFriendRequestsDate = FXCollections.observableArrayList();
    ObservableList<String> modelUsersFriendRequestsStatus = FXCollections.observableArrayList();

    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableView<Utilizator> tableViewUsers;
    @FXML
    TableColumn<Utilizator,String> tableColumnFriend;
    @FXML
    TableColumn<Utilizator,String> tableColumnUser;
    @FXML
    TableView<Tuple<Utilizator, Prietenie>> tableViewFriendRequests;
    @FXML
    TableColumn<Tuple<Utilizator, Prietenie>, String> tableColumnFriendRequestsUsername;
    @FXML
    TableColumn<Tuple<Utilizator, Prietenie>, String> tableColumnFriendRequestsDate;
    @FXML
    TableColumn<Tuple<Utilizator, Prietenie>, String> tableColumnFriendRequestsStatus;

    public void setService(UtilizatorService service, Long userId) {

        this.service = service;
        this.userId = userId;
        refreshModels();
    }

    private void refreshModels() {
        List<Tuple<Utilizator, Date>> friends = service.getFriends(userId);
        List<Utilizator> fList = friends.stream()
                .map(x -> x.getLeft())
                .collect(Collectors.toList());
        System.out.println(fList);
        model.setAll(fList);
        Iterable<Utilizator> users = service.getAll();
        List<Utilizator> uList = StreamSupport.stream(users.spliterator(), false)
                .collect(Collectors.toList());
        modelUsers.setAll(uList);
        List<Tuple<Utilizator, Prietenie>> friendRequests = service.getFriendRequests(userId);
        modelUsersFriendRequests.setAll(friendRequests);
    }

    public void initialize() {
        tableColumnFriend.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("username"));
        tableView.setItems(model);
        tableColumnUser.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("username"));
        tableViewUsers.setItems(modelUsers);
        tableColumnFriendRequestsUsername.setCellValueFactory(x -> new SimpleStringProperty(x.getValue().getLeft().getUsername()));
        tableColumnFriendRequestsDate.setCellValueFactory(x -> new SimpleStringProperty(x.getValue().getRight().getDate().toString()));
        tableColumnFriendRequestsStatus.setCellValueFactory(x -> {
            switch(x.getValue().getRight().getStatus())
            {
                default:
                    return new SimpleStringProperty("pending");
                case 2:
                    return new SimpleStringProperty("approved");
                case 3:
                    return new SimpleStringProperty("rejected");
            }
        });
        tableViewFriendRequests.setItems(modelUsersFriendRequests);
    }


    public void handleAddFriend(ActionEvent actionEvent) {
        Utilizator selectedUser = tableViewUsers.getSelectionModel().getSelectedItem();
        if(selectedUser != null) {
            try{
                service.addFriend(userId, selectedUser.getId());
                refreshModels();
            } catch (ValidationException e) {
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        } else {
            MessageAlert.showErrorMessage(null,"Niciun utilizator selectat");
        }
    }

    public void handleDeleteFriend(ActionEvent actionEvent) {
        Utilizator selectedUser = tableView.getSelectionModel().getSelectedItem();
        if(selectedUser != null) {
            try{
                service.deleteFriend(userId, selectedUser.getId());
                tableView.getItems().removeAll(tableView.getSelectionModel().getSelectedItem());
                refreshModels();
            } catch (ValidationException e) {
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        } else {
            MessageAlert.showErrorMessage(null,"Niciun utilizator selectat");
        }
    }

    public void handleAcceptFriendRequest(ActionEvent actionEvent) {
        Tuple<Utilizator, Prietenie> selectedUser = tableViewFriendRequests.getSelectionModel().getSelectedItem();
        if(selectedUser != null) {
            try{
                if(selectedUser.getRight().getId().getLeft() == userId) throw new ValidationException("Nu poti raspunde la o cerere trimisa de tine!");
                service.answerFriendRequest(userId, selectedUser.getLeft().getId(), 2);
                refreshModels();
            } catch (ValidationException e) {
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        } else {
            MessageAlert.showErrorMessage(null,"Nicio cerere de prietenie selectata");
        }
    }

    public void handleRejectFriendRequest(ActionEvent actionEvent) {
        Tuple<Utilizator, Prietenie> selectedUser = tableViewFriendRequests.getSelectionModel().getSelectedItem();
        if(selectedUser != null) {
            try{
                if(selectedUser.getRight().getId().getLeft() == userId) throw new ValidationException("Nu poti raspunde la o cerere trimisa de tine!");
                service.answerFriendRequest(userId, selectedUser.getLeft().getId(), 3);
                refreshModels();
            } catch (ValidationException e) {
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        } else {
            MessageAlert.showErrorMessage(null,"Nicio cerere de prietenie selectata");
        }
    }

    public void handleRefreshTableViews(ActionEvent actionEvent) {
        refreshModels();
    }
}
