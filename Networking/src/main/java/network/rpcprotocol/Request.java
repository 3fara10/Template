package network.rpcprotocol;

import java.io.Serializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Request implements Serializable {
    private static final Logger log = LogManager.getLogger(Request.class);
    private RequestType type;
    private Object data;
    public RequestType type() {
        log.traceEntry();
        return type;
    }

    public Object data() {
        log.traceEntry();
        return data;
    }

    public void data(Object data) {
        log.trace("Setting data to: {}", data);
        this.data = data;
    }
    public void type(RequestType type) {
        log.trace("Setting type to: {}", type);
        this.type = type;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type='" + type + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public static class Builder {
        private static final Logger logBuilder = LogManager.getLogger(Builder.class);
        private final Request request = new Request();

        public Builder() {
            logBuilder.trace("Request.Builder created.");
        }

        public Builder type(RequestType type) {
            logBuilder.trace("Builder: Setting type to: {}", type);
            request.type(type);
            return this;
        }

        public Builder data(Object data) {
            logBuilder.trace("Builder: Setting data to: {}", data);
            request.data(data);
            return this;
        }

        public Request build() {
            logBuilder.trace("Builder: Building request.");
            return request;
        }
    }

}