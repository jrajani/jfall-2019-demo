package io.blueharvest.jfall2019.event;

import lombok.Data;

@Data
public class AccountCreatedEvent extends BaseEvent {
    private String firstName;
    private int balance;

    public AccountCreatedEvent() {

    }

    public AccountCreatedEvent(String accountId, String firstName, int balance) {
        super(accountId);
        this.firstName = firstName;
        this.balance = balance;
    }
}
