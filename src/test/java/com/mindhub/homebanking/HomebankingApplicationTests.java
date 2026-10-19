package com.mindhub.homebanking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HomebankingApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void authenticatedClientCanTransferWithDecimalAmount() throws Exception {
		MvcResult login = mockMvc.perform(post("/api/login")
				.param("email", "melba@mindhub.com")
				.param("password", "123456"))
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(post("/api/transactions")
				.session((MockHttpSession) login.getRequest().getSession())
				.param("amount", "100.25")
				.param("description", "Test transfer")
				.param("fromAccountNumber", "VIN001")
				.param("toAccountNumber", "VIN003"))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/accounts/1")
				.session((MockHttpSession) login.getRequest().getSession()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.balance").value(4899.75));
	}

	@Test
	void clientCannotTransferFromAnotherClientsAccount() throws Exception {
		MvcResult login = mockMvc.perform(post("/api/login")
				.param("email", "melba@mindhub.com")
				.param("password", "123456"))
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(post("/api/transactions")
				.session((MockHttpSession) login.getRequest().getSession())
				.param("amount", "1")
				.param("description", "Unauthorized")
				.param("fromAccountNumber", "VIN003")
				.param("toAccountNumber", "VIN001"))
				.andExpect(status().isForbidden());
	}

	@Test
	void clientCanOpenOwnAccountDetailsAndSeeTransactions() throws Exception {
		MvcResult login = mockMvc.perform(post("/api/login")
				.param("email", "melba@mindhub.com")
				.param("password", "123456"))
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(get("/api/accounts/1")
				.session((MockHttpSession) login.getRequest().getSession()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.number").value("VIN001"))
				.andExpect(jsonPath("$.transactions").isArray());
	}

	@Test
	void loanPageExistsAndClientCanLoadAssignedLoans() throws Exception {
		MvcResult login = mockMvc.perform(post("/api/login")
				.param("email", "melba@mindhub.com")
				.param("password", "123456"))
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(get("/web/loan-application.html"))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("displays assigned loans")));

		mockMvc.perform(get("/api/clients/current")
				.session((MockHttpSession) login.getRequest().getSession()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.loans").isArray())
				.andExpect(jsonPath("$.loans.length()").value(2));
	}

	@Test
	@Transactional
	void authenticatedClientCanApplyForLoanIntoOwnAccount() throws Exception {
		MvcResult login = mockMvc.perform(post("/api/login")
				.param("email", "melba@mindhub.com")
				.param("password", "123456"))
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(post("/api/loans")
				.session((MockHttpSession) login.getRequest().getSession())
				.contentType("application/json")
				.content("{\"loanId\":1,\"amount\":1000,\"payments\":12,\"toAccountNumber\":\"VIN001\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("Hipotecario"))
				.andExpect(jsonPath("$.amount").value(1200));

		mockMvc.perform(get("/api/accounts/1")
				.session((MockHttpSession) login.getRequest().getSession()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.balance").value(6000));
	}

	@Test
	void clientCannotApplyForLoanIntoAnotherClientsAccount() throws Exception {
		MvcResult login = mockMvc.perform(post("/api/login")
				.param("email", "melba@mindhub.com")
				.param("password", "123456"))
				.andExpect(status().isOk())
				.andReturn();

		mockMvc.perform(post("/api/loans")
				.session((MockHttpSession) login.getRequest().getSession())
				.contentType("application/json")
				.content("{\"loanId\":1,\"amount\":1000,\"payments\":12,\"toAccountNumber\":\"VIN003\"}"))
				.andExpect(status().isForbidden());
	}

}
