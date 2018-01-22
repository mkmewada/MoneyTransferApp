package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransferInput;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.NotEnoughFundsException;

interface AmountValidator {

    void validate(final Account accountFrom, final Account accountTo, final AmountTransferInput transfer) throws AccountNotFoundException, NotEnoughFundsException;

}
