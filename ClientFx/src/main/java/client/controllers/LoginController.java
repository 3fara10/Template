package client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import model.Player;
import org.example.ServiceException;

public class LoginController extends Controller{
    @FXML
    public Text errorText;
    @FXML
    public TextField usernameTextField;
    @FXML
    public CheckBox showPassCheckBox;

    @Override
    public void init(){
        /* FUNCTION CALLED EVERY TIME VIEWMANAGER CHANGES SCENES*/
        System.out.println("LoginController init");
        errorText.setText("");
    }

    public void login(){
        System.out.println("Login");
        String username = usernameTextField.getText();

        try {
            Player user = service.login(username, " ", (MainController) viewManager.getController("Main"));
            if(user == null){
                throw new ServiceException("Login failed");
            }
            ((MainController) viewManager.getController("Main")).setUser(user);
            viewManager.activate("Main");
        } catch (Exception e){
            errorText.setText(e.getMessage());
        }
    }

}
