package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AccountsControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void setup() {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
    accountsService.getAccountsRepository().clearAccounts();
  }

  @Test
  public void accountCreateTest() throws Exception {
    createAccountMock("{\"accountId\":\"AXIS0800\",\"balance\":1000}").andExpect(status().isCreated());

    Account account = accountsService.getAccount("AXIS0800");
    assertThat(account.getAccountId()).isEqualTo("AXIS0800");
    assertThat(account.getBalance()).isEqualByComparingTo("1000");
  }

  @Test
  public void DuplicateAccountTest() throws Exception {
    createAccountMock("{\"accountId\":\"AXIS0800\",\"balance\":1000}").andExpect(status().isCreated());

    createAccountMock("{\"accountId\":\"AXIS0800\",\"balance\":1000}").andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountTest() throws Exception {
    createAccountMock("{\"balance\":1000}")
            .andExpect(status().isBadRequest());
    
    createAccountMock("{\"accountId\":\"AXIS0800\"}").andExpect(status().isBadRequest());
  }


  @Test
  public void accountWithoutBodyTest() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @Test
  public void negativeBalanceTest() throws Exception {
    createAccountMock("{\"accountId\":\"AXIS0800\",\"balance\":-1000}")
            .andExpect(status().isBadRequest());
  }

  @Test
  public void accountEmptyTest() throws Exception {
    createAccountMock("{\"accountId\":\"\",\"balance\":1000}").andExpect(status().isBadRequest());
  }

  private ResultActions createAccountMock(final String content) throws Exception {
    return this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content(content));
  }

  @Test
  public void getAccount() throws Exception {
    String uniqueAccountId = "AXIS" + System.currentTimeMillis();
    Account account = new Account(uniqueAccountId, new BigDecimal("123.45"));
    this.accountsService.createAccount(account);
    balanceVerificationTest(uniqueAccountId, new BigDecimal("123.45"));
  }

  private void balanceVerificationTest(final String accountId, final BigDecimal balance) throws Exception {
    this.mockMvc.perform(get("/v1/accounts/" + accountId))
            .andExpect(status().isOk())
            .andExpect(
                    content().string("{\"accountId\":\"" + accountId + "\",\"balance\":"+balance+"}"));
  }

  @Test
  public void wrongDataOfFromTransferTest() throws Exception {
    createAccountMock("{\"accountId\":\"AXIS2\",\"balance\":1000}").andExpect(status().isCreated());

    transferWithData("{\"accountFromId\":\"AXIS1\",\"accountToId\":\"AXIS2\",\"amount\":1000}")
            .andExpect(status().isNotFound());
  }

  @Test
  public void wrongDataOfToTransferTest() throws Exception {
    createAccountMock("{\"accountId\":\"AXIS1\",\"balance\":1000.20}").andExpect(status().isCreated());

    transferWithData("{\"accountFromId\":\"AXIS1\",\"accountToId\":\"AXIS2\",\"amount\":1000}")
            .andExpect(status().isNotFound());
  }

  private ResultActions transferWithData(String content) throws Exception {
    return this.mockMvc.perform(
            put("/v1/accounts/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(content));
  }

  @Test
  public void transferInSameAccount() throws Exception {
    createAccountMock("{\"accountId\":\"AXIS1\",\"balance\":1000}").andExpect(status().isCreated());
    transferWithData("{\"accountFromId\":\"AXIS1\",\"accountToId\":\"AXIS1\",\"amount\":1000}")
            .andExpect(status().isBadRequest());
  }

  @Test
  public void transferNegativeTest() throws Exception {
    createAccountMock("{\"accountId\":\"AXIS1\",\"balance\":100.50}").andExpect(status().isCreated());
    createAccountMock("{\"accountId\":\"AXIS2\",\"balance\":1000.50}").andExpect(status().isCreated());

    transferWithData("{\"accountFromId\":\"AXIS1\",\"accountToId\":\"AXIS2\",\"amount\":-50}")
            .andExpect(status().isBadRequest());
  }

  @Test
  public void transferWithoutJSONBody() throws Exception {
    transferWithData("{}")
            .andExpect(status().isBadRequest());
  }

  @Test
  public void transferWithoutBodyTest() throws Exception {
    this.mockMvc.perform(put("/v1/accounts/transfer").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
  }

  @Test
  public void transferZeroBalanceTest() throws Exception {
    createAccountMock("{\"accountId\":\"AXIS1\",\"balance\":2000.20}").andExpect(status().isCreated());
    createAccountMock("{\"accountId\":\"AXIS2\",\"balance\":100}").andExpect(status().isCreated());

    transferWithData("{\"accountFromId\":\"AXIS1\",\"accountToId\":\"AXIS2\",\"amount\":2000.20}")
            .andExpect(status().isOk());

    balanceVerificationTest("AXIS1", new BigDecimal("0.00"));
    balanceVerificationTest("AXIS2", new BigDecimal("2100.20"));
  }

  @Test
  public void transferPositiveBalanceTest() throws Exception {
    createAccountMock("{\"accountId\":\"AXIS1\",\"balance\":5100.21}").andExpect(status().isCreated());
    createAccountMock("{\"accountId\":\"AXIS2\",\"balance\":6000}").andExpect(status().isCreated());

    transferWithData("{\"accountFromId\":\"AXIS1\",\"accountToId\":\"AXIS2\",\"amount\":5000}")
            .andExpect(status().isOk());

    balanceVerificationTest("AXIS1", new BigDecimal("100.21"));
    balanceVerificationTest("AXIS2", new BigDecimal("11000"));
  }










}
