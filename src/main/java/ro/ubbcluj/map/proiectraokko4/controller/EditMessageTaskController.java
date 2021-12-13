package ro.ubbcluj.map.proiectraokko4.controller;


import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ro.ubbcluj.map.proiectraokko4.Message.Message;
import ro.ubbcluj.map.proiectraokko4.service.UtilizatorService;


public class EditMessageTaskController {
    @FXML
    private TextField textFieldId;
    @FXML
    private TextField textFieldDesc;
    @FXML
    private TextField textFieldFrom;
    @FXML
    private TextField textFieldTo;
    @FXML
    private TextArea textAreaMessage;
    @FXML
    private DatePicker datePickerDate;

    private UtilizatorService service;
    Stage dialogStage;
    Message message;

    @FXML
    private void initialize() {
    }


    public void setService(UtilizatorService service, Stage stage, Message m) {
        this.service = service;
        this.dialogStage=stage;
        this.message=m;
        if (null != m) {
            setFields(m);
            textFieldId.setEditable(false);
        }
    }

    @FXML
    public void handleSave(){
//        String desc=textFieldDesc.getText();
//        String from=textFieldFrom.getText();
//        Long friendUserId = service.Login(from);
//        if (service.areFriends(userId, friendUserId) == false) {
//            System.out.println(friendUserName + " is not a friend");
//            break;
//        }
//        String to=textFieldTo.getText();
//        String message=textAreaMessage.getText();
//        if (null == this.message)
//            saveMessage(m);
//        else
//            updateMessage(m);
    }

    private void updateMessage(Message m)
    {
//        try {
//            Message r= this.service.updateMessageTask(m);
//            if (r==null)
//                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Modificare mesaj","Mesajul a fost modificat");
//        } catch (ValidationException e) {
//            MessageAlert.showErrorMessage(null,e.getMessage());
//        }
//        dialogStage.close();
    }


    private void saveMessage(Message m)
    {
//        // TODO
//        try{
//            Message messageTask = service.addMessageTaskTask(m);
//            if ( messageTask == null) {
//                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Adaugare mesaj", "Mesajul a fost adaugat");
//            } else {
//                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Id invalid", "Mesajul nu a fost adaugat");
//            }
//        } catch (ValidationException e) {
//            MessageAlert.showErrorMessage(null,e.getMessage());
//        }

    }

    private void clearFields() {
        textFieldId.setText("");
        textFieldDesc.setText("");
        textFieldFrom.setText("");
        textFieldTo.setText("");
        textAreaMessage.setText("");
    }
    private void setFields(Message s)
    {
//        textFieldId.setText(s.getId());
//        textFieldDesc.setText(s.getDescription());
//        textFieldFrom.setText(s.getFrom());
//        textFieldTo.setText(s.getTo());
//        textAreaMessage.setText(s.getMessage());
    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
}
