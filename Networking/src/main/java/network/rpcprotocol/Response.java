package network.rpcprotocol;
import service.ServiceEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

public class Response implements Serializable {
    private static final Logger log = LogManager.getLogger(Response.class);
    private ResponseType type;
    private ServiceEvent serviceEventType;
    private Object data;

    public Response() {
        log.trace("Response created (empty constructor).");
    }

    public ResponseType type() {
        log.traceEntry();
        return type;
    }

    public Object data() {
        log.traceEntry();
        return data;
    }

    public ServiceEvent serviceEventType() {
        log.traceEntry();
        return serviceEventType;
    }
    public void serviceEventType(ServiceEvent serverEventType) {
        log.trace("Setting serviceEventType to: {}", serverEventType);
        this.serviceEventType = serverEventType;
    }

    public void data(Object data) {
        log.trace("Setting data to: {}", data);
        this.data = data;
    }

    public void type(ResponseType type) {
        log.trace("Setting type to: {}", type);
        this.type = type;
    }

    @Override
    public String toString() {
        return "Response{" +
                "type=" + type +
                ", serviceEventType=" + serviceEventType +
                ", data=" + data +
                '}';
    }


    public static class Builder{
        private static final Logger logBuilder = LogManager.getLogger(Builder.class);
        private final Response response=new Response();

        public Builder() {
            logBuilder.trace("Response.Builder created.");
        }

        public Builder type(ResponseType type) {
            logBuilder.trace("Builder: Setting type to: {}", type);
            response.type(type);
            return this;
        }

        public Builder data(Object data) {
            logBuilder.trace("Builder: Setting data to: {}", data);
            response.data(data);
            return this;
        }

        public Builder serviceEventType(ServiceEvent serviceEventType) {
            logBuilder.trace("Builder: Setting serviceEventType to: {}", serviceEventType);
            response.serviceEventType(serviceEventType);
            return this;
        }

        public Response build() {
            logBuilder.trace("Builder: Building response.");
            return response;
        }
    }
}