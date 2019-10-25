package io.blueharvest.jfall2019.command;

import org.springframework.util.Assert;

public class BaseCommand {

    private final String commandType = this.getClass().getSimpleName();
    private String id;

    public BaseCommand(String id) {
        Assert.notNull(id, "Id must be not null");
        this.id = id;
    }
    public BaseCommand() {}

    public String getCommandType() {
        return commandType;
    }
    public String getId() {
        return id;
    }
}
