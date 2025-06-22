import network.utils.AbstractServer;
import network.utils.RpcConcurrentServer;
import server.ServiceImpl;
import service.IGameService;

import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort = 55555;
    public static void main(String [] args){
        Properties serverProps = new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/server/properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatserver.properties "+e);
            return;
        }

        IGameService services = new ServiceImpl(serverProps);
        int serverPort=defaultPort;
        try{
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
        }catch (NumberFormatException nef){
            System.err.println("Wrong  Port Number"+nef.getMessage());
            System.err.println("Using default port "+defaultPort);
        }
        System.out.println("Starting server on port: "+serverPort);
        AbstractServer server = new RpcConcurrentServer(serverPort, services);
        try{
            server.start();
        } catch (network.utils.ServerException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                server.stop();
            } catch (network.utils.ServerException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
