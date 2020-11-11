package com.logicway.grpcserver.service.factory.impl;

import com.logicway.grpcserver.service.factory.CommandFactory;
import com.logicway.grpcserver.service.factory.exception.CommandCreationException;
import com.logicway.grpcserver.filedownload.Command;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.logicway.grpcserver.filedownload.CommandName.*;

public class CommandFactoryImpl implements CommandFactory {

    private static final Logger LOGGER = Logger.getLogger(CommandFactoryImpl.class.getName());

    @Override
    public Command createCommand(int number) throws CommandCreationException {
        Command command;
        switch (number) {
            case 1:
                command = Command.newBuilder().setCommandName(DOWNLOAD_DEX_FILE).build();
                break;
            case 2:
                command = Command.newBuilder().setCommandName(DOWNLOAD_SO_FILE).build();
                break;
            case 3:
                command = Command.newBuilder().setCommandName(GET_AD).build();
                break;
            default:
                LOGGER.log(Level.WARNING, "Command creation failed!"
                        + " Wrong number {" + number + "}");
                throw new CommandCreationException("Command creation failed!"
                        + " Wrong number {" + number + "}");
        }
        return command;
    }
}
