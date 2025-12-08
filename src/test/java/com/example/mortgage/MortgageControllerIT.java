package com.example.mortgage;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MortgageControllerIT {

	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin123";

	@Autowired
	private MockMvc mockMvc;

	@Test
	void shouldReturnRates() throws Exception {
		mockMvc.perform(get("/api/interest-rates").with(httpBasic(USERNAME, PASSWORD))).andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	void shouldReturnMortgageCheckResponse() throws Exception {
		String requestJson = """
				{
				  "income": 20000,
				  "maturityPeriod": 30,
				  "loanValue": 100000,
				  "homeValue": 90000
				}
				""";

		mockMvc.perform(post("/api/mortgage-check").contentType(MediaType.APPLICATION_JSON).content(requestJson)
				.with(httpBasic(USERNAME, PASSWORD))).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$.feasible").value(false)).andExpect(jsonPath("$.monthlyPayment").doesNotExist())
				.andExpect(jsonPath("$.reasons").isArray())
				.andExpect(jsonPath("$.reasons[0]").value("LOAN_EXCEEDS_INCOME_MULTIPLE"))
				.andExpect(jsonPath("$.reasons[1]").value("LOAN_EXCEEDS_HOME_VALUE"));
	}

}
