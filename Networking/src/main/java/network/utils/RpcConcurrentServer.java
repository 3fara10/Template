package network.utils;

import network.rpcprotocol.ClientRpcWorker;
import service.IGameService;

import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RpcConcurrentServer extends AbsConcurrentServer{
    private static final Logger log = LogManager.getLogger(RpcConcurrentServer.class);
    private final IGameService server;

    public RpcConcurrentServer(int port, IGameService server) {
        super(port);
        this.server = server;
        log.info("RpcConcurrentServer initialized on port: {}", port);
    }

    @Override
    protected Thread createWorker(Socket client) {
        log.debug("Creating RPC worker for client: {}", client.getInetAddress());
        ClientRpcWorker worker=new ClientRpcWorker(server, client);
        return new Thread(worker);
    }

    @Override
    public void stop() throws ServerException {
        log.info("Stopping RPC services ...");
        super.stop();
    }
}