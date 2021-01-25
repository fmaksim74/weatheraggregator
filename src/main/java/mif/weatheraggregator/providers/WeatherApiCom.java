package mif.weatheraggregator.providers;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WeatherApiCom implements WeatherProvider, ResponseErrorHandler {

  private static final Logger log = LoggerFactory.getLogger(WeatherApiCom.class);

  private static String apiKey;

  @Value("${weatherapicom.key:21b955b8f8ea4d71a36132704212201}")
  public void setApiKey(String key) {
    WeatherApiCom.apiKey = key;
  }

  public static final String NAME = "Weather API";

  @Override
  public String getName() {
    return WeatherApiCom.NAME;
  }

  public static final String HOME_URL = "https://www.weatherapi.com/";

  private static final String BASE_URL = "http://api.weatherapi.com/v1";
  private static final String CURRENT_WEATHER = "/current.json?key=%s&q=%s";

  private final StringBuffer sbuf = new StringBuffer();
  private String currentLocation;

  private RestTemplate restTemplate;

  @Autowired
  private ObjectMapper mapper;

  @Autowired
  public void setRestTemplate(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.errorHandler(this).build();
  }

  private String buildRequestURI(String location) {
    currentLocation = location;
    sbuf.setLength(0);
    return sbuf.append(WeatherApiCom.BASE_URL)
        .append(WeatherApiCom.CURRENT_WEATHER.formatted(WeatherApiCom.apiKey, location)).toString();
  }

  @Override
  public String getData(String location) {
    String url = buildRequestURI(location);
    ResponseEntity<String> data = restTemplate.getForEntity(url, String.class);
    if (data.getStatusCode().series() == Series.CLIENT_ERROR) {
      log4xxError(data.getBody());
      return null;
    }
    return readTempC(data.getBody());
  }

  private String readTempC(String body) {
    try {
      JsonNode node = mapper.readTree(body);
      var value = node.at("/current/temp_c").asText();
      return value;
    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return null;
  }

  private void log4xxError(String body) {
    log.warn("{}: Request faild for {} with response: {}", WeatherApiCom.NAME, currentLocation, body);
  }

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return (response.getStatusCode().series() != Series.SUCCESSFUL
        && response.getStatusCode().series() != Series.CLIENT_ERROR);
  }

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    if (this.hasError(response)) {
      log.error(response.getStatusText());
    }
  }
}
