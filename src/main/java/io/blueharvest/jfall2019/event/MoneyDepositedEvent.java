package io.blueharvest.jfall2019.event;

import lombok.Data;

@Data
public class MoneyDepositedEvent extends BaseEvent {
    private int amount;

    public MoneyDepositedEvent() {

    }

    public MoneyDepositedEvent(String accountId, int amount) {
        super(accountId);
        this.amount = amount;
    }
}
