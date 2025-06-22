package client.controllers;


import org.example.IService;

public class Controller {
    protected IService service;
    protected ViewManager viewManager;

    public void set(IService service, ViewManager viewManager){
        this.service = service;
        this.viewManager = viewManager;
    }

    public void init(){
        System.out.println("Controller init");
    }
}