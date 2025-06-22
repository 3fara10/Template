package network.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public abstract class AbstractServer {
    private static final Logger log = LogManager.getLogger(AbstractServer.class);
    private final int port;
    private ServerSocket server=null;

    public AbstractServer( int port){
        this.port=port;
        log.info("AbstractServer initialized on port: {}", port);
    }

    public void start() throws ServerException {
        try{
            server=new ServerSocket(port);
            log.info("Server started on port: {}", port);
            while(true){
                log.info("Waiting for clients ...");
                Socket client=server.accept();
                log.info("Client connected: {}", client.getInetAddress());
                processRequest(client);
            }
        } catch (IOException e) {
            log.error("Starting server error on port {}: {}", port, e.getMessage(), e);
            throw new ServerException("Starting server errror ",e);
        }finally {
            stop();
        }
    }

    protected abstract  void processRequest(Socket client);

    public void stop() throws ServerException {
        log.info("Stopping server...");
        try {
            if (server != null && !server.isClosed()) {
                server.close();
                log.info("Server stopped.");
            }
        } catch (IOException e) {
            log.error("Closing server error: {}", e.getMessage(), e);
            throw new ServerException("Closing server error ", e);
        }
    }
}