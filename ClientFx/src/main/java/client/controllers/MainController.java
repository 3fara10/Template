package client.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;
import model.Player;
import org.example.IObserver;
import org.example.ServiceEvent;
import org.example.ServiceException;

public class MainController extends Controller implements IObserver {
    Player user = null;

    @FXML
    Text errorText;

    @FXML
    Text userNameText;

    @FXML
    TabPane tabPaneScenes;
    int currentTab = 0;

    @Override
    public void handleEvent(ServiceEvent serviceEvent, Object data) throws ServiceException {Platform.runLater(()->{


    });}

    public void setUser(Player user){
        this.user = user;
        if(user != null){
            userNameText.setText(user.getAlias());
        }
        else{
            userNameText.setText("");
        }
        viewManager.getStage().setOnCloseRequest(event -> {
            try {
                assert user != null;
                service.logout(user.getAlias(), this);
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            System.exit(0);
        });
    }

    public void logout() throws Exception {
        try {
            if(service.logout(user.getAlias(), this)){
                setUser(null);
                viewManager.getStage().setOnCloseRequest(event -> System.exit(0));
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        viewManager.activate("Login");
    }

    public void changeScene(){
        currentTab = 1 - currentTab;
        setTabPaneScene(currentTab);
    }

    @Override
    public void init(){
        System.out.println("MainController init");
        currentTab = 0;
        setTabPaneScene(currentTab);
    }

    private void setTabPaneScene(int index){
        tabPaneScenes.getTabs().forEach(tab -> tab.setDisable(true));
        tabPaneScenes.getTabs().get(index).setDisable(false);
        tabPaneScenes.getSelectionModel().select(index);
    }
}
