package mif.weatheraggregator;


import static org.junit.Assert.*;

import java.util.Objects;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import mif.weatheraggregator.providers.WeatherApiCom;

@RunWith(SpringRunner.class)
@RestClientTest(WeatherApiCom.class)
@TestPropertySource("test.properties")
public class WeatherApiComTest {
    
    @Autowired
    private static WeatherApiCom client;
    
    @BeforeAll
    static void createService() {
	assertNotNull(client);
    }
    
    @Test
    void requestServiceForExistingLocation() {
	assertNotNull(client.getValue("Chelyabinsk", "temp_c"));
    }
}
