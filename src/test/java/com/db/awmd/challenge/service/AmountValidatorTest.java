package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransferInput;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.NotEnoughFundsException;
import com.db.awmd.challenge.exception.TransferBetweenSameAccountException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;


public class AmountValidatorTest {

    private AmountValidator amountValidator;

    @Before
    public void setUp() {
    	amountValidator = new AmountValidatorImpl();
    }

    @Test
    public void validateExcecptionAccountFromNotFound() throws Exception {
        final Account accountTo = new Account("AXIS2");
        final AmountTransferInput transfer = new AmountTransferInput("AXIS1", accountTo.getAccountId(), new BigDecimal("2.00"));

        try {
        	amountValidator.validate(null, new Account("AXIS2"), transfer);
            fail("Account not be found");
        } catch (AccountNotFoundException ace) {
            assertThat(ace.getMessage()).isEqualTo("Account not found.");
        }
    }

    @Test
    public void validateExcecptionAccountToNotFound() throws Exception {
        final Account accountFrom = new Account("AXIS1");
        final AmountTransferInput transfer = new AmountTransferInput("AXIS1", "AXIS5342", new BigDecimal("2.00"));

        try {
        	amountValidator.validate(accountFrom, null, transfer);
            fail("Account not found");
        } catch (AccountNotFoundException ace) {
            assertThat(ace.getMessage()).isEqualTo("Account not found.");
        }
    }

    @Test
    public void validateExceptionWhenNoFunds() throws Exception {
        final Account accountFrom = new Account("AXIS1");
        final Account accountTo = new Account("AXIS2");
        final AmountTransferInput transfer = new AmountTransferInput("AXIS1", "AXIS2", new BigDecimal("2.00"));

        try {
        	amountValidator.validate(accountFrom, accountTo, transfer);
            fail("Not enough funds");
        } catch (NotEnoughFundsException nbe) {
            assertThat(nbe.getMessage()).isEqualTo("No enough funds in Account");
        }
    }

    @Test
    public void validateExceptionWhenTransferToSameAccount() throws Exception {
        final Account accountFrom = new Account("AXIS1", new BigDecimal("20.00"));
        final Account accountTo = new Account("AXIS1");
        final AmountTransferInput transfer = new AmountTransferInput("AXIS1", "AXIS1", new BigDecimal("2.00"));

        try {
        	amountValidator.validate(accountFrom, accountTo, transfer);
            fail("Transferring to same account");
        } catch (TransferBetweenSameAccountException sae) {
            assertThat(sae.getMessage()).isEqualTo("self account transfer not permitted.");
        }
    }

    @Test
    public void validateTransferDifferentAccounts() throws Exception {
        final Account accountFrom = new Account("AXIS1", new BigDecimal("20.00"));
        final Account accountTo = new Account("AXIS2");
        final AmountTransferInput transfer = new AmountTransferInput("AXIS1", "AXIS2", new BigDecimal("2.00"));

        amountValidator.validate(accountFrom, accountTo, transfer);
    }

}