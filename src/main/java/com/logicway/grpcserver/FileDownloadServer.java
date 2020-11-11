package com.logicway.grpcserver;

import com.logicway.grpcserver.service.FileDownloadImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class FileDownloadServer {

    private static final Logger LOGGER = Logger.getLogger(FileDownloadServer.class.getName());

    private static final int PORT = 1337;

    private Server server;

    private void start() throws IOException {
        server = ServerBuilder.forPort(PORT)
                .addService(new FileDownloadImpl())
                .build()
                .start();
        LOGGER.info("Server started, listening on " + PORT);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                FileDownloadServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        FileDownloadServer server = new FileDownloadServer();
        server.start();
        server.blockUntilShutdown();
    }
}
