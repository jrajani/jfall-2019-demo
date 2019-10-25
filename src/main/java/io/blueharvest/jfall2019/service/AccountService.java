package io.blueharvest.jfall2019.service;


import io.blueharvest.jfall2019.command.CreateAccountCommand;
import io.blueharvest.jfall2019.command.DepositMoneyCommand;
import io.blueharvest.jfall2019.command.WithdrawMoneyCommand;
import io.blueharvest.jfall2019.kafka.MessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class AccountService {

    @Autowired
    MessageProducer messageProducer;

    public void performAccountCreateCommand(CreateAccountCommand command) {

//        Assert.hasLength(command.getAccountType(), "AccountType must have a value");
        Assert.hasLength(command.getId(), "Account id must have length greater than Zero");

        messageProducer.sendMessage(command);
    }

    public void performDepositMoneyCommand(DepositMoneyCommand command) {

        Assert.hasLength(command.getId(), "Account id must have length greater than Zero");

        messageProducer.sendMessage(command);
    }

    public void performWithdrawMoneyCommand(WithdrawMoneyCommand command) {

        Assert.hasLength(command.getId(), "Account id must have length greater than Zero");

        messageProducer.sendMessage(command);
    }
}
