package mif.weatheraggregator;

import org.junit.jupiter.api.Test;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Objects;

import org.apache.logging.log4j.util.Strings;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import mif.weatheraggregator.providers.WeatherApiCom;

@RestClientTest(WeatherApiCom.class)
@ContextConfiguration(classes= {WeatherApiCom.class, RestTemplate.class})
@TestPropertySource("classpath:test.properties")
public class WeatherApiComTest {
    
    @Value("${weatherapicom.key:21b955b8f8ea4d71a36132704212201}")
    private String apiKey;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private WeatherApiCom client;
    
    @Before
    void setup() {
  mockServer = MockRestServiceServer.createServer(restTemplate);
    }
    
    @Test
    void createService() {
  assert(Objects.nonNull(client));
  assert(this.apiKey.equals("21b955b8f8ea4d71a36132704212201"));
    }

    @Test
    void requestServiceForExistingLocation() {
  String location = "Chelyabinsk";
  
  mockServer.expect(requestTo("/v1/current.json?key=%s&q=%s".formatted(apiKey, location)))
         .andExpect(method(HttpMethod.GET))
         .andExpect(queryParam("key", apiKey))
         .andExpect(queryParam("q", "Chelyabinsk"))
         .andRespond(withSuccess("{ \"current\": { \"temp_c\": 8.0 } }", MediaType.APPLICATION_JSON));;
  
  String value = client.getValue(location, "temp_c");
  assert(Strings.isNotBlank(value));
  assert(value.equals("8.0"));
    }
    
    @Test
    void requestServiceForNotExistingLocation() {
  String location = "nowhere";
  
  mockServer.expect(requestTo("/v1/current.json?key=%s&q=%s".formatted(apiKey, location)))
         .andExpect(method(HttpMethod.GET))
         .andExpect(queryParam("key", apiKey))
         .andExpect(queryParam("q", "nowhere"))
         .andRespond(withBadRequest()
           .contentType(MediaType.APPLICATION_JSON)
           .body("{ \"error\": { \"code\": 1006, \"message\": \"No matching location found.\" } }"));
  
  String value = client.getValue(location, "temp_c");
  assert(Strings.isBlank(value));
    }

}
