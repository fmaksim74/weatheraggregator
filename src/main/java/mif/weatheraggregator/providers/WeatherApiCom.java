package mif.weatheraggregator.providers;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.MediaType;
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

    public static final String NAME = "weatherapi.com";

    @Override
    public String getName() {
	return WeatherApiCom.NAME;
    }

    private static final String HOME_URL = "https://www.weatherapi.com/";

    @Override
    public String getHomeUrl() {
	return WeatherApiCom.HOME_URL;
    }

    @Value("${weatherapicom.key:21b955b8f8ea4d71a36132704212201}")
    private String apiKey;
    public String getApiKey() {
	return this.apiKey;
    }

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplateBuilder restTemplateBuilder) {
	this.restTemplate = restTemplateBuilder
		.rootUri("http://api.weatherapi.com")
		.errorHandler(this)
		.build();
    }

    @Autowired
    private ObjectMapper mapper;

    private String currentLocation;

    @Override
    public String getValue(String location, String parameter) {

	this.currentLocation = location;
	
	HttpHeaders httpHeaders = new HttpHeaders();
	httpHeaders.setContentType(MediaType.APPLICATION_JSON);
	
	HttpEntity<Void> httpEntity = new HttpEntity<>(httpHeaders);
	
	ResponseEntity<String> response =  this.restTemplate
		.exchange("/v1/current.json?key={key}&q={location}", HttpMethod.GET, httpEntity, String.class, this.apiKey, location);

	if (response.getStatusCode().series() == Series.CLIENT_ERROR) {
	    log4xxError(response.getBody());
	    return null;
	}
	return readParameter(response.getBody(), parameter);
    }

    private String readParameter(String body, String parameter) {
	try {
	    JsonNode node = mapper.readTree(body);
	    var value = node.at("/current/" + parameter).asText();
	    return value;
	} catch (Exception e) {
	    log.error(e.getMessage());
	}

	return null;
    }

    private void log4xxError(String body) {
	log.warn("{}: Request faild for \"{}\" with response: {}", WeatherApiCom.NAME, currentLocation, body);
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
