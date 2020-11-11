package com.logicway.grpcserver.service.factory;

import com.logicway.grpcserver.service.factory.exception.CommandCreationException;
import com.logicway.grpcserver.filedownload.Command;

public interface CommandFactory {
    Command createCommand(int number) throws CommandCreationException;
}
