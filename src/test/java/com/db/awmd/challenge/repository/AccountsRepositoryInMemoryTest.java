package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountsRepositoryInMemoryTest {

    private AccountsRepository accountsRepository;

    @Before
    public void setUp(){
        accountsRepository = new AccountsRepositoryInMemory();
    }

    @Test
    public void testAccountsWithUpdateAccount() throws Exception {
    	Account account1 = new Account("AXIS1", BigDecimal.ZERO);
    	Account account2 = new Account("AXIS2", new BigDecimal("150.20"));
        accountsRepository.createAccount(account1);
        accountsRepository.createAccount(account2);

        accountsRepository.updateAccount(account2, account1, new BigDecimal("50.20"));
        assertBalance("AXIS1", BigDecimal.ZERO);
        assertBalance("AXIS2", new BigDecimal("100.20"));
    }

    private void assertBalance(String accountId, BigDecimal balance){
        assertThat(accountsRepository.getAccount(accountId).getBalance()).isEqualTo(balance);
    }

}