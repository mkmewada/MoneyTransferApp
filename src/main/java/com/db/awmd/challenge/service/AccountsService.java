package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransferInput;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.NotEnoughFundsException;
import com.db.awmd.challenge.exception.TransferBetweenSameAccountException;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
public class AccountsService {

    @Getter
    private final AccountsRepository accountsRepository;

    @Getter
    private final NotificationService notificationService;

    @Autowired
    private AmountValidator amountValidator;

    @Autowired
    public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
        this.accountsRepository = accountsRepository;
        this.notificationService = notificationService;
    }

    public void createAccount(Account account) {
        this.accountsRepository.createAccount(account);
    }

    public Account getAccount(String accountId) {
        return this.accountsRepository.getAccount(accountId);
    }

    /**
     * Transfer between two accounts for the balance specified by the {@link AmountTransferInput} object
     * @param transfer
     * @throws AccountNotFoundException When an account does not exist
     * @throws NotEnoughFundsException When there are not enough funds to complete the transfer
     */
    public void transferAmount(AmountTransferInput transfer) throws AccountNotFoundException, NotEnoughFundsException,TransferBetweenSameAccountException {

        final Account accountFrom = accountsRepository.getAccount(transfer.getAccountFromId());
        final Account accountTo = accountsRepository.getAccount(transfer.getAccountToId());
        final BigDecimal amount = transfer.getAmount();

        amountValidator.validate(accountFrom, accountTo, transfer);

        //ideally atomic operation in production
        boolean successful = accountsRepository.updateAccount(accountFrom,accountTo,amount);

        if (successful){
            notificationService.notifyAboutTransfer(accountFrom, "Account Number " + accountTo.getAccountId() + " debited with amount " + transfer.getAmount() + ".");
            notificationService.notifyAboutTransfer(accountTo, "Account Number + " + accountFrom.getAccountId() + " credited with amount " + transfer.getAmount() + ".");
        }
    }
    
    public AccountsRepository getAccountsRepository() {
		return accountsRepository;
	}

}
