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
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServiceRpcProxy implements IGameService {
    private static final Logger log = LogManager.getLogger(ServiceRpcProxy.class);

    private final String host;
    private final int port;
    private IObserver client;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Socket connection;
    private final BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ServiceRpcProxy(String host, int port) {
        log.traceEntry("Initializing ServiceRpcProxy with host: {} and port: {}", host, port);
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<>();
        log.traceExit("ServiceRpcProxy initialized successfully.");
    }

    private void initializeConnection() throws ServiceException {
        log.traceEntry();
        try {
            qresponses.clear();
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
            log.info("Connection to server {}:{} initialized successfully.", host, port);
        } catch (IOException e) {
            log.error("Failed to initialize connection to {}:{}: {}", host, port, e.getMessage(), e);
            throw new ServiceException("Connection failed: " + e.getMessage());
        }
        log.traceExit();
    }

    private void closeConnection() {
        log.traceEntry();
        finished = true;
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (connection != null && !connection.isClosed()) connection.close();
            qresponses.clear();
            qresponses.put(new Response.Builder().type(ResponseType.NO_OP).build());
            log.info("Connection to server closed successfully.");
        } catch (IOException | InterruptedException e) {
            log.error("Error while closing connection: {}", e.getMessage(), e);
        }
        log.traceExit();
    }

    private void startReader() {
        log.traceEntry();
        Thread tw = new Thread(new ReaderThread());
        tw.start();
        log.debug("ReaderThread started.");
        log.traceExit();
    }

    private synchronized void sendRequest(Request request) throws ServiceException {
        log.traceEntry("Sending request type: {}", request.type());
        try {
            output.writeObject(request);
            output.flush();
            log.debug("Request {} sent successfully.", request.type());
        } catch (IOException e) {
            log.error("Error sending request {}: {}", request.type(), e.getMessage(), e);
            throw new ServiceException("Error sending request: " + e.getMessage());
        }
        log.traceExit();
    }

    private Response readResponse() throws ServiceException {
        log.traceEntry();
        Response response = null;
        try {
            response = qresponses.take();
            if (response.type() == ResponseType.NO_OP) {
                log.warn("Received NO_OP response, server likely closed connection.");
                throw new ServiceException("Server closed connection.");
            }
            log.debug("Received response type: {}", response.type());
        } catch (InterruptedException e) {
            log.error("Error reading response from queue (interrupted): {}", e.getMessage(), e);
            Thread.currentThread().interrupt(); // Re-interupt the thread
            throw new ServiceException("Error reading response: " + e.getMessage());
        }
        log.traceExit("Returning response type: {}", response.type());
        return response;
    }

    @Override
    public Game startNewGame(String playerUsername, IObserver clientObserver) throws ServiceException {
        log.traceEntry("Proxy: startNewGame for player: {}", playerUsername);
        try {
            Request request = new Request.Builder()
                    .type(RequestType.START_GAME)
                    .data(playerUsername)
                    .build();

            sendRequest(request);
            Response response = readResponse();

            if (response.type() == ResponseType.OK) {
                Game newGame = (Game) response.data();
                log.info("Successfully started new game for player {}. Game ID: {}", playerUsername, newGame.getId());
                log.traceExit("New game started: {}", newGame.getId());
                return newGame;
            }
            if (response.type() == ResponseType.ERROR) {
                String err = (String) response.data();
                log.error("Failed to start new game for player {}: {}", playerUsername, err);
                log.traceExit("Start game failed with error.");
                throw new ServiceException(err);
            }
            log.error("Start game operation failed with unexpected response type: {}", response.type());
            log.traceExit("Start game failed with unexpected response.");
            throw new ServiceException("Error starting new game: Unexpected response.");
        } catch (ServiceException e) {
            log.error("ServiceException in startNewGame for player {}: {}", playerUsername, e.getMessage(), e);
            log.traceExit("Start game failed with ServiceException.");
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in startNewGame for player {}: {}", playerUsername, e.getMessage(), e);
            log.traceExit("Start game failed with unexpected exception.");
            throw new ServiceException("Unexpected error starting game: " + e.getMessage());
        }
    }

    @Override
    public User login(String username, String password, IObserver clientObserver) throws ServiceException {
        log.traceEntry("Attempting login for user: {}", username);
        initializeConnection();
        try {
            User user = new User(username, password);
            Request request = new Request.Builder().type(RequestType.LOGIN).data(user).build();
            sendRequest(request);

            Response response = readResponse();
            if (response.type() == ResponseType.OK) {
                this.client = clientObserver;
                User loggedInUser = (User) response.data();
                log.info("Login successful for user: {}", username);
                log.traceExit("Login successful.");
                return loggedInUser;
            }
            if (response.type() == ResponseType.ERROR) {
                String err = (String) response.data();
                log.error("Login failed for user {}: {}", username, err);
                closeConnection();
                log.traceExit("Login failed with error.");
                throw new ServiceException(err);
            }
            log.error("Login operation for user {} failed with unexpected response type: {}", username, response.type());
            closeConnection();
            log.traceExit("Login failed with unexpected response type.");
            throw new ServiceException("Login operation failed with unexpected response type.");
        } catch (ServiceException e) {
            log.error("ServiceException during login for user {}: {}", username, e.getMessage(), e);
            closeConnection();
            log.traceExit("Login failed with ServiceException.");
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during login for user {}: {}", username, e.getMessage(), e);
            closeConnection();
            log.traceExit("Login failed with unexpected exception.");
            throw new ServiceException("Unexpected error during login: " + e.getMessage());
        }
    }

    @Override
    public boolean logout(String username, IObserver clientObserver) throws ServiceException {
        log.traceEntry("Attempting logout for user: {}", username);
        try {
            Request request = new Request.Builder().type(RequestType.LOGOUT).data(username).build();
            sendRequest(request);

            Response response = readResponse();
            closeConnection();

            if (response.type() == ResponseType.OK) {
                this.client = null;
                log.info("Logout successful for user: {}", username);
                log.traceExit("Logout successful.");
                return true;
            }
            if (response.type() == ResponseType.ERROR) {
                String err = (String) response.data();
                log.error("Logout failed for user {}: {}", username, err);
                log.traceExit("Logout failed with error.");
                throw new ServiceException(err);
            }
            log.error("Logout operation for user {} failed with unexpected response type: {}", username, response.type());
            log.traceExit("Logout failed with unexpected response type.");
            throw new ServiceException("Logout operation failed with unexpected response type.");
        } catch (ServiceException e) {
            log.error("ServiceException during logout for user {}: {}", username, e.getMessage(), e);
            closeConnection();
            log.traceExit("Logout failed with ServiceException.");
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during logout for user {}: {}", username, e.getMessage(), e);
            closeConnection();
            log.traceExit("Logout failed with unexpected exception.");
            throw new ServiceException("Unexpected error during logout: " + e.getMessage());
        }
    }

    @Override
    public void addObserver(String identifier, IObserver observer) throws ServiceException {
        log.traceEntry("Adding observer for identifier: {}", identifier);
        this.client = observer;
        log.debug("Observer set for identifier: {}", identifier);
        log.traceExit();
    }

    @Override
    public void removeObserver(String identifier, IObserver observer) {
        log.traceEntry("Attempting to remove observer for identifier: {}", identifier);
        if (this.client == observer) {
            this.client = null;
            log.debug("Observer removed for identifier: {}", identifier);
        } else {
            log.warn("Attempted to remove observer that was not currently set for identifier: {}", identifier);
        }
        log.traceExit();
    }

    @Override
    public void notifyObservers(ServiceEvent serviceEvent, Object data) throws ServiceException {
        log.traceEntry("Notifying observers for event: {}", serviceEvent);
        if (client != null) {
            try {
                client.handleEvent(serviceEvent, data);
                log.debug("Observer notified for event: {}", serviceEvent);
            } catch (ServiceException e) {
                log.error("Error notifying observer for event {}: {}", serviceEvent, e.getMessage(), e);
                throw e; // Re-throw the exception from the observer
            }
        } else {
            log.warn("Client observer is null, cannot notify for event: {}", serviceEvent);
        }
        log.traceExit();
    }

    private class ReaderThread implements Runnable {
        private static final Logger logReader = LogManager.getLogger(ReaderThread.class);

        public void run() {
            logReader.traceEntry("ReaderThread started.");
            while (!finished) {
                try {
                    Object receivedObject = input.readObject();
                    if (receivedObject instanceof Response) {
                        Response response = (Response) receivedObject;
                        logReader.debug("Response received from server: {}", response);
                        if (response.type() == ResponseType.SERVER_EVENT) {
                            logReader.trace("Received server event: {}", response.serviceEventType());
                            ServiceRpcProxy.this.notifyObservers(response.serviceEventType(), response.data());
                        } else {
                            try {
                                qresponses.put(response);
                                logReader.trace("Response type {} put into queue.", response.type());
                            } catch (InterruptedException e) {
                                logReader.error("Error putting response into queue (interrupted): {}", e.getMessage(), e);
                                Thread.currentThread().interrupt();
                            }
                        }
                    } else {
                        logReader.warn("Received non-Response object from server: {}", receivedObject);
                    }
                } catch (IOException e) {
                    if (!finished) {
                        logReader.error("Reading error from server, closing connection: {}", e.getMessage(), e);
                        try {
                            qresponses.put(new Response.Builder().type(ResponseType.NO_OP).build());
                        } catch (InterruptedException interruptedException) {
                            logReader.error("Failed to put NO_OP response during shutdown: {}", interruptedException.getMessage());
                            Thread.currentThread().interrupt();
                        }
                        finished = true; // Mark thread as finished
                    } else {
                        logReader.debug("IOException during shutdown, expected behavior: {}", e.getMessage());
                    }
                } catch (ClassNotFoundException e) {
                    logReader.error("Class not found during object deserialization: {}", e.getMessage(), e);
                } catch (ServiceException e) {
                    logReader.error("ServiceException when handling server event: {}", e.getMessage(), e);
                }
            }
            logReader.traceExit("ReaderThread finished.");
        }
    }
}