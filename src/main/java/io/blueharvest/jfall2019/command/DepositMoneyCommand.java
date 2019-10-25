package io.blueharvest.jfall2019.command;

import lombok.Data;

@Data
public class DepositMoneyCommand extends BaseCommand {
    private int amount;

    public DepositMoneyCommand() {

    }

    public DepositMoneyCommand(String accountId, int amount) {
        super(accountId);
        this.amount = amount;
    }
}
