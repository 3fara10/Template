package network.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerException extends Exception{
    private static final Logger log = LogManager.getLogger(ServerException.class);

    public ServerException(String message) {
        super(message);
        log.error("ServerException: {}", message);
    }

    public ServerException() {
        super();
        log.error("ServerException occurred.");
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
        log.error("ServerException: {} - Cause: {}", message, cause.getMessage(), cause);
    }
}