package io.blueharvest.jfall2019.command;

import lombok.Data;

@Data
public class WithdrawMoneyCommand extends BaseCommand {
    private int amount;

    public WithdrawMoneyCommand() {

    }

    public WithdrawMoneyCommand(String accountId, int amount) {
        super(accountId);
        this.amount = amount;
    }
}
