package ro.ubbcluj.map.proiectraokko4;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ro.ubbcluj.map.proiectraokko4.controller.MessageAlert;
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

    @FXML
    TableView<Utilizator> tableView;
    @FXML
    TableView<Utilizator> tableViewUsers;
    @FXML
    TableColumn<Utilizator,String> tableColumnFriend;
    @FXML
    TableColumn<Utilizator,String> tableColumnUser;

    public void setService(UtilizatorService service, Long userId) {

        this.service = service;
        this.userId = userId;
        initModel();
    }

    private void initModel() {
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
    }

    public void initialize() {
        tableColumnFriend.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("username"));
        tableView.setItems(model);
        tableColumnUser.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("username"));
        tableViewUsers.setItems(modelUsers);
    }


    public void handleAddFriend(ActionEvent actionEvent) {
        Utilizator selectedUser = tableViewUsers.getSelectionModel().getSelectedItem();
        if(selectedUser != null) {
            try{
                service.addFriend(userId, selectedUser.getId());
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
            } catch (ValidationException e) {
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        } else {
            MessageAlert.showErrorMessage(null,"Niciun utilizator selectat");
        }
    }
}
