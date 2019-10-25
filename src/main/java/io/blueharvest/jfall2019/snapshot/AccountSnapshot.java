package io.blueharvest.jfall2019.snapshot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSnapshot {
    private String accountId;
    private int balance;
}

