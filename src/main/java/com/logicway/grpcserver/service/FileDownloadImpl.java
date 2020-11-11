package com.logicway.grpcserver.service;

import com.google.common.io.ByteStreams;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.logicway.grpcserver.filedownload.*;
import com.logicway.grpcserver.service.factory.CommandFactory;
import com.logicway.grpcserver.service.factory.exception.CommandCreationException;
import com.logicway.grpcserver.service.factory.impl.CommandFactoryImpl;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileDownloadImpl extends FileDownloadGrpc.FileDownloadImplBase {

    private static final Random RND = new Random();

    private final static int SEED = Short.MIN_VALUE / 16;

    private static final int MAX_SIZE = Math.abs(SEED) * 2;

    private static final int BOUND = 3;

    private List<Integer> integerList;

    private ListIterator<Integer> integerListIterator;

    public FileDownloadImpl() {

        integerList = Collections.synchronizedList(Stream
                .iterate(SEED, i -> ++i)
                .limit(MAX_SIZE)
                .filter(i -> i != 0)
                .collect(Collectors.toList()));

        integerListIterator = integerList.listIterator();
    }

    @Override
    public void generateRandomCommand(Empty request, StreamObserver<Command> responseObserver) {
        int number = RND.nextInt(BOUND) + 1;
        CommandFactory commandFactory = new CommandFactoryImpl();
        Command command = null;
        try {
            command = commandFactory.createCommand(number);
        } catch (CommandCreationException e) {
            e.printStackTrace();
        }
        responseObserver.onNext(command);
        responseObserver.onCompleted();
    }

    @Override
    public void downloadFile(FileDownloadRequest request, StreamObserver<DataChunk> responseObserver) {
        try {
            InputStream is = getClass().getClassLoader()
                    .getResourceAsStream(request.getUrl());
            byte[] bytes = ByteStreams.toByteArray(is);
            is.close();
            BufferedInputStream bis
                    = new BufferedInputStream(new ByteArrayInputStream(bytes));
            int bufferSize = 128;

            byte[] buffer = new byte[bufferSize];
            int length;
            while ((length = bis.read(buffer, 0, bufferSize)) != -1) {
                responseObserver.onNext(DataChunk.newBuilder()
                        .setData(ByteString.copyFrom(buffer, 0, length))
                        .build());
            }
            bis.close();
            responseObserver.onCompleted();
        } catch (Throwable e) {
            responseObserver.onError(Status.ABORTED
                    .withDescription("Unable to acquire the file " + request.getUrl())
                    .withCause(e)
                    .asException());
        }
    }

    @Override
    public void getCollectionElement(Empty request, StreamObserver<CollectionElement> responseObserver) {
        synchronized (integerListIterator) {
            if (integerListIterator.hasNext()) {
                responseObserver.onNext(CollectionElement.newBuilder()
                        .setValue(integerListIterator.next()).build());
            } else {
                responseObserver.onNext(CollectionElement.newBuilder()
                        .setValue(0).build());
            }
            responseObserver.onCompleted();
        }
    }

    @Override
    public void resetCollectionIterator(Empty request, StreamObserver<Empty> responseObserver) {
        integerListIterator = integerList.listIterator();
        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
