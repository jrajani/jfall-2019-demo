package io.blueharvest.jfall2019.command;

import lombok.Data;

@Data
public class CreateAccountCommand extends BaseCommand {
    String firstName;
    int balance;

    public CreateAccountCommand() {

    }

    public CreateAccountCommand(String accountId, String firstName, int balance) {
        super(accountId);
        this.firstName = firstName;
        this.balance = balance;
    }
}
