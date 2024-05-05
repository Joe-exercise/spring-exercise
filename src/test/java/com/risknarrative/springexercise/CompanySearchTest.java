package com.risknarrative.springexercise;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.risknarrative.springexercise.db.CompanySearchRepository;


@SpringBootTest
@WireMockTest(httpsEnabled = true, httpPort = 80, proxyMode = true)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CompanySearchTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CompanySearchRepository companySearchRepository;

  @BeforeEach
  void setup(WebApplicationContext wac) throws IOException, URISyntaxException {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

    stubFor(get(urlPathMatching("/v1/Search*"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody(TestUtil.getStringFromFile("TruProxyResponseLewis.json"))));
    
    stubFor(get(urlPathMatching("/v1/Officers*"))
        .willReturn(aResponse()
          .withStatus(200)
          .withHeader("Content-Type", "application/json")
          .withBody(TestUtil.getStringFromFile("TruProxyOfficerResponse.json"))));
  }

  @ParameterizedTest
  @ValueSource(strings = {"RequestNumberAndActive.json", "RequestNameAndActive.json"})
  void activeCompanySearchTests(String requestFileName) throws IOException, URISyntaxException, Exception {

    MvcResult mvcResult = mockMvc.perform(post("/com.risknarrative.springexercise/v1/companysearch/Search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.getStringFromFile(requestFileName))
            .header("x-api-key", "DUMMY"))
            .andExpect(status().isOk())
            .andReturn();

    var expectedJsonStr = TestUtil.getStringFromFile("ResponseNameAndActive.json");
    
    JSONAssert.assertEquals(expectedJsonStr, mvcResult.getResponse().getContentAsString(), JSONCompareMode.LENIENT);
  }

  @Test
  void companyNumberAndNameTest() throws Exception {

    MvcResult mvcResult = mockMvc.perform(post("/com.risknarrative.springexercise/v1/companysearch/Search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.getStringFromFile("RequestNameAndNumber.json"))
            .header("x-api-key", "DUMMY"))
            .andExpect(status().isOk())
            .andReturn();

    var expectedJsonStr = TestUtil.getStringFromFile("ResponseNotActive.json");
    
    JSONAssert.assertEquals(expectedJsonStr, mvcResult.getResponse().getContentAsString(), JSONCompareMode.LENIENT);
  }

  @Test
  void nullNameAndNumberTest() throws Exception {

    MvcResult mvcResult = mockMvc.perform(post("/com.risknarrative.springexercise/v1/companysearch/Search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.getStringFromFile("RequestNullNameAndNumber.json"))
            .header("x-api-key", "DUMMY"))
            .andExpect(status().isBadRequest())
            .andReturn();

    assertThat(mvcResult.getResponse().getErrorMessage()).isEqualTo("Company Name or Numnber must be supplied");
  }

  @Test
  void duplicateCompanySearchTest() throws Exception {

    // Ensure company does not exist
    var deleteCompany = companySearchRepository.findByCompanyNumber("14575777");

    if (deleteCompany != null) {
      companySearchRepository.delete(deleteCompany);
    }

    stubFor(get(urlPathMatching("/v1/Search*"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody(TestUtil.getStringFromFile("TruProxyResponseSingleCompany.json"))));
    
    stubFor(get(urlPathMatching("/v1/Officers*"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody(TestUtil.getStringFromFile("TruProxySingleOfficerResponse.json"))));

    mockMvc.perform(post("/com.risknarrative.springexercise/v1/companysearch/Search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.getStringFromFile("RequestNumber.json"))
            .header("x-api-key", "DUMMY"))
            .andExpect(status().isOk());

    // Duplicate search - with DB Saved company
    MvcResult mvcResult = mockMvc.perform(post("/com.risknarrative.springexercise/v1/companysearch/Search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.getStringFromFile("RequestNumber.json"))
            .header("x-api-key", "DUMMY"))
            .andExpect(status().isOk())
            .andReturn();

    verify(exactly(1), getRequestedFor(urlEqualTo("/v1/Search?Query=14575777")));

    var expectedJsonStr = TestUtil.getStringFromFile("ResponseSingleCompany.json");
    
    JSONAssert.assertEquals(expectedJsonStr, mvcResult.getResponse().getContentAsString(), JSONCompareMode.LENIENT);
  }
}
