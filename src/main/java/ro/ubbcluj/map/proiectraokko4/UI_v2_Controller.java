package ro.ubbcluj.map.proiectraokko4;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.w3c.dom.events.MouseEvent;

public class UI_v2_Controller {

    @FXML
    private ImageView SearchImg, HomeImg, ChatImg;

    @FXML
    private AnchorPane SearchPage, HomePage, ChatPage;


    void initialize() {

    }

    public void handleButtonAction(MouseEvent event) {
        if(event.getTarget() == SearchImg) {
            SearchPage.setVisible(true);
            ChatPage.setVisible(false);
            HomePage.setVisible(false);
        } else if (event.getTarget() == HomeImg) {
            HomePage.setVisible(true);
            ChatPage.setVisible(false);
            SearchPage.setVisible(false);
        } else if (event.getTarget() == ChatImg) {
            ChatPage.setVisible(true);
            HomePage.setVisible(false);
            SearchPage.setVisible(false);
        }
    }


    public void handleButtonAction(javafx.scene.input.MouseEvent mouseEvent) {
        if(mouseEvent.getTarget() == SearchImg) {
            SearchPage.setVisible(true);
            ChatPage.setVisible(false);
            HomePage.setVisible(false);
        } else if (mouseEvent.getTarget() == HomeImg) {
            HomePage.setVisible(true);
            ChatPage.setVisible(false);
            SearchPage.setVisible(false);
        } else if (mouseEvent.getTarget() == ChatImg) {
            ChatPage.setVisible(true);
            HomePage.setVisible(false);
            SearchPage.setVisible(false);
        }
    }
}
