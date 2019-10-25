package io.blueharvest.jfall2019.event;

import org.springframework.util.Assert;

import java.io.Serializable;

public class BaseEvent implements Serializable {
    private final String eventType = this.getClass().getSimpleName();

    private String id;

    public BaseEvent(String id) {
        Assert.notNull(id, "Id must be not null");
        this.id = id;
    }

    public BaseEvent() {

    }

    public String getEventType() {
        return eventType;
    }

    public String getId() {
        return id;
    }
}
