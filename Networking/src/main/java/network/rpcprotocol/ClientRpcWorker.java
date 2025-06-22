package network.rpcprotocol;

import model.Game;
import model.User;
import service.IGameService;
import service.IObserver;
import service.ServiceEvent;
import service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Optional;

public class ClientRpcWorker implements Runnable, IObserver {
    private static final Logger log = LogManager.getLogger(ClientRpcWorker.class);
    private User user = new User( "unknown","unknown");
    private final IGameService server;
    private final Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public ClientRpcWorker(IGameService server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
            log.info("ClientRpcWorker initialized for connection from {}", connection.getInetAddress());
        }catch (IOException e){
            log.error("Error initializing ClientRpcWorker: {}", e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        while(connected){
            try {
                Object request = input.readObject();
                log.debug("User {} received request {}", user.getUsername(), request);
                Response response = handleRequest((Request) request);
                if (response != null){
                    sendResponse(response);
                }
            } catch (IOException e) {
                log.info("User {} disconnected: {}", user.getUsername(), e.getMessage());
                connected = false;
            } catch (ClassNotFoundException e) {
                log.error("Error reading object from stream: {}", e.getMessage(), e);
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
            log.info("Client connection closed for user {}", user.getUsername());
        } catch (IOException e) {
            log.error("Error closing client connection for user {}: {}", user.getUsername(), e.getMessage(), e);
        }
    }

    private Response handleRequest(Request request){
        Response response = null;
        String handlerName = "handle" + request.type().toString();
        try {
            Method method = this.getClass().getDeclaredMethod(handlerName, Request.class);
            response = (Response) method.invoke(this, request);
            log.debug("Handled request {} with response {}", request.type(), response);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Error handling request {}: {}", request.type(), e.getMessage(), e);
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
        return response;
    }

    private synchronized void sendResponse(Response response) throws IOException{
        log.debug("User {} sending response {}", user.getUsername(), response);
        try{
            output.writeObject(response);
            output.flush();
            log.trace("Response sent successfully to user {}", user.getUsername());
        }catch (IOException e){
            connected = false;
            log.error("User {} unexpectedly disconnected while sending response: {}", user.getUsername(), e.getMessage(), e);
        }
    }

    @Override
    public void handleEvent(ServiceEvent serviceEvent, Object data) throws ServiceException {
        log.info("Handling server event: {} for user {}", serviceEvent, user.getUsername());
        Response response = new Response
                .Builder()
                .type(ResponseType.SERVER_EVENT)
                .serverEventType(serviceEvent)
                .data(data).build();
        try {
            sendResponse(response);
        } catch (IOException e) {
            log.error("Sending event error to user {}: {}", user.getUsername(), e.getMessage(), e);
            throw new ServiceException("Sending error: "+e);
        }
    }

    private Response handleLOGIN(Request request){
        log.traceEntry("Handling LOGIN request for user data: {}", request.data());
        try{
            User us = (User)request.data();
            Optional<User> loggedInUser = server.login(user.getUsername(), this);
            if(loggedInUser.isPresent()){
                this.user = loggedInUser.get();
                log.info("User {} logged in successfully.", user.getUsername());
                return new Response.Builder().type(ResponseType.OK).data(loggedInUser.get()).build();
            } else {
                log.warn("Login failed for user {}", us.getUsername());
                return new Response.Builder().type(ResponseType.ERROR).data("Login failed!").build();
            }
        } catch (ServiceException e) {
            log.error("Login service exception for user {}: {}", request.data(), e.getMessage(), e);
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        } catch (ClassCastException e) {
            log.error("Invalid data type for login request: {}", e.getMessage(), e);
            return new Response.Builder().type(ResponseType.ERROR).data("Invalid login data.").build();
        } finally {
            log.traceExit("Finished handling LOGIN request.");
        }
    }

    private Response handleLOGOUT(Request request){
        log.traceEntry("Handling LOGOUT request for user: {}", user.getUsername());
        try{
            server.logout(user.getUsername(), this);
            log.info("User {} logged out successfully.", user.getUsername());
            connected = false;
            return new Response.Builder().type(ResponseType.OK).build();
        } catch (ServiceException e) {
            log.error("Logout service exception for user {}: {}", user.getUsername(), e.getMessage(), e);
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        } finally {
            log.traceExit("Finished handling LOGOUT request.");
        }
    }

    private Response handleSTART_GAME(Request request){
        log.traceEntry("Handling START_GAME request for player alias: {}", request.data());
        try{
            String playerAlias=(String)request.data();
            Game game = server.startNewGame(playerAlias,this);
            this.user.setUsername(playerAlias);
            log.info("Game started successfully for player {}", playerAlias);
            return new Response.Builder().type(ResponseType.OK).data(game).build();
        } catch (ServiceException e) {
            log.error("Start game service exception: {}", e.getMessage(), e);
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        } catch (ClassCastException e) {
            log.error("Invalid data type for start game request: {}", e.getMessage(), e);
            return new Response.Builder().type(ResponseType.ERROR).data("Invalid game start data.").build();
        } finally {
            log.traceExit("Finished handling START_GAME request.");
        }
    }

}