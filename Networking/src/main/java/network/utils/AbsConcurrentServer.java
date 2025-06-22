package network.utils;

import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbsConcurrentServer extends AbstractServer {
    private static final Logger log = LogManager.getLogger(AbsConcurrentServer.class);

    public AbsConcurrentServer(int port) {
        super(port);
        log.info("Concurrent AbstractServer initialized on port: {}", port);
    }

    @Override
    protected void processRequest(Socket client) {
        log.info("Processing request from client: {}", client.getInetAddress());
        Thread tw=createWorker(client);
        tw.start();
        log.debug("Worker thread started for client: {}", client.getInetAddress());
    }

    protected abstract Thread createWorker(Socket client);
}
