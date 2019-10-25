package io.blueharvest.jfall2019.event;

import lombok.Data;

@Data
public class MoneyWithdrawnEvent extends BaseEvent {
    private int amount;

    public MoneyWithdrawnEvent() {

    }

    public MoneyWithdrawnEvent(String accountId, int amount) {
        super(accountId);
        this.amount = amount;
    }
}
