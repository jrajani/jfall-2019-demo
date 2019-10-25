package io.blueharvest.jfall2019.endpoint;


import io.blueharvest.jfall2019.service.AccountService;
import io.blueharvest.jfall2019.command.CreateAccountCommand;
import io.blueharvest.jfall2019.command.DepositMoneyCommand;
import io.blueharvest.jfall2019.command.WithdrawMoneyCommand;
import io.blueharvest.jfall2019.kafka.MessageListener;
import io.blueharvest.jfall2019.kafka.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;

@RestController
@RequestMapping("/accounts")
public class AccountEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(AccountEndpoint.class);

    @Autowired
    MessageProducer messageProducer;

    @Autowired
    MessageListener messageListener;

    @Autowired
    private AccountService accountService;

    @PostMapping
    @Transactional
    public void postCustomer(@RequestBody @Valid CreateAccountCommand command) throws ParseException {
        accountService.performAccountCreateCommand(command);
    }

    @PutMapping(path = "/deposit")
    public void deposit(@RequestBody DepositMoneyCommand command) {
        accountService.performDepositMoneyCommand(command);
    }

    @PutMapping(path = "/withdraw")
    public void deposit(@RequestBody WithdrawMoneyCommand command) {
        accountService.performWithdrawMoneyCommand(command);
    }
}
