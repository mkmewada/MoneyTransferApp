package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }

    public boolean updateAccount(final Account accountFrom,final Account accountTo,final BigDecimal amount) {
        final String accountFromId = accountFrom.getAccountId();
        final String accountToId = accountTo.getAccountId();
                
        accounts.computeIfPresent(accountToId, (key,account)->{
        	account.setBalance(accountTo.getBalance().add(amount));
        	return account;
        });
        
        accounts.computeIfPresent(accountFromId, (key,account)->{
        	account.setBalance(accountFrom.getBalance().subtract(amount));
        	return account;
        });
        
        return true;
    }

}
