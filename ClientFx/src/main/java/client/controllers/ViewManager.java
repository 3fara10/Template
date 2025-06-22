package client.controllers;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.example.IService;

import java.util.HashMap;
import java.util.Map;

public class ViewManager {
    private final Map<String, Pair<Scene, Controller>> scenes = new HashMap<>();
    private final Stage mainStage;

    public ViewManager(Stage mainStage){
        this.mainStage = mainStage;
    }

    public void addScene(String name, Scene scene, Controller controller, IService service){
        controller.set(service, this);
        scenes.put(name, new Pair<>(scene, controller));
    }

    public Controller getController(String name){
        return scenes.get(name).getValue();
    }

    public void activate(String name) throws Exception {
        if(!scenes.containsKey(name)){
            throw new Exception("Scene not found: " + name);
        }
        mainStage.setScene(scenes.get(name).getKey());
        scenes.get(name).getValue().init();
        mainStage.setTitle(name);
        mainStage.show();
        mainStage.close();
    }

    public Stage getStage(){
        return mainStage;
    }
}
