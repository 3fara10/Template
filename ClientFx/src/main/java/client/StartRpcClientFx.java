package client;

import client.controllers.ViewManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.rpcprotocol.ServiceRpcProxy;
import org.example.IGameService;

import java.io.IOException;
import java.util.Properties;

public class StartRpcClientFx extends Application {
    private static int defaultChatPort = 55555;
    private static String defaultServer = "localhost";


    public void start(Stage primaryStage) throws Exception {
        System.out.println("In start");
        Properties clientProps = new Properties();
        try{
            clientProps.load(StartRpcClientFx.class.getResourceAsStream("/properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);

        }catch (IOException e){
            System.err.println("Cannot find chatclient.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("server.host", defaultServer);
        int serverPort = defaultChatPort;
        try {
            serverPort = Integer.parseInt(clientProps.getProperty("server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultChatPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);
        IGameService server = new ServiceRpcProxy(serverIP, serverPort);
        ViewManager manager = new ViewManager(primaryStage);
        FXMLLoader loader1 = new FXMLLoader(getClass().getResource("/client/LoginView.fxml"));
        manager.addScene("Login", new Scene(loader1.load()), loader1.getController(), server);


        FXMLLoader loader2 = new FXMLLoader(
                getClass().getResource("/client/MainView.fxml"));
        manager.addScene("Main", new Scene(loader2.load()), loader2.getController(), server);
        server.addObserver(null, loader2.getController());
        manager.activate("Login");

    }
}
